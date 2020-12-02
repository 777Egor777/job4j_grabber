package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.PostFactory;
import ru.job4j.grabber.Post;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SqlRuParse implements Parse {
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

    @Override
    public Post detail(String link) throws IOException {
        return PostFactory.makePost(link);
    }
}
