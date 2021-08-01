package me.ae.dawn.DB;

import me.ae.dawn.Exceptions.RankDoesntExist;
import me.ae.dawn.Utils.Utility;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class PlayerEntryDatabase {

    // NOTE: Checks if user entry exists
    public static boolean playerEntryExists(UUID uuid) throws SQLException {
        try(PreparedStatement statement = Database.getConnection().prepareStatement("SELECT count(*) FROM player_data WHERE playerUUID=?")) {
            statement.setString(1, uuid.toString());
            ResultSet res = statement.executeQuery();
            return res.getInt(1) != 0;
        }
    }

    // NOTE: Create player entry
    public static void createPlayerEntry(UUID playerUUID) throws SQLException {
        if (playerEntryExists(playerUUID)) { return; }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("INSERT INTO player_data VALUES (?, ?, ?)")) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, new ArrayList<>().toString());
            statement.setString(3, new ArrayList<>().toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Get player entry
    public static PlayerEntry getPlayerEntry(UUID playerUUID) throws SQLException {
        if (!playerEntryExists(playerUUID)) {
            createPlayerEntry(playerUUID);
        }
        try(PreparedStatement statement = Database.getConnection().prepareStatement("SELECT * FROM player_data WHERE playerUUID=?")) {
            statement.setString(1, playerUUID.toString());
            ResultSet result = statement.executeQuery();
            ArrayList<String> ranks = Utility.parseStringToList(result.getString("ranks"));
            ArrayList<String> userPerms = Utility.parseStringToList(result.getString("userPerms"));
            return new PlayerEntry(playerUUID.toString(), ranks, userPerms);
        }
    }

    // NOTE: Edit player entry
    private static void editPlayerEntry(UUID playerUUID, ArrayList<String> ranks, ArrayList<String> userPerms) throws SQLException {
        if (!playerEntryExists(playerUUID)) {
            createPlayerEntry(playerUUID);
        }
        try (PreparedStatement prepstatement = Database.getConnection().prepareStatement("UPDATE player_data SET ranks=?, userPerms=? WHERE playerUUID=?")) {
            prepstatement.setString(1, ranks.toString());
            prepstatement.setString(2, userPerms.toString());
            prepstatement.setString(3, playerUUID.toString());
            prepstatement.executeUpdate();
        }
    }

    // NOTE: Delete player entry
    public static void deletePlayerEntry(UUID playerUUID) throws SQLException {
        if (!playerEntryExists(playerUUID)) {
            return;
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("DELETE from player_data WHERE playerUUID=?")) {
            statement.setString(1, playerUUID.toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Deletes a rank from all player entries
    public static void deleteRank(String rankUUID) throws SQLException {
        try (Statement statement = Database.getConnection().createStatement()) {
            ResultSet res = statement.executeQuery("SELECT * from player_data");
            while (res.next()) {
                UUID playerUUID = UUID.fromString(res.getString("playerUUID"));
                ArrayList<String> playerRanks = Utility.parseStringToList(res.getString("ranks"));
                for (String uuid : playerRanks) {
                    try {
                        if (uuid.equals(rankUUID)) {
                            playerRemoveRank(playerUUID, rankUUID);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    // ------------------------------------------------------------ //

    // NOTE: Add rank
    public static void playerAddRank(UUID playerUUID, String rank) throws SQLException, RankDoesntExist {
        PlayerEntry playerEntry = getPlayerEntry(playerUUID);
        playerEntry.getRanks().add(RankDatabase.rankNameToUUID(rank).toString());
        HashSet<String> set = new HashSet<>(playerEntry.getRanks()); // removes duplicates
        editPlayerEntry(playerUUID, new ArrayList<>(set), playerEntry.getUserPerms());
    }

    // NOTE: Add user permission
    public static void playerAddPermission(UUID playerUUID, String permission) throws SQLException, RankDoesntExist {
        PlayerEntry playerEntry = getPlayerEntry(playerUUID);
        playerEntry.getUserPerms().add(permission);
        HashSet<String> set = new HashSet<>(playerEntry.getUserPerms()); // removes duplicates
        editPlayerEntry(playerUUID, playerEntry.getRanks(), new ArrayList<>(set));
    }

    // NOTE: Remove user permission
    public static void playerRemovePermission(UUID playerUUID, String permission) throws SQLException, RankDoesntExist {
        PlayerEntry playerEntry = getPlayerEntry(playerUUID);
        playerEntry.getUserPerms().remove(permission);
        HashSet<String> set = new HashSet<>(playerEntry.getUserPerms()); // removes duplicates
        editPlayerEntry(playerUUID, playerEntry.getRanks(), new ArrayList<>(set));
    }

    // NOTE: Remove rank
    public static void playerRemoveRank(UUID playerUUID, String rankUUID) throws SQLException {
        PlayerEntry playerEntry = getPlayerEntry(playerUUID);
        ArrayList<String> ranks = playerEntry.getRanks();
        ranks.remove(rankUUID);
        editPlayerEntry(playerUUID, ranks, playerEntry.getUserPerms());
    }

    // NOTE: Get all members with a specified rank
    public static ArrayList<UUID> getRankMembers(UUID rankUUID) throws SQLException, RankDoesntExist {
        if (!RankDatabase.rankUUIDExists(rankUUID.toString())) {
            throw new RankDoesntExist();
        }
        ArrayList<UUID> rankMembers = new ArrayList<>();
        try(PreparedStatement statement = Database.getConnection().prepareStatement("SELECT * FROM player_data")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ArrayList<String> ranks = Utility.parseStringToList(result.getString("ranks"));
                if (ranks.contains(rankUUID.toString())) {
                    rankMembers.add(UUID.fromString(result.getString("playerUUID")));
                }
            }
        }
        return rankMembers;
    }

}
