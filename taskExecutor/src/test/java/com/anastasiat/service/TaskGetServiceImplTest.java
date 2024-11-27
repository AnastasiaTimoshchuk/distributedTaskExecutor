package com.anastasiat.service;

import com.anastasiat.entity.Task;
import com.anastasiat.entity.TaskDto;
import com.anastasiat.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskGetServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskGetServiceImpl taskGetService;

    @Test
    void getTaskById_success() {
        Integer taskId = 1;
        Task task = new Task();
        task.setId(taskId);
        task.setName("Тест");
        task.setMessageId("Тест-messageId");
        task.setStatus("PENDING");
        task.setCreateDate(LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskDto result = taskGetService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Тест", result.getName());
        assertEquals("PENDING", result.getStatus());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_notFound() {
        Integer taskId = 1;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskGetService.getTaskById(taskId);
        });

        assertEquals("Задача с id 1 не найдена", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskByMessageId_success() {
        String messageId = "Тест-messageId";
        Task task = new Task();
        task.setId(1);
        task.setName("Тест");
        task.setMessageId(messageId);
        task.setStatus("PENDING");
        task.setCreateDate(LocalDateTime.now());

        when(taskRepository.findByMessageId(messageId)).thenReturn(Optional.of(task));

        TaskDto result = taskGetService.getTaskByMessageId(messageId);

        assertNotNull(result);
        assertEquals(messageId, result.getMessageId());
        assertEquals("Тест", result.getName());
        assertEquals("PENDING", result.getStatus());
        verify(taskRepository, times(1)).findByMessageId(messageId);
    }

    @Test
    void getTaskByMessageId_notFound() {
        String messageId = "Тест-messageId";

        when(taskRepository.findByMessageId(messageId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskGetService.getTaskByMessageId(messageId);
        });

        assertEquals("Задача с messageId Тест-messageId не найдена", exception.getMessage());
        verify(taskRepository, times(1)).findByMessageId(messageId);
    }
}