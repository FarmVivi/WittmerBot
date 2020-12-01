package fr.farmvivi.wittmer.menu.verif;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class MenuVerifAskRole {
    public static void execute(Member member, TextChannel textChannel) {
        new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .addChoices("Élève", "Professeur")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //ELEVE
                        MenuVerifAskLevel.execute(member, textChannel, Role.ELEVE);
                    } else if (integer == 2) {
                        //PROF
                        MenuVerifAskPrenom.execute(member, textChannel, Role.PROF, null, null, false);
                    }
                })
                .build().display(textChannel);
    }
}
