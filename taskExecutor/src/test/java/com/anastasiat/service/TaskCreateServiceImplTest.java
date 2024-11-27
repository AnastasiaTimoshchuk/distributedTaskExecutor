package com.anastasiat.service;

import com.anastasiat.entity.Task;
import com.anastasiat.entity.TaskDto;
import com.anastasiat.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskCreateServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ZoneId zoneId;

    @InjectMocks
    private TaskCreateServiceImpl taskCreateService;

    private MockedStatic<LocalDateTime> mockedStatic;
    private LocalDateTime mockNow;

    @BeforeEach
    void tearUp() {
        mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        mockNow = LocalDateTime.of(2024, 11, 27, 12, 0, 0, 0);
        mockedStatic.when(() -> LocalDateTime.now(zoneId)).thenReturn(mockNow);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void createTask_success() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("Тест");
        taskDto.setDuration(100);
        taskDto.setMessageId("MessageId");

        Task task = new Task();
        task.setId(1);
        task.setName("Тест");
        task.setDuration(100);
        task.setMessageId("MessageId");
        task.setStatus("PENDING");
        task.setCreateDate(mockNow);
        task.setUpdateDate(mockNow);
        task.setExecutionTries(0);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto result = taskCreateService.createTask(taskDto);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals("Тест", result.getName());
        assertEquals("PENDING", result.getStatus());
        assertEquals(0, result.getExecutionTries());
        assertEquals(mockNow, result.getCreateDate());
        assertEquals(mockNow, result.getUpdateDate());
        assertNull(result.getExpectedExecutionDate());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_failure_saveError() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("Тест");
        taskDto.setDuration(100);
        taskDto.setMessageId("MessageId");

        when(taskRepository.save(any(Task.class))).thenThrow(new RuntimeException("Ошибка БД"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskCreateService.createTask(taskDto);
        });
        assertEquals("Ошибка БД", exception.getMessage());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}