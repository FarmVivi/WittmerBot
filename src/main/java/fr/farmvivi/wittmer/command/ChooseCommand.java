package fr.farmvivi.wittmer.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ChooseCommand extends Command {
    public ChooseCommand() {
        this.name = "choisir";
        this.help = "Prendre une décision";
        this.arguments = "<choix 1> <choix 2> ...";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Aucun choix donné !");
        } else {
            String[] items = event.getArgs().split("\\s+");

            if (items.length == 1) {
                event.replyWarning("Seulement 1 choix donné, `" + items[0] + "`");
            } else {
                event.replySuccess("J'ai choisi `" + items[(int) (Math.random() * items.length)] + "`");
            }
        }
    }
}
