package ru.job4j.grabber;

import ru.job4j.grabber.Post;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для парсинга постов.
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 01.12.2020
 */
public interface Parse {
    /**
     * Метод для парсинга всех
     * постов с определённой
     * страницы сайта.
     *
     * @param link - ссылка на страницу сайта
     * @return - список всех постов
     *           со страницы
     * @throws IOException - исключение, которое
     *                       может выброситься
     *                       при парсинге
     */
    List<Post> list(String link) throws IOException;

    /**
     * Метод для парсинга одного
     * поста со страницы, на
     * которой он находится
     * @param link - ссылка на страницу,
     *               на которой находится
     *               пост
     * @return - результирующий пост
     * @throws IOException - исключение, которое
     *                       может возникнуть при парсинге
     */
    Post detail(String link) throws IOException;
}
