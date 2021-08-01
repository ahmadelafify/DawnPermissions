package me.ae.dawn.DB;

import me.ae.dawn.Exceptions.PermissionAlreadyAdded;
import me.ae.dawn.Exceptions.PermissionNotFound;
import me.ae.dawn.Exceptions.RankAlreadyExists;
import me.ae.dawn.Exceptions.RankDoesntExist;
import me.ae.dawn.Utils.Utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class RankDatabase {

    // NOTE: Ensure there is a default role
    public static void initialise() throws SQLException {
        if (getDefaultRank() == null) {
            try (PreparedStatement statement = Database.getConnection().prepareStatement("INSERT INTO ranks_data VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, UUID.randomUUID().toString());
                statement.setString(2, "Default");
                statement.setString(3, "");
                statement.setString(4, "");
                statement.setInt(5, 999999);
                statement.setString(6, new ArrayList<>().toString());
                statement.setBoolean(7, true);
                statement.setString(8, "");
                statement.executeUpdate();
            }
        }
    }

    // NOTE: Get default rank
    public static Rank getDefaultRank() throws SQLException {
        try (PreparedStatement statement = Database.getConnection().prepareStatement("SELECT * FROM ranks_data WHERE isDefault = ?")) {
            statement.setBoolean(1, true);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String rankName = result.getString("name");
                String rankUUID = result.getString("rankUUID");
                String prefix = result.getString("prefix");
                String suffix = result.getString("suffix");
                int power = result.getInt("power");
                ArrayList<String> permissions = Utility.parseStringToList(result.getString("permissions"));
                boolean default_ = result.getBoolean("isDefault");
                String parentUUID = result.getString("parentUUID");
                return new Rank(rankUUID, rankName, prefix, suffix, power, permissions, default_, parentUUID);
            }
        }
        return null;
    }

    // NOTE: Convert rank name to UUID
    public static UUID rankNameToUUID(String name) throws SQLException, RankDoesntExist {
        if (!rankExists(name)) { throw new RankDoesntExist(); }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("SELECT * FROM ranks_data WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            return UUID.fromString(result.getString("rankUUID"));
        }
    }

    // NOTE: Convert UUID to rank name
    public static String uuidToRankName(UUID rankUUID) throws SQLException, RankDoesntExist {
        try (PreparedStatement statement = Database.getConnection().prepareStatement("SELECT * FROM ranks_data WHERE rankUUID = ?")) {
            statement.setString(1, rankUUID.toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                throw new RankDoesntExist();
            }
            return result.getString("name");
        }
    }

    // NOTE: Checks if rank exists via rankName
    public static boolean rankExists(String name) throws SQLException {
        try (PreparedStatement statement = Database.getConnection().prepareStatement("SELECT count(*) FROM ranks_data WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet res = statement.executeQuery();
            return res.getInt(1) != 0;
        }
    }

    // NOTE: Checks if rank exists via UUID
    public static boolean rankUUIDExists(String rankUUID) throws SQLException {
        try (PreparedStatement statement = Database.getConnection().prepareStatement("SELECT count(*) FROM ranks_data WHERE rankUUID = ?")) {
            statement.setString(1, rankUUID);
            ResultSet res = statement.executeQuery();
            return res.getInt(1) != 0;
        }
    }

    // NOTE: Create rank
    public static void createRank(String name) throws SQLException, RankAlreadyExists {
        if (rankExists(name)) {
            throw new RankAlreadyExists();
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("INSERT INTO ranks_data VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, name);
            statement.setString(3, "");
            statement.setString(4, "");
            statement.setInt(5, 100);
            statement.setString(6, new ArrayList<>().toString());
            statement.setBoolean(7, false);
            statement.setString(8, "");
            statement.executeUpdate();
        }
    }

    // NOTE: Get rank
    public static Rank getRank(String name) throws SQLException, RankDoesntExist {
        if (!rankExists(name)) {
            throw new RankDoesntExist();
        }
        try (Statement statement = Database.getConnection().createStatement()) {
            ResultSet result = statement.executeQuery(String.format("SELECT * FROM ranks_data WHERE name = \"%s\"", name));
            String rankUUID = result.getString("rankUUID");
            String prefix = result.getString("prefix");
            String suffix = result.getString("suffix");
            int power = result.getInt("power");
            boolean default_ = result.getBoolean("isDefault");
            ArrayList<String> permissions = Utility.parseStringToList(result.getString("permissions"));
            String parentUUID = result.getString("parentUUID");
            return new Rank(rankUUID, name, prefix, suffix, power, permissions, default_, parentUUID);
        }
    }

    // NOTE: Edit rank name
    public static void editRankName(String rank, String newName) throws SQLException, RankDoesntExist {
        if (!rankExists(rank)) {
            throw new RankDoesntExist();
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET name=? WHERE rankUUID=?")) {
            statement.setString(1, newName);
            statement.setString(2, rankNameToUUID(rank).toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Edit rank prefix
    public static void editRankPrefix(String rank, String newPrefix) throws SQLException, RankDoesntExist {
        if (!rankExists(rank)) {
            throw new RankDoesntExist();
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET prefix=? WHERE rankUUID=?")) {
            statement.setString(1, newPrefix);
            statement.setString(2, rankNameToUUID(rank).toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Edit rank suffix
    public static void editRankSuffix(String rank, String newSuffix) throws SQLException, RankDoesntExist {
        if (!rankExists(rank)) {
            throw new RankDoesntExist();
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET suffix=? WHERE rankUUID=?")) {
            statement.setString(1, newSuffix);
            statement.setString(2, rankNameToUUID(rank).toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Edit rank power
    public static void editRankPower(String rank, int newPower) throws SQLException, RankDoesntExist {
        if (!rankExists(rank)) {
            throw new RankDoesntExist();
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET power=? WHERE rankUUID=?")) {
            statement.setInt(1, newPower);
            statement.setString(2, rankNameToUUID(rank).toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Edit rank permissions
    public static void editRankPermissions(String rank, ArrayList<String> newPerms) throws SQLException, RankDoesntExist {
        if (!rankExists(rank)) {
            throw new RankDoesntExist();
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET permissions=? WHERE rankUUID=?")) {
            statement.setString(1, newPerms.toString());
            statement.setString(2, rankNameToUUID(rank).toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Edit rank parent
    public static void editRankParent(String rank, String parentUUID) throws SQLException, RankDoesntExist {
        if (!rankExists(rank)) {
            throw new RankDoesntExist();
        }
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET parentUUID=? WHERE rankUUID=?")) {
            statement.setString(1, parentUUID);
            statement.setString(2, rankNameToUUID(rank).toString());
            statement.executeUpdate();
        }
    }

    // NOTE: Delete rank
    public static void deleteRank(String name) throws SQLException, RankDoesntExist {
        if (!rankExists(name)) {
            throw new RankDoesntExist();
        }
        Rank rank = getRank(name);
        // Deleting the rank itself
        try (PreparedStatement statement = Database.getConnection().prepareStatement("DELETE from ranks_data WHERE (rankUUID = ?)")) {
            statement.setString(1, rank.getUUID());
            statement.executeUpdate();
            // Deleting rank from rank parents
            ArrayList<Rank> allRanks = getAllRanks();
            for (Rank r : allRanks) {
                if (r.getParentUUID().equals(rank.getUUID())) {
                    editRankParent(r.getName(), "");
                }
            }
            // Deleting rank from playerentry database
            PlayerEntryDatabase.deleteRank(rank.getUUID());
        }
    }

    // NOTE: Get all ranks
    public static ArrayList<Rank> getAllRanks() {
        ArrayList<Rank> allRanks = new ArrayList<>();
        try (Statement statement = Database.getConnection().createStatement()) {
            ResultSet result = statement.executeQuery("SELECT * FROM ranks_data");
            while (result.next()) {
                String rankName = result.getString("name");
                String rankUUID = result.getString("rankUUID");
                String prefix = result.getString("prefix");
                String suffix = result.getString("suffix");
                int power = result.getInt("power");
                ArrayList<String> permissions = Utility.parseStringToList(result.getString("permissions"));
                boolean default_ = result.getBoolean("isDefault");
                String parentUUID = result.getString("parentUUID");
                allRanks.add(new Rank(rankUUID, rankName, prefix, suffix, power, permissions, default_, parentUUID));
            }
            allRanks.sort(Comparator.comparing(Rank::getPower));
        } catch (Exception ignored) {}
        return allRanks;
    }

    // ------------------------------------------------------------ //

    // NOTE: Add permission
    public static void addPermission(String name, String permission) throws SQLException, RankDoesntExist, PermissionAlreadyAdded {
        if (!rankExists(name)) {
            throw new RankDoesntExist();
        }
        Rank rank = getRank(name);
        if (rank.getPermissions().contains(permission)) { throw new PermissionAlreadyAdded(); }
        rank.getPermissions().add(permission);
        HashSet<String> set = new HashSet<>(rank.getPermissions()); // removes duplicates
        editRankPermissions(name, new ArrayList<>(set));
    }

    // NOTE: Remove permission
    public static void removePermission(String name, String permission) throws SQLException, RankDoesntExist, PermissionNotFound {
        if (!rankExists(name)) {
            throw new RankDoesntExist();
        }
        Rank rank = getRank(name);
        if (!rank.getPermissions().contains(permission)) { throw new PermissionNotFound(); }
        rank.getPermissions().remove(permission);
        editRankPermissions(name, rank.getPermissions());
    }

    // NOTE: Get all rank names
    public static ArrayList<String> getAllRankNames() {
        ArrayList<String> allRankNames = new ArrayList<>();
        ArrayList<Rank> allRanks = getAllRanks();

        for (Rank r: allRanks) {
            allRankNames.add(r.getName());
        }
        return allRankNames;
    }

    // NOTE: Set default rank
    public static void setDefaultRank(String rank) throws SQLException, RankDoesntExist {
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET isDefault=? WHERE isDefault=?")) {
            statement.setBoolean(1, false);
            statement.setBoolean(2, true);
            statement.executeUpdate();
        }
        try(PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE ranks_data SET isDefault=? WHERE rankUUID=?")) {
            statement.setBoolean(1, true);
            statement.setString(2, rankNameToUUID(rank).toString());
            statement.executeUpdate();
        }
    }
}
