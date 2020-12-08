package fr.farmvivi.wittmer.menu.verif;

import com.jagrosh.jdautilities.menu.ButtonMenu;
import fr.farmvivi.wittmer.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class MenuVerifStart {
    public static void execute(Member member, TextChannel textChannel) {
        new ButtonMenu.Builder()
                .setText("Bonjour <@" + member.getIdLong() + "> et bienvenue sur le discord du **Lycée Julien Wittmer** géré par des élèves,\n" +
                        "\n" +
                        "Pour vous orienter correctement sur ce serveur, vous devrez répondre aux questions que je vais vous poser.\n" +
                        "\n" +
                        "Maintenant cliquez sur l'emoji :white_check_mark: juste en dessous de ce message.")
                .setChoices(Main.VALIDER_EMOTE)
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .setAction(re -> {
                    if (re.getName().equals(Main.VALIDER_EMOTE)) {
                        //CONTINUE
                        MenuVerifAskRole.execute(member, textChannel);
                    }
                })
                .setFinalAction(message -> message.delete().queue())
                .build().display(textChannel);
    }
}
