package ru.job4j.grabber;

import ru.job4j.SqlHelper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cn;
    private final static String CREATE_QUERY_FILE_PATH = "./db/create.sql";
    private final static String INSERT_QUERY = "insert into grabber.post(name, text, link, created) values(?, ?, ?, ?);";
    private final static String SELECT_ALL_QUERY = "select * from grabber.post;";
    private final static String FIND_BY_ID_QUERY = "select * from grabber.post where id=?;";

    public PsqlStore(Properties cfg) {
        try {
            cn = getConnection(cfg);
            createSchemaAndTable();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void createSchemaAndTable() {
        SqlHelper.executeFile(cn, CREATE_QUERY_FILE_PATH);
    }

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

    @Override
    public void close() throws Exception {
        cn.close();
    }

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

    public static void main(String[] args) {
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
