package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class MenuCommandProfCreateClasseAskClasseNumber {
    public static void execute(Member member, TextChannel textChannel, Level level, Matiere matiere) {
        new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant au numéro de la classe crée\n(exemple: 3 pour le 3ème groupe de spécialité Mathématiques,\nNormalement le numéro de groupe est marqué dans l'emploi du temps sur Éclat)")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .allowTextInput(false)
                .useNumbers()
                .addChoices("1", "2", "3", "4", "5", "6", "7", "8", "9")
                .setSelection((message, integer) -> {
                    //CONTINUE
                    MenuCommandProfCreateClasseFinal.execute(member, textChannel, level, matiere, integer, null);
                })
                .build().display(textChannel);
    }
}
