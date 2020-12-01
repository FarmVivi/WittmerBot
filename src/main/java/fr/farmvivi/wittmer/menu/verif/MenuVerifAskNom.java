package fr.farmvivi.wittmer.menu.verif;

import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class MenuVerifAskNom {
    public static void execute(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe, boolean delegue, String prenom) {
        Message message = textChannel.sendMessage("Quel est votre nom de famille?").complete();
        Main.eventWaiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().getIdLong() == member.getUser().getIdLong()
                        && e.getChannel().getIdLong() == textChannel.getIdLong(),
                e -> {
                    //CONTINUE
                    message.delete().queue();
                    MenuVerifFinal.execute(member, textChannel, role, level, classe, delegue, prenom, e.getMessage().getContentRaw().toLowerCase());
                    try {
                        if (role.equals(Role.PROF))
                            Main.dataServiceManager.createUser(new UserBean(member.getIdLong(), prenom, e.getMessage().getContentRaw().toLowerCase(), role.getId(), delegue, 0, "", false));
                        else
                            Main.dataServiceManager.createUser(new UserBean(member.getIdLong(), prenom, e.getMessage().getContentRaw().toLowerCase(), role.getId(), delegue, classe.getId(), "", false));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    e.getMessage().delete().queue();
                },
                // if the user takes more than a minute, time out
                30, TimeUnit.DAYS, () -> message.delete().queue());
    }
}
