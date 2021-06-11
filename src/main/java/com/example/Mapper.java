package com.example;

import com.example.storage.Todo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Mapper {
    public TodoDto entityToDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.id = todo.id;
        dto.description = todo.description;
        dto.title = todo.title;
        dto.todoState = todo.todoState;
        return dto;
    }

    public Todo dtoToEntity(TodoDto todo) {
        Todo entity = new Todo();

        merge(todo, entity);

        return entity;
    }

    public void merge(TodoDto source, Todo target) {
        target.id = source.id;
        target.description = source.description;
        target.title = source.title;
        target.todoState = source.todoState;
    }
}
