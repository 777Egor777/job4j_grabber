package ru.job4j.date;

import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlRuDateFormatterTest {

    @Test
    public void whenEarlierThanYesterday() {
        SqlRuDateFormatter formatter = new SqlRuDateFormatter();
        String result = formatter.format("25 июн 18, 21:56");
        String expected = "25.06.2018 21:56";
        assertThat(result, is(expected));
    }

    @Test
    public void whenToday() {
        SqlRuDateFormatter formatter = new SqlRuDateFormatter();
        String result = formatter.format("сегодня, 02:30");
        Calendar date = Calendar.getInstance();
        String expected = String.format("%02d.%02d.%4d 02:30", date.get(Calendar.DATE),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.YEAR));
        assertThat(result, is(expected));
    }
}