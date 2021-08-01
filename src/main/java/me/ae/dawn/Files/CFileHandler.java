package me.ae.dawn.Files;

import me.ae.dawn.Main;
import java.io.*;

public class CFileHandler {

    public static void initialize() throws IOException {
        if(!Main.getPlugin().getDataFolder().exists()) { Main.getPlugin().getDataFolder().mkdirs(); }
        File chatFormat = new File(Main.getPlugin().getDataFolder() + File.separator + "chat_format.txt");
        if (!chatFormat.exists()) {
            if (chatFormat.createNewFile()) {
                FileWriter fw = new FileWriter(chatFormat.getPath());
                fw.write("{prefix}{playerName}{suffix}&f: {chatMessage}");
                fw.flush();
                fw.close();
            }
        }
    }

    public static String getFileContents(String path) throws IOException {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder data = new StringBuilder();
        String x;
        while ((x = br.readLine()) != null){
            data.append(x);
        }
        fr.close();
        br.close();
        return data.toString();
    }

}
