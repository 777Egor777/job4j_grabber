[![Build Status](https://travis-ci.org/777Egor777/job4j_grabber.svg?branch=master)](https://travis-ci.org/777Egor777/job4j_grabber)
[![codecov](https://codecov.io/gh/777Egor777/job4j_grabber/branch/master/graph/badge.svg?token=81G1TFYOK0)](https://codecov.io/gh/777Egor777/job4j_grabber)

# grabber

Проект "Аггрегатор Java вакансий".
Приложение парсит Java-вакансии с сайта sql.ru.
Архитектура позволяет расширить его для парсинга
вакансий с других сайтов.

В проекте используются следующие технологии:
- Postgresql в качестве хранилища данных
- JDBC для коннекта к БД
- Jsoup для парсинга
- Quartz для периодизации работы