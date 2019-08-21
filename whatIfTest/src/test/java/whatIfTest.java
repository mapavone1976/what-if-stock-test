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

public class whatIfTest {

    private static final int POSCOLCLOSE = 4;
    private static final String PERMISSIONFAILEDCODE = "QEPx04";
    private static final String SYMBOLINCORRECTCODE = "QECx02";

    @Test
    public void whatIfTest_M() {

        //Positive Paths
        System.out.println("APPL");
        TestSymbol("AAPL");
        System.out.println("HD");
        TestSymbol("HD");
        System.out.println("MSFT");
        TestSymbol("MSFT");
        System.out.println("FB");
        TestSymbol("FB");

        //Negative Path
        System.out.println("Test1");
        TestNegativePath("Test1");
        System.out.println("Test2");
        TestNegativePath("Test2");


    }

    private void TestSymbol(String xSymbol) {
        try {
            //Declaring variables
            String sMaxDate; String sMaxValue; int sMaxIndex;
            String sMinDate; String sMinValue; int sMinIndex;

            //Reading CSV File
            RestAssured.baseURI ="https://stock-whatif.herokuapp.com/whatif";
            Response response = given().contentType("text").when().get("/" + xSymbol + "/raw").then().statusCode(200).
                    extract().response();

            if (response.asString().indexOf(SYMBOLINCORRECTCODE) != -1 ) {
                throw new IOException( "The symbol doesn't exists " + xSymbol);
            }

            if (response.asString().indexOf(PERMISSIONFAILEDCODE) != -1 ) {
                throw new IOException("You don have permissions to see the symbol " + xSymbol);
            }

            InputStreamReader readerCSV = new InputStreamReader(response.asInputStream());

            // create csvParser and CSVReader objects
            CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(readerCSV).withCSVParser(parser).withSkipLines(1).build();

            // Read all data at once from CSV
            List<String[]> allData = csvReader.readAll();

            //Getting Max & Min
            sMaxIndex = getMaxLine(allData);
            //sMaxDate = new SimpleDateFormat("yyyy-MM-dd").parse( allData.get(sMaxIndex)[0]);
            sMaxDate = allData.get(sMaxIndex)[0];
            //sMaxValue = Double.parseDouble(allData.get(sMaxIndex)[POSCOLCLOSE]);
            sMaxValue = allData.get(sMaxIndex)[POSCOLCLOSE];

            sMinIndex = getMinLine(allData);
            //sMinDate = new SimpleDateFormat("yyyy-MM-dd").parse( allData.get(sMinIndex)[0]);
            sMinDate = allData.get(sMinIndex)[0];
            //sMinValue = Double.parseDouble(allData.get(sMinIndex)[POSCOLCLOSE]);
            sMinValue = allData.get(sMinIndex)[POSCOLCLOSE];

            //Checking API Return
            given().contentType("application/json").accept("application/jason").when().get("/" + xSymbol).then().
                    statusCode(200).
                    assertThat().body(matchesJsonSchemaInClasspath("Template.json")).   //All data types are validated in JSon format template
                    assertThat().body("high.date", equalTo(sMaxDate) ).
                    assertThat().body("high.price", equalTo(sMaxValue) ).
                    assertThat().body("low.date", equalTo( sMinDate) ).
                    assertThat().body("low.price", equalTo( sMinValue) );

            //Checking API Return - Reinvest - I include it just to show the behavior reported un Bug #2.
            given().contentType("application/json").accept("application/jason").when().get("/" + xSymbol + "?reinvest=true").then().
                    statusCode(200).
                    assertThat().body(matchesJsonSchemaInClasspath("Template.json")).   //All data types are validated in JSon format template
                    assertThat().body("high.date", equalTo(sMaxDate) ).
                    assertThat().body("high.price", equalTo(sMaxValue) ).
                    assertThat().body("low.date", equalTo( sMinDate) ).
                    assertThat().body("low.price", equalTo( sMinValue) );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void TestNegativePath(String xSymbol) {
        try {

            RestAssured.baseURI ="https://stock-whatif.herokuapp.com/whatif";

            //Checking API Return
            given().contentType("application/json").accept("application/jason").when().get("/" + xSymbol).then().
                    statusCode(200).
                    assertThat().body(matchesJsonSchemaInClasspath("Template.json"))  ; //All data types are validated in JSon format template

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getMaxLine (List<String []> xCSV) {
        int i;
        int iMax;
        double sVAC;
        double sMaxVAC;

        i=0;
        sMaxVAC = 0;
        iMax = 0;
        for (String[] row : xCSV) {
            sVAC = Double.parseDouble( row[POSCOLCLOSE]);
            if (sVAC > sMaxVAC ) {
                iMax    = i;
                sMaxVAC = sVAC;
            }

            i++;
        }

        return iMax;

    }

    private int getMinLine (List<String []> xCSV) {
        int i;
        int iMin;
        double sVAC;
        double sMinVAC;

        i=0;
        sMinVAC = 999999;
        iMin = 9999999;
        for (String[] row : xCSV) {
            sVAC = Double.parseDouble( row[POSCOLCLOSE]);
            if (sVAC < sMinVAC ) {
                iMin    = i;
                sMinVAC = sVAC;
            }

            i++;
        }

        return iMin;

    }

}