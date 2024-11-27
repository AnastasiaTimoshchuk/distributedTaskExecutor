package com.anastasiat.config.workerpool;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableConfigurationProperties(WorkerPoolProperties.class)
@Configuration
public class WorkerPoolConfig {

    private final WorkerPoolProperties workerPoolProperties;

    public WorkerPoolConfig(WorkerPoolProperties workerPoolProperties) {
        this.workerPoolProperties = workerPoolProperties;
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(workerPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(workerPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(workerPoolProperties.getQueueCapacity());
        executor.setThreadNamePrefix(workerPoolProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
