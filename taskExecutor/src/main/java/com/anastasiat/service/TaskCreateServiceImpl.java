package com.anastasiat.service;

import com.anastasiat.entity.Task;
import com.anastasiat.entity.TaskDto;
import com.anastasiat.entity.TaskMapper;
import com.anastasiat.entity.TaskStatus;
import com.anastasiat.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@AllArgsConstructor
public class TaskCreateServiceImpl implements TaskCreateService {

    private final TaskRepository taskRepository;
    private final ZoneId zoneId;

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Task task = TaskMapper.mapToEntity(taskDto);

        task.setStatus(TaskStatus.PENDING.toString());
        task.setCreateDate(LocalDateTime.now(zoneId));
        task.setUpdateDate(LocalDateTime.now(zoneId));
        task.setExecutionTries(0);

        Task savedTask = taskRepository.save(task);
        return TaskMapper.mapToDto(savedTask);
    }
}
