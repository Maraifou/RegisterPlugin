package org.plugin.register;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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

        startTimer(player); // kick player if he does not log in for 5min

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            DataBaseManager.connect();
            if (!(isInDataBase(uuid))) {
                player.sendMessage("Зарегистрируйтесь! /reg [ваш пароль] [повтор пароля]");
            } else {
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
            if (event.getFrom().getX() != event.getTo().getX()
                    || event.getFrom().getZ() != event.getTo().getZ()
                    || event.getFrom().getBlockY() != event.getTo().getBlockY()) {
                event.setTo(event.getFrom());
                if (!(isInDataBase(uuid))) {
                    player.sendMessage("Зарегистрируйтесь! /reg [ваш пароль] [повтор пароля]");
                } else {
                    player.sendMessage("Авторизируйтесь! /log [пароль]");
                }
            }

        }


    }

    @EventHandler
    public void onPlayerExitServer(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        editStatus(uuid, false);
    }

    private void startTimer(Player player) {
        new BukkitRunnable() {
            final String kickMessage = "Вы не прошли авторизацию";
            final String youNeedToLogInStr = "Авторизируйтесь!";
            final String youNeedToRegisterCommandStr = "/reg [пароль] [повтор пароля]";
            int timeLeft = 120; // 2 min

            @Override
            public void run() {
                if (UserMetaDataManager.isLogIn(player.getUniqueId().toString())) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    cancel();
                    return;
                }

                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (timeLeft > 0) {
                    timeLeft--;

                    //Text on the screen
                    player.sendTitle(ChatColor.DARK_RED + youNeedToLogInStr,
                            ChatColor.DARK_GREEN + youNeedToRegisterCommandStr,
                            0, 100, 0);

                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.BLINDNESS,
                            100, // Duration
                            255, // Power
                            false, // Particulars
                            false, // Icons
                            true // Rewritable
                    ));
                } else {
                    player.kick(Component.text(kickMessage));
                    cancel();
                }

            }
        }.runTaskTimer(Register.getInstance(), 0L, 20);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        // if player is log in return from the method
        if (UserMetaDataManager.isLogIn(player.getUniqueId().toString()))
            return;

        String message = event.getMessage(); // get player command
        String uuid = player.getUniqueId().toString();

        // Player can use only /log command
        if (!message.toLowerCase().startsWith("/log")) {
            event.setCancelled(true);
            if (!(isInDataBase(uuid))) {
                player.sendMessage("Зарегистрируйтесь! /reg [ваш пароль] [повтор пароля]");
            } else {
                player.sendMessage("Авторизируйтесь! /log [пароль]");
            }
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // if player is log in return from the method
        if (UserMetaDataManager.isLogIn(player.getUniqueId().toString()))
            return;
        event.setCancelled(true);
    }
}
