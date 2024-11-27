package com.anastasiat.controller;

import com.anastasiat.entity.TaskDto;
import com.anastasiat.service.TaskGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskGetService taskGetService;

    @Operation(summary = "Получение задачи по id",
            description = "Получение задачи по id (индекс в БД)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "404", description = "Не найдено"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            })
    @GetMapping("/id/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Integer id) {
        TaskDto task = taskGetService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Получение задачи по messageId",
            description = "Получение задачи по messageId (messageId из сообщения из кафки)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "404", description = "Не найдено"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            })
    @GetMapping("/messageId/{messageId}")
    public ResponseEntity<TaskDto> getTaskByMessageId(@PathVariable String messageId) {
        TaskDto task = taskGetService.getTaskByMessageId(messageId);
        return ResponseEntity.ok(task);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(EntityNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex) {
        return "Непредвиденная ошибка: %s".formatted(ex.getMessage());
    }
}

