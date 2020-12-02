package ru.job4j.grabber;

import java.util.List;
import ru.job4j.grabber.Post;

/**
 * Интерфейс для работы
 * с хранилищем данных
 * (память, БД, ...)
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 01.12.2020
 */
public interface Store {
    void save(Post post);
    List<Post> getAll();
    Post findById(String id);
}
