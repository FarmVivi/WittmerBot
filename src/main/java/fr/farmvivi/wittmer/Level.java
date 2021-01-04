package fr.farmvivi.wittmer;

public enum Level {
    SECONDE(1L, "S", "Seconde"),
    PREMIERE(2L, "P", "Première"),
    TERMINALE(3L, "T", "Terminale");

    private final long id;
    private final String prefix;
    private final String name;

    Level(long id, String prefix, String name) {
        this.id = id;
        this.prefix = prefix;
        this.name = name;
    }

    public static Level getById(long id) {
        for (Level level : values())
            if (level.getId() == id)
                return level;
        return null;
    }

    public long getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }
}
