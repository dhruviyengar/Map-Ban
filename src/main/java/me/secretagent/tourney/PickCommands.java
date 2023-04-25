package me.secretagent.tourney;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.EnumSet;
import java.util.Locale;

@CommandController
public class PickCommands {

    @Command(value="startmatch", name="Start Match", usage="{prefix}startmatch @Member1 @Member2 match-name")
    @com.github.kaktushose.jda.commands.annotations.Permission("CREATE_CHANNEL")
    public void startMatch(CommandEvent event, Member member1, Member member2, String matchName) {
        Role everyoneRole = event.getGuild().getPublicRole();
        event.getChannel().asTextChannel().getParentCategory().createTextChannel(matchName)
                .addMemberPermissionOverride(member1.getIdLong(), EnumSet.of(Permission.MESSAGE_SEND), null)
                .addMemberPermissionOverride(member2.getIdLong(), EnumSet.of(Permission.MESSAGE_SEND), null)
                .addMemberPermissionOverride(everyoneRole.getIdLong(), null, EnumSet.of(Permission.MESSAGE_SEND))
                .queue(channel -> {
                    MapPick mapPick = new MapPick(channel, member1, member2);
                    TournamentBot.mapPicks.put(channel.getIdLong(), mapPick);
                    event.reply(member1 + ", it is your turn to ban.");
                });
    }

    @Command(value="banmap", name="Ban Map", usage="{prefix}banmap [map-name]")
    public void banMap(CommandEvent event, String mapName) {
        Long id = event.getGuildChannel().asTextChannel().getIdLong();
        if (TournamentBot.mapPicks.containsKey(id)) {
            MapPick mapPick = TournamentBot.mapPicks.get(id);
            if (!mapPick.getAvailableMaps().contains(mapName.toLowerCase(Locale.ROOT))) return;
            if (mapPick.getStage() == MapPickStage.BAN_TEAM1_1 && event.getMember().getIdLong() == mapPick.getMember1().getIdLong()) {
                mapPick.banMap(mapName);
                mapPick.setStage(MapPickStage.BAN_TEAM2_1);
                event.reply(mapPick.getMember2().getAsMention() + ", it is your turn to ban.");
            } else if (mapPick.getStage() == MapPickStage.BAN_TEAM2_1 && event.getMember().getIdLong() == mapPick.getMember2().getIdLong()) {
                mapPick.banMap(mapName);
                mapPick.setStage(MapPickStage.PICK_TEAM1);
                event.reply(mapPick.getMember1().getAsMention() + ", it is your turn to pick.");
            } else if (mapPick.getStage() == MapPickStage.BAN_TEAM1_2 && event.getMember().getIdLong() == mapPick.getMember1().getIdLong()) {
                mapPick.banMap(mapName);
                mapPick.setStage(MapPickStage.BAN_TEAM2_2);
                event.reply(mapPick.getMember2().getAsMention() + ", it is your turn to ban.");
            } else if (mapPick.getStage() == MapPickStage.BAN_TEAM2_2 && event.getMember().getIdLong() == mapPick.getMember2().getIdLong()) {
                mapPick.banMap(mapName);
                mapPick.setStage(MapPickStage.BAN_TEAM1_3);
                event.reply(mapPick.getMember1().getAsMention() + ", it is your turn to ban.");
            } else if (mapPick.getStage() == MapPickStage.BAN_TEAM1_3 && event.getMember().getIdLong() == mapPick.getMember1().getIdLong()) {
                mapPick.banMap(mapName);
                mapPick.setStage(MapPickStage.BAN_TEAM2_3);
                event.reply(mapPick.getMember2().getAsMention() + ", it is your turn to ban.");
            } else if (mapPick.getStage() == MapPickStage.BAN_TEAM2_3 && event.getMember().getIdLong() == mapPick.getMember2().getIdLong()) {
                mapPick.banMap(mapName);
                mapPick.setStage(MapPickStage.SIDETEAM1_1);
                event.reply(mapPick.getMember1().getAsMention() + ", it is your turn to pick a side for map " + mapPick.getAvailableMaps().get(0) + ".");
            }
        }
    }

    @Command(value="pickmap", name="Pick Map", usage="{prefix}pickmap [map-name]")
    public void pickMap(CommandEvent event, String mapName) {
        Long id = event.getGuildChannel().asTextChannel().getIdLong();
        if (TournamentBot.mapPicks.containsKey(id)) {
            MapPick mapPick = TournamentBot.mapPicks.get(id);
            if (!mapPick.getAvailableMaps().contains(mapName.toLowerCase(Locale.ROOT))) return;
            if (mapPick.getStage() == MapPickStage.PICK_TEAM1 && event.getMember().getIdLong() == mapPick.getMember1().getIdLong()) {
                mapPick.pickMap(mapName);
                mapPick.setStage(MapPickStage.SIDE_TEAM2);
                event.reply(mapPick.getMember2().getAsMention() + ", please pick a side for map " + mapName + ".");
            } else if (mapPick.getStage() == MapPickStage.PICK_TEAM2 && event.getMember().getIdLong() == mapPick.getMember2().getIdLong()) {
                mapPick.pickMap(mapName);
                mapPick.setStage(MapPickStage.SIDE_TEAM1);
                event.reply(mapPick.getMember1().getAsMention() + ", please pick a side for map " + mapName + ".");
            }
        }
    }

    @Command(value="pickside", name="Pick Side", usage="{prefix}pickside [attack|defense]")
    public void pickSide(CommandEvent event, String sideName) {
        Long id = event.getGuildChannel().asTextChannel().getIdLong();
        TextChannel channel = event.getChannel().asTextChannel();
        if (TournamentBot.mapPicks.containsKey(id)) {
            MapPick mapPick = TournamentBot.mapPicks.get(id);
            if (!MapPick.SIDES.contains(sideName.toLowerCase(Locale.ROOT))) return;
            if (mapPick.getStage() == MapPickStage.SIDE_TEAM1 && event.getMember().getIdLong() == mapPick.getMember1().getIdLong()) {
                mapPick.pickSide(mapPick.getCurrentMap(), sideName);
                mapPick.setStage(MapPickStage.BAN_TEAM1_2);
                event.reply(mapPick.getMember1().getAsMention() + ", it is your turn to ban.");
            } else if (mapPick.getStage() == MapPickStage.SIDE_TEAM2 && event.getMember().getIdLong() == mapPick.getMember2().getIdLong()) {
                mapPick.pickSide(mapPick.getCurrentMap(), sideName);
                mapPick.setStage(MapPickStage.PICK_TEAM2);
                event.reply(mapPick.getMember2().getAsMention() + ", it is your turn to pick.");
            } else if (mapPick.getStage() == MapPickStage.SIDETEAM1_1 && event.getMember().getIdLong() == mapPick.getMember1().getIdLong()) {
                mapPick.pickMap(mapPick.getAvailableMaps().get(0));
                mapPick.pickSide(mapPick.getCurrentMap(), sideName);
                mapPick.setStage(MapPickStage.COMPLETE);
                String message = "";
                int i = 1;
                for (String key : mapPick.getPickedMapsOrder()) {
                    String member;
                    if (i == 1 || i == 3) {
                        member = mapPick.getMember2().getAsMention();
                    } else if (i == 2) {
                        member = mapPick.getMember1().getAsMention();
                    } else {
                        member = "null";
                    }
                    message += "Map " + i + ": " + key + " | " + member + " starts on " + mapPick.getPickedMaps().get(key) + "\n";
                    i++;
                }
                event.reply(message);
            }
        }
    }

    @Command(value="delete", name="Delete Match", usage="Delete Match")
    @com.github.kaktushose.jda.commands.annotations.Permission("DELETE_CHANNEL")
    public void deleteMatch(CommandEvent event) {
        Long id = event.getGuildChannel().asTextChannel().getIdLong();
        TextChannel channel = event.getChannel().asTextChannel();
        if (TournamentBot.mapPicks.containsKey(id)) {
            TournamentBot.mapPicks.remove(id);
            channel.delete().queue();
        }
    }

}
