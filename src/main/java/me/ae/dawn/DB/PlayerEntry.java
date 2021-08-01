package me.ae.dawn.DB;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerEntry {
    private UUID uuid;
    private ArrayList<String> ranks;
    private ArrayList<String> userPerms;

    public PlayerEntry(String uuid, ArrayList<String> ranks, ArrayList<String> userPerms) {
        this.uuid = UUID.fromString(uuid);
        this.ranks = ranks;
        this.userPerms = userPerms;
    }

    public UUID getUUID() { return uuid; }

    public ArrayList<String> getRanks() { return ranks; }

    public ArrayList<String> getUserPerms() { return userPerms; }

}
