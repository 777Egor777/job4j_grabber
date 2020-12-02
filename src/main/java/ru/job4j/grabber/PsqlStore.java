package ru.job4j.grabber;

import ru.job4j.SqlHelper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Класс реализует интерфейс
 * Store, и использует
 * базу данных PostgreSQL
 * в качестве хранилища
 * данных.
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 02.12.2020
 */
public class PsqlStore implements Store, AutoCloseable {
    /**
     * Базовое соединение.
     */
    private final Connection cn;

    /**
     * Адрес скрипта для создания
     * базы данных и таблицы.
     */
    private final static String CREATE_QUERY_FILE_PATH = "./db/create.sql";

    /**
     * Запрос на добавление записи
     * в базу данных.
     */
    private final static String INSERT_QUERY = "insert into grabber.post(name, text, link, created) values(?, ?, ?, ?);";

    /**
     * Запрос на получение всех
     * записей из базы.
     */
    private final static String SELECT_ALL_QUERY = "select * from grabber.post;";

    /**
     * Запрос на получение
     * записи по id.
     */
    private final static String FIND_BY_ID_QUERY = "select * from grabber.post where id=?;";

    /**
     * Путь конфигурационного файла,
     * который содержит данные
     * для подключения к базе
     * данных через JDBC.
     */
    private final static String CFG_FILE_NAME = "app.properties";

    /**
     * Конструктор
     * @param cfg - настройки для подключения
     *              к БД
     */
    public PsqlStore(Properties cfg) {
        try {
            cn = getConnection(cfg);
            createSchemaAndTable();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Создаём sql-схему и таблицу
     * в ней, если они ещё не
     * созданы.
     */
    private void createSchemaAndTable() {
        SqlHelper.executeFile(cn, CREATE_QUERY_FILE_PATH);
    }

    /**
     * Метод создаёт соединение
     * @param cfg - объект с найстроками
     *              подключения к БД
     *              через JDBC
     * @return созданное соединение
     * @throws SQLException
     */
    private Connection getConnection(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        return DriverManager.getConnection(
                cfg.getProperty("jdbc.url"),
                cfg.getProperty("jdbc.username"),
                cfg.getProperty("jdbc.password")
        );
    }

    /**
     * Класс реализует интерфейс
     * Autocloseable.
     * Поэтому при вызове метода
     * close, соединение которое
     * использует метод закрывается,
     * и ресурсы освобождаются.
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        cn.close();
    }

    /**
     * Метод добавляет запись
     * в БД с данными из
     * объекта post
     * @param post - объект, данные из которого
     *               добавляются в БД.
     */
    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cn.prepareStatement(INSERT_QUERY)) {
            ps.setString(1, post.getVacancyHeader());
            ps.setString(2, post.getVacancyContent());
            ps.setString(3, post.getVacancyLink());
            ps.setTimestamp(4, new Timestamp(post.getVacancyDate().getTimeInMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Метод загружает все записи
     * из БД, конвертирует их в
     * объекты типа <code>Post</>
     * и возвращает список
     * этих объектов.
     * @return список полученных
     *         объектов по записям из БД
     */
    @Override
    public List<Post> getAll() {
        List<Post> result = new LinkedList<>();
        try (PreparedStatement ps = cn.prepareStatement(SELECT_ALL_QUERY)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(rs.getTimestamp("created").getTime());
                result.add(new Post(
                        rs.getString("name"),
                        rs.getString("text"),
                        rs.getString("link"),
                        cal
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    /**
     * Метод ищет запись в БД,
     * которая имеет идентификатор
     * <code>id</>, получает
     * её и конвертирует в объект
     * типа <code>Post</code>,
     * а затем возвращает его
     * @param id - идентификатор искомой
     *             записи
     * @return объект типа <code>Post</code>,
     *         в который сконвертировалась
     *         найденная запись
     */
    @Override
    public Post findById(String id) {
        Post result = null;
        try (PreparedStatement ps = cn.prepareStatement(FIND_BY_ID_QUERY)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(rs.getTimestamp("created").getTime());
                result = new Post(
                        rs.getString("name"),
                        rs.getString("text"),
                        rs.getString("link"),
                        cal
                );
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    /**
     * Метод считывает файл
     * настроек.
     * @return объект настроек типа
     *         <code>Properties</code>
     */
    private static Properties cfgLoad() {
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream(CFG_FILE_NAME)) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfg;
    }

    /**
     * Демонстрация работы класса
     * @param args
     */
    public static void main(String[] args) {
        Properties cfg = cfgLoad();
        Store store = new PsqlStore(cfg);
        Post post = new Post(
                "Header",
                "Content",
                "http://job4j.ru",
                Calendar.getInstance()
        );
        store.save(post);
        List<Post> posts = store.getAll();
        System.out.println(posts.get(0));
    }
}
