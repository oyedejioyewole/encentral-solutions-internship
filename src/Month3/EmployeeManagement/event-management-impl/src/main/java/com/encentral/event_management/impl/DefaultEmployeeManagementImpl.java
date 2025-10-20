package com.encentral.event_management.impl;

import com.encentral.entities.AttendanceStatus;
import com.encentral.entities.JpaAttendance;
import com.encentral.entities.JpaUser;
import com.encentral.entities.UserRole;
import com.encentral.event_management.api.IEmployeeManagement;
import com.encentral.event_management.model.*;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultEmployeeManagementImpl implements IEmployeeManagement {
    private final JPAApi jpaApi;

    @Inject
    public DefaultEmployeeManagementImpl(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @Override
    public LoginResponse signIn(LoginRequest request) {
        return jpaApi.withTransaction(em -> {
            JpaUser user = em.createQuery(
                            "SELECT u FROM JpaUser u WHERE u.email = :email", JpaUser.class)
                    .setParameter("email", request.getEmail())
                    .getResultList()
                    .stream().findFirst()
                    .orElse(null);

            if (user == null) {
                return new LoginResponse(null, "Invalid email or password", null);
            }

            if (!user.getPassword().equals(request.getPassword())) {
                return new LoginResponse(null, "Invalid email or password", null);
            }

            return new LoginResponse(
                    user.getToken(),
                    "Login successful",
                    UserMapper.jpaUserToUser(user)
            );
        });
    }

    @Override
    public EmployeeResponse addEmployee(String adminToken, EmployeeRequest request) {
        return jpaApi.withTransaction(em -> {
            JpaUser admin = findUserByToken(em, adminToken);
            if (admin == null || admin.getRole() != UserRole.ADMIN) {
                return new EmployeeResponse("Unauthorized. Admin access required", null, null);
            }

            // Check if email already exists
            long count = em.createQuery(
                            "SELECT COUNT(u) FROM JpaUser u WHERE u.email = :email", Long.class)
                    .setParameter("email", request.getEmail())
                    .getSingleResult();

            if (count > 0) {
                return new EmployeeResponse("Email already exists", null, null);
            }

            // Generate 4-digit pin
            String pin = String.format("%04d", new Random().nextInt(10000));
            String token = UUID.randomUUID().toString();

            JpaUser employee = new JpaUser(
                    request.getEmail(),
                    pin,
                    request.getFirstName(),
                    request.getLastName(),
                    UserRole.EMPLOYEE,
                    token
            );
            employee.setDepartment(request.getDepartment());

            em.persist(employee);

            return new EmployeeResponse(
                    "Employee added successfully",
                    pin,
                    UserMapper.jpaUserToUser(employee)
            );
        });
    }

    @Override
    public ApiResponse removeEmployee(String adminToken, String employeeId) {
        return jpaApi.withTransaction(em -> {
            JpaUser admin = findUserByToken(em, adminToken);
            if (admin == null || admin.getRole() != UserRole.ADMIN) {
                return new ApiResponse(false, "Unauthorized. Admin access required");
            }

            JpaUser employee = em.find(JpaUser.class, employeeId);
            if (employee == null) {
                return new ApiResponse(false, "Employee not found");
            }

            if (employee.getRole() == UserRole.ADMIN) {
                return new ApiResponse(false, "Cannot remove admin user");
            }

            em.remove(employee);
            return new ApiResponse(true, "Employee removed successfully");
        });
    }

    @Override
    public List<User> getEmployees(String adminToken) {
        return jpaApi.withTransaction(em -> {
            JpaUser admin = findUserByToken(em, adminToken);
            if (admin == null || admin.getRole() != UserRole.ADMIN) {
                return List.of();
            }

            List<JpaUser> employees = em.createQuery(
                            "SELECT u FROM JpaUser u WHERE u.role = :role", JpaUser.class)
                    .setParameter("role", UserRole.EMPLOYEE)
                    .getResultList();

            return employees.stream()
                    .map(UserMapper::jpaUserToUser)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public AttendanceResponse markAttendance(String userToken) {
        return jpaApi.withTransaction(em -> {
            JpaUser user = findUserByToken(em, userToken);
            if (user == null) {
                return new AttendanceResponse(false, "Invalid token", null);
            }

            if (user.getRole() != UserRole.EMPLOYEE) {
                return new AttendanceResponse(false, "Only employees can mark attendance", null);
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            LocalTime currentTime = now.toLocalTime();
            DayOfWeek dayOfWeek = today.getDayOfWeek();

            // Check if weekend
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                return new AttendanceResponse(false, "It's not a work day", null);
            }

            // Check if too early (before 9 AM)
            if (currentTime.isBefore(LocalTime.of(9, 0))) {
                return new AttendanceResponse(false, "Too early to mark attendance", null);
            }

            // Check if too late (after 5 PM)
            if (currentTime.isAfter(LocalTime.of(17, 0))) {
                return new AttendanceResponse(false, "Too late to mark attendance", null);
            }

            // Check if already marked today
            long count = em.createQuery(
                            "SELECT COUNT(a) FROM JpaAttendance a WHERE a.employee = :employee AND a.date = :date",
                            Long.class)
                    .setParameter("employee", user)
                    .setParameter("date", today)
                    .getSingleResult();

            if (count > 0) {
                return new AttendanceResponse(false, "Attendance already marked for today", null);
            }

            // Mark attendance
            JpaAttendance attendance = new JpaAttendance(
                    user,
                    today,
                    AttendanceStatus.PRESENT
            );

            em.persist(attendance);

            return new AttendanceResponse(
                    true,
                    "Attendance marked successfully",
                    AttendanceMapper.jpaAttendanceToAttendance(attendance)
            );
        });
    }

    @Override
    public List<Attendance> getDailyAttendance(String adminToken, LocalDate date) {
        return jpaApi.withTransaction(em -> {
            JpaUser admin = findUserByToken(em, adminToken);
            if (admin == null || admin.getRole() != UserRole.ADMIN) {
                return List.of();
            }

            List<JpaAttendance> attendances = em.createQuery(
                            "SELECT a FROM JpaAttendance a WHERE a.date = :date", JpaAttendance.class)
                    .setParameter("date", date)
                    .getResultList();

            return attendances.stream()
                    .map(AttendanceMapper::jpaAttendanceToAttendance)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public ApiResponse updatePassword(String userToken, UpdatePasswordRequest request) {
        return jpaApi.withTransaction(em -> {
            JpaUser user = findUserByToken(em, userToken);
            if (user == null) {
                return new ApiResponse(false, "Invalid token");
            }

            if (!user.getPassword().equals(request.getOldPassword())) {
                return new ApiResponse(false, "Old password is incorrect");
            }

            user.setPassword(request.getNewPassword());
            em.merge(user);

            return new ApiResponse(true, "Password updated successfully");
        });
    }

    private JpaUser findUserByToken(javax.persistence.EntityManager em, String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        return em.createQuery("SELECT u FROM JpaUser u WHERE u.token = :token", JpaUser.class)
                .setParameter("token", token)
                .getResultList().stream()
                .findFirst()
                .orElse(null);
    }
}