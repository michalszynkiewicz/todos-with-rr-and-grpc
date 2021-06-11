package com.example;

import com.example.storage.Todo;
import com.example.storage.TodoStorage;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/todos")
public class TodoResource {

    @Inject
    TodoStorage storage;

    @Inject
    Mapper mapper;

    @POST
    public Uni<TodoDto> todoDto(TodoDto dto) {
        dto.todoState = TodoState.NEW;
        return storage.add(mapper.dtoToEntity(dto))
               .onItem().transform(mapper::entityToDto);
    }

    @GET
    public Multi<TodoDto> getAll() {
        return storage.streamAll()
                .map(mapper::entityToDto);
    }

    @PUT
    @Path("/{id}")
    public TodoDto updateTodo(@RestPath Long id, TodoDto dto) {
        Todo todo = storage.update(id, entity -> mapper.merge(dto, entity));
        return mapper.entityToDto(todo);
    }
}
