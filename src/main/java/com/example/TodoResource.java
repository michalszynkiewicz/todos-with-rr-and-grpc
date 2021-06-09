package com.example;

import com.example.storage.Todo;
import com.example.storage.TodoStorage;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/todos")
@ApplicationScoped
public class TodoResource {

    @Inject
    TodoStorage storage;

    @Inject
    Mapper mapper;

    @GET
    public Multi<TodoDto> getAll() {
        return storage.listAll()
                .onItem().transform(mapper::entityToDto);
    }

    @POST
    public Uni<TodoDto> add(TodoDto todo) {
        todo.todoState = TodoState.NEW;
        Todo entity = mapper.dtoToEntity(todo);
        return storage.add(entity)
                .map(mapper::entityToDto);
    }

    @Path("/{id}")
    @PUT
    public Uni<TodoDto> update(@RestPath("id") long id, TodoDto todo) {
        todo.id = id;
        return storage.update(id, entity -> mapper.merge(todo, entity))
                .onItem().transform(mapper::entityToDto);
    }
}
