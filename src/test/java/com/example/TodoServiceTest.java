package com.example;

import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.awaitility.Awaitility.await;

@QuarkusTest
public class TodoServiceTest {

    @GrpcClient
    Todos client;

    @Test
    public void shouldAddTodo() {
        addTodo();
    }
    @Test
    public void shouldReturnTodo() {
        Map<Long, TodosOuterClass.Todo> todos = new ConcurrentHashMap<>();
        client.watch(TodosOuterClass.Void.getDefaultInstance())
                .subscribe()
                .with(todo -> todos.put(todo.getId(), todo));
        addTodo();

        await().atMost(Duration.ofSeconds(10))
                .until(() -> todos.size() > 0);
    }

    private void addTodo() {
        client.add(
                TodosOuterClass.Todo.newBuilder()
                        .setDescription("pack the washing machine")
                        .setTitle("laundry")
                        .build()
        ).await().atMost(Duration.ofSeconds(5));
    }
}
