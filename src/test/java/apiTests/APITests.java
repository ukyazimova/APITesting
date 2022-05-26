package apiTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.ResponseAwareMatcher.*;
import static org.hamcrest.Matchers.*;

public class APITests {

    static String authToken = "";
    String postId;
    String postIdToDelete;
    static Integer userId;
    String commentId;

    @BeforeTest
    public void loginTest() throws IOException {
        //crete login pojo object
        LoginPOJO login = new LoginPOJO();
        FileReader reader = new FileReader("credentials.properties");
        Properties properties = new Properties();

        properties.load(reader);
        System.out.println(properties.getProperty("username"));
        System.out.println(properties.getProperty("password"));
        //set credentials
        login.setUsernameOrEmail("ukyazimova");
        login.setPassword("Password1");


        baseURI = "http://training.skillo-bg.com:3100";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(login)
                .when()
                .post("/users/login");

        response
                .then()
                .statusCode(201);

        //convert the response body json into string
        String loginResponseBody = response.getBody().asString();
        authToken = JsonPath.parse(loginResponseBody).read("$.token");
        System.out.println("The extracted token is: " + authToken);
        userId = JsonPath.parse(loginResponseBody).read("$.user.id");
        System.out.println("The userId is: " + userId);

    }

    @Test
    public void getPosts() {


        Random rn = new Random();
        int rand = rn.nextInt(0, 20);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/posts?take=20&skip=0");
        response
                .then()
                .statusCode(200);

        ValidatableResponse validatableResponse =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + authToken)
                        .when()
                        .get("/posts?take=20&skip=0")
                        .then()
                        .statusCode(200);
        //Integer tempPostId = validatableResponse.extract().path("id");
        // Assert.assertNotEquals(tempPostId,null);
        String getPostsResponseBody = response.getBody().asString();
        postId = JsonPath.parse(getPostsResponseBody).read("$.[" + rand + "].id").toString();
        System.out.println("The extracted post id is: " + postId);
    }

    @Test(dependsOnMethods = "getPosts")
    public void likePost() {
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");
        ValidatableResponse validatableResponse =

                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + authToken)
                        .body(likePost)
                        .when()
                        .patch("/posts/" + postId)
                        .then()
                        .assertThat().body("user.id", equalTo(userId))
                        .log()
                        .all();

        validatableResponse.statusCode(200);
    }

    @Test
    public void addPosts() {
        ActionsPOJO captionPost = new ActionsPOJO();
        captionPost.setCaption("Y's test post");
        captionPost.setCoverUrl("https://i.imgur.com/D6XNAkZ.jpg");
        captionPost.setPostStatus("public");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(captionPost)
                .when()
                .post("/posts")
                .then()
                .assertThat().body("caption", equalTo("Y's test post"))
                .statusCode(201)
                .log()
                .all();


    }

    @Test
    public void getMyPosts() {

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/" + userId + "/posts?take=20&skip=0");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/" + userId + "/posts?take=20&skip=0")
                .then()
                .statusCode(200);

        String getPostsResponseBody = response.getBody().asString();
        postId = JsonPath.parse(getPostsResponseBody).read("$.[?(@.commentsCount!=0)].id").toString();
        if (JsonPath.parse(getPostsResponseBody).read("$.[?(@.commentsCount==0)].id") != "[]") {
            postIdToDelete = JsonPath.parse(getPostsResponseBody).read("$.[?(@.commentsCount==0)].id").toString();
            postIdToDelete = JsonPath.parse(postIdToDelete).read("$").toString();
            postIdToDelete = (postIdToDelete.length() <= 3 ? "0" : postIdToDelete.substring(1, 5));
        } else {
            postIdToDelete = "0";
        }
        postId = postId.substring(1, 5);
        System.out.println("The extracted post id with most comments is: " + postId);
        System.out.println("The extracted post id to be deleted is: " + postIdToDelete);
    }

    @Test(dependsOnMethods = "getMyPosts")
    public void commentPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("comment Post");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(commentPost)
                .when()
                .post("/posts/" + postId + "/comment")
                .then()
                .assertThat().body("content", equalTo("comment Post"))
                .statusCode(201);
    }

    @Test(dependsOnMethods = "getMyPosts")
    public void editPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setCaption("New Caption");
        commentPost.setPostStatus("private");
        System.out.println("Post to Edit:" + postId);
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(commentPost)
                .when()
                .put("/posts/" + postId)
                .then()
                .assertThat().body("caption", equalTo("New Caption"))
                .log()
                .all();
    }

    @Test(dependsOnMethods = "getMyPosts")
    public void removePost() {
        if (postIdToDelete != "0") {
            given()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + authToken)
                    .when()
                    .delete("/posts/" + postIdToDelete)
                    .then()
                    .assertThat().body("msg", equalTo("Post was deleted!"))
                    .log()
                    .all();

        } else {
            System.out.println("Nothing to delete");
//Eternal calls to addPosts
            // addPosts();
            // removePost();
        }

    }

    @Test(dependsOnMethods = "getMyPosts")
    public void actionPost() {
        ActionsPOJO actionPost = new ActionsPOJO();
        actionPost.setAction("likePost");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(actionPost)
                .when()
                .patch("/posts/" + postId)
                .then()
                .assertThat().body("user.id", equalTo(userId))
                .statusCode(200);
    }

    @Test(dependsOnMethods = "getPosts")
    public void getComments() {

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/" + userId + "/posts?take=20&skip=0");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/posts/" + postId + "comments")
                .then()
                .log()
                .all()
                .statusCode(200)
                .assertThat().body("id", notNullValue());


    }
    @Test(dependsOnMethods = "getMyPosts")
    public void postComments() {

        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("comment Post");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(commentPost)
                .when()
                .post("/posts/" + postId + "/comment")
                .then()
                .log()
                .all()
                .assertThat().body("content", equalTo("comment Post"))
                .statusCode(201);

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + authToken)
                        .body(commentPost)
                        .when()
                        .post("/posts/" + postId + "/comment");
        String getPostsResponseBody = response.getBody().asString();
        commentId = JsonPath.parse(getPostsResponseBody).read("$.id").toString();
        System.out.println("CommentID: " + commentId);
    }

    @Test(dependsOnMethods = "getMyPosts")
    public void removeComments() {

       postComments();

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/posts/" + postId + "/comments/"+commentId)
                .then()
                .log()
                .all()
                .statusCode(200);


    }
}
