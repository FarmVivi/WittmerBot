package fr.farmvivi.wittmer.menu.command;

import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.custom.CustomOrderedMenu;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MenuCommandStart {
    public static void execute(TextChannel textChannel, Role role) {
        if (role.equals(Role.PROF)) {
            new CustomOrderedMenu.Builder()
                    .setText("Cliquez sur l'emoji correspondant à ce que vous voulez faire")
                    .setEventWaiter(Main.eventWaiter)
                    .setTimeout(3000, TimeUnit.DAYS)
                    .addChoices("Créer un salon vocal de cours", "Créer une classe")
                    .setSelection((message, memberIntegerPair) -> {
                        memberIntegerPair.getLeft().getReaction().removeReaction(Objects.requireNonNull(memberIntegerPair.getLeft().getUser())).queue();
                        Main.disallow(memberIntegerPair.getLeft().getMember(), Role.ELEVE);
                        Category commandsCategory = textChannel.getGuild().getCategoryById(Main.ACTIONS_CATEGORY_ID);
                        ChannelAction<TextChannel> channelAction = Objects.requireNonNull(commandsCategory).createTextChannel(Objects.requireNonNull(memberIntegerPair.getLeft().getMember()).getUser().getName());
                        List<Permission> channelAllow = new ArrayList<>();
                        channelAllow.add(Permission.MESSAGE_READ);
                        channelAllow.add(Permission.VOICE_CONNECT);
                        channelAllow.add(Permission.MESSAGE_ADD_REACTION);
                        channelAllow.add(Permission.MESSAGE_WRITE);
                        channelAllow.add(Permission.MESSAGE_EXT_EMOJI);
                        channelAllow.add(Permission.MESSAGE_HISTORY);
                        channelAction.addMemberPermissionOverride(memberIntegerPair.getLeft().getMember().getIdLong(), channelAllow, new ArrayList<>());
                        TextChannel textChannel2 = channelAction.complete();
                        textChannel2.sendMessage("<@" + memberIntegerPair.getLeft().getMember().getIdLong() + ">").complete();
                        if (memberIntegerPair.getRight() == 1) {
                            //CREATE VOCAL
                            MenuCommandProfCreateChannelAskLevel.execute(memberIntegerPair.getLeft().getMember(), textChannel2);
                        } else if (memberIntegerPair.getRight() == 2) {
                            //CREATE CLASSE
                            MenuCommandProfCreateClasseAskLevel.execute(memberIntegerPair.getLeft().getMember(), textChannel2);
                        }
                    })
                    .build().display(textChannel);
        } else {
            new CustomOrderedMenu.Builder()
                    .setText("Cliquez sur l'emoji correspondant à ce que vous voulez faire")
                    .setEventWaiter(Main.eventWaiter)
                    .setTimeout(3000, TimeUnit.DAYS)
                    .addChoices("Rejoindre une classe")
                    .setSelection((message, memberIntegerPair) -> {
                        memberIntegerPair.getLeft().getReaction().removeReaction(Objects.requireNonNull(memberIntegerPair.getLeft().getUser())).queue();
                        Main.disallow(memberIntegerPair.getLeft().getMember(), Role.ELEVE);
                        Category commandsCategory = textChannel.getGuild().getCategoryById(Main.ACTIONS_CATEGORY_ID);
                        ChannelAction<TextChannel> channelAction = Objects.requireNonNull(commandsCategory).createTextChannel(Objects.requireNonNull(memberIntegerPair.getLeft().getMember()).getUser().getName());
                        List<Permission> channelAllow = new ArrayList<>();
                        channelAllow.add(Permission.MESSAGE_READ);
                        channelAllow.add(Permission.VOICE_CONNECT);
                        channelAllow.add(Permission.MESSAGE_ADD_REACTION);
                        channelAllow.add(Permission.MESSAGE_WRITE);
                        channelAllow.add(Permission.MESSAGE_EXT_EMOJI);
                        channelAllow.add(Permission.MESSAGE_HISTORY);
                        channelAction.addMemberPermissionOverride(memberIntegerPair.getLeft().getMember().getIdLong(), channelAllow, new ArrayList<>());
                        TextChannel textChannel2 = channelAction.complete();
                        textChannel2.sendMessage("<@" + memberIntegerPair.getLeft().getMember().getIdLong() + ">").complete();
                        if (memberIntegerPair.getRight() == 1) {
                            //JOIN CLASSE
                            MenuCommandEleveJoinClasseAskMatiere.execute(memberIntegerPair.getLeft().getMember(), textChannel2);
                        }
                    })
                    .build().display(textChannel);
        }
    }
}
