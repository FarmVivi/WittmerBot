package fr.farmvivi.wittmer;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import fr.farmvivi.wittmer.command.*;
import fr.farmvivi.wittmer.listener.JoinListener;
import fr.farmvivi.wittmer.listener.RenameListener;
import fr.farmvivi.wittmer.persistanceapi.DataServiceManager;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final String version = "1.0.0.1";
    public static final String name = "Wittmer";
    public static final boolean production = false;
    //public static final String _EMOTE = "";
    public static final String ANNULER_EMOTE = "\u274C";
    public static final String VALIDER_EMOTE = "\u2705";
    public static final long OWNER_ID = 177135083222859776L;
    public static final long GUILD_ID = 753631957606203474L;
    public static final long VERIF_CATEGORY_ID = 782655620586668102L;
    public static final Logger logger = LoggerFactory.getLogger(name);
    private static final String TOKEN = "NzU0MDI5NjAxMDM0MDc2MTYw.X1uyyg.BhL4K5V9vvEUenmfTR31xrCt6IA";
    public static long DEMANDES_CHANNEL_ID;
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
                dataServiceManager = new DataServiceManager("172.18.0.1", "u273_MqKTNG9st6", "qRymrkh+x!6!OJNZtHLyNmQs", "s273_wittmer", 3307);
            else
                dataServiceManager = new DataServiceManager("daemon-1.avadia.fr", "u273_MqKTNG9st6", "qRymrkh+x!6!OJNZtHLyNmQs", "s273_wittmer", 3306);
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
        commandClientBuilder.setPrefix("!");
        commandClientBuilder.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        commandClientBuilder.addCommand(new ShutdownCommand());
        commandClientBuilder.addCommand(new CatCommand());
        commandClientBuilder.addCommand(new ChooseCommand());
        commandClientBuilder.addCommand(new PingCommand());
        commandClientBuilder.addCommand(new CreateClasseCommand());
        commandClient = commandClientBuilder.build();
        jda.addEventListener(commandClient, eventWaiter, new JoinListener(), new RenameListener());
        logger.info("OK");

        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        if (production)
            jda.getPresence().setActivity(Activity.playing("V" + version + " - PRODUCTION"));
        else
            jda.getPresence().setActivity(Activity.playing("V" + version + " - DEV"));
        logger.info(name + " (V" + version + ") (Prod: " + production + ") ready and started !");

        verif();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void verif() {
        Guild guild = jda.getGuildById(GUILD_ID);
        Category category = Objects.requireNonNull(guild).getCategoryById(VERIF_CATEGORY_ID);
        for (GuildChannel guildChannel : Objects.requireNonNull(category).getChannels()) {
            logger.info("Delete " + guildChannel.getName() + " verif channel...");
            guildChannel.delete().queue();
        }
        ChannelAction<TextChannel> channelAction1 = category.createTextChannel("\uD83C\uDD95 Demandes");
        List<Permission> channelAllow1 = new ArrayList<>();
        channelAllow1.add(Permission.MESSAGE_READ);
        channelAllow1.add(Permission.VOICE_CONNECT);
        channelAction1.addMemberPermissionOverride(OWNER_ID, channelAllow1, new ArrayList<>());
        logger.info("Create \uD83C\uDD95 Demandes verif channel...");
        DEMANDES_CHANNEL_ID = channelAction1.complete().getIdLong();
        for (Member member : guild.getMembers()) {
            if (!production && member.getIdLong() != OWNER_ID && member.getIdLong() != 751882667812847706L)
                continue;
            if (member.getRoles().isEmpty()) {
                logger.info("Create " + member.getUser().getName() + " verif channel...");
                ChannelAction<TextChannel> channelAction = category.createTextChannel(member.getUser().getName());
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
                        UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
                        Role role = Role.getById(userBean.getRole());
                        if (Objects.requireNonNull(role).equals(Role.PROF)) {
                            verifFinal(member, textChannel, role, null, null, userBean.isDelegue(), userBean.getPrenom(), userBean.getNom());
                        } else {
                            ClasseBean classeBean = dataServiceManager.getUserDefaultClasse(userBean);
                            verifFinal(member, textChannel, role, classeBean.getLevel(), classeBean, userBean.isDelegue(), userBean.getPrenom(), userBean.getNom());
                        }
                    } else {
                        verifAskStart(member, textChannel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Member member : guild.getMembers()) {
            logger.info("Renaming " + member.getEffectiveName() + "...");
            if (member.getEffectiveName().contains(" ("))
                rename(member, member.getEffectiveName().split(" \\(")[0]);
            else
                rename(member, member.getEffectiveName());
        }
    }

    public static void verifAskStart(Member member, TextChannel textChannel) {
        new ButtonMenu.Builder()
                .setText("Bonjour <@" + member.getIdLong() + "> et bienvenue sur le discord du **Lycée Julien Wittmer**,\n" +
                        "\n" +
                        "Pour vous orienter correctement sur ce serveur, vous devrez répondre aux questions que je vais vous poser.\n" +
                        "\n" +
                        "Maintenant cliquez sur l'emoji :white_check_mark: juste en dessous de ce message.")
                .setChoices(VALIDER_EMOTE)
                .setEventWaiter(eventWaiter)
                .setTimeout(15, TimeUnit.DAYS)
                .setAction(re -> {
                    if (re.getName().equals(VALIDER_EMOTE)) {
                        //CONTINUE
                        verifAskType(member, textChannel);
                    }
                })
                .setFinalAction(message -> message.delete().queue())
                .build().display(textChannel);
    }

    public static void verifAskType(Member member, TextChannel textChannel) {
        new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(eventWaiter)
                .setTimeout(15, TimeUnit.DAYS)
                .useNumbers()
                .allowTextInput(true)
                .addChoices("Élève", "Professeur")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //ELEVE
                        verifAskLevel(member, textChannel, Role.ELEVE);
                    } else if (integer == 2) {
                        //PROF
                        verifAskPrenom(member, textChannel, Role.PROF, null, null, false);
                    }
                })
                .build().display(textChannel);
    }

    public static void verifAskLevel(Member member, TextChannel textChannel, Role role) {
        new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(eventWaiter)
                .setTimeout(15, TimeUnit.DAYS)
                .useNumbers()
                .allowTextInput(true)
                .addChoices("Seconde", "Première", "Terminale")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //Seconde
                        verifAskClasse(member, textChannel, role, Level.SECONDE);
                    } else if (integer == 2) {
                        //Première
                        verifAskClasse(member, textChannel, role, Level.PREMIERE);
                    } else if (integer == 3) {
                        //Terminale
                        verifAskClasse(member, textChannel, role, Level.TERMINALE);
                    }
                })
                .build().display(textChannel);
    }

    public static void verifAskClasse(Member member, TextChannel textChannel, Role role, Level level) {
        Map<Integer, ClasseBean> classes = new HashMap<>();
        OrderedMenu.Builder builder = new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(eventWaiter)
                .setTimeout(15, TimeUnit.DAYS)
                .useNumbers()
                .allowTextInput(true)
                .setSelection((message, integer) -> {
                    //CONTINUE
                    verifAskDeleguee(member, textChannel, role, level, classes.get(integer));
                });
        int i = 0;
        try {
            List<ClasseBean> classeBeans = dataServiceManager.getClassesListOfALevelAndMatiere(level, Matiere.AUCUNE);
            if (classeBeans.isEmpty()) {
                textChannel.sendMessage(commandClient.getError() + " ERREUR: Aucune classe d'enregistré en " + level.getName())
                        .delay(10, TimeUnit.SECONDS)
                        .flatMap(message -> {
                            message.delete().queue();
                            verifAskStart(member, textChannel);
                            return null;
                        }).queue();
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

    public static void verifAskDeleguee(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe) {
        new OrderedMenu.Builder()
                .setText("Cliquez sur l'emoji correspondant à votre situation")
                .setEventWaiter(eventWaiter)
                .setTimeout(15, TimeUnit.DAYS)
                .useNumbers()
                .allowTextInput(true)
                .addChoices("Délégué•e", "Pas délégué•e")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //Delegue
                        verifAskPrenom(member, textChannel, role, level, classe, true);
                    } else if (integer == 2) {
                        //Pas delegue
                        verifAskPrenom(member, textChannel, role, level, classe, false);
                    }
                })
                .build().display(textChannel);
    }

    public static void verifAskPrenom(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe, boolean delegue) {
        if (role.equals(Role.PROF)) {
            new OrderedMenu.Builder()
                    .setText("Cliquez sur l'emoji correspondant à votre situation")
                    .setEventWaiter(eventWaiter)
                    .setTimeout(15, TimeUnit.DAYS)
                    .useNumbers()
                    .allowTextInput(true)
                    .addChoices("Monsieur", "Madame")
                    .setSelection((message, integer) -> {
                        if (integer == 1) {
                            //Mr
                            verifAskNom(member, textChannel, role, level, classe, delegue, "Mr");
                        } else if (integer == 2) {
                            //Mme
                            verifAskNom(member, textChannel, role, level, classe, delegue, "Mme");
                        }
                    })
                    .build().display(textChannel);
        } else {
            Message message = textChannel.sendMessage("Quel est votre prénom?").complete();
            eventWaiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().getIdLong() == member.getUser().getIdLong()
                            && e.getChannel().getIdLong() == textChannel.getIdLong(),
                    e -> {
                        //CONTINUE
                        message.delete().queue();
                        verifAskNom(member, textChannel, role, level, classe, delegue, e.getMessage().getContentRaw().toLowerCase());
                        e.getMessage().delete().queue();
                    },
                    // if the user takes more than a minute, time out
                    15, TimeUnit.DAYS, () -> message.delete().queue());
        }
    }

    public static void verifAskNom(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe, boolean delegue, String prenom) {
        Message message = textChannel.sendMessage("Quel est votre nom de famille?").complete();
        eventWaiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().getIdLong() == member.getUser().getIdLong()
                        && e.getChannel().getIdLong() == textChannel.getIdLong(),
                e -> {
                    //CONTINUE
                    message.delete().queue();
                    verifFinal(member, textChannel, role, level, classe, delegue, prenom, e.getMessage().getContentRaw().toLowerCase());
                    try {
                        if (role.equals(Role.PROF))
                            dataServiceManager.createUser(new UserBean(member.getIdLong(), prenom, e.getMessage().getContentRaw().toLowerCase(), role.getId(), delegue, 0, "", false));
                        else
                            dataServiceManager.createUser(new UserBean(member.getIdLong(), prenom, e.getMessage().getContentRaw().toLowerCase(), role.getId(), delegue, classe.getId(), "", false));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    e.getMessage().delete().queue();
                },
                // if the user takes more than a minute, time out
                15, TimeUnit.DAYS, () -> message.delete().queue());
    }

    public static void verifFinal(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe, boolean delegue, String prenom, String nom) {
        Message messageVerif = textChannel.sendMessage(new EmbedBuilder().setDescription("Demande en attende de vérification...").setColor(Color.GREEN).build()).complete();
        StringBuilder text = new StringBuilder("<@" + member.getIdLong() + ">, " + prenom + " " + nom +
                "\n" + role.name());
        if (level != null)
            text.append("\n").append(level.getName());
        if (classe != null)
            text.append("\n").append(classe.getName());
        text.append("\n").append(delegue);
        new ButtonMenu.Builder()
                .setText(text.toString())
                .setChoices(VALIDER_EMOTE, ANNULER_EMOTE)
                .setEventWaiter(eventWaiter)
                .setTimeout(15, TimeUnit.DAYS)
                .setAction(re -> {
                    if (re.getName().equals(VALIDER_EMOTE)) {
                        //ACCEPT
                        try {
                            UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), prenom, nom, role.getId(), delegue, 0, "", false));
                            userBean.setVerified(true);
                            if (userBean.isDelegue())
                                Objects.requireNonNull(jda.getGuildById(GUILD_ID)).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(Role.DELEGUE.getRoleId()))).queue();
                            Objects.requireNonNull(jda.getGuildById(GUILD_ID)).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(Objects.requireNonNull(Role.getById(userBean.getRole())).getRoleId()))).queue();
                            if (classe != null)
                                joinClasse(member, classe);
                            messageVerif.getTextChannel().delete().queue();
                            dataServiceManager.updateUser(userBean);
                            rename(member, member.getEffectiveName());
                            logger.info("Accepted " + member.getEffectiveName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (re.getName().equals(ANNULER_EMOTE)) {
                        //REFUSE
                        try {
                            dataServiceManager.deleteUser(member.getIdLong());
                            messageVerif.delete().queue();
                            verifAskStart(member, textChannel);
                            logger.info("Refused " + member.getEffectiveName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setFinalAction(message -> message.delete().queue())
                .build().display(jda.getTextChannelById(DEMANDES_CHANNEL_ID));
    }

    public static void joinClasse(Member member, ClasseBean classeBean) {
        try {
            UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
            Objects.requireNonNull(jda.getGuildById(GUILD_ID)).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(Objects.requireNonNull(classeBean).getDiscord_role_id()))).queue();
            @SuppressWarnings("StringBufferReplaceableByString") StringBuilder pseudo = new StringBuilder();
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
                UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
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
}
