package fr.farmvivi.wittmer.menu.command;

import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class MenuCommandSucess {
    public static void execute(Member member, TextChannel textChannel, Role role) {
        textChannel.sendMessage(new EmbedBuilder().setDescription("Succès !").setColor(Color.GREEN).build())
                .delay(30, TimeUnit.SECONDS)
                .flatMap(message -> textChannel.delete())
                .queue();
        Main.allowActions(member, role);
    }
}
