package ru.job4j.model;

import java.util.Calendar;

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
}
