package scenarios;

import io.qameta.allure.Description;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import reuse.AllureLog4jListener;
import reuse.BaseTest;
import reuse.RetryAnalyzer;
import java.io.IOException;
import java.util.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static java.lang.invoke.MethodHandles.lookup;

@SuppressWarnings("ALL")
@Listeners(AllureLog4jListener.class)
public class TrelloAPITests extends BaseTest {

    private final SoftAssert softAssert = new SoftAssert();
    private Map<String, String> filteredQueryParameters;
    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private String boardId;
    private String boardName;
    private final List<String> listsIds = new ArrayList<>();
    private final List<String> listsNames = new ArrayList<>();
    private final List<String> cardsIds = new ArrayList<>();
    private final List<String> cardsNames = new ArrayList<>();
    private final Map<String, String> checklistsNamesIds = new LinkedHashMap<>();
    private final Map<String, String> listsNamesIds = new LinkedHashMap<>();
    private final Map<String, String> cardsNamesIds = new LinkedHashMap<>();
    private final Map<String, String> checkItemsNamesIds = new LinkedHashMap<>();


    @Description("Delete the created board")
    @Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
    public void DeleteBoard() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        boardId = propertyUpdate.getPropertyUpdate("boardId");
        if (!boardId.isEmpty()) {
            try {
                Response res =
                        given()
                                .baseUri(url)
                                .contentType(ContentType.JSON)
                                .queryParams(keyTokenMap)
                                .when()
                                .delete(boardsEndPoint + propertyUpdate.getPropertyUpdate("boardId"))
                                .then()
                                .extract().response();
                if (res.getBody().asString().isEmpty()) {
                    log.error("The API response for '{}' method is empty!", methodName);
                } else {
                    log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
                }
                log.debug("Starting assertion to ensure the delete of the board");
                String empty = res.path("_value");
                softAssert.assertEquals(null, empty, "The board hasn't been deleted!");
                softAssert.assertEquals(res.getStatusCode(), 200);
                softAssert.assertAll();
                log.info("The Board is deleted successfully");
                log.info("Testcase: {} completed successfully.", methodName);
            } catch (Exception e) {
                log.warn("The board of the id {} isn't exist '{}'", boardId, e.getStackTrace());
            }
        } else {
            log.info("There is no boardId '{}' logged in the property file to be deleted", propertyUpdate.getPropertyUpdate("boardId"));
        }
    }

    @Description("Create a board on the trello boards with the attached name")
    @Test(dependsOnMethods = "DeleteBoard", retryAnalyzer = RetryAnalyzer.class)
    public void CreateBoard() throws IOException {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        filteredQueryParameters = getMapByKey(queryParamMap, "CreateBoard");
        log.debug("Request Param loaded for '{}' method: {}", methodName, filteredQueryParameters);
        //----------------------------------------------------------------//
        log.info("Sending POST '{}' request to the URL: {}{}", methodName, url, boardsEndPoint);
        Response res =
                given()
                        .baseUri(url)
                        .contentType(ContentType.JSON)
                        .queryParams(keyTokenMap)
                        .queryParams(filteredQueryParameters)
                        .when()
                        .post(boardsEndPoint)
                        .then()
                        .statusCode(200)
                        .extract().response();
        if (res.getBody().asString().isEmpty()) {
            log.error("The API response for '{}' method is empty!", methodName);
        } else {
            log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
        }
        boardId = res.body().path("id").toString();
        boardName = res.body().path("name").toString();
        softAssert.assertEquals(boardName, filteredQueryParameters.get("name"));
        softAssert.assertAll();
        log.info("The name of the board is created as expected with name: {}", boardName);
        propertyUpdate.setProperty("boardId", boardId);
        log.info("Testcase: '{}' completed successfully.", methodName);
    }

    @Description("Get the board to check that the board is created successfully")
    @Test(dependsOnMethods = {"CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void GetBoard() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        log.info("Sending Get '{}' request to the URL: {}{}", methodName, url, boardsEndPoint);
        Response res =
                given()
                        .baseUri(url)
                        .contentType(ContentType.JSON)
                        .queryParams(keyTokenMap)
                        .when()
                        .get(boardsEndPoint + boardId)
                        .then()
                        .assertThat().body("id", is(equalTo(boardId)))
                        .statusCode(200)
                        .extract().response();
        if (res.getBody().asString().isEmpty()) {
            log.error("The API response for '{}' method is empty!", methodName);
        } else {
            log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
        }
        String actualBoardName = res.body().path("name").toString();
        String actualBoardId = res.body().path("id").toString();
        softAssert.assertEquals(boardName, actualBoardName,
                "The board name: " + actualBoardName + " isn't the same as expected: " + boardName);
        softAssert.assertEquals(boardId, actualBoardId,
                "The board Id: " + actualBoardName + " isn't the same as expected: " + boardId);
        softAssert.assertAll();
        log.info("The name of the board is found as expected with name: {} and the expected Id {}", actualBoardName, actualBoardId);
        log.info("Testcase: '{}' completed successfully.", methodName);


    }

    @Description("Create list/s depending on chosen number with the attached name/s")
    @Test(dependsOnMethods = {"GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void CreateList_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        for (int flag = 1; flag <= lists.size(); flag++) {
            filteredQueryParameters = getMapByKey(queryParamMap, lists.get(flag - 1));
            log.debug("Request Param loaded for '{}' method: {}", methodName, filteredQueryParameters);
            //----------------------------------------------------------------//
            log.info("Sending POST '{}' request to the URL: {}{}", methodName, url, listsEndPoint);
            Response res =
                    given().baseUri(url)
                            .contentType(ContentType.JSON)
                            .queryParams(keyTokenMap)
                            .queryParams(filteredQueryParameters)
                            .queryParam("idBoard", boardId)
                            .when()
                            .post(listsEndPoint)
                            .then()
                            .statusCode(200)
                            .extract().response();
            if (res.getBody().asString().isEmpty()) {
                log.error("The API response for '{}' method is empty!", methodName);
            } else {
                log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
            }
            listsIds.add(res.path("id").toString());                             //To save all the lists id created.
            listsNames.add(res.path("name").toString());                         //To save all the lists names created.
            listsNamesIds.put(res.path("name").toString(), res.path("id").toString());
            log.debug("Starting assertion for the created name of the list/s");
            softAssert.assertEquals(res.path("name").toString(), filteredQueryParameters.get("name"));
            softAssert.assertAll();
            log.info("The name of the list '{}' is created as expected with name: {}", flag, res.path("name").toString());
        }
        log.info("Testcase: {} completed successfully.", methodName);

    }

    @Description("Get list/s to check that the list/s are/is created successfully")
    @Test(dependsOnMethods = {"CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void GetList_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        for (int flag = 1; flag <= lists.size(); flag++) {
            log.info("Sending GET '{}' request to the URL: {}{}", methodName, url, boardsEndPoint);
            Response res =
                    given()
                            .baseUri(url)
                            .contentType(ContentType.JSON)
                            .queryParams(keyTokenMap)
                            .when()
                            .get(listsEndPoint + listsIds.get(flag - 1))
                            .then()
                            .statusCode(200)
                            .extract().response();
            if (res.getBody().asString().isEmpty()) {
                log.error("The API response for '{}' method is empty!", methodName);
            } else {
                log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
            }
            log.debug("Starting assertion for the created id/name of the list/s");
            softAssert.assertEquals(res.path("id").toString(), listsIds.get(flag - 1)
                    , "The Created list id isn't the same as expected");
            softAssert.assertEquals(res.path("name").toString(), listsNames.get(flag - 1)
                    , "The Created list name isn't the same as expected");
            softAssert.assertEquals(res.path("idBoard").toString(), boardId
                    , "The Created list isn't exist on the expected board");
            softAssert.assertAll();
            log.info("The id of the list is found as expected with name: {}", listsIds.get(flag - 1));
            log.info("The name of the list is found as expected with name: {}", listsNames.get(flag - 1));
        }
        log.info("Testcase: {} completed successfully.", methodName);
    }

    @Description("Create card/s based on chosen number with the attached name/s to a specific list")
    @Test(dependsOnMethods = {"GetList_s", "CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void CreateCard_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        for (int flag = 1; flag <= cardsList.size(); flag++) {
            filteredQueryParameters = getMapByKey(queryParamMap, cardsList.get(flag - 1));
            log.debug("Request Param loaded for '{}' method: {}", methodName, filteredQueryParameters);
            //----------------------------------------------------------------//
            log.info("Sending POST '{}' request to the URL: {}{}", methodName, url, cardsEndPoint);
            Response res =
                    given().baseUri(url)
                            .contentType(ContentType.JSON)
                            .queryParams(keyTokenMap)
                            .queryParams(filteredQueryParameters)
                            .queryParam("idList", listsIds.getLast())
                            .when()
                            .post(cardsEndPoint)
                            .then()
                            .statusCode(200)
                            .extract().response();
            if (res.getBody().asString().isEmpty()) {
                log.error("The API response for '{}' method is empty!", methodName);
            } else {
                log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
            }
            cardsNamesIds.put(res.path("name").toString(), res.path("id").toString());
            cardsIds.add(res.path("id").toString());                             //To save all the cards id created.
            cardsNames.add(res.path("name").toString());                         //To save all the cards names created.
            log.debug("Starting assertion for the created name of the card/s");
            softAssert.assertEquals(res.path("name").toString(), filteredQueryParameters.get("name"));
            softAssert.assertAll();
            log.info("The name of the cards '{}' is created as expected with name: {}", flag, res.path("name").toString());
        }
        log.info("Testcase: {} completed successfully.", methodName);
    }

    @Description("Get card/s to check that the card/s are/is created successfully")
    @Test(dependsOnMethods = {"CreateCard_s", "GetList_s", "CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void GetCard_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        for (int flag = 1; flag <= cardsList.size(); flag++) {
            log.info("Sending GET '{}' request to the URL: {}{}", methodName, url, cardsEndPoint);
            Response res =
                    given()
                            .baseUri(url)
                            .contentType(ContentType.JSON)
                            .queryParams(keyTokenMap)
                            .when()
                            .get(cardsEndPoint + cardsIds.get(flag - 1))
                            .then()
                            .statusCode(200)
                            .extract().response();
            if (res.getBody().asString().isEmpty()) {
                log.error("The API response for '{}' method is empty!", methodName);
            } else {
                log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
            }
            log.debug("Starting assertion for the created id/name of the card/s");
            softAssert.assertEquals(res.path("id").toString(), cardsIds.get(flag - 1)
                    , "The Created card id isn't the same as expected");
            softAssert.assertEquals(res.path("name").toString(), cardsNames.get(flag - 1)
                    , "The Created list name isn't the same as expected");
            softAssert.assertEquals(res.path("idList").toString(), listsIds.getLast()
                    , "The Created card isn't exist on the expected list");
            softAssert.assertAll();
            log.info("The id of the card is found as expected with name: {}", cardsIds.get(flag - 1));
            log.info("The name of the card is found as expected with name: {}", cardsNames.get(flag - 1));
        }
        log.info("Testcase: {} completed successfully.", methodName);
    }

    @Description("Create checklist/s depending on chosen number with the attached name/s to a specific list")
    @Test(dependsOnMethods = {"GetCard_s", "CreateCard_s", "GetList_s", "CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void CreateChecklist_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");       /* looping for the total no of checklistsPerCard
         then per each checklist looping for each value inside creating the name of the checklist per card*/
        for (int flag = 1; flag <= checkList.size(); flag++) {
            List<String> tempCheckListValues = getListByKey(queryParamMap, checkList.get(flag - 1));
            String tempCardId = getObjectId(cardsNamesIds, checkList.get(flag - 1), "card");
            if (tempCardId == null) {
                continue;
            }
            for (int i = 0; i < tempCheckListValues.size(); i++) {
                log.info("Sending POST '{}' request to the URL: {}{}{}{}", methodName, url, cardsEndPoint, cardsIds.get(flag - 1), checklistEndPoint);
                Response res =
                        given().baseUri(url)
                                .contentType(ContentType.JSON)
                                .queryParams(keyTokenMap)
                                .queryParams("name", tempCheckListValues.get(i))
                                .when()
                                .post(cardsEndPoint + tempCardId + checklistEndPoint)
                                .then()
                                .statusCode(200)
                                .extract().response();
                if (res.getBody().asString().isEmpty()) {
                    log.error("The API response for '{}' method is empty!", methodName);
                } else {
                    log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
                }
                checklistsNamesIds.put(res.path("name").toString().replaceAll("\\s+", ""), res.path("id").toString());    // save the id & name of each checklist as a map
                log.debug("Starting assertion for the created name of the checklist/s");
                softAssert.assertEquals(res.path("name").toString(), tempCheckListValues.get(i));
                softAssert.assertAll();
                log.info("The name of the checklists {} for card no" +
                        " '{}' is created as expected with name: {}", i + 1, flag, res.path("name").toString());
            }
        }
        log.info("Testcase: {} completed successfully.", methodName);
    }

    @Description("Get checklist/s to check that the checklists/s are/is created successfully to specific card/s")
    @Test(dependsOnMethods = {"CreateChecklist_s", "GetCard_s", "CreateCard_s", "GetList_s", "CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void GetChecklist_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");        /* looping for the total no of checklistsPerCard
         then per each checklist looping for each value inside get the Id of the checklist to get the response */
        for (int flag = 1; flag <= checkList.size(); flag++) {
            List<String> tempCheckListValues = getListByKey(queryParamMap, checkList.get(flag - 1));
            String tempCardId = getObjectId(cardsNamesIds, checkList.get(flag - 1), "card");
            if (tempCardId == null) {
                continue;
            }
            for (int i = 0; i < tempCheckListValues.size(); i++) {
                String tempChecklistId = getObjectId(checklistsNamesIds, tempCheckListValues.get(i), "checklist");
                log.info("Sending Get '{}' request to the URL: {}{}{}", methodName, url, checklistEndPoint, tempChecklistId);
                Response res =
                        given().baseUri(url)
                                .contentType(ContentType.JSON)
                                .queryParams(keyTokenMap)
                                .when()
                                .get(checklistEndPoint + tempChecklistId)
                                .then()
                                .statusCode(200)
                                .extract().response();
                if (res.getBody().asString().isEmpty()) {
                    log.error("The API response for '{}' method is empty!", methodName);
                } else {
                    log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
                }
                log.debug("Starting assertion for the created name of the checklist/s");
                softAssert.assertEquals(res.path("id").toString(), tempChecklistId
                        , "The Created checklist id isn't the same as expected");
                softAssert.assertEquals(res.path("name").toString(), tempCheckListValues.get(i)
                        , "The Created checklist name isn't the same as expected");
                softAssert.assertEquals(res.path("idCard").toString(), tempCardId
                        , "The Created checklist isn't exist in the expected card");
                log.info("The id of the checklist is found as expected with name: {}", res.path("id").toString());
                log.info("The name of the checklists no. '{}'  for card id " +
                        " '{}' is created as expected with name: '{}'", i + 1, tempCardId, res.path("name").toString());
                softAssert.assertAll();
            }
            log.info("Testcase: {} completed successfully.", methodName);
        }
    }

    @Description("Create checkItems depending on chosen number with the attached name/s to a specific checklist")
    @Test(dependsOnMethods = {"GetChecklist_s", "CreateChecklist_s", "GetCard_s", "CreateCard_s", "GetList_s", "CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void CreateCheckItem_sOnChecklist_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");       /* looping for the total values of checkItems generated in a list , create the list of every checkItem
        and loop for every item on it while having the value of the list*/
        for (int flag = 1; flag <= checkItemsList.size(); flag++) {
            List<String> temp = getListByKey(queryParamMap, checkItemsList.get(flag - 1));
            String tempCheckListId = getCheckListId(checklistsNamesIds, checkItemsList.get(flag - 1));
            if (tempCheckListId == null) {
                continue;
            }
            for (String s : temp) {
                log.info("Sending Post '{}' request to the URL: {}{}{}{}", methodName, url, checklistEndPoint, tempCheckListId, checkItemsEndPoint);
                Response res =
                        given().baseUri(url)
                                .contentType(ContentType.JSON)
                                .queryParams(keyTokenMap)
                                .queryParams("name", s)
                                .when()
                                .post(checklistEndPoint + tempCheckListId + checkItemsEndPoint)
                                .then()
                                .statusCode(200)
                                .extract().response();

                if (res.getBody().asString().isEmpty()) {
                    log.error("The API response for '{}' method is empty!", methodName);
                } else {
                    log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
                }
                checkItemsNamesIds.put(res.path("name").toString().replaceAll("\\s+", ""), res.path("id").toString());    // save the id & name of each checkItem as a map
                log.debug("Starting assertion for the created name of the checkItem/s");
                softAssert.assertEquals(res.path("name").toString(), s);
                softAssert.assertAll();
                log.info("The name of the checkItem '{}' for checklist named" +
                        " '{}' is created as expected with name: '{}'", res.path("name").toString(), checkItemsList.get(flag - 1), s);
            }
        }
        log.info("Testcase: {} completed successfully.", methodName);
    }

    @Description("Get checkItem/s to check that the checkItem/s are/is created successfully to specific checklist/s")
    @Test(dependsOnMethods = {"CreateCheckItem_sOnChecklist_s", "GetChecklist_s", "CreateChecklist_s", "GetCard_s", "CreateCard_s", "GetList_s", "CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void GetCheckItem_sOnChecklist_s() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");       /* looping for the total values of checkItems generated in a list , get the list of every checkItem
        and loop for every item on it while having the value of the list*/
        for (int flag = 1; flag <= checkItemsList.size(); flag++) {
            List<String> temp = getListByKey(queryParamMap, checkItemsList.get(flag - 1));
            String tempCheckListId = getCheckListId(checklistsNamesIds, checkItemsList.get(flag - 1));
            if (tempCheckListId == null) {
                continue;
            }
            for (int i = 0; i < temp.size(); i++) {
                log.info("Sending GET '{}' request to the URL: {}{}{}{}", methodName, url, checklistEndPoint, tempCheckListId, checkItemsEndPoint);
                String tempCheckItemId = getObjectId(checkItemsNamesIds, temp.get(i), "checkItem");
                Response res =
                        given().baseUri(url)
                                .contentType(ContentType.JSON)
                                .queryParams(keyTokenMap)
                                .when()
                                .get(checklistEndPoint + tempCheckListId + checkItemsEndPoint + tempCheckItemId)
                                .then()
                                .statusCode(200)
                                .extract().response();

                if (res.getBody().asString().isEmpty()) {
                    log.error("The API response for '{}' method is empty!", methodName);
                } else {
                    log.info("API request was successfully sent '{}' and the response is logged to a file as : {}", methodName, res.asPrettyString());
                }
                log.debug("Starting assertion for the created name of the checklist/s");
                softAssert.assertEquals(res.path("id").toString(), tempCheckItemId
                        , "The Created checklist id isn't the same as expected");
                softAssert.assertEquals(res.path("name").toString(), temp.get(i)
                        , "The Created checklist name isn't the same as expected");
                softAssert.assertEquals(res.path("idChecklist").toString(), tempCheckListId
                        , "The Created checkItem isn't exist in the expected checklist");
                log.info("The id of the checklist is found as expected with name: {}", res.path("id").toString());
                log.info("The name of the checkItem no. '{}'  for checklist id " +
                        " '{}' is created as expected with name: '{}'", i + 1, tempCheckListId, res.path("name").toString());
                softAssert.assertAll();
            }
        }
        log.info("Testcase: {} completed successfully.", methodName);
    }

    @Description("Put/Move the chosen card/s to the desired list/s")
    @Test(dependsOnMethods = {"GetCheckItem_sOnChecklist_s", "CreateCheckItem_sOnChecklist_s", "CreateChecklist_s", "CreateCard_s", "GetCard_s", "GetList_s", "CreateList_s", "GetBoard", "CreateBoard", "DeleteBoard"}, retryAnalyzer = RetryAnalyzer.class)
    public void MoveCardsToLists() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        log.info("*********************************************************");
        log.info("---> Starting the test case: {}", methodName);
        log.info("*********************************************************\n");
        for (int flag = 1; flag <= moveToList.size(); flag++) {
            List<String> temp = getListByKey(queryParamMap, moveToList.get(flag - 1));
            if (temp == null) {
                continue;
            }
            for (String s : temp) {
                log.info("Sending Put '{}' request to the URL: {}{}{}{}", methodName, url,
                        cardsEndPoint, getObjectId(listsNamesIds, moveToList.get(flag - 1),
                                "list"), getObjectId(cardsNamesIds, s, "card"));
                String tempListId = getObjectId(listsNamesIds, moveToList.get(flag - 1), "list");
                String tempCardId = getObjectId(cardsNamesIds, s, "card");

                Response res =
                        given()
                                .baseUri(url)
                                .contentType(ContentType.JSON)
                                .queryParams(keyTokenMap)
                                .queryParams("idList", tempListId)
                                .when()
                                .put(cardsEndPoint + tempCardId)
                                .then()
                                .statusCode(200)
                                .extract().response();
                log.debug("Starting assertion that card moved to the desired List");
                softAssert.assertEquals(res.path("id").toString(), tempCardId
                        , "The CardId is the same as expected");
                softAssert.assertEquals(res.path("idList").toString(), tempListId
                        , "The listId is the same as expected");
                softAssert.assertAll();
            }
        }

        log.info("Testcase: {} completed successfully.", methodName);
    }
}






