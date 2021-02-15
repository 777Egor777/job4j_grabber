package ru.job4j.grabber;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.job4j.util.ConnectionUtil;
import ru.job4j.util.PostFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PsqlStoreTest {
    private final static String CFG_PATH = "app.properties";
    public Properties cfg = loadCfg();

    public Connection cn;
    public Store store;

    public Properties loadCfg() {
        Properties result = new Properties();
        try (InputStream in = PsqlStoreTest.class.getClassLoader().getResourceAsStream(CFG_PATH)) {
            result.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    @Before
    public void setUp() throws Exception {
        cn = ConnectionUtil.makeConnectionRollback(ConnectionUtil.getConnectionByCfg(cfg));
        store = new PsqlStore(cn);
    }

    @After
    public void tearDown() throws Exception {
        store.close();
    }

    @Test
    public void save() {
        Post post = null;
        try {
            post = PostFactory.makePost("https://www.sql.ru/forum/1333169/java-middle-remote-full-time-proekty-ot-2h-let");
        } catch (IOException e) {
            e.printStackTrace();
        }
        store.save(post);
        assertThat(store.getAll().get(0), is(post));
    }

    @Test
    public void findById() {
        Post post = null;
        try {
            post = PostFactory.makePost("https://www.sql.ru/forum/1333169/java-middle-remote-full-time-proekty-ot-2h-let");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int id = store.save(post);
        assertThat(store.findById("" + id), is(post));
    }
}