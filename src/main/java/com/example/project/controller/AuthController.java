package com.example.project.controller;

import com.example.project.dto.request.LoginRequest;
import com.example.project.dto.request.RefreshTokenRequest;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.exception.APIException;
import com.example.project.model.RefreshToken;
import com.example.project.model.Role;
import com.example.project.model.User;
import com.example.project.repositories.RoleRepository;
import com.example.project.repositories.UserRepository;
import com.example.project.security.UserDetailsImpl;
import com.example.project.security.jwt.JwtUtils;
import com.example.project.security.response.JwtResponse;
import com.example.project.security.response.MessageResponse;
import com.example.project.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rms")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new APIException("Invalid email or password"));


        if (!Boolean.TRUE.equals(user.getActive())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Account is not active"));
        }

//        if (!Boolean.TRUE.equals(user.getIsAdmin()) && !Boolean.TRUE.equals(user.getEmailVerified())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(new MessageResponse("Please verify your email before login"));
//        }

        try{
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        return ResponseEntity.ok(
                new JwtResponse(
                        userDetails.getId(),
                        accessToken,
                        refreshToken.getToken(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        role
                )
        );
    } catch (
            BadCredentialsException e) {
            throw new APIException("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> authenticateUser(@RequestBody RegisterRequest registerRequest){

        if(userRepository.existsByEmail(registerRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists!"));
        }

        if(userRepository.existsByUserName((registerRequest.getUsername()))){
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists!"));
        }

        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = roleRepository
                .findByRoleName(registerRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Role Not Found"));

        System.out.println("Role ID = " + role.getId());
        System.out.println("Role Name = " + role.getRoleName());

        user.setRole(role);

        System.out.println("User Role ID = " + user.getRole().getId());

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User Registered successfully"));

    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(
                new MessageResponse("Logged out successfully.")
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();


        if (!Boolean.TRUE.equals(user.getActive())) {

            return ResponseEntity
                    .status(401)
                    .body(
                            new MessageResponse(
                                    "User account is inactive"
                            )
                    );
        }

        String newAccessToken = jwtUtils.generateTokenFromUsername(user.getEmail());

        return ResponseEntity.ok(new JwtResponse(
                        user.getId(),
                        newAccessToken,
                        refreshToken.getToken(),
                        user.getUserName(),
                        user.getEmail())
        );
    }

}
