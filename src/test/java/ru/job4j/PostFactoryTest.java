package ru.job4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import ru.job4j.grabber.Post;
import ru.job4j.util.PostFactory;

import java.io.IOException;
import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PostFactoryTest {
    public final String url = "https://www.sql.ru/forum/1333169/java-middle-remote-full-time-proekty-ot-2h-let";

    @Test
    public void makePost() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PostFactory factory = new PostFactory();
        Calendar date = Calendar.getInstance();
        date.set(2021, Calendar.FEBRUARY, 5, 12, 29);
        Post expected = new Post(
                "Java middle.(Remote, full-time). Проекты от 2х лет. [new]",
                "Мы небольшой интегратор, ищем Java Middle+разработчиков в команду на долгосрочные проекты с крупной финансовой организацией. Работа удаленная на полный рабочий день."
                        + " Проекты связаны с разработкой платформы, имеется небольшой ML. Высокий Transaction per second, терабайты данных итд"
                        + " Оплата: до 270 000 мес"
                        + " Оформление: ГПХ/ИП"
                        + " Для выполнения задач необходим стандартный набор:"
                        + " Опыт разработки Java от 3-х лет;"
                        + " Знание Java Сore, Сollections, Multithreading итд"
                        + " Опыт использования ORM, оптимизация запросов."
                        + " Опыт работы с распределенными вычислениями и распределенными комитами."
                        + " Понимание работы транзакционных систем/финансовых систем."
                        + " Зание Git или SVN"
                        + " Зание Spring"
                        + " Использование Maven"
                        + " Будет плюсом:"
                        + " Если использовали Oracle, PostgreSQL"
                        + " Понимаете принцип работы Kafka или RabbitMQ"
                        + " Пробовали Docker"
                        + " Локация вашего размещения, не важна"
                        + " Высокая ставка",
                    url,
                    date
                );
        try {
            assertThat(PostFactory.makePost(url), is(expected));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getVacancyHeader() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PostFactory factory = new PostFactory();
        String result = factory.getVacancyHeader(doc);
        String expected = "Java middle.(Remote, full-time). Проекты от 2х лет. [new]";
        assertThat(result, is(expected));
    }

    @Test
    public void getVacancyDateStr() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PostFactory factory = new PostFactory();
        String result = factory.getVacancyDateStr(doc);
        String expected = "5 фев 21, 12:29";
        assertThat(result, is(expected));
    }

    @Test
    public void getVacancyContent() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PostFactory factory = new PostFactory();
        String result = factory.getVacancyContent(doc);
        String expected = "Мы небольшой интегратор, ищем Java Middle+разработчиков в команду на долгосрочные проекты с крупной финансовой организацией. Работа удаленная на полный рабочий день."
                + " Проекты связаны с разработкой платформы, имеется небольшой ML. Высокий Transaction per second, терабайты данных итд"
                + " Оплата: до 270 000 мес"
                + " Оформление: ГПХ/ИП"
                + " Для выполнения задач необходим стандартный набор:"
                + " Опыт разработки Java от 3-х лет;"
                + " Знание Java Сore, Сollections, Multithreading итд"
                + " Опыт использования ORM, оптимизация запросов."
                + " Опыт работы с распределенными вычислениями и распределенными комитами."
                + " Понимание работы транзакционных систем/финансовых систем."
                + " Зание Git или SVN"
                + " Зание Spring"
                + " Использование Maven"
                + " Будет плюсом:"
                + " Если использовали Oracle, PostgreSQL"
                + " Понимаете принцип работы Kafka или RabbitMQ"
                + " Пробовали Docker"
                + " Локация вашего размещения, не важна"
                + " Высокая ставка";
        assertThat(result, is(expected));
    }
}