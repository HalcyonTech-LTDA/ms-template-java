package com.example.templatejava.common.infrastructure.job.config;

import com.mongodb.client.MongoClient;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration(proxyBeanMethods = false)
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "${app.scheduling.shedlock.defaults.lock-at-most-for}")
@Profile("scheduling")
public class SchedulingConfig implements SchedulingConfigurer {

    @Value("${app.scheduling.thread-pool.thread-name-prefix:scheduling-worker-}")
    private String threadNamePrefix;

    @Bean
    public LockProvider lockProvider(
            MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
        return new MongoLockProvider(mongoClient.getDatabase(databaseName));
    }

    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler() {
        SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setVirtualThreads(true);
        scheduler.setThreadNamePrefix(threadNamePrefix);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
