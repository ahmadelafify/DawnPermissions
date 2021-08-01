package me.ae.dawn.Utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Utility {

    public static ArrayList<String> parseStringToList(String s) {
        if (!(s.startsWith("[") && s.endsWith("]"))){
            return new ArrayList<>();
        }
        String trimmed = s.substring(1, s.length() - 1);
        return new ArrayList<>(Arrays.asList(StringUtils.split(trimmed, ", ")));
    }

    public static String getValueOrNull(String[] list, int i) {
        try {
            return list[i];
        } catch (Exception e) {
            return null;
        }
    }

//    Add this to a function to your Object/Class to get resource files
//    public InputStream getResource(String filename) {
//        try {
//            URL url = this.getClass().getClassLoader().getResource(filename);
//            if (url == null) {
//                return null;
//            } else {
//                URLConnection connection = url.openConnection();
//                connection.setUseCaches(false);
//                return connection.getInputStream();
//            }
//        } catch (IOException var4) {
//            return null;
//        }
//    }
    public static UUID getPlayerUUID_A(String playerName) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
        return op.getUniqueId();
    }
    public static UUID getPlayerUUID(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
            if (!op.hasPlayedBefore()) { return null; }
            return op.getUniqueId();
        }
        return player.getUniqueId();
    }

    public static ArrayList<String> getOnlinePlayerList(Server server) {
        ArrayList<String> onlineUserNameList = Lists.newArrayList();
        server.getOnlinePlayers().forEach(e -> onlineUserNameList.add(e.getName()));
        return onlineUserNameList;
    }

    public static ArrayList<?> subList(ArrayList<?> array, int startIndex, int travelAmount) {
        ArrayList<Object> returnList = new ArrayList<>();
        for(int i = startIndex; i < startIndex+travelAmount; i++) {
            try { returnList.add(array.get(i)); } catch (IndexOutOfBoundsException e) { break; }
        }
        return returnList;
    }

    public static ArrayList<?> paginate(ArrayList<?> array, int page, int viewCount) {
        if (!array.isEmpty()) {
            int fromIndex = (page * viewCount) - viewCount;
            return Utility.subList(array, fromIndex, viewCount);
        }
        return new ArrayList<>();
    }
}
