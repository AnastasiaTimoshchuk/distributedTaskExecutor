package com.anastasiat.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "task_executor", name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "status")
    private String status;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "expected_execution_date")
    private LocalDateTime expectedExecutionDate;

    @Column(name = "execution_tries")
    private Integer executionTries;
}

