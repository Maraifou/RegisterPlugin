package org.plugin.register;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.plugin.register.PasswordUtil.checkPassword;
import static org.plugin.register.PasswordUtil.hashPassword;
import static org.plugin.register.UserMetaDataManager.editStatus;

public class LogInCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            Bukkit.getServer().sendMessage(Component.text("Only players can use this command"));
            return true;
        }

        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();

        if (UserMetaDataManager.isLogIn(uuid)) {
            player.sendMessage("Вы уже авторизовалсь!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("/log [пароль]");
            return true;
        }

        String password = args[0];
        String hash = DataBaseManager.getPlayerHashPassword(uuid);
        Bukkit.getLogger().info("Data get from DataBase");

        if (checkPassword(password, hash)) { // if password is correct
            editStatus(uuid, true);
            player.sendMessage("Вы успешно авторизировались!");
            LoginAttempts.resetAttemptsMeta(player);
            return true;
        }

        LoginAttempts.decreaseAttempts(player); // if password is incorrect
        player.sendMessage("Неверный пароль. У вас осталось " + LoginAttempts.getAttempts(player) + " попыток!");


        if (LoginAttempts.getAttempts(player) <= 0) // if 3 times incorrect password
            player.kick(Component.text("Вы ввели неверный пароль 3 раза!"));

        return true;
    }
}