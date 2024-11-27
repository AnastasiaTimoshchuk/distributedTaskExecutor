package com.anastasiat.repository;

import com.anastasiat.entity.Task;

import java.time.ZoneId;
import java.util.List;

public interface CustomTaskRepository {

    List<Task> findByStatuses(List<String> statuses, int limit);

    List<Task> findStuckTasks(List<String> statuses, int limit, ZoneId zoneId, int shift);
}
