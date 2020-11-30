package fr.farmvivi.wittmer.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class PingCommand extends Command {
    public PingCommand() {
        this.name = "ping";
        this.help = "Affiche la latence du bot";
        this.guildOnly = true;
        this.aliases = new String[]{"pong"};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Ping: ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms")
                    .delay(30, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
        });
        event.getMessage().delete().queue();
    }
}
