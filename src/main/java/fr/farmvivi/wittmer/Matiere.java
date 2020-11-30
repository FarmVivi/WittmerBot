package fr.farmvivi.wittmer;

public enum Matiere {
    AUCUNE(0, "", true),
    ESSEIGNEMENT_SCIENTIFIQUE(1, "Sciences", true),
    HISTOIRE_GEOGRAPHIE(2, "Histoire Géographie", true),
    PHILOSOPHIE(3, "Philosophie", true),
    ANGLAIS(4, "Anglais", false),
    ALLEMAND(5, "Allemand", false),
    ESPAGNOL(6, "Espagnol", false),
    SPE_MATHEMATIQUES(7, "Spé Mathématiques", false),
    SPE_PHYSIQUE_CHIMIE(8, "Spé Physique Chimie", false),
    SPE_SVT(9, "Spé SVT", false);

    private final int id;
    private final String name;
    private final boolean entireClasse;

    Matiere(int id, String name, boolean entireClasse) {
        this.id = id;
        this.name = name;
        this.entireClasse = entireClasse;
    }

    public static Matiere getById(int id) {
        for (Matiere matiere : values())
            if (matiere.getId() == id)
                return matiere;
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEntireClasse() {
        return entireClasse;
    }
}
