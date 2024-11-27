package com.anastasiat.service.restore;

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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StuckTasksRecoverServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ZoneId zoneId;

    @InjectMocks
    private StuckTasksRecoverServiceImpl stuckTasksRecoverService;

    private MockedStatic<LocalDateTime> mockedStatic;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime mockNow = LocalDateTime.of(2024, 11, 27, 12, 0, 0, 0);
        mockedStatic.when(() -> LocalDateTime.now(zoneId)).thenReturn(mockNow);

        ReflectionTestUtils.setField(stuckTasksRecoverService, "batchSize", 10);
        ReflectionTestUtils.setField(stuckTasksRecoverService, "shiftMinutes", 5);

        task1 = new Task();
        task1.setMessageId("messageId1");
        task1.setStatus(TaskStatus.IN_PROGRESS.toString());
        task1.setUpdateDate(mockNow);

        task2 = new Task();
        task2.setMessageId("messageId2");
        task2.setStatus(TaskStatus.IN_PROGRESS.toString());
        task2.setUpdateDate(mockNow);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void recoverStuckTasks_success() {
        List<Task> stuckTasks = Arrays.asList(task1, task2);
        when(taskRepository.findStuckTasks(
                List.of(TaskStatus.IN_PROGRESS.toString()),
                10,
                zoneId,
                5
        )).thenReturn(stuckTasks);

        stuckTasksRecoverService.recoverStuckTasks();

        assertEquals(TaskStatus.PENDING.toString(), task1.getStatus());
        assertEquals(TaskStatus.PENDING.toString(), task2.getStatus());
        assertNull(task1.getExpectedExecutionDate());
        assertNull(task2.getExpectedExecutionDate());
        assertNotNull(task1.getUpdateDate());
        assertNotNull(task2.getUpdateDate());

        verify(taskRepository, times(2)).save(any(Task.class));
    }

    @Test
    void recoverStuckTasks_noTasksToRecover() {
        when(taskRepository.findStuckTasks(
                List.of(TaskStatus.IN_PROGRESS.toString()),
                10,
                zoneId,
                5
        )).thenReturn(Arrays.asList());

        stuckTasksRecoverService.recoverStuckTasks();

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void recoverStuckTasks_handlesException() {
        List<Task> stuckTasks = Arrays.asList(task1, task2);
        when(taskRepository.findStuckTasks(
                List.of(TaskStatus.IN_PROGRESS.toString()),
                10,
                zoneId,
                5
        )).thenReturn(stuckTasks);
        doThrow(new RuntimeException("Ошибка БД")).when(taskRepository).save(any(Task.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stuckTasksRecoverService.recoverStuckTasks();
        });
        assertEquals("Ошибка БД", exception.getMessage());

        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
