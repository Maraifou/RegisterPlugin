package org.plugin.register;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegisterCommand implements CommandExecutor {


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

        if (DataBaseManager.isInDataBase(uuid)) {
            player.sendMessage("Игрок с таким именем уже зарегистрирован!");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("/reg [пароль] [повтор пароля]");
            return true;
        }

        if (!(args[0].equals(args[1]))) {
            player.sendMessage("Пароли не совпадают!");
            return true;
        }

        final String pattern = "^(?=(.*\\d)){3,} (?=.*[A-Z]) [A-Za-z0-9\\W_] {8,20}$";
                                //ищет цифры   ищет заглав.букву разерешю люб. см от 8 до 20
//                              не менее 3     не менее 1

        if (!(args[0].matches(pattern))){
            player.sendMessage("Пароль должен быть от 8 до 20 символов " +
                    "и содержать хотя бы 3 цифры и одну заглавную букву!");
            return true;
        }

        DataBaseManager.addPlayerToDataBase(uuid, args[0]);
        Bukkit.getLogger().info("Data send in DataBase");

        player.sendMessage("Вы успешно зарегистрировались!");
        player.sendMessage("Авторизируйтесь! /log [ваш пароль!]");
        return true;
    }
}
