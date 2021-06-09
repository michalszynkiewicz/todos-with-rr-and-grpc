package com.example;

import com.example.storage.Todo;
import com.example.storage.TodoStorage;
import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@GrpcService
public class TodoService implements Todos {

    public static final TodosOuterClass.Void VOID = TodosOuterClass.Void.newBuilder().getDefaultInstanceForType();

    @Inject
    TodoStorage storage;

    @Inject
    Mapper mapper;

    private final BroadcastProcessor<TodosOuterClass.Todo> broadcast = BroadcastProcessor.create();

    @Override
    public Uni<TodosOuterClass.Void> add(TodosOuterClass.Todo request) {
        Todo entity = mapper.grpcToEntity(request);
        return storage.add(entity)
                .onItem().transform(todo -> VOID);
    }

    @Override
    public Uni<TodosOuterClass.Void> markDone(TodosOuterClass.Todo request) {
        return null;
    }

    @Override
    public Multi<TodosOuterClass.Todo> watch(TodosOuterClass.Void request) {
        return broadcast;
    }

    void observeTodos(@ObservesAsync Todo todo) {
        broadcast.onNext(mapper.entityToGrpc(todo));
    }
}
