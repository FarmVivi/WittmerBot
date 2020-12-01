package fr.farmvivi.wittmer.menu.verif;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class MenuVerifAskLevel {
    public static void execute(Member member, TextChannel textChannel, Role role) {
        new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .addChoices("Seconde", "Première", "Terminale")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //Seconde
                        MenuVerifAskClasse.execute(member, textChannel, role, Level.SECONDE);
                    } else if (integer == 2) {
                        //Première
                        MenuVerifAskClasse.execute(member, textChannel, role, Level.PREMIERE);
                    } else if (integer == 3) {
                        //Terminale
                        MenuVerifAskClasse.execute(member, textChannel, role, Level.TERMINALE);
                    }
                })
                .build().display(textChannel);
    }
}
