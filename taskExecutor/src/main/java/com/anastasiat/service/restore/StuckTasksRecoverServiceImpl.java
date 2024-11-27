package com.anastasiat.service.restore;

import com.anastasiat.entity.Task;
import com.anastasiat.entity.TaskStatus;
import com.anastasiat.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@EnableScheduling
@Service
public class StuckTasksRecoverServiceImpl implements StuckTasksRecoverService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StuckTasksRecoverServiceImpl.class);

    @Value("${scheduler.restore-stuck-tasks.batch-size}")
    private int batchSize;

    @Value("${scheduler.restore-stuck-tasks.shift-minutes}")
    private int shiftMinutes;

    private final TaskRepository taskRepository;
    private final ZoneId zoneId;

    public StuckTasksRecoverServiceImpl(
            TaskRepository taskRepository,
            ZoneId zoneId
    ) {
        this.taskRepository = taskRepository;
        this.zoneId = zoneId;
    }

    @Scheduled(cron = "${scheduler.restore-stuck-tasks.cron}")
    @Transactional
    @Override
    public void recoverStuckTasks() {
        List<Task> stuckTasks = taskRepository.findStuckTasks(
                List.of(TaskStatus.IN_PROGRESS.toString()),
                batchSize,
                zoneId,
                shiftMinutes
        );

        for (Task task : stuckTasks) {
            task.setStatus(TaskStatus.PENDING.toString());
            task.setUpdateDate(LocalDateTime.now(zoneId));
            task.setExpectedExecutionDate(null);
            taskRepository.save(task);
            LOGGER.debug("Задача messageId " + task.getMessageId() + " восстановлена в статус " + TaskStatus.PENDING);
        }
    }
}
