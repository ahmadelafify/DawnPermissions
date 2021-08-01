package me.ae.dawn;

import me.ae.dawn.Chat.Prefixes;
import me.ae.dawn.DB.Database;
import me.ae.dawn.Files.CFileHandler;
import me.ae.dawn.Permissions.PermissionsListener;
import me.ae.dawn.Permissions.PermsCommand;
import me.ae.dawn.Permissions.RanksCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin {

    private static Main plugin;
    public static Main getPlugin() { return plugin; }
    public static PluginManager getPluginManager() { return Bukkit.getPluginManager(); }
    public static ArrayList<String> getOnlineUsernameList() {
        ArrayList<String> usernames = new ArrayList<>();
        Main.getPlugin().getServer().getOnlinePlayers().forEach(e -> usernames.add(e.getName()));
        return usernames;
    }

    @Override
    public void onEnable() {
        plugin = this;

        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        try {
            CFileHandler.initialize(); // Initializing essential files
            Database.initialise(); // Initializing database.
        } catch (Exception e) {
            e.printStackTrace(); // Print error to terminal.
            getPluginManager().disablePlugin(plugin); // Disable Plugin
        }
        plugin.getCommand("perms").setExecutor(new PermsCommand());
        plugin.getCommand("ranks").setExecutor(new RanksCommand());
        if (getConfig().getBoolean("Enable Chat Formatting")) {
            getPluginManager().registerEvents(new Prefixes(), plugin);
        }
        getPluginManager().registerEvents(new PermissionsListener(), plugin);
    }

    @Override
    public void onDisable() {
    }

}
