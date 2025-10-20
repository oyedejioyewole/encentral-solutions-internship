package com.encentral.event_management.model;

import com.encentral.entities.JpaUser;

public class UserMapper {
    public static User jpaUserToUser(JpaUser jpaUser) {
        if (jpaUser == null) return null;

        User user = new User();
        user.setUserId(jpaUser.getId());
        user.setEmail(jpaUser.getEmail());
        user.setFirstName(jpaUser.getFirstName());
        user.setLastName(jpaUser.getLastName());
        user.setRole(jpaUser.getRole());
        user.setDepartment(jpaUser.getDepartment());
        user.setToken(jpaUser.getToken());
        return user;
    }
}