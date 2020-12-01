package fr.farmvivi.wittmer.menu.command;

import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;

import java.util.ArrayList;
import java.util.List;

public class MenuCommandEleveJoinClasseUtils {
    public static List<Matiere> getJoinableMatieres(UserBean userBean) {
        List<Matiere> matieres = new ArrayList<>();
        for (Matiere matiere : Matiere.values()) {
            if (!getJoinableClasses(userBean, matiere).isEmpty())
                matieres.add(matiere);
        }
        return matieres;
    }

    public static List<ClasseBean> getJoinableClasses(UserBean userBean, Matiere matiere) {
        List<ClasseBean> finalClasses = new ArrayList<>();
        try {
            ClasseBean defaultClasse = Main.dataServiceManager.getUserDefaultClasse(userBean);
            List<ClasseBean> alreadyJoinedClasses = Main.dataServiceManager.getUserClasses(userBean);
            List<ClasseBean> availableClasses = Main.dataServiceManager.getClassesListOfALevelAndMatiere(defaultClasse.getLevel(), matiere);
            for (ClasseBean availableClasse : availableClasses) {
                boolean add = true;
                if (availableClasse.getMatiere().isEntireClasse() && !availableClasse.getName().contains(defaultClasse.getName().split("⌋ ")[1]))
                    add = false;
                for (ClasseBean alreadyJoinedClasse : alreadyJoinedClasses) {
                    if (availableClasse.getId() == alreadyJoinedClasse.getId()) {
                        add = false;
                        break;
                    }
                }
                if (add)
                    finalClasses.add(availableClasse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalClasses;
    }
}
