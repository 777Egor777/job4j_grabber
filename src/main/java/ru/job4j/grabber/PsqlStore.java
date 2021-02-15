package ru.job4j.grabber;

import ru.job4j.util.SqlHelper;

import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

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

    public PsqlStore(Connection cn) {
        this.cn = cn;
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
     * @return
     */
    @Override
    public int save(Post post) {
        int result = -1;
        try (PreparedStatement ps = cn.prepareStatement(INSERT_QUERY,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getVacancyHeader());
            ps.setString(2, post.getVacancyContent());
            ps.setString(3, post.getVacancyLink());
            ps.setTimestamp(4, new Timestamp(post.getVacancyDate().getTimeInMillis()));
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    result = gk.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
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
}
