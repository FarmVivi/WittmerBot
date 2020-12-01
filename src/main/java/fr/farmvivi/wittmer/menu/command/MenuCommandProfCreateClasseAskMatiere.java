package fr.farmvivi.wittmer.menu.command;

import com.jagrosh.jdautilities.menu.SelectionDialog;
import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Main;
import fr.farmvivi.wittmer.Matiere;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MenuCommandProfCreateClasseAskMatiere {
    public static void execute(Member member, TextChannel textChannel, Level level) {
        try {
            Map<Integer, Matiere> matieres = new HashMap<>();
            SelectionDialog.Builder builder = new SelectionDialog.Builder()
                    .setText("Naviguez parmi ce menu pour choisir la matière de la classe qui va être crée")
                    .setEventWaiter(Main.eventWaiter)
                    .setTimeout(30, TimeUnit.DAYS)
                    .setSelectedEnds(">**", "**<")
                    .useSingleSelectionMode(true)
                    .useLooping(true)
                    .setSelectionConsumer((message, integer) -> {
                        //CONTINUE
                        Matiere selectedMatiere = matieres.get(integer);
                        if (selectedMatiere.isEntireClasse()) {
                            MenuCommandProfCreateClasseAskClasse.execute(member, textChannel, level, selectedMatiere);
                        } else {
                            MenuCommandProfCreateClasseAskClasseNumber.execute(member, textChannel, level, selectedMatiere);
                        }
                        message.delete().queue();
                    })
                    .setCanceled(message -> {
                        message.delete().queue();
                        MenuCommandStart.execute(member, textChannel);
                    });
            int i = 0;
            try {
                Matiere[] matieresTemp = Matiere.values();
                for (Matiere matiere : matieresTemp) {
                    if (matiere.equals(Matiere.AUCUNE))
                        continue;
                    i++;
                    builder.addChoices(matiere.getName());
                    matieres.put(i, matiere);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.build().display(textChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
