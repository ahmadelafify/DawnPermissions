package me.ae.dawn.Resources;

import me.ae.dawn.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class Language {

    private static YamlConfiguration langFile = null;

    public static YamlConfiguration getLanguage() {
        if (langFile == null) {
            InputStream langStream = Main.getPlugin().getResource("assets/lang/en_US.yml");
            if (langStream != null) {
                langFile = YamlConfiguration.loadConfiguration(new InputStreamReader(langStream));
            }
        }
        return langFile;
    }
    
    public static String getChatString(String key) {
        YamlConfiguration lang = getLanguage();
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(lang.getString(key)));
    }
    
}
