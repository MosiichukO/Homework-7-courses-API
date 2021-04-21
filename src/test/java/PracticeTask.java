import com.google.gson.Gson;
import entities.*;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class PracticeTask {

    String BASE_URL = "https://petstore.swagger.io/v2";

    Category snakeCategory = new Category(12, "Snakes");

    @Test
    public void addPet_invalidID() {

        System.out.println("Test data for creating Pet is preparing...");

        BigInteger myID = new BigInteger("2929991188881177227733");

        Pet PetToBeAdded = Pet.builder()
                .id(myID)
                .category(snakeCategory)
                .name("Sara")
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

        System.out.println("Response: " + addingPetResponse.asString());

        Assert.assertEquals("Status code in not 500", 500, addingPetResponse.getStatusCode());

        ResponseBlock checkMessage = addingPetResponse.as(ResponseBlock.class);

        Assert.assertEquals("Wrong message", "something bad happened", checkMessage.getMessage());
    }

    @Test
    public void addDeleteCheckPet() {
        System.out.println("Test data for creating Pet is preparing...");

        BigInteger newPetID = new BigInteger("22");

        Pet PetToBeAdded = Pet.builder()
                .id(newPetID)
                .category(snakeCategory)
                .name("Tira")
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

        System.out.println("Response: " + addingPetResponse.asString());
        Assert.assertEquals("Status code in not 200", 200, addingPetResponse.getStatusCode());

        System.out.println("Deleting Pet...");

        Response deletingPetResponse = given()
                .baseUri(BASE_URL)
                .pathParam("petID", newPetID)
                .when()
                .delete("/pet/{petID}");

        System.out.println("Response: " + deletingPetResponse.asString());
        Assert.assertEquals("Status code in not 200", 200, deletingPetResponse.getStatusCode());

        System.out.println("Checking Pet is deleted...");

        Response getDeletedPetResponse = given()
                .baseUri(BASE_URL)
                .pathParam("petID", newPetID)
                .when()
                .get("/pet/{petID}");

        System.out.println("Response: " + getDeletedPetResponse.asString());
        ResponseBlock checkDeletedPet = getDeletedPetResponse.as(ResponseBlock.class);
        Assert.assertEquals("Pet is not deleted", "Pet not found", checkDeletedPet.getMessage());
    }

    @Test
    public void addUserCheckSchema() {
        System.out.println("Test data for creating User is preparing...");

        User userToBeRegistered = User.builder()
                .id(5)
                .username("TestUser")
                .firstName("Alex")
                .lastName("Car")
                .email("alexa@gmail.com")
                .password("AlexCar12")
                .phone("0739998777")
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

        Response gettingInfoAboutUser = given()
                .baseUri(BASE_URL)
                .pathParam("username", "TestUser")
                .when()
                .get("/user/{username}")
                .then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserSchema.json")).extract().response();

        Assert.assertEquals("Status code in not 200", 200, gettingInfoAboutUser.getStatusCode());
    }

    @Test
    public void addPetWithStatusSold() throws InterruptedException {
        System.out.println("Test data for creating Pet is preparing...");

        BigInteger newPetID = new BigInteger("22");

        Pet PetToBeAdded = Pet.builder()
                .id(newPetID)
                .category(snakeCategory)
                .name("Tushka")
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("sold")
                .build();

        System.out.println("Body to send: " + new Gson().toJson(PetToBeAdded));

        Response addingPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(PetToBeAdded)
                .when()
                .post();

        System.out.println("Response: " + addingPetResponse.asString());
        Assert.assertEquals("Status code in not 200", 200, addingPetResponse.getStatusCode());

        System.out.println("Test data for getting pets with status Sold is preparing...");

        TimeUnit.SECONDS.sleep(5);

        Response gettingPetsWithStatusSold = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/findByStatus/?status=sold");

        System.out.println("Sold pets request done");

        List<Pet> petsSold = Arrays.stream(gettingPetsWithStatusSold.as(Pet[].class))
                .filter(pet -> pet.getId().equals(PetToBeAdded.getId()))
                .collect(Collectors.toList());

        Assert.assertEquals("Name is not needed", PetToBeAdded.getName(), petsSold.get(0).getName());

    }

    @Test
    public void freeIDs() {

        int idFree = 0;

        for (int i = 1; i <= 100; i++) {
            int gettingPetsResponse = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/pet/" + i)
                    .then().extract().statusCode();

            if (gettingPetsResponse != 200) {
                idFree++;
            }
        }
        System.out.println(idFree);
    }
}
