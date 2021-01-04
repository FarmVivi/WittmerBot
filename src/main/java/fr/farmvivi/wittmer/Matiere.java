package fr.farmvivi.wittmer;

public enum Matiere {
    AUCUNE(0L, "", "\uD83D\uDCDA", true),
    ESSEIGNEMENT_SCIENTIFIQUE_PHYSIQUE_CHIMIE(1L, "ES - Physique - Chimie", "\uD83E\uDE90", true),
    ESSEIGNEMENT_SCIENTIFIQUE_SVT(2L, "ES - SVT", "\uD83C\uDF0D", true),
    HISTOIRE_GEOGRAPHIE(3L, "Histoire Géographie", "\uD83D\uDCD6", true),
    PHILOSOPHIE(4L, "Philosophie", "\uD83D\uDCD1", true),
    ANGLAIS(5L, "Anglais", "\uD83C\uDDFA\uD83C\uDDF8", false),
    ALLEMAND(6L, "Allemand", "\uD83C\uDDE9\uD83C\uDDEA", false),
    ESPAGNOL(7L, "Espagnol", "\uD83C\uDDEA\uD83C\uDDF8", false),
    SPE_MATHEMATIQUES(8L, "Spé Mathématiques", "\uD83D\uDD22", false),
    SPE_PHYSIQUE_CHIMIE(9L, "Spé Physique Chimie", "\uD83E\uDDEA", false),
    SPE_SVT(10L, "Spé SVT", "\uD83E\uDDEC", false);

    private final long id;
    private final String name;
    private final String emoji;
    private final boolean entireClasse;

    Matiere(long id, String name, String emoji, boolean entireClasse) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.entireClasse = entireClasse;
    }

    public static Matiere getById(long id) {
        for (Matiere matiere : values())
            if (matiere.getId() == id)
                return matiere;
        return null;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    public boolean isEntireClasse() {
        return entireClasse;
    }
}
