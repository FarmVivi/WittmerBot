package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.SelectionDialog;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MenuCommandEleveJoinClasseAskClasse {
    @SuppressWarnings("StringBufferReplaceableByString")
    public static void execute(Member member, TextChannel textChannel, UserBean userBean, Matiere matiere) {
        Map<Integer, ClasseBean> classes = new HashMap<>();
        SelectionDialog.Builder builder = new SelectionDialog.Builder()
                .setText("Naviguez parmi ce menu pour choisir la classe que vous voulez rejoindre")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .setSelectedEnds(">**", "**<")
                .useSingleSelectionMode(true)
                .useLooping(true)
                .setSelectionConsumer((message, integer) -> {
                    //CONTINUE
                    Main.joinClasse(member, classes.get(integer));
                    message.delete().queue();
                    MenuCommandSucess.execute(member, textChannel, Role.ELEVE);
                })
                .setCanceled(message -> {
                    message.delete().queue();
                    MenuCommandFailure.execute(member, textChannel, Role.ELEVE);
                });
        int i = 0;
        List<ClasseBean> finalClasses = MenuCommandEleveJoinClasseUtils.getJoinableClasses(userBean, matiere);
        if (finalClasses.isEmpty()) {
            textChannel.sendMessage(Main.commandClient.getError() + " ERREUR: Vous ne pouvez rejoindre aucune classe de la matière " + matiere.getName())
                    .delay(30, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
            MenuCommandFailure.execute(member, textChannel, Role.ELEVE);
            return;
        } else {
            for (ClasseBean classeBean : finalClasses) {
                try {
                    i++;
                    UserBean profBean = Main.dataServiceManager.getUser(classeBean.getDiscord_prof_id(), new UserBean(member.getIdLong(), "", "", 0L, false, 0, "", false));
                    StringBuilder classeName = new StringBuilder(classeBean.getName());
                    classeName.append(" - ");
                    classeName.append(profBean.getPrenom().toUpperCase(), 0, 1);
                    classeName.append(profBean.getPrenom(), 1, profBean.getPrenom().length());
                    classeName.append(" ");
                    classeName.append(profBean.getNom().toUpperCase());
                    builder.addChoices(classeName.toString());
                    classes.put(i, classeBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        builder.build().display(textChannel);
    }
}
