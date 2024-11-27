package com.anastasiat.service;

import com.anastasiat.entity.TaskDto;
import com.anastasiat.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotentTaskCreateServiceImplTest {

    @Mock
    private TaskCreateService delegate;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private IdempotentTaskCreateServiceImpl idempotentTaskCreateService;

    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto();
        taskDto.setMessageId("MessageId1");
        taskDto.setName("Тест");
        taskDto.setDuration(100);
    }

    @Test
    void createTask_taskExists_throwsIllegalStateException() {
        when(taskRepository.existsByMessageId("MessageId1")).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> idempotentTaskCreateService.createTask(taskDto)
        );

        assertEquals("Задача с messageId MessageId1 уже существует", exception.getMessage());
        verify(taskRepository, times(1)).existsByMessageId("MessageId1");
        verify(delegate, never()).createTask(any());
    }

    @Test
    void createTask_taskDoesNotExist_callsDelegate() {
        when(taskRepository.existsByMessageId("MessageId1")).thenReturn(false);
        TaskDto createdTaskDto = new TaskDto();
        createdTaskDto.setMessageId("MessageId1");
        when(delegate.createTask(taskDto)).thenReturn(createdTaskDto);

        TaskDto result = idempotentTaskCreateService.createTask(taskDto);

        assertNotNull(result);
        assertEquals("MessageId1", result.getMessageId());
        verify(taskRepository, times(1)).existsByMessageId("MessageId1");
        verify(delegate, times(1)).createTask(taskDto);
    }
}