package org.plugin.register;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

import static org.plugin.register.DataBaseManager.connect;
import static org.plugin.register.PasswordUtil.checkPassword;
import static org.plugin.register.PasswordUtil.hashPassword;

public final class Register extends JavaPlugin {

    private static Register instance;


    @Override
    public void onEnable() {

        instance = this;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getCommand("reg").setExecutor(new RegisterCommand());
        getCommand("log").setExecutor(new LogInCommand());





    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Register getInstance(){
        return instance;
    }

}
