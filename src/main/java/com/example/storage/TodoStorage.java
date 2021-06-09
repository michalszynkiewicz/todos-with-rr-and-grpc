package com.example.storage;


import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
public class TodoStorage {

    @Inject
    Event<Todo> events;

    public Multi<Todo> listAll() {
        return Todo.streamAll();
    }

    public Uni<Todo> add(Todo entity) {
        return Panache.withTransaction(() ->
                Todo.persist(entity)
                        .onItem().transform(ignored -> entity)
                        .onItem().invoke(events::fireAsync)
        );
    }

    public Uni<Todo> update(long id, Consumer<Todo> update) {
        return Panache.withTransaction(
                () -> {
                    Uni<Todo> entity = Todo.findById(id);
                    return entity.onItem().invoke(update);
                }
        ).onItem().invoke(events::fireAsync);
    }
}
