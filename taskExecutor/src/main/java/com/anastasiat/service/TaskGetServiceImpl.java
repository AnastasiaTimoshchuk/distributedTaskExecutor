package com.anastasiat.service;

import com.anastasiat.entity.Task;
import com.anastasiat.entity.TaskDto;
import com.anastasiat.entity.TaskMapper;
import com.anastasiat.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskGetServiceImpl implements TaskGetService {

    private final TaskRepository taskRepository;

    @Override
    public TaskDto getTaskById(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с id %d не найдена".formatted(id)));
        return TaskMapper.mapToDto(task);
    }

    @Override
    public TaskDto getTaskByMessageId(String messageId) {
        Task task = taskRepository.findByMessageId(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Задача с messageId %s не найдена".formatted(messageId)));
        return TaskMapper.mapToDto(task);
    }
}


