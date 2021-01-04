package fr.farmvivi.wittmer.menu.verif;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MenuVerifFinal {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void execute(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe, boolean delegue, String prenom, String nom) {
        Message messageVerif = textChannel.sendMessage(new EmbedBuilder().setDescription("Demande en attende de vérification par un délégué•e ou un professeur...").setColor(Color.GREEN).build()).complete();
        StringBuilder text = new StringBuilder();
        if (role.equals(Role.PROF))
            text.append("<@&").append(Role.DELEGUE.getRoleId()).append(">, ").append("<@").append(member.getIdLong()).append("> souhaite rejoindre le discord en tant que professeur, cette personne est-elle bien professeur?");
        else
            text.append("<@&").append(classe.getDiscord_role_id()).append(">, ").append("<@").append(member.getIdLong()).append("> souhaite rejoindre le discord en tant qu'élève, cette personne est-elle bien en ").append(classe.getName()).append("?");
        if (prenom.length() != 0)
            text.append("\nPrénom: ").append(prenom);
        if (nom.length() != 0)
            text.append("\nNom: ").append(nom);
        if (level != null)
            text.append("\nNiveau: ").append(level.getName());
        if (classe != null)
            text.append("\nClasse principale: ").append(classe.getName());
        if (!role.equals(Role.PROF))
            if (delegue)
                text.append("\nDélégué•e: Oui");
            else
                text.append("\nDélégué•e: Non");
        new OrderedMenu.Builder()
                .setText(text.toString())
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .allowTextInput(false)
                .useNumbers()
                .addChoices("Oui", "Non")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //ACCEPT
                        try {
                            UserBean userBean = Main.dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), prenom, nom, role.getId(), delegue, 0, "", false));
                            userBean.setVerified(true);
                            if (userBean.isDelegue())
                                Objects.requireNonNull(Main.jda.getGuildById(Main.GUILD_ID)).addRoleToMember(member, Objects.requireNonNull(Main.jda.getRoleById(Role.DELEGUE.getRoleId()))).queue();
                            Objects.requireNonNull(Main.jda.getGuildById(Main.GUILD_ID)).addRoleToMember(member, Objects.requireNonNull(Main.jda.getRoleById(Objects.requireNonNull(Role.getById(userBean.getRole())).getRoleId()))).queue();
                            if (classe != null)
                                Main.joinClasse(member, classe);
                            messageVerif.getTextChannel().delete().queue();
                            Main.dataServiceManager.updateUser(userBean);
                            Main.rename(member, member.getEffectiveName());
                            Main.logger.info("Accepted " + member.getEffectiveName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (integer == 2) {
                        //REFUSE
                        try {
                            Main.dataServiceManager.deleteUser(member.getIdLong());
                            messageVerif.delete().queue();
                            MenuVerifStart.execute(member, textChannel);
                            Main.logger.info("Refused " + member.getEffectiveName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .build().display(Main.jda.getTextChannelById(Main.DEMANDES_CHANNEL_ID));
    }
}
