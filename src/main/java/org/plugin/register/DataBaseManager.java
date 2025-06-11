package org.plugin.register;

import java.sql.*;

public class DataBaseManager {
    private static Connection connection;

    public static void connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/users";
        String user = "test";
        String password = "test";

        connection = DriverManager.getConnection(url, user, password);
    }

    public static boolean isInDataBase(String uuid) {
        String sql = "SELECT COUNT(*) FROM userdata WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                int count = res.getInt(1);
                return count != 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public static void addPlayerToDataBase(String uuid, String password) {
        String sql = "INSERT INTO userdata (id, password_hash) VALUES (?,?)";
        String hash = PasswordUtil.hashPassword(password);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, hash);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getPlayerHashPassword(String uuid) {
        String passwordHash = null;
        String sql = "SELECT password_hash FROM userdata WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                passwordHash = res.getString("password_hash");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return passwordHash;
    }
}
