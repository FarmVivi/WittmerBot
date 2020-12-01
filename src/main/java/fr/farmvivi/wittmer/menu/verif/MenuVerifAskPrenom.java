package fr.farmvivi.wittmer.menu.verif;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class MenuVerifAskPrenom {
    public static void execute(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe, boolean delegue) {
        if (role.equals(Role.PROF)) {
            new OrderedMenu.Builder()
                    .setText("Cliquez sur l'emoji correspondant à votre situation")
                    .setEventWaiter(Main.eventWaiter)
                    .setTimeout(30, TimeUnit.DAYS)
                    .useNumbers()
                    .addChoices("Monsieur", "Madame")
                    .setSelection((message, integer) -> {
                        if (integer == 1) {
                            //Mr
                            MenuVerifAskNom.execute(member, textChannel, role, level, classe, delegue, "Mr");
                        } else if (integer == 2) {
                            //Mme
                            MenuVerifAskNom.execute(member, textChannel, role, level, classe, delegue, "Mme");
                        }
                    })
                    .build().display(textChannel);
        } else {
            Message message = textChannel.sendMessage("Quel est votre prénom?").complete();
            Main.eventWaiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().getIdLong() == member.getUser().getIdLong()
                            && e.getChannel().getIdLong() == textChannel.getIdLong(),
                    e -> {
                        //CONTINUE
                        message.delete().queue();
                        MenuVerifAskNom.execute(member, textChannel, role, level, classe, delegue, e.getMessage().getContentRaw().toLowerCase());
                        e.getMessage().delete().queue();
                    },
                    // if the user takes more than a minute, time out
                    30, TimeUnit.DAYS, () -> message.delete().queue());
        }
    }
}
