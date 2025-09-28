package org.todos.smart.repository;

import com.google.common.base.Preconditions;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.todos.smart.model.ApiResponse;
import org.todos.smart.model.User;
import org.todos.smart.util.ErrorHandler;
import org.todos.smart.util.JacksonUtil;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final BidiMap<String, String> userEmailToId;
    private final Map<String, User> users;

    private static UserRepository instance;

    public UserRepository() {
        this.userEmailToId = new DualHashBidiMap<>();
        this.users = new HashMap<>();


    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public String createUser(User user) {
        return ErrorHandler.executeWithErrorHandling(() -> {
            Preconditions.checkArgument(!userEmailToId.containsKey(user.getEmail()), "Email already exists");
            userEmailToId.put(user.getEmail(), user.getId());
            users.put(user.getId(), user);

            ApiResponse<User> response = ApiResponse.success("User registered successfully", user);
            return JacksonUtil.stringify(response);
        });
    }

    public String findUserId(String email) {
        return userEmailToId.get(email);
    }

    public User findUserWithId(String userId) {
        return users.get(userId);
    }

    public void clean() {
        userEmailToId.clear();
        users.clear();
    }
}
