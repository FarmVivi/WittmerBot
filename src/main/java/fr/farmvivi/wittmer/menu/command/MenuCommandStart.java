package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MenuCommandStart {
    public static void execute(Member member, TextChannel textChannel) {
        try {
            UserBean userBean = Main.dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
            if (Objects.requireNonNull(Role.getById(userBean.getRole())).equals(Role.PROF)) {
                new OrderedMenu.Builder()
                        .setText("Cliquez sur l'emoji correspondant à ce que vous voulez faire")
                        .setEventWaiter(Main.eventWaiter)
                        .setTimeout(30, TimeUnit.DAYS)
                        .useNumbers()
                        .addChoices("Créer un salon vocal de cours")
                        .setSelection((message, integer) -> {
                            if (integer == 1) {
                                //CREATE VOCAL
                                MenuCommandProfCreateChannelAskLevel.execute(member, textChannel);
                            }
                        })
                        .build().display(textChannel);
            } else {
                new OrderedMenu.Builder()
                        .setText("Cliquez sur l'emoji correspondant à ce que vous voulez faire")
                        .setEventWaiter(Main.eventWaiter)
                        .setTimeout(30, TimeUnit.DAYS)
                        .useNumbers()
                        .addChoices("Rejoindre une classe")
                        .setSelection((message, integer) -> {
                            if (integer == 1) {
                                //JOIN CLASS
                                MenuCommandEleveJoinClasseAskMatiere.execute(member, textChannel);
                            }
                        })
                        .build().display(textChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
