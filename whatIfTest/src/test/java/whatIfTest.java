import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.equalTo;

import static io.restassured.RestAssured.given;

import org.junit.Test;

public class whatIfTest {

    @Test
    public void whatIfTest_M() {
        RestAssured.baseURI ="https://stock-whatif.herokuapp.com/whatif";

        Response response = given().
                contentType("application/json").
                accept("application/jason").
            when().
                get("/AAPL").
            then().
                statusCode(200).
                assertThat().body("high.date", equalTo( "2018-10-03") ).
                assertThat().body("high.price", equalTo( "232.07") ).
                assertThat().body("low.date", equalTo( "2019-01-03") ).
                assertThat().body("low.price", equalTo( "142.19") ).
                extract().
                response();

        response.print();

        Response response2 = given().
                contentType("application/json").
                accept("application/jason").
                when().
                get("/HD").
                then().
                statusCode(200).
                assertThat().body("high.date", equalTo( "2018-09-11") ).
                assertThat().body("high.price", equalTo( "213.85") ).
                assertThat().body("low.date", equalTo( "2018-12-24") ).
                assertThat().body("low.price", equalTo( "158.14") ).
                extract().
                response();

        response2.print();

        Response response3 = given().
                contentType("application/json").
                accept("application/jason").
                when().
                get("/MSFT").
                then().
                statusCode(200).
                assertThat().body("high.date", equalTo( "2019-04-30") ).
                assertThat().body("high.price", equalTo( "130.6") ).
                assertThat().body("low.date", equalTo( "2018-02-08") ).
                assertThat().body("low.price", equalTo( "85.01") ).
                extract().
                response();

        response3.print();

    }

}