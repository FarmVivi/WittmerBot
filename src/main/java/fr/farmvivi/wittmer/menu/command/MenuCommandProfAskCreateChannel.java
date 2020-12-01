package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class MenuCommandProfAskCreateChannel {
    public static void execute(Member member, TextChannel textChannel) {
        new OrderedMenu.Builder()
                .setText("Quel est le niveau concerné?")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .addChoices("Seconde", "Première", "Terminale")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //Seconde
                        MenuCommandProfAskCreateChannelLevel.execute(member, textChannel, Level.SECONDE);
                    } else if (integer == 2) {
                        //Première
                        MenuCommandProfAskCreateChannelLevel.execute(member, textChannel, Level.PREMIERE);
                    } else if (integer == 3) {
                        //Terminale
                        MenuCommandProfAskCreateChannelLevel.execute(member, textChannel, Level.TERMINALE);
                    }
                })
                .build().display(textChannel);
    }
}
