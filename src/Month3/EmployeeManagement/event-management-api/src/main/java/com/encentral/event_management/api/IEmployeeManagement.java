package com.encentral.event_management.api;

import com.encentral.event_management.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IEmployeeManagement {
    LoginResponse signIn(LoginRequest request);
    EmployeeResponse addEmployee(String adminToken, EmployeeRequest request);
    ApiResponse removeEmployee(String adminToken, String employeeId);
    List<User> getEmployees(String adminToken);
    AttendanceResponse markAttendance(String userToken);
    List<Attendance> getDailyAttendance(String adminToken, LocalDate date);
    ApiResponse updatePassword(String userToken, UpdatePasswordRequest request);
}