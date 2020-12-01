package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class MenuCommandProfCreateClasseAskLevel {
    public static void execute(Member member, TextChannel textChannel) {
        new OrderedMenu.Builder()
                .setText("Quel est le niveau concerné par la création de classe?")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .addChoices("Seconde", "Première", "Terminale")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //Seconde
                        MenuCommandProfCreateClasseAskMatiere.execute(member, textChannel, Level.SECONDE);
                    } else if (integer == 2) {
                        //Première
                        MenuCommandProfCreateClasseAskMatiere.execute(member, textChannel, Level.PREMIERE);
                    } else if (integer == 3) {
                        //Terminale
                        MenuCommandProfCreateClasseAskMatiere.execute(member, textChannel, Level.TERMINALE);
                    }
                })
                .build().display(textChannel);
    }
}
