package com.example.project.controller;

import com.example.project.dto.request.AddEmployeeRequest;
import com.example.project.dto.request.AcceptInvitationRequest;
import com.example.project.dto.request.UpdateEmployeeRequest;
import com.example.project.dto.response.EmployeeResponse;
import com.example.project.security.UserDetailsImpl;
import com.example.project.service.EmployeeInvitationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rms")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeInvitationService employeeInvitationService;

    // Owner invites an employee
    @PostMapping("/owner/employee/invite")
    public ResponseEntity<?> inviteEmployee(@Valid @RequestBody AddEmployeeRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        employeeInvitationService.inviteEmployee(request, userDetails.getId());

        return ResponseEntity.ok("Employee invitation sent successfully");
    }

    // Employee accepts the invitation
    @PostMapping("/employee/accept-invitation")
    public ResponseEntity<?> acceptInvitation(
            @Valid @RequestBody AcceptInvitationRequest request
    ) {

        employeeInvitationService.acceptInvitation(request);

        return ResponseEntity.ok(
                "Invitation accepted. Your account has been created successfully."
        );
    }

    @GetMapping("/owner/restaurant/{restaurantId}/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(@PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<EmployeeResponse> employees = employeeInvitationService.getEmployees(restaurantId, userDetails.getId());
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/owner/employee/assignment/{assignmentId}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long assignmentId,
            @Valid @RequestBody UpdateEmployeeRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        employeeInvitationService.updateEmployee(assignmentId, request, userDetails.getId());

        return ResponseEntity.ok("Employee updated successfully");
    }

    @DeleteMapping("/owner/employee/assignment/{assignmentId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long assignmentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        employeeInvitationService.deleteEmployee(
                assignmentId,
                userDetails.getId()
        );

        return ResponseEntity.ok(
                "Employee removed from restaurant successfully"
        );
    }
}
