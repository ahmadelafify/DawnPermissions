package me.ae.dawn.Chat;

import me.ae.dawn.DB.Rank;
import me.ae.dawn.Files.CFileHandler;
import me.ae.dawn.Main;
import me.ae.dawn.Permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;

public class Prefixes implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Rank playerRank;
        try { playerRank = Permissions.getPlayerStrongestRank(player); } catch (Exception ex) { ex.printStackTrace(); e.setCancelled(true); return;}
        if (playerRank != null) {
            String message = null;
            try {
                message = CFileHandler.getFileContents(Main.getPlugin().getDataFolder() + File.separator + "chat_format.txt");
            } catch (Exception exc) { exc.printStackTrace(); }
            if (message != null){
                message = message.replace("{prefix}", playerRank.getPrefix());
                message = message.replace("{playerName}", player.getDisplayName());
                message = message.replace("{suffix}", playerRank.getSuffix());
                message = message.replace("{chatMessage}", e.getMessage());
                e.setFormat(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

}