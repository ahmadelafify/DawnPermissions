package me.ae.dawn.Permissions;

import me.ae.dawn.DB.Rank;
import me.ae.dawn.Exceptions.*;
import me.ae.dawn.Resources.Language;
import me.ae.dawn.DB.PlayerEntryDatabase;
import me.ae.dawn.DB.RankDatabase;
import me.ae.dawn.Exceptions.*;
import me.ae.dawn.Main;
import me.ae.dawn.Utils.Utility;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.*;

public class RanksCommand implements CommandExecutor, TabExecutor {

    String RANKS_NODE = "dawn.ranks";
    String RANKS_GRANT_NODE = "dawn.ranks.grant";
    String RANKS_REVOKE_NODE = "dawn.ranks.revoke";
    String RANKS_VIEW_NODE = "dawn.ranks.view";
    String RANKS_MODIFY_NODE = "dawn.ranks.modify";

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        try {
            if (!Permissions.hasPermission(sender, RANKS_NODE)) {
                throw new NoPermission();
            }
            ArrayList<String> allRankNames = RankDatabase.getAllRankNames();
            String arg1 = Utility.getValueOrNull(args, 0);
            String arg2 = Utility.getValueOrNull(args, 1);
            String arg3 = Utility.getValueOrNull(args, 2);

            if (arg1 == null) {
                throw new InvalidSubcommand();
            }
            else if (arg1.equalsIgnoreCase("grant")) {
                if (!Permissions.hasPermission(sender, RANKS_GRANT_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidPlayer();
                }
                if (arg3 == null || !allRankNames.contains(arg3)) {
                    throw new InvalidRankName();
                }

                // NOTE: Getting player UUID
                UUID playerUUID = Utility.getPlayerUUID_A(arg2);
                System.out.println(playerUUID);
                PlayerEntryDatabase.playerAddRank(playerUUID, arg3);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s has been &agranted &eto %s.", arg3, arg2)));
                return true;
            } else if (arg1.equalsIgnoreCase("revoke")) {
                if (!Permissions.hasPermission(sender, RANKS_REVOKE_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidPlayer();
                }
                if (arg3 == null || !allRankNames.contains(arg3)) {
                    throw new InvalidRankName();
                }

                // NOTE: Getting player UUID
                UUID playerUUID = Utility.getPlayerUUID_A(arg2);
                PlayerEntryDatabase.playerRemoveRank(playerUUID, RankDatabase.rankNameToUUID(arg3).toString());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s was &crevoked&e from %s.", arg3, arg2)));
                return true;
            } else if (arg1.equalsIgnoreCase("setprefix")) {
                if (!Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null || !allRankNames.contains(arg2)) {
                    throw new InvalidRankName();
                }
                if (arg3 == null) {
                    arg3 = "";
                    RankDatabase.editRankPrefix(arg2, arg3);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s has been cleared of a prefix.", arg2)));
                    return true;
                }
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', arg3)).length() > 16) {
                    throw new MaxCharacters(Language.getChatString("error.prefix_max_char"));
                }
                arg3 = arg3.replace("\\s", " ");
                if (arg3.endsWith(" ")) { arg3 = arg3 + "&r"; }
                RankDatabase.editRankPrefix(arg2, arg3);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s's prefix has been set to &r%s&e.", arg2, arg3)));
            } else if (arg1.equalsIgnoreCase("setsuffix")) {
                if (!Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null || !allRankNames.contains(arg2)) {
                    throw new InvalidRankName();
                }
                if (arg3 == null) {
                    arg3 = "";
                    RankDatabase.editRankSuffix(arg2, arg3);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s has been cleared of a suffix.", arg2)));
                    return true;
                }
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', arg3)).length() > 16) {
                    throw new MaxCharacters(Language.getChatString("error.suffix_max_char"));
                }
                arg3 = arg3.replace("\\s", " ");
                if (arg3.endsWith(" ")) { arg3 = arg3 + "&r"; }
                RankDatabase.editRankSuffix(arg2, arg3);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s's suffix has been set to &r%s&e.", arg2, arg3)));
            } else if (arg1.equalsIgnoreCase("setpower")) {
                if (!Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null || !allRankNames.contains(arg2)) {
                    throw new InvalidRankName();
                }
                if (!NumberUtils.isNumber(arg3)) {
                    throw new InvalidNumber();
                }
                int newPower = NumberUtils.toInt(arg3);
                RankDatabase.editRankPower(arg2, newPower);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s's power has been set to &a%d.", arg2, newPower)));
            } else if (arg1.equalsIgnoreCase("delete")) {
                if (!Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null || !allRankNames.contains(arg2)) {
                    throw new InvalidRankName();
                }
                if (RankDatabase.getRank(arg2).isDefault()) {
                    sender.sendMessage(Language.getChatString("error.defaultrank_deletion"));
                    return true;
                }
                RankDatabase.deleteRank(arg2);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s has been &cdeleted&e.", arg2)));
            } else if (arg1.equalsIgnoreCase("rename")) {
                if (!Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null || !allRankNames.contains(arg2)) {
                    throw new InvalidRankName();
                }
                if (arg3 == null || allRankNames.contains(arg3) || arg2.equals(arg3)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must provide a valid new rank name."));
                    return true;
                }
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', arg3)).length() > 16) {
                    throw new MaxCharacters(Language.getChatString("error.rankname_max_char"));
                }
                arg3 = arg3.replace("\\s", " ");
                RankDatabase.editRankName(arg2, arg3);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&e%s's name has been set to &r%s&e.", arg2, arg3)));
            } else if (arg1.equalsIgnoreCase("view")) {
                if (!Permissions.hasPermission(sender, RANKS_VIEW_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidPlayer();
                }
                // NOTE: Getting player UUID
                UUID playerUUID = Utility.getPlayerUUID_A(arg2);
                ArrayList<Rank> playerRanks = Permissions.getPlayerRanks(playerUUID);
                int viewCount = 10;

                // NOTE: Getting pageNumber int
                int pageNumber;
                if (!NumberUtils.isDigits(arg2)) {
                    pageNumber = 1;
                }
                else {
                    pageNumber = Integer.parseInt(arg2);
                }
                // NOTE: Getting last pageNumber int
                int lastPage = (int) Math.ceil(((float) playerRanks.size()) / (float) viewCount);
                if (pageNumber > lastPage) {
                    pageNumber = lastPage;
                }
                // NOTE: Displaying paginated list
                String finalDisplayOutput;
                ArrayList<Rank> rankDisplayList = (ArrayList<Rank>) Utility.paginate(playerRanks, pageNumber, 10);
                if (!rankDisplayList.isEmpty()) {
                    ArrayList<String> displayStringList = new ArrayList<>();
                    displayStringList.add(ChatColor.translateAlternateColorCodes('&', "&7  Name &f:&7 Power &f:&7 Prefix"));
                    for (Rank r : rankDisplayList) {
                        String prefix = ChatColor.RED + "null";
                        if (!r.getPrefix().equals("")) { prefix = r.getPrefix(); }
                        displayStringList.add(ChatColor.translateAlternateColorCodes('&', "&a- " + r.getName() + "&f : &3" + r.getPower() + "&f : " + prefix));
                    }
                    finalDisplayOutput = String.join("\n", displayStringList);
                } else {
                    finalDisplayOutput = Language.getChatString("error.no_ranks_found");
                }

                sender.sendMessage("");
                sender.sendMessage(ChatColor.YELLOW + arg2 + "'s ranks:");
                sender.sendMessage("");
                sender.sendMessage(finalDisplayOutput);
                sender.sendMessage("");
                sender.sendMessage(ChatColor.YELLOW + "Page " + pageNumber + "/" + lastPage);
            } else if (arg1.equalsIgnoreCase("list")) {
                if (!Permissions.hasPermission(sender, RANKS_VIEW_NODE)) {
                    throw new NoPermission();
                }

                ArrayList<Rank> allRanks = RankDatabase.getAllRanks();
                int viewCount = 10;

                // NOTE: Getting pageNumber int
                int pageNumber;
                if (!NumberUtils.isDigits(arg2)) {
                    pageNumber = 1;
                }
                else {
                    pageNumber = Integer.parseInt(arg2);
                }
                // NOTE: Getting last pageNumber int
                int lastPage = (int) Math.ceil(((float) allRanks.size()) / (float) viewCount);
                if (pageNumber > lastPage) {
                    pageNumber = lastPage;
                }
                // NOTE: Displaying paginated list
                String finalDisplayOutput;
                ArrayList<Rank> rankDisplayList = (ArrayList<Rank>) Utility.paginate(allRanks, pageNumber, 10);
                if (!rankDisplayList.isEmpty()) {
                    ArrayList<String> displayStringList = new ArrayList<>();
                    displayStringList.add(ChatColor.translateAlternateColorCodes('&', "&7  Name &f:&7 Power &f:&7 Prefix"));
                    for (Rank r : rankDisplayList) {
                        String prefix = ChatColor.RED + "null";
                        if (!r.getPrefix().equals("")) { prefix = r.getPrefix(); }
                        displayStringList.add(ChatColor.translateAlternateColorCodes('&', "&a- " + r.getName() + "&f : &3" + r.getPower() + "&f : " + prefix));
                    }
                    finalDisplayOutput = String.join("\n", displayStringList);
                } else {
                    finalDisplayOutput = Language.getChatString("error.no_ranks_found");
                }
                sender.sendMessage("");
                sender.sendMessage(ChatColor.YELLOW + "List of all ranks:");
                sender.sendMessage("");
                sender.sendMessage(finalDisplayOutput);
                sender.sendMessage("");
                sender.sendMessage(ChatColor.YELLOW + "Page: " + pageNumber + "/" + lastPage);
            } else if (arg1.equalsIgnoreCase("create")) {
                if (!Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null) {
                    throw new InvalidRankName();
                }
                if (allRankNames.contains(arg2)) {
                    throw new RankAlreadyExists();
                }
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', arg2)).length() > 16) {
                    throw new MaxCharacters(Language.getChatString("error.rankname_max_char"));
                }
                RankDatabase.createRank(arg2);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&a%s &ehas been created.", arg2)));
            } else if (arg1.equalsIgnoreCase("setdefaultrank")) {
                if (!Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null || !allRankNames.contains(arg2)) {
                    throw new InvalidRankName();
                }
                RankDatabase.setDefaultRank(arg2);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&a%s &ehas been set as the default rank.", arg2)));
            } else if (arg1.equalsIgnoreCase("members")) {
                if (!Permissions.hasPermission(sender, RANKS_VIEW_NODE)) {
                    throw new NoPermission();
                }
                if (arg2 == null || !allRankNames.contains(arg2)) {
                    throw new InvalidRankName();
                }
                Rank rank = RankDatabase.getRank(arg2);
                ArrayList<String> rankMembers = rank.getMembersNames();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&eThere are %d members in &a%s&e:&r %s", rankMembers.size(), arg2, String.join(", ", rankMembers))));
            }
            else {
                throw new InvalidSubcommand();
            }
            return true;
        } catch (NoPermission | InvalidPlayer | InvalidRankName | InvalidSubcommand | MaxCharacters | RankDoesntExist | RankAlreadyExists e) {
            sender.sendMessage(e.getMessage());
        } catch (Exception e) { e.printStackTrace(); }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> subcommands = new ArrayList<>();
        List<String> allRanks = RankDatabase.getAllRankNames();
        if (command.getName().equalsIgnoreCase("ranks")) {
            if (!Permissions.hasPermission(sender, RANKS_NODE)) { return new ArrayList<>(); }
            if (args.length == 1) {
                if (Permissions.hasPermission(sender, RANKS_VIEW_NODE)) {
                    subcommands.add("list");
                    subcommands.add("view");
                    subcommands.add("members");
                }
                if (Permissions.hasPermission(sender, RANKS_GRANT_NODE)) {
                    subcommands.add("grant");
                }
                if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                    subcommands.add("setprefix");
                    subcommands.add("setsuffix");
                    subcommands.add("setpower");
                    subcommands.add("rename");
                    subcommands.add("create");
                    subcommands.add("delete");
                    subcommands.add("setdefaultrank");
                }
                if (Permissions.hasPermission(sender, RANKS_REVOKE_NODE)) {
                    subcommands.add("revoke");
                }
                return StringUtil.copyPartialMatches(args[0], subcommands, new ArrayList<>());
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("grant")) {
                    if (Permissions.hasPermission(sender, RANKS_GRANT_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<player>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], Main.getOnlineUsernameList(), new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("revoke")) {
                    if (Permissions.hasPermission(sender, RANKS_REVOKE_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<player>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], Main.getOnlineUsernameList(), new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("setprefix")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("setsuffix")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("setdefaultrank")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("rename")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<name>"));
                        if (args[1].length() == 0) { return placeholder; }
                    }
                }
                if (args[0].equalsIgnoreCase("delete")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("view")) {
                    if (Permissions.hasPermission(sender, RANKS_VIEW_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<player>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], Main.getOnlineUsernameList(), new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("list")) {
                    if (Permissions.hasPermission(sender, RANKS_VIEW_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<page>"));
                        if (args[1].length() == 0) { return placeholder; }
                    }
                }
                if (args[0].equalsIgnoreCase("members")) {
                    if (Permissions.hasPermission(sender, RANKS_VIEW_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[1].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("setprefix")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<prefix>"));
                        if (args[2].length() == 0) { return placeholder; }
                    }
                }
                if (args[0].equalsIgnoreCase("setsuffix")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<suffix>"));
                        if (args[2].length() == 0) { return placeholder; }
                    }
                }
                if (args[0].equalsIgnoreCase("rename")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<name>"));
                        if (args[2].length() == 0) { return placeholder; }
                    }
                }
                if (args[0].equalsIgnoreCase("grant")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[2].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
                if (args[0].equalsIgnoreCase("revoke")) {
                    if (Permissions.hasPermission(sender, RANKS_MODIFY_NODE)) {
                        ArrayList<String> placeholder = new ArrayList<>(Collections.singletonList("<rank>"));
                        if (args[2].length() == 0) { return placeholder; }
                        return StringUtil.copyPartialMatches(args[1], allRanks, new ArrayList<>());
                    }
                }
            }
        }
        return new ArrayList<>();
    }

}
