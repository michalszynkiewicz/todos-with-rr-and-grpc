package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TodoResourceTest {
    @Test
    public void shouldAddTodo() {
        addTodo("{\"title\": \"dog\", " +
                "\"description\":\"walk the dog\"}");
    }

    private Response addTodo(String dto) {
        Response post = given()
                .accept(ContentType.JSON).contentType(ContentType.JSON)
                .body(dto)
                .when().post("/todos");
        post.then().statusCode(200);
        return post;
    }

    @Test
    public void shouldListTodos() {
        Response post = addTodo("{\"title\": \"cat\", \"description\": \"feed the cat\"}");
        long id = post.jsonPath().getLong("id");

        assertState(id, "NEW");
    }

    private void assertState(long id, String expectedState) {
        Response get = given()
                .accept(ContentType.JSON).contentType(ContentType.JSON)
                .when().get("/todos");

        get.then().statusCode(200);
        String state = get.jsonPath().getString(String.format("find {it.id == %s}.todoState", id));

        assertThat(state).isEqualTo(expectedState);
    }

    @Test
    public void shouldUpdateTodo() {
        Response post = addTodo("{\"title\": \"cat\", \"description\": \"feed the cat\"}");
        long id = post.jsonPath().getLong("id");

        Response put = given()
                .accept(ContentType.JSON).contentType(ContentType.JSON)
                .body("{\"title\": \"cat\", \"description\": \"feed the cat\", " +
                        "\"id\": " + id + ", \"todoState\": \"DONE\"}")
                .when().put("/todos/" + id);
        put.then().statusCode(200);
        assertState(id, "DONE");
    }



}
