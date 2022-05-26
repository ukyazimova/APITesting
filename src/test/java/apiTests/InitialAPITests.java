package apiTests;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.ResponseAwareMatcher.*;
import static org.hamcrest.Matchers.*;

public class InitialAPITests {
    @Test
    public void getPosts(){
        given()
                .when()
                .get("http://training.skillo-bg.com:3100/posts/public?take=5&skip=0")
                .then()
                .log()
                .all();
    }
}
