package fr.farmvivi.wittmer.menu.verif;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MenuVerifAskClasse {
    public static void execute(Member member, TextChannel textChannel, Role role, Level level) {
        Map<Integer, ClasseBean> classes = new HashMap<>();
        OrderedMenu.Builder builder = new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .setSelection((message, integer) -> {
                    //CONTINUE
                    MenuVerifAskDelegue.execute(member, textChannel, role, level, classes.get(integer));
                });
        int i = 0;
        try {
            List<ClasseBean> classeBeans = Main.dataServiceManager.getClassesListOfALevelAndMatiere(level, Matiere.AUCUNE);
            if (classeBeans.isEmpty()) {
                textChannel.sendMessage(Main.commandClient.getError() + " ERREUR: Aucune classe d'enregistré en " + level.getName())
                        .delay(10, TimeUnit.SECONDS)
                        .flatMap(message -> {
                            message.delete().queue();
                            MenuVerifStart.execute(member, textChannel);
                            return null;
                        }).queue();
                return;
            } else {
                for (ClasseBean classeBean : classeBeans) {
                    i++;
                    builder.addChoice(classeBean.getName());
                    classes.put(i, classeBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.build().display(textChannel);
    }
}
