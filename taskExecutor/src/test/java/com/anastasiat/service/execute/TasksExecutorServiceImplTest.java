package com.anastasiat.service.execute;

import com.anastasiat.entity.Task;
import com.anastasiat.entity.TaskStatus;
import com.anastasiat.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksExecutorServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Mock
    private ZoneId zoneId;

    @InjectMocks
    private TasksExecutorServiceImpl tasksExecutorService;

    private MockedStatic<LocalDateTime> mockedStatic;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime mockNow = LocalDateTime.of(2024, 11, 27, 12, 0, 0, 0);
        mockedStatic.when(() -> LocalDateTime.now(zoneId)).thenReturn(mockNow);

        task1 = new Task();
        task1.setMessageId("messageId1");
        task1.setStatus(TaskStatus.PENDING.toString());
        task1.setExecutionTries(0);
        task1.setDuration(1000);  // 1 секунда
        task1.setUpdateDate(mockNow);

        task2 = new Task();
        task2.setMessageId("messageId2");
        task2.setStatus(TaskStatus.PENDING.toString());
        task2.setExecutionTries(0);
        task2.setDuration(1000);
        task2.setUpdateDate(mockNow);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void executeTasks_success() {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findByStatuses(any(), anyInt())).thenReturn(tasks);

        tasksExecutorService.executeTasks();

        verify(taskRepository, times(2)).save(any(Task.class));
        verify(threadPoolTaskExecutor, times(2)).execute(any(Runnable.class));
    }

    @Test
    void executeTasks_taskRejected() {
        // Arrange
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findByStatuses(any(), anyInt())).thenReturn(tasks);
        doThrow(new TaskRejectedException("Очередь переполнена")).when(threadPoolTaskExecutor).execute(any(Runnable.class));

        // Act
        tasksExecutorService.executeTasks();

        // Assert
        verify(taskRepository, times(4)).save(any(Task.class));
        verify(threadPoolTaskExecutor, times(2)).execute(any(Runnable.class));
    }

    @Test
    void executeTasks_taskSuccess() {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findByStatuses(any(), anyInt())).thenReturn(tasks);

        doNothing().when(threadPoolTaskExecutor).execute(any(Runnable.class));

        tasksExecutorService.executeTasks();

        verify(taskRepository, times(2)).save(any(Task.class));
        verify(threadPoolTaskExecutor, times(2)).execute(any(Runnable.class));
    }
}
