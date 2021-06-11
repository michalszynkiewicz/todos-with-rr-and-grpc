package com.example.storage;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ApplicationScoped
public class TodoStorage {

    public Uni<Todo> add(Todo todo) {
        return Panache.withTransaction(
                () -> Todo.persist(todo)
                        .replaceWith(todo)
        );
    }

    public Stream<Todo> streamAll() {
        return null; //Todo.streamAll();
    }

    @Transactional
    public Todo update(long id, Consumer<Todo> update) {
//        Todo todo = Todo.findById(id);
//        update.accept(todo);
//        return todo;
        return null;
    }
}
