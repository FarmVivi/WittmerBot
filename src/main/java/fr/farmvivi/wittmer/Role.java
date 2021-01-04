package fr.farmvivi.wittmer;

public enum Role {
    ELEVE(1L, 753696822995714225L),
    PROF(2L, 753696271872819331L),
    DELEGUE(3L, 782576791197974538L);

    private final long id;
    private final long roleId;

    Role(long id, long roleId) {
        this.id = id;
        this.roleId = roleId;
    }

    public static Role getById(long id) {
        for (Role role : values())
            if (role.getId() == id)
                return role;
        return null;
    }

    public long getId() {
        return id;
    }

    public long getRoleId() {
        return roleId;
    }
}
