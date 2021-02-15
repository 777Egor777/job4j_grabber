package ru.job4j.date;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс отвечает за форматирование
 * даты при парсинге с сайта Sql.ru.
 *
 * Дата имеет следующий формат:
 * 2 дек 19, 22:29
 * или
 * сегодня, 02:30
 * или
 * вчера, 12:06.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 09.02.2021
 */
public class SqlRuDateFormatter {
    private final static String TODAY = "сегодня";
    private final static String YESTERDAY = "вчера";
    private final static int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;
    private final static Map<String, Integer> MONTH_MAP = new HashMap<>();
    private final static int YEAR_ADDITION = 2000;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private Calendar date;

    static {
        MONTH_MAP.put("янв", Calendar.JANUARY);
        MONTH_MAP.put("фев", Calendar.FEBRUARY);
        MONTH_MAP.put("мар", Calendar.MARCH);
        MONTH_MAP.put("апр", Calendar.APRIL);
        MONTH_MAP.put("май", Calendar.MAY);
        MONTH_MAP.put("июн", Calendar.JUNE);
        MONTH_MAP.put("июл", Calendar.JULY);
        MONTH_MAP.put("авг", Calendar.AUGUST);
        MONTH_MAP.put("сен", Calendar.SEPTEMBER);
        MONTH_MAP.put("окт", Calendar.OCTOBER);
        MONTH_MAP.put("ноя", Calendar.NOVEMBER);
        MONTH_MAP.put("дек", Calendar.DECEMBER);
    }

    /**
     * Результирующий метод.
     * Получает строку, содержащую дату
     * в определённом входном формате
     * (например "2 дек 19, 22:29"),
     * и возвращает строку с датой
     * в нужном формате
     * (например "2.12.19 22:29")
     *
     * Внутри метод использует то,
     * что дата и время во входной строке
     * всегда разделены запятой.
     * Соответственно метод разделяет
     * строку на две, затем парсит
     * по отдельности дату и время,
     * и затем объединяет результат.
     *
     * @param inDateStr - входная строка
     *                  с датой
     * @return Результирующая строка с датой
     *         в нужном формате.
     */
    public String format(String inDateStr) {
        String[] parts = inDateStr.split(",");
        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();
        Calendar date = Calendar.getInstance();
        parseDate(date, parts[0]);
        parseTime(date, parts[1]);
        date.set(year, month, day, hour, min);
        return format(date);
    }

    /**
     * Метод получает объект
     * типа {@code Calendar}
     * и возвращает его
     * корректное строковое
     * представление в нужном формате:
     * "dd.MM.yyyy kk:mm"
     *
     * @param date - объект даты
     * @return Корректное строковое
     *         представление в нужном
     *         формате
     */
    public String format(Calendar date) {
        this.date = date;
        return new SimpleDateFormat("dd.MM.yyyy kk:mm").format(date.getTime());
    }

    /**
     * Метод устанавливает
     * значения для переменных
     * year, month и day
     * объекта данного класса.
     *
     * То есть, вычисляет и
     * устанавливает параметры даты.
     *
     * @param date - входной параметр даты.
     *               Был получен с помощью
     *               Calendar.getInstance(),
     *               то есть это фактически
     *               дата на данный момент
     * @param inDateStr - входная часть строки
     *                    парсинга, содержащая
     *                    дату
     */
    private void parseDate(Calendar date, String inDateStr) {
        if (inDateStr.equals(TODAY) || inDateStr.equals(YESTERDAY)) {
            if (inDateStr.equals(YESTERDAY)) {
                date.setTimeInMillis(date.getTimeInMillis() - MILLIS_IN_DAY);
            }
            year = date.get(Calendar.YEAR);
            month = date.get(Calendar.MONTH);
            day = date.get(Calendar.DATE);
        } else {
            String[] parts = inDateStr.split(" ");
            day =  Integer.parseInt(parts[0]);
            month =  MONTH_MAP.get(parts[1]);
            year =  Integer.parseInt(parts[2]) + YEAR_ADDITION;
        }
    }

    /**
     * Метод устанавливает
     * значения для переменных
     * hour и min
     * объекта данного класса.
     *
     * То есть, вычисляет и
     * устанавливает параметры времени.
     *
     * @param date - входной параметр даты.
     *               Был получен с помощью
     *               Calendar.getInstance(),
     *               то есть это фактически
     *               дата на данный момент
     * @param inDateStr - входная часть строки
     *                    парсинга, содержащая
     *                    время
     */
    private void parseTime(Calendar date, String inDateStr) {
        String[] parts = inDateStr.split(":");
        hour =  Integer.parseInt(parts[0]);
        min =  Integer.parseInt(parts[1]);
    }

    /**
     * Геттер для результирующей
     * даты.
     * @return {@code date} - поле
     */
    public Calendar getDate() {
        return date;
    }
}
