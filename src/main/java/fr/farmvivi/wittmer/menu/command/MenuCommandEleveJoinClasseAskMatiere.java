package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.SelectionDialog;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MenuCommandEleveJoinClasseAskMatiere {
    public static void execute(Member member, TextChannel textChannel) {
        try {
            UserBean userBean = Main.dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", 0L, false, 0, "", false));
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
                        MenuCommandEleveJoinClasseAskClasse.execute(member, textChannel, userBean, matieres.get(integer));
                        message.delete().queue();
                    })
                    .setCanceled(message -> {
                        message.delete().queue();
                        MenuCommandFailure.execute(member, textChannel, Role.ELEVE);
                    });
            int i = 0;
            try {
                List<Matiere> matieresTemp = MenuCommandEleveJoinClasseUtils.getJoinableMatieres(userBean);
                if (matieresTemp.isEmpty()) {
                    textChannel.sendMessage(Main.commandClient.getError() + " Vous ne pouvez pas rejoindre d'autres classes")
                            .delay(30, TimeUnit.SECONDS)
                            .flatMap(Message::delete)
                            .queue();
                    MenuCommandFailure.execute(member, textChannel, Role.ELEVE);
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
