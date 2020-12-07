package fr.farmvivi.wittmer.menu.verif;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class MenuVerifAskDelegue {
    public static void execute(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe) {
        new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .allowTextInput(false)
                .useNumbers()
                .addChoices("Délégué•e", "Pas délégué•e")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //Delegue
                        MenuVerifAskPrenom.execute(member, textChannel, role, level, classe, true);
                    } else if (integer == 2) {
                        //Pas delegue
                        MenuVerifAskPrenom.execute(member, textChannel, role, level, classe, false);
                    }
                })
                .build().display(textChannel);
    }
}
