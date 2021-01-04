package fr.farmvivi.wittmer.listener;

import fr.farmvivi.wittmer.Main;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RenameListener extends ListenerAdapter {
    public void onGuildMemberChangeNickname(GuildMemberUpdateNicknameEvent event) {
        Main.rename(event.getMember(), event.getNewNickname());
    }
}
