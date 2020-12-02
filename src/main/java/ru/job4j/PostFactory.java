package ru.job4j;

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

    public String getVacancyHeader(Document doc) {
        Elements vacancyHeaderTable = doc.select(".messageHeader");
        Element head  = vacancyHeaderTable.get(1);
        return head.text();
    }

    public String getVacancyContent(Document doc) {
        Elements vacancyContentTable = doc.select(".msgBody");
        Element content = vacancyContentTable.get(1);
        return content.text();
    }

    public String getVacancyDateStr(Document doc) {
        Elements vacancyFooterTable = doc.select(".msgFooter");
        Element footer = vacancyFooterTable.get(0);
        String vacancyDateStr = footer.text();
        int indexOfOpenBracket = vacancyDateStr.indexOf('[');
        return vacancyDateStr.substring(0, indexOfOpenBracket);
    }

}
