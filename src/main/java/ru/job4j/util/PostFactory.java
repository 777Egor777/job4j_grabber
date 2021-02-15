package ru.job4j.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.date.SqlRuDateFormatter;
import ru.job4j.grabber.Post;

import java.io.IOException;

/**
 * Вспомогательный класс, реализующий
 * парсинг одного поста.
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 01.12.2020
 */
public class PostFactory {
    /**
     * Метод парсит пост по ссылке.
     * Вызывает вспомогательные методы
     * для парсинга частей поста.
     * В парсинге используется Jsoup.
     *
     * @param url - ссылка на страницу
     *              вакансии
     * @return готовый объект вакансии
     * @throws IOException - возможное исключение
     *                       при парсинге
     */
    public static Post makePost(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        PostFactory factory = new PostFactory();
        SqlRuDateFormatter dateFormatter = new SqlRuDateFormatter();
        dateFormatter.format(factory.getVacancyDateStr(doc));
        return new Post(factory.getVacancyHeader(doc),
                             factory.getVacancyContent(doc),
                             url,
                             dateFormatter.getDate());
    }

    /**
     * Метод для парсинга
     * заголовка вакансии
     * @param doc - объект Jsoup,
     *              содержащий элементы
     *              контента html-
     *              страницы
     * @return заголовок вакансии
     */
    public String getVacancyHeader(Document doc) {
        Elements vacancyHeaderTable = doc.select(".messageHeader");
        Element head  = vacancyHeaderTable.get(0);
        return head.text();
    }

    /**
     * Метод для парсинга
     * описания вакансии
     * @param doc - объект Jsoup,
     *              содержащий элементы
     *              контента html-
     *              страницы
     * @return описание вакансии
     */
    public String getVacancyContent(Document doc) {
        Elements vacancyContentTable = doc.select(".msgBody");
        Element content = vacancyContentTable.get(1);
        return content.text();
    }

    /**
     * Метод для парсинга
     * даты размещения вакансии
     * @param doc - объект Jsoup,
     *              содержащий элементы
     *              контента html-
     *              страницы
     * @return дата размещения вакансии,
     *         в виде неформатированной
     *         строки
     */
    public String getVacancyDateStr(Document doc) {
        Elements vacancyFooterTable = doc.select(".msgFooter");
        Element footer = vacancyFooterTable.get(0);
        String vacancyDateStr = footer.text();
        int indexOfOpenBracket = vacancyDateStr.indexOf('[');
        return vacancyDateStr.substring(0, indexOfOpenBracket).trim();
    }

}
