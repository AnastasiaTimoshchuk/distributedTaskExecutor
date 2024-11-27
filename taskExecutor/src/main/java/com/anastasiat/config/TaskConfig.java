package com.anastasiat.config;

import com.anastasiat.repository.TaskRepository;
import com.anastasiat.service.IdempotentTaskCreateServiceImpl;
import com.anastasiat.service.TaskCreateService;
import com.anastasiat.service.TaskCreateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class TaskConfig {

    @Bean
    public TaskCreateService taskCreateService(
            TaskRepository taskRepository,
            ZoneId zoneId
    ) {
        TaskCreateService service = new TaskCreateServiceImpl(taskRepository, zoneId);
        return new IdempotentTaskCreateServiceImpl(service, taskRepository);
    }

}
