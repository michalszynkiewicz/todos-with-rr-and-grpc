package com.example;

import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.awaitility.Awaitility.await;

@QuarkusTest
public class TodoServiceTest {
    @GrpcClient
    Todos client;

    @Test
    void shouldAddTodo() {
        List<TodosOuterClass.Todo> results = new CopyOnWriteArrayList<>();

        client.watch(TodosOuterClass.Void.newBuilder().getDefaultInstanceForType())
                .subscribe().with(results::add);

        TodosOuterClass.Void aVoid = client.add(TodosOuterClass.Todo.newBuilder()
                .setTitle("cat")
                .setDescription("feed the cat")
                .build()).await().atMost(Duration.ofSeconds(5));

        await().atMost(Duration.ofSeconds(5))
                .until(() -> results.stream().anyMatch(t -> t.getTitle().equals("cat")));

    }

    @Test
    void shouldMarkTodoDone() {
        List<TodosOuterClass.Todo> results = new CopyOnWriteArrayList<>();

        client.watch(TodosOuterClass.Void.newBuilder().getDefaultInstanceForType())
                .subscribe().with(results::add);

        client.add(TodosOuterClass.Todo.newBuilder()
                .setTitle("fridge")
                .setDescription("do some shopping")
                .build()).await().atMost(Duration.ofSeconds(5));

        long id = getIdOfTodo(results, "fridge");

        client.markDone(TodosOuterClass.Todo.newBuilder()
                .setId(id)
                .setTitle("fridge")
                .setDescription("do some shopping")
                .build()).await().atMost(Duration.ofSeconds(5));

        await().atMost(Duration.ofSeconds(5))
                .until(() ->
                        results.stream()
                                .anyMatch(todo -> todo.getId() == id
                                        && todo.getTodoState() == TodosOuterClass.State.DONE)
                );
    }

    private long getIdOfTodo(List<TodosOuterClass.Todo> results, String title) {
        await().atMost(Duration.ofSeconds(5))
                .until(() -> results.stream().anyMatch(todo -> todo.getTitle().equals(title)));

        return results.stream().filter(todo -> title.equals(todo.getTitle())).findFirst().get().getId();
    }
}
