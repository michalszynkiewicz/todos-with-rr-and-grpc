package com.example;

import com.example.storage.TodoStorage;
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
import java.util.List;

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
        return storage.streamAll().onItem().transform(mapper::entityToDto);
    }

    @PUT
    @Path("/{id}")
    public Uni<TodoDto> updateTodo(@RestPath Long id, TodoDto dto) {
        return storage.update(id, entity -> mapper.merge(dto, entity))
                .onItem().transform(mapper::entityToDto);
    }
}
