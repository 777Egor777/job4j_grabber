package ru.job4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Вспомогательный класс.
 *
 * Служит для выполнения всех запросов,
 * содержащихся в каком-то файле
 * в формате по 1 строчке на запрос.
 *
 * Также для выполнения одного
 * единичного запроса.
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 02.12.2020
 */
public class SqlHelper {
    public static void execute(Connection cn, String query) {
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void executeFile(Connection cn, String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.lines().forEach(line -> execute(cn, line));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
