package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class AlertRabbit {
    public static void main(String[] args) throws ClassNotFoundException {
        int interval = 10;
        Properties cfg = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
            interval = Integer.parseInt(cfg.getProperty("rabbit.interval"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class.forName(cfg.getProperty("driver-class-name"));

        try (Connection cn = DriverManager.getConnection(
                cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password")
        )) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", cn);
            JobDetail job = JobBuilder.newJob(Rabbit.class).setJobData(data).build();
            SimpleScheduleBuilder times = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (SchedulerException | SQLException | InterruptedException se) {
            se.printStackTrace();
        }


    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement ps = cn.prepareStatement(
                    "insert into alert_rabbit.rabbit(created) values(?);")) {
                ps.setDate(1, new Date(new java.util.Date().getTime()));
                ps.executeUpdate();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
