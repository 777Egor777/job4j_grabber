package ru.job4j.grabber;

import ru.job4j.date.SqlRuDateFormatter;

import java.util.Calendar;
import java.util.Objects;

/**
 * Data model.
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 01.12.2020
 */
public class Post {
    private String vacancyHeader;
    private String vacancyContent;
    private String vacancyLink;
    private Calendar vacancyDate;

    public Post(String vacancyHeader, String vacancyContent, String vacancyLink, Calendar vacancyDate) {
        this.vacancyHeader = vacancyHeader;
        this.vacancyContent = vacancyContent;
        this.vacancyLink = vacancyLink;
        this.vacancyDate = vacancyDate;
    }

    public String getVacancyHeader() {
        return vacancyHeader;
    }

    public String getVacancyContent() {
        return vacancyContent;
    }

    public String getVacancyLink() {
        return vacancyLink;
    }

    public Calendar getVacancyDate() {
        return vacancyDate;
    }

    public void setVacancyHeader(String vacancyHeader) {
        this.vacancyHeader = vacancyHeader;
    }

    public void setVacancyContent(String vacancyContent) {
        this.vacancyContent = vacancyContent;
    }

    public void setVacancyLink(String vacancyLink) {
        this.vacancyLink = vacancyLink;
    }

    public void setVacancyDate(Calendar vacancyDate) {
        this.vacancyDate = vacancyDate;
    }

    @Override
    public String toString() {
        return String.format("Post{\n%s\n%s\n%s\n%s\n}",
                vacancyHeader,
                vacancyContent,
                vacancyLink,
                new SqlRuDateFormatter().format(vacancyDate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post post = (Post) o;

        if (!Objects.equals(vacancyHeader, post.vacancyHeader)) {
            return false;
        }
        if (!Objects.equals(vacancyContent, post.vacancyContent)) {
            return false;
        }
        if (!Objects.equals(vacancyLink, post.vacancyLink)) {
            return false;
        }
        return vacancyDate.get(Calendar.YEAR) == post.vacancyDate.get(Calendar.YEAR)
                && vacancyDate.get(Calendar.MONTH) == post.vacancyDate.get(Calendar.MONTH)
                && vacancyDate.get(Calendar.DATE) == post.vacancyDate.get(Calendar.DATE)
                && vacancyDate.get(Calendar.HOUR) == post.vacancyDate.get(Calendar.HOUR)
                && vacancyDate.get(Calendar.MINUTE) == post.vacancyDate.get(Calendar.MINUTE);
    }

    @Override
    public int hashCode() {
        int result = vacancyHeader != null ? vacancyHeader.hashCode() : 0;
        result = 31 * result + (vacancyContent != null ? vacancyContent.hashCode() : 0);
        result = 31 * result + (vacancyLink != null ? vacancyLink.hashCode() : 0);
        result = 31 * result + (vacancyDate != null ? vacancyDate.hashCode() : 0);
        return result;
    }
}
