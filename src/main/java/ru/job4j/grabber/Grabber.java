package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler =  StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfgLoad() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream(CFG_PATH)) {
            CFG.load(in);
        }
    }

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
        scheduler.scheduleJob(job, trigger);
    }

    public static boolean correct(Post post) {
        String header = post.getVacancyHeader().toLowerCase();
        return header.contains(INCLUDED_LANGUAGE)
                && !header.contains(EXCLUDED_LANGUAGE);
    }

    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map =  context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            int currentPage = Integer.parseInt(CFG.getProperty("current_page"));
            int numberOfPages = Integer.parseInt(CFG.getProperty("number_of_pages"));
            if (currentPage <= numberOfPages) {
                String parsePageLink = String.format("%s/%s",
                        CFG.getProperty("base_parse_url"),
                        CFG.getProperty("current_page"));
                List<Post> list = new LinkedList<>();
                try {
                    list = parse.list(parsePageLink);
                    System.out.println("!");
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
                currentPage++;
                CFG.setProperty("current_page", "" + currentPage);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfgLoad();
        try (Store store = new PsqlStore(CFG)) {
            Scheduler scheduler = grab.scheduler();
            Parse parse = new SqlRuParse();
            grab.init(parse, store, scheduler);
            long workTimeInMillis =
                    (long) Integer.parseInt(CFG.getProperty("time_for_page"))
                  * (1 + Integer.parseInt(CFG.getProperty("number_of_pages")))
                  * 1000;
            Thread.sleep(workTimeInMillis);
            scheduler.shutdown();
        }
    }
}
