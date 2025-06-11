package org.plugin.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.UUID;

import static org.plugin.register.DataBaseManager.*;
import static org.plugin.register.UserMetaDataManager.editStatus;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        LoginAttempts.resetAttemptsMeta(player);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            DataBaseManager.connect();
            if (!(isInDataBase(uuid))) {
                player.sendMessage("Зарегистрируйтесь! /reg [ваш пароль] [повтор пароля]");
            }
            else {
                player.sendMessage("Авторизируйтесь! /log [пароль]");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    @EventHandler
    public void blockMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!(UserMetaDataManager.isLogIn(uuid))) {
            // Cancel position change
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());

                player.sendMessage("Вы должны авторизироваться!");
            }


        }

    }
    @EventHandler
    public void onPlayerExitServer(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        editStatus(uuid, false);
    }

}
