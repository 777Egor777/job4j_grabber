package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Интерфейс, связывающий все
 * компоненты программы.
 *
 * Для хранения данных используется
 * хранилище, реализующее
 * интерфейс Store.
 *
 * Для получения данных из источника
 * используется интерфейс Parse.
 *
 * Для периодизации работы
 * используется библиотека Quartz.
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 01.12.2020
 */
public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
