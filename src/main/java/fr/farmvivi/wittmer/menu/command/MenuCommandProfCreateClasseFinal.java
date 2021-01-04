package fr.farmvivi.wittmer.menu.command;

import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuCommandProfCreateClasseFinal {
    @SuppressWarnings({"ResultOfMethodCallIgnored", "MismatchedQueryAndUpdateOfStringBuilder", "StringBufferReplaceableByString"})
    public static void execute(Member member, TextChannel textChannel, Level level, Matiere matiere, int classe_number, ClasseBean originClasse) {
        StringBuilder name = new StringBuilder("⌈" + Objects.requireNonNull(matiere).getEmoji() + "⌋ " + Objects.requireNonNull(level).getPrefix());
        if (Objects.requireNonNull(matiere).getName().isEmpty()) {
            name.append(classe_number);
        } else if (matiere.isEntireClasse()) {
            name.append(classe_number).append(" - ").append(matiere.getName());
        } else {
            name.append(" - ").append(matiere.getName()).append(" - ").append(classe_number);
        }
        Guild guild = Main.jda.getGuildById(Main.GUILD_ID);
        RoleAction roleAction = Objects.requireNonNull(guild).createRole();
        roleAction.setName(name.toString());
        roleAction.setMentionable(false);
        roleAction.setHoisted(false);
        roleAction.setPermissions(Permission.EMPTY_PERMISSIONS);
        Role role = roleAction.complete();
        ChannelAction<net.dv8tion.jda.api.entities.Category> categoryAction = Objects.requireNonNull(guild).createCategory(name.toString());
        List<Permission> categoryAllow = new ArrayList<>();
        categoryAllow.add(Permission.MESSAGE_READ);
        categoryAllow.add(Permission.VOICE_CONNECT);
        categoryAction.addRolePermissionOverride(role.getIdLong(), categoryAllow, new ArrayList<>());
        List<Permission> categoryDeny = new ArrayList<>();
        categoryDeny.add(Permission.MESSAGE_READ);
        categoryDeny.add(Permission.VOICE_CONNECT);
        categoryAction.addRolePermissionOverride(guild.getPublicRole().getIdLong(), new ArrayList<>(), categoryDeny);
        net.dv8tion.jda.api.entities.Category category = categoryAction.complete();
        TextChannel discussionTextChannel = category.createTextChannel("discussion").complete();
        try {
            Main.dataServiceManager.createClasse(new ClasseBean(0, level, matiere, name.toString(), category.getIdLong(), role.getIdLong(), discussionTextChannel.getIdLong(), member.getIdLong()));
            ClasseBean classeBean = Main.dataServiceManager.getClasseOfACategory(category.getIdLong());
            Main.joinClasse(Objects.requireNonNull(Main.jda.getGuildById(Main.GUILD_ID)).getMemberById(classeBean.getDiscord_prof_id()), classeBean);
            UserBean prof = Main.dataServiceManager.getUser(classeBean.getDiscord_prof_id(), new UserBean(member.getIdLong(), "", "", 0L, false, 0, "", false));
            StringBuilder text = new StringBuilder("<@&" + fr.farmvivi.wittmer.Role.ELEVE.getRoleId() + ">, ");
            text.append(prof.getPrenom().toUpperCase(), 0, 1)
                    .append(prof.getPrenom(), 1, prof.getPrenom().length());
            text.append(" ");
            text.append(prof.getNom().toUpperCase());
            text.append(" a crée une nouvelle classe en ").append(matiere.getName());
            MessageEmbed messageEmbed = new EmbedBuilder().setDescription(text.toString()).setColor(Color.GREEN).build();
            if (matiere.isEntireClasse()) {
                Objects.requireNonNull(guild.getTextChannelById(originClasse.getDiscord_default_channel_id())).sendMessage(messageEmbed).queue();
            } else {
                List<ClasseBean> levelClasses = Main.dataServiceManager.getClassesListOfALevelAndMatiere(level, Matiere.AUCUNE);
                for (ClasseBean levelClasse : levelClasses) {
                    Objects.requireNonNull(guild.getTextChannelById(levelClasse.getDiscord_default_channel_id())).sendMessage(messageEmbed).queue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MenuCommandSucess.execute(member, textChannel, fr.farmvivi.wittmer.Role.PROF);
    }
}
