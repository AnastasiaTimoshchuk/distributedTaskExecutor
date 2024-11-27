package com.anastasiat.service;

import com.anastasiat.entity.TaskDto;

public interface TaskGetService {

    TaskDto getTaskById(Integer id);

    TaskDto getTaskByMessageId(String messageId);
}
