package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Role;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MenuCommandProfCreateChannelAskClasse {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void execute(Member member, TextChannel textChannel, Level level) {
        Map<Integer, ClasseBean> classes = new HashMap<>();
        OrderedMenu.Builder builder = new OrderedMenu.Builder()
                .setText("Quelle est la classe concernée?")
                .setEventWaiter(Main.eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .allowTextInput(false)
                .useNumbers()
                .setSelection((message, integer) -> {
                    //CONTINUE
                    Guild guild = Main.jda.getGuildById(Main.GUILD_ID);
                    Category category = Objects.requireNonNull(guild).getCategoryById(classes.get(integer).getDiscord_category_id());
                    ChannelAction<VoiceChannel> channelAction = Objects.requireNonNull(category).createVoiceChannel("Cours");
                    List<Permission> channelAllow = new ArrayList<>();
                    channelAllow.add(Permission.MESSAGE_READ);
                    channelAllow.add(Permission.VOICE_CONNECT);
                    channelAction.addMemberPermissionOverride(member.getIdLong(), channelAllow, new ArrayList<>());
                    channelAction.setUserlimit(99);
                    channelAction.queue();
                    textChannel.sendMessage(new EmbedBuilder().setDescription("Salon crée !").setColor(Color.GREEN).build())
                            .delay(5, TimeUnit.SECONDS)
                            .flatMap(Message::delete)
                            .queue();
                    MenuCommandSucess.execute(member, textChannel, Role.PROF);
                });
        int i = 0;
        try {
            List<ClasseBean> classeBeans = Main.dataServiceManager.getClasseOfAProf(member.getIdLong(), level);
            if (classeBeans.isEmpty()) {
                textChannel.sendMessage(Main.commandClient.getError() + " ERREUR: Aucune classe d'enregistré en " + level.getName())
                        .delay(30, TimeUnit.SECONDS)
                        .flatMap(Message::delete)
                        .queue();
                MenuCommandFailure.execute(member, textChannel, Role.PROF);
                return;
            } else {
                for (ClasseBean classeBean : classeBeans) {
                    i++;
                    builder.addChoice(classeBean.getName());
                    classes.put(i, classeBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.build().display(textChannel);
    }
}
