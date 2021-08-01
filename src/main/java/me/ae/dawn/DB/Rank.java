package me.ae.dawn.DB;

import me.ae.dawn.Exceptions.RankDoesntExist;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Rank {
    private String rankUUID;
    private String name;
    private String prefix;
    private String suffix;
    private int power;
    private ArrayList<String> permissions;
    private boolean default_;
    private String parentUUID;
    private String members;

    public Rank(String rankUUID, String name, String prefix, String suffix, int power, ArrayList<String> permissions, boolean default_, String parentUUID) {
        this.rankUUID = rankUUID;
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.power = power;
        this.permissions = permissions;
        this.default_ = default_;
        this.parentUUID = parentUUID;
    }

    public String getUUID() {
        return rankUUID;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getPower() {
        return power;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public boolean isDefault() {
        return default_;
    }

    public Rank getParent() {
        if (!parentUUID.isEmpty()) {
            try {
                return RankDatabase.getRank(RankDatabase.uuidToRankName(UUID.fromString(parentUUID)));
            } catch (Exception ignored) {}
        }
        return null;
    }

    public String getParentUUID() { return parentUUID; }

    public ArrayList<UUID> getMembersUUIDs() throws SQLException, RankDoesntExist { return PlayerEntryDatabase.getRankMembers(UUID.fromString(rankUUID)); }

    public ArrayList<String> getMembersNames() throws SQLException, RankDoesntExist {
        ArrayList<String> result = new ArrayList<>();
        for (UUID u : PlayerEntryDatabase.getRankMembers(UUID.fromString(rankUUID))) {
            result.add(Objects.requireNonNull(Bukkit.getPlayer(u)).getDisplayName());
        }
        return result;
    }

}
