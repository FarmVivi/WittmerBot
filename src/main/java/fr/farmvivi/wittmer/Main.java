package fr.farmvivi.wittmer;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.farmvivi.wittmer.command.*;
import fr.farmvivi.wittmer.listener.JoinListener;
import fr.farmvivi.wittmer.listener.RenameListener;
import fr.farmvivi.wittmer.menu.command.MenuCommandStart;
import fr.farmvivi.wittmer.menu.verif.MenuVerifFinal;
import fr.farmvivi.wittmer.menu.verif.MenuVerifStart;
import fr.farmvivi.wittmer.persistanceapi.DataServiceManager;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    public static final String version = "1.0.3.1";
    public static final String name = "Wittmer";
    public static final boolean production = false;
    public static final String VALIDER_EMOTE = "\u2705";
    public static final long OWNER_ID = 751882667812847706L;
    public static final long GUILD_ID = 753631957606203474L;
    public static final long ADMIN_ROLE_ID = 783309691748089867L;
    public static final long VERIF_CATEGORY_ID = 782655620586668102L;
    public static final long ACTIONS_CATEGORY_ID = 782945519806316584L;
    public static final Logger logger = LoggerFactory.getLogger(name);
    private static final String TOKEN = "NzU0MDI5NjAxMDM0MDc2MTYw.X1uyyg.BhL4K5V9vvEUenmfTR31xrCt6IA";
    public static long DEMANDES_CHANNEL_ID;
    public static long ACTIONS_ELEVES_CHANNEL_ID;
    public static long ACTIONS_PROFS_CHANNEL_ID;
    public static JDA jda;
    public static DataServiceManager dataServiceManager;
    public static CommandClient commandClient;
    public static EventWaiter eventWaiter;

    public static void main(String[] args) {
        logger.info("Démarrage de " + name + " (V" + version + ") (Prod: " + production + ") en cours...");

        logger.info("System.getProperty('os.name') == '" + System.getProperty("os.name") + "'");
        logger.info("System.getProperty('os.version') == '" + System.getProperty("os.version") + "'");
        logger.info("System.getProperty('os.arch') == '" + System.getProperty("os.arch") + "'");
        logger.info("System.getProperty('java.version') == '" + System.getProperty("java.version") + "'");
        logger.info("System.getProperty('java.vendor') == '" + System.getProperty("java.vendor") + "'");
        logger.info("System.getProperty('sun.arch.data.model') == '" + System.getProperty("sun.arch.data.model") + "'");

        enable();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void enable() {
        logger.info("Connexion à Discord...");
        try {
            JDABuilder jdaBuilder = JDABuilder.create(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
            jdaBuilder.enableCache(new ArrayList<>(Arrays.asList(CacheFlag.values())));
            jdaBuilder.setToken(TOKEN);
            jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
            jdaBuilder.setActivity(Activity.playing("Chargement..."));
            jdaBuilder.setLargeThreshold(250);
            jda = jdaBuilder.build();
            jda.awaitReady();
            logger.info("Connecté !");
        } catch (LoginException | InterruptedException e) {
            logger.error("Erreur de connexion à Discord", e);
            disable();
            return;
        }

        logger.info("Connexion à la base de données...");
        try {
            if (production)
                dataServiceManager = new DataServiceManager("172.17.0.1", "u3_wqC0SILRvd", "!aGDZc.YRy!YWFdIjI67lqHj", "s3_wittmer", 3306);
            else
                dataServiceManager = new DataServiceManager("pterodactyl.home", "u3_wqC0SILRvd", "!aGDZc.YRy!YWFdIjI67lqHj", "s3_wittmer", 3306);
            logger.info("Connecté !");
        } catch (Exception e) {
            logger.error("Erreur de connexion à la base de données", e);
            disable();
            return;
        }

        eventWaiter = new EventWaiter();

        logger.info("Initialisation des commandes...");
        CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
        commandClientBuilder.setOwnerId(OWNER_ID + "");
        commandClientBuilder.setCoOwnerIds("177135083222859776");
        commandClientBuilder.setPrefix("!");
        commandClientBuilder.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        commandClientBuilder.addCommand(new ShutdownCommand());
        commandClientBuilder.addCommand(new AdminOnCommand());
        commandClientBuilder.addCommand(new AdminOffCommand());
        commandClientBuilder.addCommand(new PingCommand());
        commandClientBuilder.addCommand(new CreateClasseCommand());
        commandClient = commandClientBuilder.build();
        jda.addEventListener(commandClient, eventWaiter, new JoinListener(), new RenameListener());
        logger.info("OK");

        logger.info("Processus de vérification des membres...");
        Guild guild = jda.getGuildById(GUILD_ID);
        Category verifCategory = Objects.requireNonNull(guild).getCategoryById(VERIF_CATEGORY_ID);
        for (GuildChannel deprecatedVerifChannel : Objects.requireNonNull(verifCategory).getChannels()) {
            logger.info("Delete " + deprecatedVerifChannel.getName() + " verif channel...");
            deprecatedVerifChannel.delete().queue();
        }
        ChannelAction<TextChannel> demandesChannelAction = verifCategory.createTextChannel("\uD83C\uDD95 Demandes");
        List<Permission> demandesChannelAllowPermissions = new ArrayList<>();
        demandesChannelAllowPermissions.add(Permission.MESSAGE_READ);
        demandesChannelAllowPermissions.add(Permission.VOICE_CONNECT);
        demandesChannelAction.addRolePermissionOverride(Role.DELEGUE.getRoleId(), demandesChannelAllowPermissions, new ArrayList<>());
        demandesChannelAction.addRolePermissionOverride(Role.PROF.getRoleId(), demandesChannelAllowPermissions, new ArrayList<>());
        logger.info("Create \uD83C\uDD95 Demandes verif channel...");
        DEMANDES_CHANNEL_ID = demandesChannelAction.complete().getIdLong();
        for (Member member : guild.getMembers()) {
            if (!production && member.getIdLong() != OWNER_ID)
                continue;
            if (member.getRoles().isEmpty()) {
                logger.info("Create " + member.getUser().getName() + " verif channel...");
                ChannelAction<TextChannel> channelAction = verifCategory.createTextChannel(member.getUser().getName());
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
                    if (dataServiceManager.isUserCreated(member.getIdLong())) {
                        UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", 0L, false, 0, "", false));
                        Role role = Role.getById(userBean.getRole());
                        if (Objects.requireNonNull(role).equals(Role.PROF)) {
                            MenuVerifFinal.execute(member, textChannel, role, null, null, userBean.isDelegue(), userBean.getPrenom(), userBean.getNom());
                        } else {
                            ClasseBean classeBean = dataServiceManager.getUserDefaultClasse(userBean);
                            MenuVerifFinal.execute(member, textChannel, role, classeBean.getLevel(), classeBean, userBean.isDelegue(), userBean.getPrenom(), userBean.getNom());
                        }
                    } else {
                        MenuVerifStart.execute(member, textChannel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                logger.info("Renaming " + member.getEffectiveName() + "...");
                rename(member, member.getEffectiveName().split(" \\(")[0]);
            }
        }
        Category actionsCategory = Objects.requireNonNull(guild).getCategoryById(ACTIONS_CATEGORY_ID);
        for (GuildChannel deprecatedActionChannel : Objects.requireNonNull(actionsCategory).getChannels()) {
            logger.info("Delete " + deprecatedActionChannel.getName() + " command channel...");
            deprecatedActionChannel.delete().queue();
        }

        ChannelAction<TextChannel> createActionsEleveChannelAction = actionsCategory.createTextChannel("Actions élèves");
        List<Permission> createActionsEleveChannelAllowPermissions = new ArrayList<>();
        createActionsEleveChannelAllowPermissions.add(Permission.MESSAGE_READ);
        createActionsEleveChannelAllowPermissions.add(Permission.VOICE_CONNECT);
        createActionsEleveChannelAllowPermissions.add(Permission.MESSAGE_ADD_REACTION);
        createActionsEleveChannelAllowPermissions.add(Permission.MESSAGE_EXT_EMOJI);
        createActionsEleveChannelAllowPermissions.add(Permission.MESSAGE_HISTORY);
        createActionsEleveChannelAction.addRolePermissionOverride(Role.ELEVE.getRoleId(), createActionsEleveChannelAllowPermissions, new ArrayList<>());
        logger.info("Create Actions élèves channel...");
        TextChannel actionsEleveChannel = createActionsEleveChannelAction.complete();
        ACTIONS_ELEVES_CHANNEL_ID = actionsEleveChannel.getIdLong();
        MenuCommandStart.execute(actionsEleveChannel, Role.ELEVE);

        ChannelAction<TextChannel> createActionsProfChannelAction = actionsCategory.createTextChannel("Actions profs");
        List<Permission> createActionsProfChannelAllowPermissions = new ArrayList<>();
        createActionsProfChannelAllowPermissions.add(Permission.MESSAGE_READ);
        createActionsProfChannelAllowPermissions.add(Permission.VOICE_CONNECT);
        createActionsProfChannelAllowPermissions.add(Permission.MESSAGE_ADD_REACTION);
        createActionsProfChannelAllowPermissions.add(Permission.MESSAGE_EXT_EMOJI);
        createActionsProfChannelAllowPermissions.add(Permission.MESSAGE_HISTORY);
        createActionsProfChannelAction.addRolePermissionOverride(Role.PROF.getRoleId(), createActionsProfChannelAllowPermissions, new ArrayList<>());
        logger.info("Create Actions profs channel...");
        TextChannel actionsProfChannel = createActionsProfChannelAction.complete();
        ACTIONS_PROFS_CHANNEL_ID = actionsProfChannel.getIdLong();
        MenuCommandStart.execute(actionsProfChannel, Role.PROF);

        logger.info("OK");

        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        if (production)
            jda.getPresence().setActivity(Activity.playing("V" + version + " - PROD"));
        else
            jda.getPresence().setActivity(Activity.playing("V" + version + " - DEV"));
        logger.info(name + " (V" + version + ") (Prod: " + production + ") ready and started !");
    }

    public static void disable() {
        logger.info("Extinction du bot en cours...");
        if (dataServiceManager != null) {
            logger.info("Déconnexion de la base de données en cours...");
            dataServiceManager.disconnect();
            logger.info("OK");
        }
        if (jda != null) {
            logger.info("Extinction et déconnexion de Discord en cours...");
            jda.shutdown();
            logger.info("OK");
        }
        logger.info("Bye...");
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public static void joinClasse(Member member, ClasseBean classeBean) {
        try {
            UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", 0L, false, 0, "", false));
            Objects.requireNonNull(jda.getGuildById(GUILD_ID)).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(Objects.requireNonNull(classeBean).getDiscord_role_id()))).queue();
            StringBuilder pseudo = new StringBuilder();
            pseudo.append(userBean.getPrenom().toUpperCase(), 0, 1)
                    .append(userBean.getPrenom(), 1, userBean.getPrenom().length());
            pseudo.append(" ");
            pseudo.append(userBean.getNom().toUpperCase());
            TextChannel defaultChannel = jda.getTextChannelById(classeBean.getDiscord_default_channel_id());
            if (userBean.getClasses().length() == 0)
                userBean.setClasses(classeBean.getId() + "");
            else
                userBean.setClasses(userBean.getClasses() + ";" + classeBean.getId());
            dataServiceManager.updateUser(userBean);
            Objects.requireNonNull(defaultChannel).sendMessage(new EmbedBuilder().setDescription(pseudo.toString() + " a rejoint la classe !").setColor(Color.GREEN).build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void rename(Member member, String newPseudo) {
        try {
            if (dataServiceManager.isUserCreated(member.getIdLong())) {
                UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", 0L, false, 0, "", false));
                StringBuilder pseudo = new StringBuilder();
                pseudo.append(userBean.getPrenom().toUpperCase(), 0, 1)
                        .append(userBean.getPrenom(), 1, userBean.getPrenom().length())
                        .append(" ");
                Role role = Role.getById(userBean.getRole());
                if (Objects.requireNonNull(role).equals(Role.PROF)) {
                    pseudo.append(userBean.getNom().toUpperCase());
                } else {
                    pseudo.append(userBean.getNom().toUpperCase(), 0, 1)
                            .append(".");
                }
                if (newPseudo == null) {
                    member.modifyNickname(pseudo.toString());
                    return;
                }
                String fullPseudo = newPseudo + " (" + pseudo.toString() + ")";
                if (fullPseudo.length() <= 32) {
                    member.modifyNickname(fullPseudo).queue();
                } else {
                    member.modifyNickname(pseudo.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void allowActions(Member member, Role role) {
        List<Permission> perm = new ArrayList<>();
        perm.add(Permission.VIEW_CHANNEL);
        perm.add(Permission.MESSAGE_ADD_REACTION);
        perm.add(Permission.MESSAGE_READ);
        TextChannel channel;
        if (role.equals(Role.PROF))
            channel = jda.getTextChannelById(ACTIONS_PROFS_CHANNEL_ID);
        else
            channel = jda.getTextChannelById(ACTIONS_ELEVES_CHANNEL_ID);
        Objects.requireNonNull(channel).getManager().putPermissionOverride(member, perm, new ArrayList<>()).complete();
    }

    public static void disallow(Member member, Role role) {
        List<Permission> perm = new ArrayList<>();
        perm.add(Permission.VIEW_CHANNEL);
        perm.add(Permission.MESSAGE_ADD_REACTION);
        perm.add(Permission.MESSAGE_READ);
        TextChannel channel;
        if (role.equals(Role.PROF))
            channel = jda.getTextChannelById(ACTIONS_PROFS_CHANNEL_ID);
        else
            channel = jda.getTextChannelById(ACTIONS_ELEVES_CHANNEL_ID);
        Objects.requireNonNull(channel).getManager().putPermissionOverride(member, new ArrayList<>(), perm).complete();
    }
}
