package com.example.project.service;

import com.example.project.dto.request.AcceptInvitationRequest;
import com.example.project.dto.request.AddEmployeeRequest;
import com.example.project.dto.request.UpdateEmployeeRequest;
import com.example.project.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeInvitationService {

    void inviteEmployee(AddEmployeeRequest request, Long ownerId);
    void acceptInvitation(AcceptInvitationRequest request);

    List<EmployeeResponse> getEmployees(Long restaurantId, Long ownerId);

    void updateEmployee(Long assignmentId, UpdateEmployeeRequest request, Long ownerId);

    void deleteEmployee(Long assignmentId, Long ownerId);
}
