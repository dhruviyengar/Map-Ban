package me.secretagent.tourney;

import com.github.kaktushose.jda.commands.JDACommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.HashMap;

public class TournamentBot {

    public static HashMap<Long, MapPick> mapPicks = new HashMap<>();

    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("token")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        JDACommands.start(jda, TournamentBot.class);
    }

}
