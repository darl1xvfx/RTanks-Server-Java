package rt.server.services.quartz.impl;

import rt.server.services.quartz.QuartzJob;
import rt.server.services.quartz.QuartzService;
import rt.server.services.quartz.TimeType;
import java.util.Date;
import org.quartz.JobKey;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzServiceImpl implements QuartzService {
    private static QuartzServiceImpl instance = new QuartzServiceImpl();
    private Scheduler scheduler;

    private QuartzServiceImpl() {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            this.scheduler = schedulerFactory.getScheduler();
            this.scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace(); // Handle exception or log it appropriately
        }
    }

    private JobDetail createJob(String name, String group, QuartzJob job) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(QuartzJobRunner.jobRunKey, job);

        return JobBuilder.newJob(QuartzJobRunner.class)
                .withIdentity(name, group)
                .usingJobData(jobDataMap)
                .build();
    }

    @Override
    public JobDetail addJobInterval(String name, String group, QuartzJob job, TimeType type, long interval, int repeatCount) {
        JobDetail jobDetail = this.createJob(name, group, job);
        try {
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(name, group)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(type.time(interval))
                            .withRepeatCount(repeatCount))
                    .build();
            this.scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace(); // Handle exception or log it appropriately
        }
        return jobDetail;
    }

    @Override
    public JobDetail addJobInterval(String name, String group, QuartzJob job, TimeType type, long interval) {
        return this.addJobInterval(name, group, job, type, interval, -1);
    }

    @Override
    public JobDetail addJob(String name, String group, QuartzJob job, TimeType type, long time) {
        JobDetail jobDetail = this.createJob(name, group, job);
        try {
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(name, group)
                    .startAt(new Date(System.currentTimeMillis() + type.time(time)))
                    .build();
            this.scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace(); // Handle exception or log it appropriately
        }
        return jobDetail;
    }

    @Override
    public void deleteJob(String name, String group) {
        try {
            this.scheduler.deleteJob(new JobKey(name, group));
        } catch (SchedulerException e) {
            e.printStackTrace(); // Handle exception or log it appropriately
        }
    }

    public static QuartzService inject() {
        return instance;
    }
}
