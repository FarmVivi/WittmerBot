package fr.farmvivi.wittmer;

public enum Role {
    ELEVE((short) 1, 753696822995714225L),
    PROF((short) 2, 753696271872819331L),
    DELEGUE((short) 3, 782576791197974538L);

    private final short id;
    private final long roleId;

    Role(short id, long roleId) {
        this.id = id;
        this.roleId = roleId;
    }

    public static Role getById(short id) {
        for (Role role : values())
            if (role.getId() == id)
                return role;
        return null;
    }

    public short getId() {
        return id;
    }

    public long getRoleId() {
        return roleId;
    }
}
