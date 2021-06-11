package com.example.storage;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class TodoStorage {

    @Inject
    Event<Todo> event;

    public Uni<Todo> add(Todo todo) {
        return Panache.withTransaction(
                () -> Todo.persist(todo)
                        .replaceWith(todo)
        ).invoke(event::fireAsync);
    }

    public Uni<List<Todo>> streamAll() {
        return Panache.withTransaction(Todo::listAll);
    }

    public <T> Uni<List<T>> streamAndMapAll(Function<Todo, T> mapper) {
        return Panache.withTransaction(
                () -> Todo.<Todo>listAll()
                    .onItem().transform(list -> list.stream().map(mapper).collect(Collectors.toList()))
        );
    }

    public Uni<Todo> update(long id, Consumer<Todo> update) {
        return Panache.withTransaction(
                () -> Todo.<Todo>findById(id)
                        .invoke(update)
        ).invoke(event::fireAsync);
    }
}
