package me.ae.dawn.Permissions;

import com.google.common.collect.Lists;
import me.ae.dawn.DB.PlayerEntryDatabase;
import me.ae.dawn.DB.Rank;
import me.ae.dawn.Exceptions.*;
import me.ae.dawn.Resources.Language;
import me.ae.dawn.DB.RankDatabase;
import me.ae.dawn.Utils.Utility;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import java.util.*;

public class PermsCommand implements CommandExecutor, TabExecutor {

    String PERMS_NODE = "dawn.perms";
    String PERMS_VIEW_NODE = "dawn.perms.view";
    String PERMS_MODIFY_NODE = "dawn.perms.modify";
    String PLAYER_PERMS_ALL_NODE = "dawn.perms.player.*";

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        try {
            if (!Permissions.hasPermission(sender, PERMS_NODE)) {
                throw new NoPermission();
            }
            List<String> allRanks = RankDatabase.getAllRankNames();
            String arg1 = Utility.getValueOrNull(args, 0);
            String arg2 = Utility.getValueOrNull(args, 1);
            String arg3 = Utility.getValueOrNull(args, 2);
            String arg4 = Utility.getValueOrNull(args, 3);
            if (arg1 == null) {
                throw new InvalidSubcommand();
            }
            if (arg1.equalsIgnoreCase("add")) {

                if (!Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidRankName();
                }
                if (!allRanks.contains(arg2)) {
                    throw new RankDoesntExist();
                }
                if (arg3 == null) {
                    throw new EmptyArgument(Language.getChatString("error.empty_field_permission"));
                }
                try {
                    RankDatabase.addPermission(arg2, arg3);
                } catch (PermissionAlreadyAdded e) {
                    throw new PermissionAlreadyAdded();
                }
                sender.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', "&e%s has been &agiven &ethe &3%s &epermission node."), arg2, arg3));
            } else if (arg1.equalsIgnoreCase("remove")) {
                if (!Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidRankName();
                }
                if (!allRanks.contains(arg2)) {
                    throw new RankDoesntExist();
                }
                if (arg3 == null) {
                    sender.sendMessage(Language.getChatString("error.empty_field_permission"));
                    return true;
                }
                try {
                    RankDatabase.removePermission(arg2, arg3);
                } catch (PermissionNotFound e) {
                    sender.sendMessage(e.getMessage());
                    return true;
                }
                sender.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', "&eSuccesfully &cremoved &3%s &efrom %s."), arg3, arg2));
            } else if (arg1.equalsIgnoreCase("setparent")) {
                if (!Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidRankName();
                }
                if (!allRanks.contains(arg2)) {
                    throw new RankDoesntExist();
                }
                if (arg3 == null) {
                    RankDatabase.editRankParent(arg2, "");
                    sender.sendMessage(String.format(ChatColor.GREEN + "%s's parent has been removed.", arg2));
                    return true;
                }
                if (!allRanks.contains(arg3)) {
                    throw new RankDoesntExist(ChatColor.RED + "Please enter a valid parent rank name.");
                }
                if (arg2.equals(arg3)) {
                    sender.sendMessage(ChatColor.RED + "You cannot have a rank parent itself.");
                    return true;
                }
                RankDatabase.editRankParent(arg2, RankDatabase.rankNameToUUID(arg3).toString());
                sender.sendMessage(String.format(ChatColor.GREEN + "%s's parent has been set to %s.", arg2, arg3));
            } else if (arg1.equalsIgnoreCase("player")) {
                if (!Permissions.hasPermission(sender, PLAYER_PERMS_ALL_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidSubcommand();
                }
                if (arg2.equalsIgnoreCase("add")) {
                    if (arg3 == null) {
                        throw new EmptyArgument(Language.getChatString("error.empty_field_player"));
                    }
                    if (arg4 == null) {
                        throw new EmptyArgument(Language.getChatString("error.empty_field_permission"));
                    }
                    UUID playerUUID = Utility.getPlayerUUID_A(arg3);
                    if (!PlayerEntryDatabase.playerEntryExists(playerUUID)) {
                        PlayerEntryDatabase.createPlayerEntry(playerUUID);
                    }
                    PlayerEntryDatabase.playerAddPermission(playerUUID, arg4);
                    sender.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', "&e%s has been &agiven &ethe &3%s &epermission node."), arg3, arg4));
                } else if (arg2.equalsIgnoreCase("remove")) {
                    if (arg3 == null) {
                        throw new EmptyArgument(Language.getChatString("error.empty_field_player"));
                    }
                    if (arg4 == null) {
                        throw new EmptyArgument(Language.getChatString("error.empty_field_permission"));
                    }
                    UUID playerUUID = Utility.getPlayerUUID_A(arg3);
                    if (!PlayerEntryDatabase.playerEntryExists(playerUUID)) {
                        PlayerEntryDatabase.createPlayerEntry(playerUUID);
                    }
                    PlayerEntryDatabase.playerRemovePermission(playerUUID, arg4);
                    sender.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', "&eSuccesfully &cremoved &3%s &efrom %s."), arg4, arg3));
                } else if (arg2.equalsIgnoreCase("view")) {
                    if (arg3 == null) {
                        throw new EmptyArgument(Language.getChatString("error.empty_field_player"));
                    }

                    String finalDisplayOutput;
                    int pageNumber = 0;
                    int lastPage = 0;
                    UUID playerUUID = Utility.getPlayerUUID_A(arg3);
                    if (PlayerEntryDatabase.playerEntryExists(playerUUID) && !PlayerEntryDatabase.getPlayerEntry(playerUUID).getUserPerms().isEmpty()) {
                        ArrayList<String> perms = PlayerEntryDatabase.getPlayerEntry(playerUUID).getUserPerms();
                        int viewCount = 10;
                        if (!NumberUtils.isDigits(arg4)) {
                            pageNumber = 1;
                        }
                        else {
                            pageNumber = Integer.parseInt(arg4);
                        }
                        lastPage = (int) Math.ceil(((float) perms.size()) / (float) viewCount);
                        if (pageNumber > lastPage) {
                            pageNumber = lastPage;
                        }

                        ArrayList<String> permsList = (ArrayList<String>) Utility.paginate(perms, pageNumber, 10);
                        ArrayList<String> displayStringList = new ArrayList<>();
                        for (String p : permsList) {
                            String node = ChatColor.DARK_GREEN + p;
                            if (p.startsWith("-")) { node = ChatColor.RED + p; }
                            displayStringList.add("§e- " + node);
                        }
                        finalDisplayOutput = String.join("\n", displayStringList);
                    } else {
                        finalDisplayOutput = Language.getChatString("error.no_perms_found");
                    }
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.GREEN + "Permissions for " + arg3 + ":");
                    sender.sendMessage("");
                    sender.sendMessage(finalDisplayOutput);
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.GREEN + "Page: " + pageNumber + "/" + lastPage);
                } else if (arg2.equalsIgnoreCase("purge")) {
                    if (arg3 == null) {
                        throw new EmptyArgument(Language.getChatString("error.empty_field_player"));
                    }
                    UUID playerUUID = Utility.getPlayerUUID_A(arg3);
                    PlayerEntryDatabase.deletePlayerEntry(playerUUID);
                    sender.sendMessage(ChatColor.YELLOW + String.format("Successfully purged all the permissions and ranks of %s.", arg3));
                }
            } else if (arg1.equalsIgnoreCase("view")) {
                if (!Permissions.hasPermission(sender, PERMS_VIEW_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidRankName();
                }
                if (!allRanks.contains(arg2)) {
                    throw new RankDoesntExist();
                }

                Rank rank = RankDatabase.getRank(arg2);
                Rank parentRank = rank.getParent();
                ArrayList<String> perms = rank.getPermissions();
                int viewCount = 10;

                // NOTE: Getting pageNumber int
                int pageNumber;
                if (!NumberUtils.isDigits(arg3)) {
                    pageNumber = 1;
                }
                else {
                    pageNumber = Integer.parseInt(arg3);
                }
                // NOTE: Getting last pageNumber int
                int lastPage = (int) Math.ceil(((float) perms.size()) / (float) viewCount);
                if (pageNumber > lastPage) {
                    pageNumber = lastPage;
                }

                // NOTE: Displaying paginated list
                String finalDisplayOutput;
                ArrayList<String> permsList = (ArrayList<String>) Utility.paginate(perms, pageNumber, 10);
                if (!permsList.isEmpty()) {
                    ArrayList<String> displayStringList = new ArrayList<>();
                    for (String p : permsList) {
                        String node = ChatColor.DARK_GREEN + p;
                        if (p.startsWith("-")) { node = ChatColor.RED + p; }
                        displayStringList.add("§e- " + node);
                    }
                    finalDisplayOutput = String.join("\n", displayStringList);
                } else {
                    finalDisplayOutput = Language.getChatString("error.no_perms_found");
                }

                sender.sendMessage("");
                sender.sendMessage(ChatColor.GREEN + "Permissions for " + arg2 + " rank:");
                if (parentRank != null) { sender.sendMessage(ChatColor.GREEN + "Parent Rank: " + ChatColor.DARK_AQUA + parentRank.getName()); }
                sender.sendMessage("");
                sender.sendMessage(finalDisplayOutput);
                sender.sendMessage("");
                sender.sendMessage(ChatColor.GREEN + "Page: " + pageNumber + "/" + lastPage);
            } else if (arg1.equalsIgnoreCase("purge")) {
                if (!Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidRankName();
                }
                if (!allRanks.contains(arg2)) {
                    throw new RankDoesntExist();
                }
                RankDatabase.editRankPermissions(arg2, new ArrayList<>());
                sender.sendMessage(ChatColor.YELLOW + String.format("Successfully purged all the permissions of %s.", arg2));
            } else if (arg1.equalsIgnoreCase("copy")) {
                if (!Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidRankName();
                }
                if (!allRanks.contains(arg2)) {
                    throw new RankDoesntExist();
                }
                if (arg3 == null) {
                    throw new InvalidRankName(ChatColor.RED + "Please provide a target rank for the permissions to be copied to.");
                }
                if (!allRanks.contains(arg3)) {
                    throw new RankDoesntExist(ChatColor.RED + "That target rank does not exist.");
                }
                ArrayList<String> fromRankPerms = RankDatabase.getRank(arg2).getPermissions();
                RankDatabase.editRankPermissions(arg3, fromRankPerms);
                sender.sendMessage(ChatColor.YELLOW + String.format("Successfully copied all the permissions (%d) of %s to %s.", fromRankPerms.size(), arg2, arg3));
            } else {
                throw new InvalidSubcommand();
            }
        }
        catch (NoPermission | InvalidSubcommand | PermissionAlreadyAdded | RankDoesntExist | EmptyArgument | InvalidRankName e) {
            sender.sendMessage(e.getMessage());
        } catch (Exception e) { e.printStackTrace(); }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> subcommands = new ArrayList<>();
        List<String> allRanks = RankDatabase.getAllRankNames();
        if (command.getName().equalsIgnoreCase("perms")) {
            if (!Permissions.hasPermission(sender, PERMS_NODE)) { return new ArrayList<>(); }
            if (args.length == 1) {

                if (Permissions.hasPermission(sender, PERMS_VIEW_NODE)) {
                    subcommands.add("view");
                }
                if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                    subcommands.addAll(Arrays.asList("add", "remove", "setparent", "purge", "player", "copy"));
                }
                return StringUtil.copyPartialMatches(args[0], subcommands, new ArrayList<>());
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("player")) {
                    if (Permissions.hasPermission(sender, PLAYER_PERMS_ALL_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Arrays.asList("add", "remove", "purge", "view"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], placeholder, new ArrayList<>());
                    }
                }
                else if (args[0].equalsIgnoreCase("add")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                else if (args[0].equalsIgnoreCase("view")) {
                    if (Permissions.hasPermission(sender, PERMS_VIEW_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                else if (args[0].equalsIgnoreCase("setparent")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                else if (args[0].equalsIgnoreCase("copy")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                else if (args[0].equalsIgnoreCase("purge")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<permission>"));
                        if (args[2].length() == 0) { return placeholder; }
                    }
                }
                if (args[0].equalsIgnoreCase("player")) {
                    if (Permissions.hasPermission(sender, PLAYER_PERMS_ALL_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<player>"));
                        if (args[2].length() == 0) { return placeholder; }
                        ArrayList<String> onlineUserNameList = Lists.newArrayList();
                        sender.getServer().getOnlinePlayers().forEach(e -> onlineUserNameList.add(e.getName()));
                        return StringUtil.copyPartialMatches(args[2], onlineUserNameList, new ArrayList<>());
                    }
                }
                else if (args[0].equalsIgnoreCase("add")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<permission>"));
                        if (args[2].length() == 0) { return placeholder; }
                    }
                }
                else if (args[0].equalsIgnoreCase("setparent")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<parent>"));
                        if (args[2].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[2], allRanks, new ArrayList<>());
                    }
                }
                else if (args[0].equalsIgnoreCase("copy")) {
                    if (Permissions.hasPermission(sender, PERMS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<target>"));
                        if (args[2].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[2], allRanks, new ArrayList<>());
                    }
                }
            }
        }
        return new ArrayList<>();
    }

}
