package com.anastasiat.kafka;

import com.anastasiat.entity.TaskDto;
import com.anastasiat.service.TaskCreateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.annotation.EnableKafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableKafka
class TaskListenerTest {

    @InjectMocks
    private TaskListener taskListener;

    @Mock
    private TaskCreateService taskCreateService;

    private TaskDto validTaskDto;

    @BeforeEach
    void setUp() {
        validTaskDto = new TaskDto();
        validTaskDto.setMessageId("validMessageId");
        validTaskDto.setName("Valid Task");
        validTaskDto.setDuration(100);
    }

    @Test
    void testListenTask_validMessage() {
        String message = "{\"messageId\":\"validMessageId\",\"name\":\"Valid Task\",\"duration\":100}";

        taskListener.listenTask(message);

        verify(taskCreateService, times(1)).createTask(validTaskDto);
    }

    @Test
    void testListenTask_invalidMessage_dueToJsonProcessingException() {
        String message = "{\"messageId:\"validMessageId\",\"name\":\"Valid Task\",\"duration\":100}";

        taskListener.listenTask(message);

        verify(taskCreateService, never()).createTask(any(TaskDto.class));
    }

    @Test
    void testListenTask_invalidMessage_dueToValidationErrors() {
        String message = "{\"messageId\":\"invalidMessageId\",\"duration\":100}";

        taskListener.listenTask(message);

        verify(taskCreateService, never()).createTask(any(TaskDto.class));
    }

    @Test
    void testListenTask_taskCreationThrowsException() {
        String message = "{\"messageId\":\"validMessageId\",\"name\":\"Valid Task\",\"duration\":100}";

        doThrow(new IllegalStateException("Задача уже существует")).when(taskCreateService).createTask(any(TaskDto.class));

        taskListener.listenTask(message);

        verify(taskCreateService, times(1)).createTask(validTaskDto);
    }
}