package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.util.PostFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс для парсинга постов
 * с сайта Sql.ru
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 01.12.2020
 */
public class SqlRuParse implements Parse {
    /**
     * Метод для парсинга всех
     * постов с определённой
     * страницы сайта.
     *
     * Парсинг происходит
     * при помощи Jsoup.
     *
     * @param link - ссылка на страницу сайта
     * @return - список всех постов
     *           со страницы
     * @throws IOException - исключение, которое
     *                       может выброситься
     *                       при парсинге
     */
    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> result = new LinkedList<>();
        Document doc = Jsoup.connect(link).get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            String postLink = href.attr("href");
            result.add(detail(postLink));
        }
        return result;
    }

    /**
     * Метод для парсинга одного
     * поста со страницы, на
     * которой он находится.
     *
     * Фактически метод использует
     * утилитный класс {@code PostFactory},
     * передаёт всю работу ему.
     * @param link - ссылка на страницу,
     *               на которой находится
     *               пост
     * @return - результирующий пост
     * @throws IOException - исключение, которое
     *                       может возникнуть при парсинге
     */
    @Override
    public Post detail(String link) throws IOException {
        return PostFactory.makePost(link);
    }

    public static void main(String[] args) throws IOException {
        Parse parse = new SqlRuParse();
        String link = "https://www.sql.ru/forum/job-offers/1";
        List<Post> list = parse.list(link);
        list.forEach(System.out::println);
    }
}
