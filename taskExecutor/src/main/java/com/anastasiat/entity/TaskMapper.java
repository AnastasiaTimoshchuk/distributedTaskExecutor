package com.anastasiat.entity;

public class TaskMapper {

    public static TaskDto mapToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setMessageId(task.getMessageId());
        dto.setDuration(task.getDuration());
        dto.setStatus(task.getStatus());
        dto.setCreateDate(task.getCreateDate());
        dto.setUpdateDate(task.getUpdateDate());
        dto.setExpectedExecutionDate(task.getExpectedExecutionDate());
        dto.setExecutionTries(task.getExecutionTries());
        return dto;
    }

    public static Task mapToEntity(TaskDto dto) {
        Task entity = new Task();
        entity.setName(dto.getName());
        entity.setDuration(dto.getDuration());
        entity.setMessageId(dto.getMessageId());
        return entity;
    }
}
