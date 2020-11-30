package fr.farmvivi.wittmer;

public enum Level {
    SECONDE((short) 1, "S", "Seconde"),
    PREMIERE((short) 2, "P", "Première"),
    TERMINALE((short) 3, "T", "Terminale");

    private final short id;
    private final String prefix;
    private final String name;

    Level(short id, String prefix, String name) {
        this.id = id;
        this.prefix = prefix;
        this.name = name;
    }

    public static Level getById(short id) {
        for (Level level : values())
            if (level.getId() == id)
                return level;
        return null;
    }

    public short getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }
}
