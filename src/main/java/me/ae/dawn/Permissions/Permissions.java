package me.ae.dawn.Permissions;

import me.ae.dawn.DB.PlayerEntryDatabase;
import me.ae.dawn.DB.Rank;
import me.ae.dawn.DB.RankDatabase;
import me.ae.dawn.Exceptions.RankDoesntExist;
import me.ae.dawn.Main;
import me.ae.dawn.Utils.Utility;
import me.ae.dawn.DB.PlayerEntry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.sql.SQLException;
import java.util.*;

public class Permissions {

    public static HashMap<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    private static ArrayList<String> fetchPlayerPerms(UUID playerUUID) throws SQLException, RankDoesntExist {
        PlayerEntry player = PlayerEntryDatabase.getPlayerEntry(playerUUID);
        HashSet<String> allPerms = new HashSet<>();
        ArrayList<String> ranks = player.getRanks();
        if (ranks.isEmpty()) { ranks.add(Objects.requireNonNull(RankDatabase.getDefaultRank()).getUUID()); }
        for (String rank : ranks) {
            String rankName = RankDatabase.uuidToRankName(UUID.fromString(rank));
            Rank rankObj = RankDatabase.getRank(rankName);
            allPerms.addAll(rankObj.getPermissions());

            if (rankObj.getParent() != null) {
                Rank parentRank = rankObj.getParent();
                allPerms.addAll(parentRank.getPermissions());
                while (parentRank.getParent() != null) {
                    parentRank = parentRank.getParent();
                    allPerms.addAll(parentRank.getPermissions());
                }
            }
        }
        allPerms.addAll(player.getUserPerms());
        return new ArrayList<>(allPerms);
    }

    public static void setupPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(Main.getPlugin());
        playerPermissions.put(player.getUniqueId(), attachment);
        ArrayList<String> perms = new ArrayList<>();
        try {
            perms = fetchPlayerPerms(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String p : perms) {
            if (p.startsWith("-")) {
                attachment.setPermission(p.substring(1), false);
                continue;
            }
            attachment.setPermission(p, true);
        }
    }

    public static void removePermissions(Player player) {
        player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getAttachment).filter(Objects::nonNull).forEach(player::removeAttachment);
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        ArrayList<String> checks = new ArrayList<>();
        checks.add(permission);
        String[] perm_splitted =  permission.split("\\.");
        for (int i = perm_splitted.length; i > 0; i--) {
            ArrayList<String> splittedArray = new ArrayList<String>(Arrays.asList(perm_splitted));
            ArrayList<String> zzz = (ArrayList<String>) Utility.subList(splittedArray, 0, i);
            if (!zzz.isEmpty()) { zzz.remove(zzz.size()-1); }
            zzz.add("*");
            checks.add(String.join(".", zzz));
        }
        for (String p : checks) {
            if (sender.hasPermission(p)) {
                return true;
            }
        }
        return false;
    }

    public static Rank getPlayerStrongestRank(Player player) throws SQLException, RankDoesntExist {
        ArrayList<Rank> ranks = new ArrayList<>();
        ArrayList<String> rankUUIDs = PlayerEntryDatabase.getPlayerEntry(player.getUniqueId()).getRanks();
        if (rankUUIDs.isEmpty()) { rankUUIDs.add(Objects.requireNonNull(RankDatabase.getDefaultRank()).getUUID()); }
        for (String r : rankUUIDs) {
            String rankName = RankDatabase.uuidToRankName(UUID.fromString(r));
            Rank rank = RankDatabase.getRank(rankName);
            ranks.add(rank);
        }
        ranks.sort(Comparator.comparing(Rank::getPower));
        return ranks.get(0);
    }

    public static ArrayList<Rank> getPlayerRanks(UUID playerUUID) throws SQLException, RankDoesntExist {
        PlayerEntry playerEntry = PlayerEntryDatabase.getPlayerEntry(playerUUID);
        ArrayList<String> rankUUIDs = playerEntry.getRanks();
        if (rankUUIDs.isEmpty()) { rankUUIDs.add(Objects.requireNonNull(RankDatabase.getDefaultRank()).getUUID()); }
        ArrayList<Rank> playerRanks = new ArrayList<>();
        for (String r : rankUUIDs) {
            playerRanks.add(RankDatabase.getRank(RankDatabase.uuidToRankName(UUID.fromString(r))));
        }
        playerRanks.sort(Comparator.comparing(Rank::getPower));
        return playerRanks;
    }

}
