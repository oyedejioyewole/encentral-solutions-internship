package controllers;

import com.encentral.event_management.api.IEmployeeManagement;
import com.encentral.event_management.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDate;

@Transactional
public class EmployeeController extends Controller {

    private final IEmployeeManagement employeeManagement;
    private final ObjectMapper objectMapper;

    @Inject
    public EmployeeController(IEmployeeManagement employeeManagement, ObjectMapper objectMapper) {
        this.employeeManagement = employeeManagement;
        this.objectMapper = objectMapper;
    }

    public Result signIn(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            LoginRequest loginRequest = objectMapper.treeToValue(json, LoginRequest.class);
            LoginResponse response = employeeManagement.signIn(loginRequest);

            if (response.getToken() == null) {
                return unauthorized(objectMapper.writeValueAsString(response));
            }

            return ok(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            return badRequest("Invalid request: " + e.getMessage());
        }
    }

    public Result addEmployee(Http.Request request) {
        try {
            String token = request.header("Authorization").orElse(null);
            JsonNode json = request.body().asJson();
            EmployeeRequest employeeRequest = objectMapper.treeToValue(json, EmployeeRequest.class);

            EmployeeResponse response = employeeManagement.addEmployee(token, employeeRequest);

            if (response.getPin() == null) {
                return badRequest(objectMapper.writeValueAsString(response));
            }

            return ok(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            return badRequest("Invalid request: " + e.getMessage());
        }
    }

    public Result removeEmployee(Http.Request request, String employeeId) {
        try {
            String token = request.header("Authorization").orElse(null);
            ApiResponse response = employeeManagement.removeEmployee(token, employeeId);

            if (!response.isSuccess()) {
                return badRequest(objectMapper.writeValueAsString(response));
            }

            return ok(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            return badRequest("Invalid request: " + e.getMessage());
        }
    }

    public Result getEmployees(Http.Request request) {
        try {
            String token = request.header("Authorization").orElse(null);
            var employees = employeeManagement.getEmployees(token);
            return ok(objectMapper.writeValueAsString(employees));
        } catch (Exception e) {
            return badRequest("Invalid request: " + e.getMessage());
        }
    }

    public Result markAttendance(Http.Request request) {
        try {
            String token = request.header("Authorization").orElse(null);
            AttendanceResponse response = employeeManagement.markAttendance(token);

            if (!response.isSuccess()) {
                return badRequest(objectMapper.writeValueAsString(response));
            }

            return ok(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            return badRequest("Invalid request: " + e.getMessage());
        }
    }

    public Result getDailyAttendance(Http.Request request, String date) {
        try {
            String token = request.header("Authorization").orElse(null);
            LocalDate localDate = LocalDate.parse(date);
            var attendances = employeeManagement.getDailyAttendance(token, localDate);
            return ok(objectMapper.writeValueAsString(attendances));
        } catch (Exception e) {
            return badRequest("Invalid request: " + e.getMessage());
        }
    }

    public Result updatePassword(Http.Request request) {
        try {
            String token = request.header("Authorization").orElse(null);
            JsonNode json = request.body().asJson();
            UpdatePasswordRequest passwordRequest = objectMapper.treeToValue(json, UpdatePasswordRequest.class);

            ApiResponse response = employeeManagement.updatePassword(token, passwordRequest);

            if (!response.isSuccess()) {
                return badRequest(objectMapper.writeValueAsString(response));
            }

            return ok(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            return badRequest("Invalid request: " + e.getMessage());
        }
    }
}