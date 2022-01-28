package net.keeperxtl.springbootproject.DB.models;

public enum Role {
    USER("user"),
    ADMIN("admin"),
    MODER("moder");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Role fromString(String text) {
        for (Role r: Role.values()) {
            if (r.name.equalsIgnoreCase(text))
                return r;
        }
        return null;
    }
}
