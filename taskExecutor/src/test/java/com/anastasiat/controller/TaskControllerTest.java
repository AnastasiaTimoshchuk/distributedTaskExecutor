package com.anastasiat.controller;

import com.anastasiat.entity.TaskDto;
import com.anastasiat.service.TaskGetService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskGetService taskGetService;

    @InjectMocks
    private TaskController taskController;

    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        taskDto = new TaskDto();
        taskDto.setId(1);
        taskDto.setName("Тест");
        taskDto.setMessageId("messageId");
        taskDto.setStatus("PENDING");
        taskDto.setDuration(100);
    }

    @Test
    void getTaskById_success() throws Exception {
        when(taskGetService.getTaskById(any())).thenReturn(taskDto);

        mockMvc.perform(get("/tasks/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тест"))
                .andExpect(jsonPath("$.messageId").value("messageId"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(taskGetService, times(1)).getTaskById(1);
    }

    @Test
    void getTaskById_notFound() throws Exception {
        when(taskGetService.getTaskById(any())).thenThrow(new EntityNotFoundException("Задача не найдена"));

        mockMvc.perform(get("/tasks/id/999"))
                .andExpect(status().isNotFound());

        verify(taskGetService, times(1)).getTaskById(999);
    }

    @Test
    void getTaskByMessageId_success() throws Exception {
        when(taskGetService.getTaskByMessageId(any())).thenReturn(taskDto);

        mockMvc.perform(get("/tasks/messageId/messageId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тест"))
                .andExpect(jsonPath("$.messageId").value("messageId"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(taskGetService, times(1)).getTaskByMessageId("messageId");
    }

    @Test
    void getTaskByMessageId_notFound() throws Exception {
        when(taskGetService.getTaskByMessageId(any())).thenThrow(new EntityNotFoundException("Задача не найдена"));

        mockMvc.perform(get("/tasks/messageId/invalidMessageId"))
                .andExpect(status().isNotFound());

        verify(taskGetService, times(1)).getTaskByMessageId("invalidMessageId");
    }

    @Test
    void getTaskById_serverError() throws Exception {
        when(taskGetService.getTaskById(any())).thenThrow(new RuntimeException("Ошибка сервера"));

        mockMvc.perform(get("/tasks/id/1"))
                .andExpect(status().isInternalServerError());

        verify(taskGetService, times(1)).getTaskById(1);
    }

    @Test
    void getTaskByMessageId_serverError() throws Exception {
        when(taskGetService.getTaskByMessageId("messageId")).thenThrow(new RuntimeException("Ошибка сервера"));

        mockMvc.perform(get("/tasks/messageId/messageId"))
                .andExpect(status().isInternalServerError());

        verify(taskGetService, times(1)).getTaskByMessageId("messageId");
    }
}
