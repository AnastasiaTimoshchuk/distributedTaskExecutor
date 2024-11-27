package com.anastasiat.repository;

import com.anastasiat.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>, CustomTaskRepository {

    Optional<Task> findByMessageId(String taskId);

    boolean existsByMessageId(String messageId);
}

