package ru.job4j.grabber;

import java.util.List;

/**
 * Интерфейс для работы
 * с хранилищем данных
 * (память, БД, ...)
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 01.12.2020
 */
public interface Store extends AutoCloseable {
    /**
     * Метод для сохранения
     * поста в хранилище
     * @param post - пост, который
     *               сохраняем
     * @return идентификатор
     *         сохранённого поста
     */
    int save(Post post);

    /**
     * Метод возвращает список
     * всех постов, которые
     * находятся в хранилище
     * на данный момент
     * @return - список всех постов
     */
    List<Post> getAll();

    /**
     * Метод вытаскивает из хранилища
     * пост с определённым id
     * @param id - id того поста,
     *             который нужно извлечь
     *             из хранилища
     * @return - пост с заданным id
     */
    Post findById(String id);
}
