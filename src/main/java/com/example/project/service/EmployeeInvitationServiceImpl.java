package com.example.project.service;

import com.example.project.dto.request.AcceptInvitationRequest;
import com.example.project.dto.request.UpdateEmployeeRequest;
import com.example.project.dto.response.EmployeeResponse;
import com.example.project.model.*;
import com.example.project.dto.request.AddEmployeeRequest;
import com.example.project.repositories.EmployeeInvitationRepository;
import com.example.project.repositories.RestaurantRepository;
import com.example.project.repositories.RoleRepository;
import com.example.project.repositories.UserRepository;
import com.example.project.repositories.UserRestaurantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeInvitationServiceImpl implements EmployeeInvitationService {

    @Autowired
    private EmployeeInvitationRepository invitationRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRestaurantRepository userRestaurantRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Set<RoleType> EMPLOYEE_ROLES = Set.of(
            RoleType.ROLE_MANAGER,
            RoleType.ROLE_CHEF,
            RoleType.ROLE_WAITER,
            RoleType.ROLE_CASHIER
    );

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Transactional
    public void inviteEmployee(AddEmployeeRequest request, Long ownerId) {

        // 1. Find the restaurant
        Restaurants restaurant = restaurantRepository
                .findById(request.getRestaurantId())
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found"));

        // 2. Verify that the logged-in user owns this restaurant
        boolean isOwner = userRestaurantRepository
                .existsByUser_IdAndRestaurant_IdAndRole_RoleName(
                        ownerId,
                        restaurant.getId(),
                        RoleType.ROLE_OWNER
                );

        if (!isOwner) {
            throw new RuntimeException(
                    "You are not authorized to invite employees to this restaurant"
            );
        }

        // 3. Validate the selected employee role
        RoleType roleType;

        try {
            roleType = RoleType.valueOf(request.getRole().trim());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid employee role");
        }

        if (!EMPLOYEE_ROLES.contains(roleType)) {
            throw new RuntimeException(
                    "Only Manager, Chef, Waiter and Cashier roles are allowed"
            );
        }

        Role employeeRole = roleRepository.findByRoleName(roleType)
                .orElseThrow(() ->
                        new RuntimeException("Employee role not found in database"));

        // 4. Normalize email
        String email = request.getEmail().trim().toLowerCase();

        // 5. Prevent duplicate pending invitations
        if (invitationRepository.existsByEmailAndRestaurant_IdAndAcceptedFalse(
                email, restaurant.getId())) {
            throw new RuntimeException(
                    "An invitation is already pending for this employee"
            );
        }

        // 6. Prevent inviting someone who is already assigned
        userRepository.findByEmail(email).ifPresent(existingUser -> {
            boolean alreadyAssigned = userRestaurantRepository
                    .existsByUserAndRestaurant(existingUser, restaurant);

            if (alreadyAssigned) {
                throw new RuntimeException(
                        "This employee is already assigned to the restaurant"
                );
            }
        });

        // 7. Generate a secure random token
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // 8. Hash the token before storing it
        String tokenHash = hashToken(rawToken);

        // 9. Create invitation
        EmployeeInvitation invitation = new EmployeeInvitation();

        invitation.setEmployeeName(request.getEmployeeName().trim());
        invitation.setEmail(email);
        invitation.setPhone(request.getPhone().trim());
        invitation.setRestaurant(restaurant);
        invitation.setRole(employeeRole);
        invitation.setTokenHash(tokenHash);
        invitation.setExpiresAt(LocalDateTime.now().plusHours(24));
        invitation.setAccepted(false);

        invitationRepository.save(invitation);

        // 10. Send the raw token only through the invitation email
        String invitationLink = frontendUrl
                + "/accept-invitation?token="
                + rawToken;

        emailService.sendEmployeeInvitationEmail(
                email,
                request.getEmployeeName(),
                restaurant.getRestaurantName(),
                invitationLink
        );
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new RuntimeException("Unable to generate invitation hash", ex);
        }
    }

    @Override
    @Transactional
    public void acceptInvitation(AcceptInvitationRequest request) {

        // 1. Hash the token received from the frontend
        String tokenHash = hashToken(request.getToken().trim());

        // 2. Find and lock the invitation
        EmployeeInvitation invitation = invitationRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() ->
                        new RuntimeException("Invalid invitation link"));

        // 3. Check whether invitation is already accepted
        if (invitation.isAccepted()) {
            throw new RuntimeException("Invitation has already been accepted");
        }

        // 4. Check invitation expiry
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitation link has expired");
        }

        // 5. Find the employee using the invited email
        String email = invitation.getEmail().trim().toLowerCase();

        User employee = userRepository.findByEmail(email)
                .orElse(null);

        // 6. Create a new account if the employee doesn't exist
        if (employee == null) {

            if (userRepository.existsByUserName(request.getUsername().trim())) {
                throw new RuntimeException("Username is already taken");
            }

            Role employeeGlobalRole = roleRepository
                    .findByRoleName(RoleType.ROLE_EMPLOYEE)
                    .orElseThrow(() ->
                            new RuntimeException("ROLE_EMPLOYEE not found"));

            employee = new User(
                    request.getUsername().trim(),
                    email,
                    passwordEncoder.encode(request.getPassword())
            );

            employee.setPhone(invitation.getPhone());
            employee.setActive(true);
            employee.setEmailVerified(true);
            employee.setIsAdmin(false);
            employee.setRole(employeeGlobalRole);

            employee = userRepository.save(employee);

        } else {

            // Existing account must already be verified and active.
            // Do not overwrite its password or global role.
            if (!Boolean.TRUE.equals(employee.getActive())
                    || !Boolean.TRUE.equals(employee.getEmailVerified())) {

                throw new RuntimeException(
                        "Existing account is inactive or email is not verified. " +
                                "Please resolve account verification before accepting."
                );
            }
        }

        // 7. Prevent duplicate restaurant assignment
        boolean alreadyAssigned = userRestaurantRepository
                .existsByUserAndRestaurant(
                        employee,
                        invitation.getRestaurant()
                );

        if (alreadyAssigned) {
            throw new RuntimeException(
                    "Employee is already assigned to this restaurant"
            );
        }

        // 8. Create restaurant-specific employee assignment
        UserRestaurant userRestaurant = new UserRestaurant();

        userRestaurant.setUser(employee);
        userRestaurant.setRestaurant(invitation.getRestaurant());
        userRestaurant.setRole(invitation.getRole());

        userRestaurantRepository.save(userRestaurant);

        // 9. Mark invitation as accepted
        invitation.setAccepted(true);

        invitationRepository.save(invitation);
    }

    @Override
    @Transactional
    public List<EmployeeResponse> getEmployees(Long restaurantId, Long ownerId) {

        // 1. Find restaurant
        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // 2. Verify restaurant ownership
        boolean isOwner = userRestaurantRepository
                .existsByUser_IdAndRestaurant_IdAndRole_RoleName(
                        ownerId,
                        restaurantId,
                        RoleType.ROLE_OWNER
                );

        if (!isOwner) {
            throw new RuntimeException(
                    "You are not authorized to view employees of this restaurant"
            );
        }

        // 3. Get all user-restaurant assignments
        List<UserRestaurant> assignments =
                userRestaurantRepository.findByRestaurant(restaurant);

        // 4. Convert assignments into response DTOs
        return assignments.stream()
                // Exclude the owner from the employee list
                .filter(assignment ->
                        assignment.getRole() != null &&
                                assignment.getRole().getRoleName() != RoleType.ROLE_OWNER
                )
                .map(assignment -> EmployeeResponse.builder()
                        .employeeId(assignment.getUser().getId())
                        .assignmentId(assignment.getId())
                        .employeeName(assignment.getUser().getUserName())
                        .email(assignment.getUser().getEmail())
                        .phone(assignment.getUser().getPhone())
                        .role(assignment.getRole().getRoleName().name())
                        .build()
                )
                .toList();
    }

    @Override
    @Transactional
    public void updateEmployee(Long assignmentId, UpdateEmployeeRequest request, Long ownerId) {

        UserRestaurant assignment = userRestaurantRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Employee assignment not found"));

        Restaurants restaurant = assignment.getRestaurant();
        boolean isOwner = userRestaurantRepository.existsByUser_IdAndRestaurant_IdAndRole_RoleName(
                        ownerId, restaurant.getId(), RoleType.ROLE_OWNER);

        if (!isOwner) {
            throw new RuntimeException("You are not authorized to update this employee");
        }

        RoleType roleType;

        try {
            roleType = RoleType.valueOf(request.getRole().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid employee role");
        }

        if (!EMPLOYEE_ROLES.contains(roleType)) {
            throw new RuntimeException(
                    "Only Manager, Chef, Waiter and Cashier roles are allowed");
        }

        // 5. Find role in database
        Role employeeRole = roleRepository.findByRoleName(roleType)
                .orElseThrow(() -> new RuntimeException("Employee role not found"));

        // 6. Update employee details
        User employee = assignment.getUser();

        employee.setUserName(request.getEmployeeName().trim());
        employee.setPhone(request.getPhone().trim());

        // 7. Update restaurant-specific role
        assignment.setRole(employeeRole);

        // 8. Save changes
        userRepository.save(employee);
        userRestaurantRepository.save(assignment);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long assignmentId, Long ownerId) {

        // 1. Find employee's restaurant assignment
        UserRestaurant assignment = userRestaurantRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException("Employee assignment not found"));

        // 2. Get restaurant
        Restaurants restaurant = assignment.getRestaurant();

        // 3. Verify that the logged-in user owns this restaurant
        boolean isOwner = userRestaurantRepository
                .existsByUser_IdAndRestaurant_IdAndRole_RoleName(
                        ownerId,
                        restaurant.getId(),
                        RoleType.ROLE_OWNER
                );

        if (!isOwner) {
            throw new RuntimeException(
                    "You are not authorized to remove this employee"
            );
        }

        // 4. Prevent deletion of the owner assignment
        if (assignment.getRole() == null ||
                assignment.getRole().getRoleName() == RoleType.ROLE_OWNER) {
            throw new RuntimeException(
                    "Owner assignment cannot be deleted"
            );
        }

        // 5. Delete only the restaurant assignment
        userRestaurantRepository.delete(assignment);
    }
}