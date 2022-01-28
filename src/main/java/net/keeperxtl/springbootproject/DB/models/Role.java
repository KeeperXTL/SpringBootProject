package net.keeperxtl.springbootproject.DB.models;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN"),
    MODER("MODER");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
