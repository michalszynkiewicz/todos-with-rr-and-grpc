package com.example;

import com.example.storage.Todo;
import com.example.storage.TodoStorage;
import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import java.util.List;

@GrpcService
public class TodoService implements Todos {

    public static final TodosOuterClass.Void VOID = TodosOuterClass.Void.getDefaultInstance();

    final BroadcastProcessor<TodosOuterClass.Todo> broadcastProcessor =
            BroadcastProcessor.create();

    @Inject
    TodoStorage storage;

    @Inject
    Mapper mapper;


    @Override
    public Uni<TodosOuterClass.Void> add(TodosOuterClass.Todo request) {
        return storage.add(mapper.grpcToEntity(request))
                .replaceWith(VOID);
    }

    @Override
    public Uni<TodosOuterClass.Void> markDone(TodosOuterClass.Todo request) {
        return null;
    }

    @Override
    public Multi<TodosOuterClass.Todo> watch(TodosOuterClass.Void request) {
        Multi<TodosOuterClass.Todo> existingTodos = storage.streamAndMapAll(mapper::entityToGrpc)
                .onItem().transformToMulti(l -> Multi.createFrom().iterable(l));

        return Multi.createBy().concatenating()
                .streams(existingTodos, broadcastProcessor);
    }


    public void watchForTodos(@Observes Todo todo) {
        broadcastProcessor.onNext(mapper.entityToGrpc(todo));
    }
}
