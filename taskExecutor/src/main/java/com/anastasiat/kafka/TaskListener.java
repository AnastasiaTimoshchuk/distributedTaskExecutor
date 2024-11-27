package com.anastasiat.kafka;

import com.anastasiat.entity.TaskDto;
import com.anastasiat.service.TaskCreateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TaskListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskListener.class);

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final TaskCreateService taskCreateService;

    public TaskListener(TaskCreateService taskCreateService) {
        this.objectMapper = new ObjectMapper();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.taskCreateService = taskCreateService;
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenTask(String message) {
        TaskDto task;

        // преобразование json в объект TaskDto
        try {
            task = objectMapper.readValue(message, TaskDto.class);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Ошибки преобразования сообщения: " + e.getMessage());
            return;
        }

        // валидация объекта TaskDto
        Set<ConstraintViolation<TaskDto>> violations = validator.validate(task);
        if (!violations.isEmpty()) {
            StringBuilder errors = new StringBuilder("Ошибки валидации:");
            for (ConstraintViolation<TaskDto> violation : violations) {
                errors.append(" ").append(violation.getMessage()).append(";");
            }
            LOGGER.error(errors.toString());
            return;
        }

        // процессинг объекта TaskDto
        try {
            taskCreateService.createTask(task);
        } catch (IllegalStateException e) {
            LOGGER.debug(e.getMessage());
        }
    }
}

