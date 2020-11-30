package fr.farmvivi.wittmer.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class CatCommand extends Command {
    public CatCommand() {
        this.name = "chat";
        this.help = "Affiche un chat au hasard";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        // use Unirest to poll an API
        Unirest.get("https://aws.random.cat/meow").asJsonAsync(new Callback<JsonNode>() {

            // The API call was successful
            @Override
            public void completed(HttpResponse<JsonNode> hr) {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(event.isFromType(ChannelType.TEXT) ? event.getSelfMember().getColor() : Color.GREEN)
                        .setImage(hr.getBody().getObject().getString("file"))
                        .setFooter("Demandé par " + event.getMember().getEffectiveName())
                        .build();
                event.reply(embed);
                event.getMessage().delete().queue();
            }

            // The API call failed
            @Override
            public void failed(UnirestException ue) {
                event.reactError();
            }

            // The API call was cancelled (this should never happen)
            @Override
            public void cancelled() {
                event.reactError();
            }
        });
    }
}
