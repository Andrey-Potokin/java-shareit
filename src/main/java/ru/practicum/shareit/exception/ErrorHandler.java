package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse repositoryNotFoundExceptionHandle(Exception e) {
        return new ExceptionResponse("Запрашиваемый ресурс не найден", e.getMessage());
    }

    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse repositoryDuplicatedDataExceptionHandle(Exception e) {
        return new ExceptionResponse("Ресурс дублируется", e.getMessage());
    }

    @ExceptionHandler({InternalServerException.class, Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse internalServerExceptionHandle(InternalServerException e) {
        return new ExceptionResponse("Ошибка сервера", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse validationExceptionHandle(Exception e) {
        return new ExceptionResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse forbiddenExceptionHandle(Exception e) {
        return new ExceptionResponse("Доступ запрещен", e.getMessage());
    }

    @Getter
    @AllArgsConstructor
    public static class ExceptionResponse {
        String error;
        String description;
    }
}