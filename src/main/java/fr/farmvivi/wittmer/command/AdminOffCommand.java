package fr.farmvivi.wittmer.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.farmvivi.wittmer.Main;

import java.util.Objects;

public class AdminOffCommand extends Command {
    public AdminOffCommand() {
        this.name = "adminoff";
        this.help = "Ne plus devenir administrateur";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().complete();
        commandEvent.getGuild().removeRoleFromMember(commandEvent.getAuthor().getIdLong(), Objects.requireNonNull(commandEvent.getGuild().getRoleById(Main.ADMIN_ROLE_ID))).queue();
    }
}
