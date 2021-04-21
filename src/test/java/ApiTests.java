import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addNewPet_myTest() {
        Category snakeCategory = new Category(12, "Snakes");

        System.out.println("Test data is preparing...");

        Pet PetToBeAdded = Pet.builder()
                .id(new Random().nextInt(4))
                .category(snakeCategory)
                .name("Kira")
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("available")
                .build();

        System.out.println("Body to send: " + new Gson().toJson(PetToBeAdded));

        Response addingPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(PetToBeAdded)
                .when()
                .post();

        System.out.println("Preparing for GET request by ID...");

        long id = PetToBeAdded.getId();

        Response gettingInfoAboutPet = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}");

        System.out.println("Response GET: " + gettingInfoAboutPet.asString());

        Pet gotPetResponse = gettingInfoAboutPet.as(Pet.class);

        Assert.assertEquals("Wrong name", PetToBeAdded.getName(), gotPetResponse.getName());
    }

    @Test
    public void registerNewUser_myTest () {

        System.out.println("Test data is preparing...");

        User userToBeRegistered = User.builder()
                .id(5)
                .username("Alex")
                .firstName("Oleksandr")
                .lastName("Mosiichuk")
                .email("mosiychuk1998a@gmail.com")
                .password("Alex121212")
                .phone("0956278968")
                .userStatus(2)
                .build();

        System.out.println("Body to send: " + new Gson().toJson(userToBeRegistered));

        Response creatingNewUserResponse = given()
                .baseUri(BASE_URL)
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(userToBeRegistered)
                .when()
                .post();


        System.out.println("Response: " + creatingNewUserResponse.asString());


        Assert.assertEquals("Status code in not 200", 200, creatingNewUserResponse.getStatusCode());

        System.out.println("Preparing for GET request by ID...");

        Response gettingInfoAboutUser = given()
                .baseUri(BASE_URL)
                .pathParam("username", "Alex")
                .when()
                .get("/user/{username}");

        System.out.println("Response GET: " + gettingInfoAboutUser.asString());

        User gotUserResponse = gettingInfoAboutUser.as(User.class);

        Assert.assertEquals("Wrong name", userToBeRegistered.getUsername(), gotUserResponse.getUsername());

    }
}

