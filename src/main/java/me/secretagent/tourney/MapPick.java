package me.secretagent.tourney;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;
import java.util.stream.Collectors;

public class MapPick {

    public static List<String> SIDES = Arrays.asList("attack", "defense");

    private MapPickStage stage = MapPickStage.BAN_TEAM1_1;

    private final List<String> availableMaps = new ArrayList<>();
    private final List<String> pickedMapsOrder = new ArrayList<>();
    private final HashMap<String, String> pickedMaps = new HashMap<>();

    private final TextChannel channel;
    private final Member member1;
    private final Member member2;

    private String currentMap;

    private int turn = 1;

    public MapPick(TextChannel channel, Member member1, Member member2) {
        this.channel = channel;
        this.member1 = member1;
        this.member2 = member2;
        availableMaps.add("ascent");
        availableMaps.add("bind");
        availableMaps.add("breeze");
        availableMaps.add("fracture");
        availableMaps.add("haven");
        availableMaps.add("icebox");
        availableMaps.add("lotus");
        availableMaps.add("pearl");
        availableMaps.add("split");
    }

    public MapPickStage getStage() {
        return stage;
    }

    public void setStage(MapPickStage stage) {
        this.stage = stage;
    }

    public Member getMember1() {
        return member1;
    }

    public Member getMember2() {
        return member2;
    }

    public List<String> getAvailableMaps() {
        return availableMaps;
    }

    public void banMap(String map) {
        availableMaps.remove(map);
    }

    public void pickMap(String map) {
        pickedMaps.put(map, null);
        pickedMapsOrder.add(map);
        availableMaps.remove(map);
        currentMap = map;
    }

    public void pickSide(String map, String side) {
        pickedMaps.put(map, side);
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public HashMap<String, String> getPickedMaps() {
        return pickedMaps;
    }

    public List<String> getPickedMapsOrder() {
        return pickedMapsOrder;
    }

}
