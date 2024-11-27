package com.anastasiat.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    @Test
    void testMapToDto() {
        Task task = new Task();
        task.setId(1);
        task.setName("Тест");
        task.setMessageId("MessageId");
        task.setDuration(100);
        task.setStatus("PENDING");
        task.setCreateDate(LocalDateTime.of(2024, 11, 27, 12, 0, 0, 0));
        task.setUpdateDate(LocalDateTime.of(2024, 11, 27, 12, 30, 0, 0));
        task.setExpectedExecutionDate(LocalDateTime.of(2024, 11, 27, 14, 0, 0, 0));
        task.setExecutionTries(3);

        TaskDto dto = TaskMapper.mapToDto(task);

        assertNotNull(dto);
        assertEquals(task.getId(), dto.getId());
        assertEquals(task.getName(), dto.getName());
        assertEquals(task.getMessageId(), dto.getMessageId());
        assertEquals(task.getDuration(), dto.getDuration());
        assertEquals(task.getStatus(), dto.getStatus());
        assertEquals(task.getCreateDate(), dto.getCreateDate());
        assertEquals(task.getUpdateDate(), dto.getUpdateDate());
        assertEquals(task.getExpectedExecutionDate(), dto.getExpectedExecutionDate());
        assertEquals(task.getExecutionTries(), dto.getExecutionTries());
    }

    @Test
    void testMapToEntity() {
        TaskDto dto = new TaskDto();
        dto.setName("Тест");
        dto.setMessageId("MessageId");
        dto.setDuration(100);

        Task entity = TaskMapper.mapToEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getMessageId(), entity.getMessageId());
        assertEquals(dto.getDuration(), entity.getDuration());
        assertNull(entity.getId());
    }

    @Test
    void testMapToEntityWithNullFields() {
        TaskDto dto = new TaskDto();
        dto.setName(null);
        dto.setMessageId(null);
        dto.setDuration(0);

        Task entity = TaskMapper.mapToEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getName());
        assertNull(entity.getMessageId());
        assertEquals(0, entity.getDuration());
    }
}
