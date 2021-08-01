package me.ae.dawn.DB;

import me.ae.dawn.Main;

import java.io.File;
import java.sql.*;

public class Database {

    protected static Connection con = null;

    public static Connection getConnection() throws SQLException {
        if (con == null) {
            String dbPath = "jdbc:sqlite:" + Main.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "database.db";
            con = DriverManager.getConnection(dbPath);
        }
        return con;
    }

    // NOTE: Setup for connection variable and tables (if not existing)
    public static void initialise() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        if(!Main.getPlugin().getDataFolder().exists()) {
            Main.getPlugin().getDataFolder().mkdirs();
        }
        Statement statement = getConnection().createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS ranks_data (rankUUID STRING, name STRING, prefix STRING, suffix STRING, power INTEGER, permissions STRING, isDefault INTEGER, parentUUID STRING)");
        statement.execute("CREATE TABLE IF NOT EXISTS player_data (playerUUID string, ranks string, userPerms string)");
        RankDatabase.initialise();

    }

}
