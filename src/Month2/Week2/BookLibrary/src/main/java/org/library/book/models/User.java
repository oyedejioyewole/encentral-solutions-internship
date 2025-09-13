package org.library.book.models;

interface UserPrototype {
    public int getPriorityLevel();
}

public class User implements UserPrototype {
    private final String id;
    private final String name;
    private final UserType userType;

    public User(String id, String name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public UserType getUserType() { return userType; }

    @Override
    public int getPriorityLevel() { return 0; }
}