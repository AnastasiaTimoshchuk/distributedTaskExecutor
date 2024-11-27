package com.anastasiat.service;

import com.anastasiat.entity.TaskDto;
import com.anastasiat.repository.TaskRepository;

public class IdempotentTaskCreateServiceImpl implements TaskCreateService {

    private final TaskCreateService delegate;
    private final TaskRepository taskRepository;

    public IdempotentTaskCreateServiceImpl(
            TaskCreateService delegate,
            TaskRepository taskRepository
    ) {
        this.delegate = delegate;
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) throws IllegalStateException {
        if (taskRepository.existsByMessageId(taskDto.getMessageId())) {
            throw new IllegalStateException("Задача с messageId %s уже существует".formatted(taskDto.getMessageId()));
        }
        return delegate.createTask(taskDto);
    }
}
