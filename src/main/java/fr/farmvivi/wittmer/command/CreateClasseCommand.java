package fr.farmvivi.wittmer.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateClasseCommand extends Command {
    public CreateClasseCommand() {
        this.name = "createclasse";
        this.help = "Créer une classe";
        this.arguments = "<level> <matiere> <numéro de classe>";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Erreur arguments");
        } else {
            String[] args = event.getArgs().split("\\s+");

            if (args.length < 3) {
                event.replyWarning("Erreur arguments");
            } else {
                Level level = Level.getById(Short.parseShort(args[0]));
                Matiere matiere = Matiere.getById(Integer.parseInt(args[1]));
                int num = Integer.parseInt(args[2]);
                StringBuilder name = new StringBuilder(Objects.requireNonNull(level).getPrefix());
                if (Objects.requireNonNull(matiere).getName().isEmpty()) {
                    name.append(num);
                } else if (matiere.isEntireClasse()) {
                    name.append(num).append(" - ").append(matiere.getName());
                } else {
                    name.append(" - ").append(matiere.getName()).append(" - ").append(num);
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
                try {
                    Main.dataServiceManager.createClasse(new ClasseBean(0, level, matiere, name.toString(), category.getIdLong(), role.getIdLong()));
                    event.replySuccess("Succès");
                } catch (Exception e) {
                    e.printStackTrace();
                    event.replyError("Erreur bdd");
                }
                category.createTextChannel("discussion").queue();
            }
        }
        event.getMessage().delete().queue();
    }
}
