package com.anastasiat.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {

    private Integer id;

    @NotBlank(message = "Поле name должно быть заполнено")
    private String name;

    @NotBlank(message = "Поле messageId должно быть заполнено")
    private String messageId;

    @NotNull(message = "Поле duration должно быть заполнено")
    @Min(value = 1, message = "Значения поля duration должно быть больше 0")
    private Integer duration;

    private String status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private LocalDateTime expectedExecutionDate;

    private Integer executionTries;
}

