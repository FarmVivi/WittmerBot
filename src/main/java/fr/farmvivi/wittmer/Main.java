package fr.farmvivi.wittmer;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jdautilities.menu.SelectionDialog;
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
    public static final String version = "1.0.1.1";
    public static final String name = "Wittmer";
    public static final boolean production = false;
    public static final String ANNULER_EMOTE = "\u274C";
    public static final String VALIDER_EMOTE = "\u2705";
    public static final long OWNER_ID = 751882667812847706L;
    public static final long GUILD_ID = 753631957606203474L;
    public static final long VERIF_CATEGORY_ID = 782655620586668102L;
    public static final long COMMANDS_CATEGORY_ID = 782945519806316584L;
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
        commandClientBuilder.setCoOwnerIds("177135083222859776");
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

        logger.info("Processus de vérification des membres...");
        verif();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void verif() {
        Guild guild = jda.getGuildById(GUILD_ID);
        Category verifCategory = Objects.requireNonNull(guild).getCategoryById(VERIF_CATEGORY_ID);
        for (GuildChannel guildChannel : Objects.requireNonNull(verifCategory).getChannels()) {
            logger.info("Delete " + guildChannel.getName() + " verif channel...");
            guildChannel.delete().queue();
        }
        ChannelAction<TextChannel> channelAction1 = verifCategory.createTextChannel("\uD83C\uDD95 Demandes");
        List<Permission> channelAllow1 = new ArrayList<>();
        channelAllow1.add(Permission.MESSAGE_READ);
        channelAllow1.add(Permission.VOICE_CONNECT);
        channelAction1.addRolePermissionOverride(Role.DELEGUE.getRoleId(), channelAllow1, new ArrayList<>());
        channelAction1.addRolePermissionOverride(Role.PROF.getRoleId(), channelAllow1, new ArrayList<>());
        logger.info("Create \uD83C\uDD95 Demandes verif channel...");
        DEMANDES_CHANNEL_ID = channelAction1.complete().getIdLong();
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
        Category commandsCategory = Objects.requireNonNull(guild).getCategoryById(COMMANDS_CATEGORY_ID);
        for (GuildChannel guildChannel : Objects.requireNonNull(commandsCategory).getChannels()) {
            logger.info("Delete " + guildChannel.getName() + " command channel...");
            guildChannel.delete().queue();
        }
        for (Member member : guild.getMembers()) {
            if (!production && member.getIdLong() != OWNER_ID)
                continue;
            try {
                if (!dataServiceManager.isUserCreated(member.getIdLong()))
                    continue;
                UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
                if (userBean.isVerified()) {
                    logger.info("Renaming " + member.getEffectiveName() + "...");
                    if (member.getEffectiveName().contains(" ("))
                        rename(member, member.getEffectiveName().split(" \\(")[0]);
                    else
                        rename(member, member.getEffectiveName());
                    logger.info("Sending commands to " + member.getEffectiveName() + "...");
                    ChannelAction<TextChannel> channelAction = commandsCategory.createTextChannel(member.getUser().getName());
                    List<Permission> channelAllow = new ArrayList<>();
                    channelAllow.add(Permission.MESSAGE_READ);
                    channelAllow.add(Permission.VOICE_CONNECT);
                    channelAllow.add(Permission.MESSAGE_ADD_REACTION);
                    channelAllow.add(Permission.MESSAGE_WRITE);
                    channelAllow.add(Permission.MESSAGE_EXT_EMOJI);
                    channelAllow.add(Permission.MESSAGE_HISTORY);
                    channelAction.addMemberPermissionOverride(member.getIdLong(), channelAllow, new ArrayList<>());
                    TextChannel textChannel = channelAction.complete();
                    cmdStart(member, textChannel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void cmdStart(Member member, TextChannel textChannel) {
        try {
            UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
            if (Objects.requireNonNull(Role.getById(userBean.getRole())).equals(Role.PROF)) {
                new OrderedMenu.Builder()
                        .setText("Cliquez sur l'emoji correspondant à ce que vous voulez faire")
                        .setEventWaiter(eventWaiter)
                        .setTimeout(30, TimeUnit.DAYS)
                        .useNumbers()
                        .addChoices("Créer un salon vocal de cours")
                        .setSelection((message, integer) -> {
                            if (integer == 1) {
                                //CREATE VOCAL
                                cmdProfAskCreateChannel(member, textChannel);
                            }
                        })
                        .build().display(textChannel);
            } else {
                new OrderedMenu.Builder()
                        .setText("Cliquez sur l'emoji correspondant à ce que vous voulez faire")
                        .setEventWaiter(eventWaiter)
                        .setTimeout(30, TimeUnit.DAYS)
                        .useNumbers()
                        .addChoices("Rejoindre une classe")
                        .setSelection((message, integer) -> {
                            if (integer == 1) {
                                //JOIN CLASS
                                cmdEleveAskJoinClasse(member, textChannel);
                            }
                        })
                        .build().display(textChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cmdEleveAskJoinClasse(Member member, TextChannel textChannel) {
        try {
            UserBean userBean = dataServiceManager.getUser(member.getIdLong(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
            Map<Integer, Matiere> matieres = new HashMap<>();
            SelectionDialog.Builder builder = new SelectionDialog.Builder()
                    .setText("Naviguez parmi ce menu pour choisir la matière que vous voulez rejoindre")
                    .setEventWaiter(eventWaiter)
                    .setTimeout(30, TimeUnit.DAYS)
                    .setSelectedEnds(">**", "**<")
                    .useSingleSelectionMode(true)
                    .useLooping(true)
                    .setSelectionConsumer((message, integer) -> {
                        //CONTINUE
                        cmdEleveAskJoinClasseMatiere(member, textChannel, userBean, matieres.get(integer));
                        message.delete().queue();
                    })
                    .setCanceled(message -> {
                        message.delete().queue();
                        cmdStart(member, textChannel);
                    });
            int i = 0;
            try {
                List<Matiere> matieresTemp = getJoinableMatieres(userBean);
                if (matieresTemp.isEmpty()) {
                    textChannel.sendMessage(commandClient.getError() + " Vous ne pouvez pas rejoindre d'autres classes")
                            .delay(10, TimeUnit.SECONDS)
                            .flatMap(message -> {
                                message.delete().queue();
                                cmdStart(member, textChannel);
                                return null;
                            }).queue();
                    return;
                } else {
                    for (Matiere matiere : matieresTemp) {
                        if (matiere.equals(Matiere.AUCUNE))
                            continue;
                        i++;
                        builder.addChoices(matiere.getName());
                        matieres.put(i, matiere);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.build().display(textChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cmdEleveAskJoinClasseMatiere(Member member, TextChannel textChannel, UserBean userBean, Matiere matiere) {
        Map<Integer, ClasseBean> classes = new HashMap<>();
        SelectionDialog.Builder builder = new SelectionDialog.Builder()
                .setText("Naviguez parmi ce menu pour choisir la classe que vous voulez rejoindre")
                .setEventWaiter(eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .setSelectedEnds(">**", "**<")
                .useSingleSelectionMode(true)
                .useLooping(true)
                .setSelectionConsumer((message, integer) -> {
                    //CONTINUE
                    Main.joinClasse(member, classes.get(integer));
                    cmdStart(member, textChannel);
                })
                .setCanceled(message -> {
                    message.delete().queue();
                    cmdStart(member, textChannel);
                });
        int i = 0;
        List<ClasseBean> finalClasses = getJoinableClasses(userBean, matiere);
        if (finalClasses.isEmpty()) {
            textChannel.sendMessage(commandClient.getError() + " ERREUR: Vous ne pouvez rejoindre aucune classe de la matière " + matiere.getName())
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(message -> {
                        message.delete().queue();
                        cmdStart(member, textChannel);
                        return null;
                    }).queue();
            return;
        } else {
            for (ClasseBean classeBean : finalClasses) {
                try {
                    i++;
                    UserBean profBean = dataServiceManager.getUser(classeBean.getDiscord_prof_id(), new UserBean(member.getIdLong(), "", "", (short) 0, false, 0, "", false));
                    @SuppressWarnings("StringBufferReplaceableByString") StringBuilder classeName = new StringBuilder(classeBean.getName());
                    classeName.append(" - ");
                    classeName.append(profBean.getPrenom().toUpperCase(), 0, 1);
                    classeName.append(profBean.getPrenom(), 1, profBean.getPrenom().length());
                    classeName.append(" ");
                    classeName.append(profBean.getNom().toUpperCase());
                    builder.addChoices(classeName.toString());
                    classes.put(i, classeBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        builder.build().display(textChannel);
    }

    private static List<Matiere> getJoinableMatieres(UserBean userBean) {
        List<Matiere> matieres = new ArrayList<>();
        for (Matiere matiere : Matiere.values()) {
            if (!getJoinableClasses(userBean, matiere).isEmpty())
                matieres.add(matiere);
        }
        return matieres;
    }

    private static List<ClasseBean> getJoinableClasses(UserBean userBean, Matiere matiere) {
        List<ClasseBean> finalClasses = new ArrayList<>();
        try {
            ClasseBean defaultClasse = dataServiceManager.getUserDefaultClasse(userBean);
            List<ClasseBean> alreadyJoinedClasses = dataServiceManager.getUserClasses(userBean);
            List<ClasseBean> availableClasses = dataServiceManager.getClassesListOfALevelAndMatiere(defaultClasse.getLevel(), matiere);
            for (ClasseBean availableClasse : availableClasses) {
                boolean add = true;
                if (availableClasse.getMatiere().isEntireClasse() && !availableClasse.getName().contains(defaultClasse.getName().split("⌋ ")[1]))
                    add = false;
                for (ClasseBean alreadyJoinedClasse : alreadyJoinedClasses) {
                    if (availableClasse.getId() == alreadyJoinedClasse.getId()) {
                        add = false;
                        break;
                    }
                }
                if (add)
                    finalClasses.add(availableClasse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalClasses;
    }

    public static void cmdProfAskCreateChannel(Member member, TextChannel textChannel) {
        new OrderedMenu.Builder()
                .setText("Quel est le niveau concerné?")
                .setEventWaiter(eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .addChoices("Seconde", "Première", "Terminale")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
                        //Seconde
                        cmdProfAskCreateChannelLevel(member, textChannel, Level.SECONDE);
                    } else if (integer == 2) {
                        //Première
                        cmdProfAskCreateChannelLevel(member, textChannel, Level.PREMIERE);
                    } else if (integer == 3) {
                        //Terminale
                        cmdProfAskCreateChannelLevel(member, textChannel, Level.TERMINALE);
                    }
                })
                .build().display(textChannel);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "MismatchedQueryAndUpdateOfCollection"})
    public static void cmdProfAskCreateChannelLevel(Member member, TextChannel textChannel, Level level) {
        Map<Integer, ClasseBean> classes = new HashMap<>();
        OrderedMenu.Builder builder = new OrderedMenu.Builder()
                .setText("Quelle est la classe concernée?")
                .setEventWaiter(eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .setSelection((message, integer) -> {
                    //CONTINUE
                    Guild guild = jda.getGuildById(GUILD_ID);
                    Category category = Objects.requireNonNull(guild).getCategoryById(classes.get(integer).getDiscord_category_id());
                    ChannelAction<VoiceChannel> channelAction = Objects.requireNonNull(category).createVoiceChannel("Cours");
                    List<Permission> channelAllow = new ArrayList<>();
                    channelAllow.add(Permission.MESSAGE_READ);
                    channelAllow.add(Permission.VOICE_CONNECT);
                    channelAction.addMemberPermissionOverride(member.getIdLong(), channelAllow, new ArrayList<>());
                    channelAction.setUserlimit(99);
                    channelAction.queue();
                    textChannel.sendMessage(new EmbedBuilder().setDescription("Salon crée !").setColor(Color.GREEN).build()).delay(5, TimeUnit.SECONDS).flatMap(message1 -> {
                        message1.delete().queue();
                        cmdStart(member, textChannel);
                        return null;
                    }).queue();
                });
        int i = 0;
        try {
            List<ClasseBean> classeBeans = dataServiceManager.getClasseOfAProf(member.getIdLong(), level);
            if (classeBeans.isEmpty()) {
                textChannel.sendMessage(commandClient.getError() + " ERREUR: Aucune classe d'enregistré en " + level.getName())
                        .delay(10, TimeUnit.SECONDS)
                        .flatMap(message -> {
                            message.delete().queue();
                            cmdStart(member, textChannel);
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

    public static void verifAskStart(Member member, TextChannel textChannel) {
        new ButtonMenu.Builder()
                .setText("Bonjour <@" + member.getIdLong() + "> et bienvenue sur le discord du **Lycée Julien Wittmer**,\n" +
                        "\n" +
                        "Pour vous orienter correctement sur ce serveur, vous devrez répondre aux questions que je vais vous poser.\n" +
                        "\n" +
                        "Maintenant cliquez sur l'emoji :white_check_mark: juste en dessous de ce message.")
                .setChoices(VALIDER_EMOTE)
                .setEventWaiter(eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
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
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
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
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
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
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
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
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
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
                    .setTimeout(30, TimeUnit.DAYS)
                    .useNumbers()
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
                    30, TimeUnit.DAYS, () -> message.delete().queue());
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
                30, TimeUnit.DAYS, () -> message.delete().queue());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void verifFinal(Member member, TextChannel textChannel, Role role, Level level, ClasseBean classe, boolean delegue, String prenom, String nom) {
        Message messageVerif = textChannel.sendMessage(new EmbedBuilder().setDescription("Demande en attende de vérification par un délégué•e ou un professeur...").setColor(Color.GREEN).build()).complete();
        StringBuilder text = new StringBuilder();
        if (role.equals(Role.PROF))
            text.append("<@&").append(Role.DELEGUE.getRoleId()).append(">, ").append("<@").append(member.getIdLong()).append("> souhaite rejoindre le discord en tant que professeur, cette personne est-elle bien professeur?");
        else
            text.append("<@&").append(classe.getDiscord_role_id()).append(">, ").append("<@").append(member.getIdLong()).append("> souhaite rejoindre le discord en tant qu'élève, cette personne est-elle dans votre classe?");
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
                .setEventWaiter(eventWaiter)
                .setTimeout(30, TimeUnit.DAYS)
                .useNumbers()
                .addChoices("Accepter", "Refuser")
                .setSelection((message, integer) -> {
                    if (integer == 1) {
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
                            Guild guild = jda.getGuildById(GUILD_ID);
                            Category commandsCategory = Objects.requireNonNull(guild).getCategoryById(COMMANDS_CATEGORY_ID);
                            ChannelAction<TextChannel> channelAction = Objects.requireNonNull(commandsCategory).createTextChannel(member.getUser().getName());
                            List<Permission> channelAllow = new ArrayList<>();
                            channelAllow.add(Permission.MESSAGE_READ);
                            channelAllow.add(Permission.VOICE_CONNECT);
                            channelAllow.add(Permission.MESSAGE_ADD_REACTION);
                            channelAllow.add(Permission.MESSAGE_WRITE);
                            channelAllow.add(Permission.MESSAGE_EXT_EMOJI);
                            channelAllow.add(Permission.MESSAGE_HISTORY);
                            channelAction.addMemberPermissionOverride(member.getIdLong(), channelAllow, new ArrayList<>());
                            TextChannel cmdChannel = channelAction.complete();
                            cmdStart(member, cmdChannel);
                            logger.info("Accepted " + member.getEffectiveName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (integer == 2) {
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
