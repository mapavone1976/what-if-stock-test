import com.opencsv.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Nasa {

    private static final String baseURL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/";

    @Test
    public void GetNasaData() {
        RestAssured.baseURI = baseURL;
        Response response =
                    given().
                        param("sol", "1000").
                        param("api_key", "qzrMQfK1eDxvAoj5gX84hgrLkB0SUq6yc87TYcWJ").
                    when().
                        get("/photos").
                    then().
                        contentType("application/json; charset=utf-8").
                        statusCode(200).
                        //body("title", equalTo("My Title")).
                    extract().
                        response();


        String nextTitleLink = response.path("_links.next.href");
        String headerValue = response.header("headerName");

    }
}
