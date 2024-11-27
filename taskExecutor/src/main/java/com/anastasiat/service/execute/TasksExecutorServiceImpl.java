package com.anastasiat.service.execute;

import com.anastasiat.entity.Task;
import com.anastasiat.entity.TaskStatus;
import com.anastasiat.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@EnableScheduling
@Service
public class TasksExecutorServiceImpl implements TasksExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksExecutorServiceImpl.class);

    @Value("${scheduler.execute-tasks.batch-size}")
    private int batchSize;

    @Value("${scheduler.execute-tasks.max-retries}")
    private int maxRetries;

    private final TaskRepository taskRepository;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final ZoneId zoneId;

    public TasksExecutorServiceImpl(
            TaskRepository taskRepository,
            ThreadPoolTaskExecutor threadPoolTaskExecutor,
            ZoneId zoneId
    ) {
        this.taskRepository = taskRepository;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.zoneId = zoneId;
    }

    @Scheduled(fixedRateString = "${scheduler.execute-tasks.rate}")
    @Transactional
    public void executeTasks() {
        List<Task> tasks = taskRepository.findByStatuses(
                List.of(TaskStatus.PENDING.toString(), TaskStatus.FAILURE.toString()),
                batchSize
        );

        for (Task task : tasks) {
            try {
                // обновление статуса задачи перед выполнением
                task.setStatus(TaskStatus.IN_PROGRESS.toString());
                task.setUpdateDate(LocalDateTime.now(zoneId));
                task.setExecutionTries(task.getExecutionTries() + 1);
                task.setExpectedExecutionDate(LocalDateTime.now(zoneId).plus(task.getDuration(), ChronoUnit.MILLIS));
                taskRepository.save(task);

                // добавление задачи в пул потоков
                threadPoolTaskExecutor.execute(() -> executeTask(task));
            } catch (TaskRejectedException ex) {
                LOGGER.warn("Задача messageId {} отклонена из-за переполнения очереди потоков", task.getMessageId());

                // возврат задачи в исходное состояние
                task.setStatus(TaskStatus.PENDING.toString());
                task.setUpdateDate(LocalDateTime.now(zoneId));
                task.setExecutionTries(task.getExecutionTries() - 1);
                task.setExpectedExecutionDate(null);
                taskRepository.save(task);
            }
        }
    }

    @Async
    private void executeTask(Task task) {
        try {
            // симуляция выполнения задачи
            LOGGER.debug(Thread.currentThread().getName() + " - Задача messageId " + task.getMessageId() + " запустилась");
            Thread.sleep(task.getDuration());
            LOGGER.debug(Thread.currentThread().getName() + " - Задача messageId " + task.getMessageId() + " закончилась");

            task.setStatus(TaskStatus.SUCCESS.toString());
        } catch (Exception e) {
            LOGGER.error("Задача messageId {} закончилась c ошибкой: {}", task.getMessageId(), e.getMessage());
            if (task.getExecutionTries() >= maxRetries) {
                task.setStatus(TaskStatus.ERROR.toString());
            } else {
                task.setStatus(TaskStatus.FAILURE.toString());
            }
        } finally {
            task.setUpdateDate(LocalDateTime.now(zoneId));
            taskRepository.save(task);
            LOGGER.debug("Задача messageId " + task.getMessageId() + " переведена в статус " + task.getStatus());
        }
    }
}

