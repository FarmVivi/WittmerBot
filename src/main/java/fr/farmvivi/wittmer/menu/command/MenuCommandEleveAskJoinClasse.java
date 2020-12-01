package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.SelectionDialog;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MenuCommandEleveAskJoinClasse {
    public static void execute(Member member, TextChannel textChannel) {
        try {
            UserBean userBean = Main.dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
            Map<Integer, Matiere> matieres = new HashMap<>();
            SelectionDialog.Builder builder = new SelectionDialog.Builder()
                    .setText("Naviguez parmi ce menu pour choisir la matière que vous voulez rejoindre")
                    .setEventWaiter(Main.eventWaiter)
                    .setTimeout(30, TimeUnit.DAYS)
                    .setSelectedEnds(">**", "**<")
                    .useSingleSelectionMode(true)
                    .useLooping(true)
                    .setSelectionConsumer((message, integer) -> {
                        //CONTINUE
                        MenuCommandEleveAskJoinClasseMatiere.execute(member, textChannel, userBean, matieres.get(integer));
                        message.delete().queue();
                    })
                    .setCanceled(message -> {
                        message.delete().queue();
                        MenuCommandStart.execute(member, textChannel);
                    });
            int i = 0;
            try {
                List<Matiere> matieresTemp = MenuCommandEleveAskJoinClasseUtils.getJoinableMatieres(userBean);
                if (matieresTemp.isEmpty()) {
                    textChannel.sendMessage(Main.commandClient.getError() + " Vous ne pouvez pas rejoindre d'autres classes")
                            .delay(10, TimeUnit.SECONDS)
                            .flatMap(message -> {
                                message.delete().queue();
                                MenuCommandStart.execute(member, textChannel);
                                return null;
                            }).queue();
                    return;
                } else {
                    for (Matiere matiere : matieresTemp) {
                        if (matiere.equals(Matiere.AUCUNE))
                            continue;
                        i++;
                        builder.addChoices(matiere.getName());
                        matieres.put(i, matiere);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.build().display(textChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
