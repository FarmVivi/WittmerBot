package fr.farmvivi.wittmer.listener;

import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.menu.verif.MenuVerifFinal;
import fr.farmvivi.wittmer.menu.verif.MenuVerifStart;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JoinListener extends ListenerAdapter {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        JDA jda = event.getJDA();
        Main.logger.info("Create " + member.getUser().getName() + " verif channel...");
        Guild guild = jda.getGuildById(Main.GUILD_ID);
        Category category = Objects.requireNonNull(guild).getCategoryById(Main.VERIF_CATEGORY_ID);
        ChannelAction<TextChannel> channelAction = Objects.requireNonNull(category).createTextChannel(member.getUser().getName());
        List<Permission> channelAllow = new ArrayList<>();
        channelAllow.add(Permission.MESSAGE_READ);
        channelAllow.add(Permission.VOICE_CONNECT);
        channelAllow.add(Permission.MESSAGE_ADD_REACTION);
        channelAllow.add(Permission.MESSAGE_WRITE);
        channelAllow.add(Permission.MESSAGE_EXT_EMOJI);
        channelAllow.add(Permission.MESSAGE_HISTORY);
        channelAction.addMemberPermissionOverride(member.getIdLong(), channelAllow, new ArrayList<>());
        try {
            TextChannel textChannel = channelAction.complete();
            if (Main.dataServiceManager.isUserCreated(member.getIdLong())) {
                UserBean userBean = Main.dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", 0L, false, 0, "", false));
                Role role = Role.getById(userBean.getRole());
                if (Objects.requireNonNull(role).equals(Role.PROF)) {
                    MenuVerifFinal.execute(member, textChannel, role, null, null, userBean.isDelegue(), userBean.getPrenom(), userBean.getNom());
                } else {
                    ClasseBean classeBean = Main.dataServiceManager.getUserDefaultClasse(userBean);
                    MenuVerifFinal.execute(member, textChannel, role, classeBean.getLevel(), classeBean, userBean.isDelegue(), userBean.getPrenom(), userBean.getNom());
                }
            } else {
                MenuVerifStart.execute(member, textChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
