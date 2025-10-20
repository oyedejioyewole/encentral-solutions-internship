package com.encentral.event_management.model;

import com.encentral.entities.JpaAttendance;

public class AttendanceMapper {
    public static Attendance jpaAttendanceToAttendance(JpaAttendance jpaAttendance) {
        if (jpaAttendance == null) return null;

        Attendance attendance = new Attendance();
        attendance.setAttendanceId(jpaAttendance.getId());
        attendance.setEmployeeId(jpaAttendance.getEmployee().getId());
        attendance.setDate(jpaAttendance.getDate());
        attendance.setStatus(jpaAttendance.getStatus());
        attendance.setCheckInTime(jpaAttendance.getCheckInTime());
        return attendance;
    }
}