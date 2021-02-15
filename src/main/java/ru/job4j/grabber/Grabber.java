package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.util.ConnectionUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Основной класс приложения,
 * соединяющий в себе все
 * его компоненты,
 * и запускающий процесс парсинга
 * Java-вакансий с сайта
 * sql.ru/forum/job-offers
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 02.12.2020
 */
public class Grabber implements Grab {
    private final static String CFG_PATH = "app.properties";
    private final static Properties CFG = new Properties();
    private final static String INCLUDED_LANGUAGE = "java";
    private final static String EXCLUDED_LANGUAGE = "javascript";
    private final static AtomicInteger CUR_PAGE_NUM = new AtomicInteger(1);

    /**
     * Метод создаёт объект
     * для периодизации
     * работы по парсингу
     * вакансий.
     * @return - объект периодизации
     * @throws SchedulerException - исключение, которое
     *                             может быть выброшено
     *                             при инициализации объекта
     *                             периодизации
     */
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler =  StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * Метод загружает cfg-
     * Properties объект
     * из .properties
     * файла с помощью
     * потока ввода.
     *
     * @throws IOException - исключение, которое
     *                       может быть выброшено
     *                       при считывании
     *                       данных из файла
     */
    public void cfgLoad() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream(CFG_PATH)) {
            CFG.load(in);
        }
    }

    /**
     * Основной метод для запуска работы программы.
     * @param parse - объект, реализующий
     *                интерфейс для парсинга
     *                постов
     * @param store - объект, реализующий
     *                интерфейс для хранилища
     *                постов
     * @param scheduler - объект, реализующий
     *                    периодичность парсинга
     * @throws SchedulerException - исключение, которое возникает
     *                              при некорректной работе
     *                              периодизатора парсинга
     */
    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = JobBuilder.newJob(GrabJob.class).setJobData(data).build();
        SimpleScheduleBuilder times = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(CFG.getProperty("time_for_page")))
                .repeatForever();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        CUR_PAGE_NUM.set(1);
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * В данном проекте мы парсим
     * Java-вакансии. Соответственно
     * Javascript-вакансии надо
     * стараться исключать.
     *
     * @param post - вакансия, принадлежность
     *               которой именно к Java
     *               мы проверяем
     * @return true - если это Java-вакансия
     *         false - иначе
     */
    private static boolean correct(Post post) {
        String header = post.getVacancyHeader().toLowerCase();
        return header.contains(INCLUDED_LANGUAGE)
                && !header.contains(EXCLUDED_LANGUAGE);
    }

    /**
     * Класс, который используется
     * Scheduler-ом для парсинга
     * очередной страницы сайта с
     * вакансиями
     */
    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map =  context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            int currentPage = CUR_PAGE_NUM.getAndIncrement();
            int numberOfPages = Integer.parseInt(CFG.getProperty("number_of_pages"));
            if (currentPage <= numberOfPages) {
                String parsePageLink = String.format("%s/%d",
                        CFG.getProperty("base_parse_url"),
                        currentPage);
                List<Post> list = new LinkedList<>();
                try {
                    list = parse.list(parsePageLink);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                list.forEach(post -> {
                    if (correct(post)) {
                        System.out.println(post);
                        store.save(post);
                        System.out.println("Post saved in Postgres");
                    }
                });
            }
        }
    }

    /**
     * Класс для вывода всех
     * вакансий, содержащихся в
     * хранилище на Web-сервер
     * с помощью сокетов.
     *
     * @param store - хранилище
     */
    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(CFG.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();

                    try (OutputStream out = socket.getOutputStream()) {
                        store.getAll().forEach(post -> {
                            try {
                                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                                out.write(System.lineSeparator().getBytes());
                                out.write(post.toString().getBytes());
                                out.write(System.lineSeparator().getBytes());
                                out.write(System.lineSeparator().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });
                    }


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Точка входа программы. Здесь создаются
     * все необходимые объекты и запускается парсинг.
     */
    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfgLoad();
        Store store = new PsqlStore(ConnectionUtil.getConnectionByCfg(CFG));
        Scheduler scheduler = grab.scheduler();
        Parse parse = new SqlRuParse();
        grab.init(parse, store, scheduler);
        long workTimeInMillis =
                (long) Integer.parseInt(CFG.getProperty("time_for_page"))
                        * (1 + Integer.parseInt(CFG.getProperty("number_of_pages")))
                        * 1000;
        Thread.sleep(workTimeInMillis);
        scheduler.shutdown();
        //grab.web(store);
    }
}
