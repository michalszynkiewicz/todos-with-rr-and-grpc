package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TodoResourceTest {
    @Test
    public void shouldAddTodo() {
        given()
                .body("{\"title\": \"dog\", \"description\":\"walk the dog\"}")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/todos")
                .then()
                .statusCode(200);

        when().get("/todos")
                .then()
                .body(CoreMatchers.containsString("walk the dog"));
    }

    @Test
    public void shouldUpdateTodo() {
        Response add = given()
                .body("{\"title\": \"cat\", \"description\":\"feed the cat\"}")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/todos");
        add.then()
                .statusCode(200);

        long id = add.jsonPath().getLong("id");
        assertState(id, "NEW");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{\"title\": \"cat\", \"description\":\"feed the cat\", \"todoState\": \"DONE\", \"id\": " + id + "}")
                .when().put("/todos/" + id)
                .then().statusCode(200);

        assertState(id, "DONE");
    }

    private void assertState(long id, String expectedState) {
        String state = when().get("/todos")
                .jsonPath().getString(String.format("find {it.id == %s}.todoState", id));
        assertThat(state).isEqualTo(expectedState);
    }

}
