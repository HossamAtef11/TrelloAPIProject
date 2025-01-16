# API Test Automation for Trello
___
## TrelloAPI

This **repository** contains API test cases using **RestAssured** and **TestNG** to validate Trello functionalities.  
Tests cover key operations like board creation, list management, card creation, checklist handling, and item manipulation.  
The goal is to **ensure that the Trello API behaves as expected depending on the required entered data**.

___
## Test Scenarios 

1. **Delete a board:** If a board is already created, it will be deleted before running any new tests. If the board doesn't exist, the test will pass.
2. **Create a board:** A new board will be created with the specified name on Trello and verified for correctness.
3. **Get the board:** Retrieves the board by its ID to verify that the board was created successfully with the expected name and Id.
4. **Create lists:** Multiple lists will be created on the board and verified for correctness.
5. **Get lists:** Retrieves each created list to verify their successful creation on the board with the correct name and Id.
6. **Create cards:** Multiple cards will be created on the last list and verified for correctness.
7. **Get cards:** Retrieves each created card to verify their successful creation on the list with the correct name and Id.
8. **Create checklists:** Creates checklists with specified names for each card and validates the creation with correct names and Ids.
9. **Get checklists:** Retrieves checklists for cards and validates that each checklist exists with the correct name and Id.
10. **Create check items:** Creates check items with specified names within checklists and validates that each check item exists with the correct name and Id.
11. **Get check items:** Retrieves check items from checklists and validates that each check item exists with the correct name, Id, and associated checklist.
12. **Move cards to lists:** Moves cards to specified lists and validates that the cards are moved successfully to the desired list.
___
## Prerequisites

**Before running the tests,** Ensure you have the following:

- **Java**        (Version 8 or higher)
- **Maven**       (For dependency management)
- **RestAssured** (For API testing)
- **TestNG**      (For running the test cases)
- **Allure**      (For test reporting)
- **Log4j**       (For logging)
- **Gson**      (For JSON serialization and deserialization)

___
## Project Demo
- **Demo:**  [Trello API](https://drive.google.com/file/d/1veP_iqAhvaRzM-z43z7dhyCUn7Hkb7em/view?usp=sharing)
___
## Project Structure
```
Trello.API
│
├── java
│
├── logs     #Log Directory
│
├── resources
│   ├── config.properties         #Contain Configurations
│   ├── log4j.properties          #Log4j Configurations
│   ├── log4j2.xml                #Alternative Log4j XML Configurations
│   ├── queryParm.json            #TestData Storage
│   ├── queryParamKeyToken.json   #Credentials TestData Storge
│   └── updated.properties        #Read/Write TestData
│
│── test
│    ├── java
│        ├── reuse
│        │   ├── Allurelog4jListener.java   
│        │   ├── BaseTest.java
│        │   ├── ConfigLoader.java
│        │   └── RetryAnalyzer.java
│        └── scenarios
│── target          └── TrelloApiTestCases.java
│
├── pom.xml              #Maven Configuration File
├── README.md
├── RunningFile.xml      #XML Running Testing Suite
├── RunScript.bat        #Batch File for Running Project
```

___
## Classes & TestCases

## Testcases

1. ### **Test Case: DeleteBoard (Delete Existing Board)**

   - This test case is designed to verify the deletion of a previously created board in Trello via an API endpoint.
     It sends a **DELETE** request to the Trello API to remove a board based on the `boardId`.
     The test ensures that the response is not empty and confirms the successful deletion of the board.
     Note: `If the updated.properties file doesn't contain a value for the boardId, the test case will pass, logging that there is no board to be deleted.`

   **Test Method:**
   - **DeleteBoard():** Deletes the specified board by sending a **DELETE** request with the `boardId`.

   **Test Steps:**
   1. The test reads the boardId from the property file.
   2. A DELETE request is sent to the Trello API using the boardId.
   3. The API response is validated:
      1. The status code should be 200. 
      2. The response body should indicate the board has been deleted.
   
   4. The test logs the API request, response, and verification of the deletion.
   
   **Expected Result:**

       1. The board should be successfully deleted, and the response should confirm the deletion.
       2. The test should pass if the board is deleted as expected.

___

2. ###  **Test Case: CreateBoard**

   - This test case is designed to **create a new board** in Trello using a **POST** request to the Trello API. 
   It depends on the successful execution of the **DeleteBoard test case** to ensure that the environment is clean and ready for creating a new board.

   **Test Method:**
   - **CreateBoard():** Sends a **POST** request to the Trello API to create a new board with the specified name.

   **Test Steps:**

   1. The test case begins by reading the board creation parameters from the `json file (queryParm) CreateBoard`
   2. It sends a **POST** request to the Trello API to create a new board with the specified name 
   and other relevant parameters (such as API key, token).
   3. The API response is validated:
      1. The status code should be 200. indicating successful creation of the board.
      2. The response body should contain the correct boardName.
   4. The test logs the API request, response, and validation of the board creation.
   5. The `boardId` is saved in the property file for future use (for deletion in the DeleteBoard test case).
   
   **Expected Result:**

       1. A new board should be created successfully, and the response should confirm the creation with the boardName.
       2. The test should pass if the board is created as expected, and the boardId is saved correcyly.

___

3. ###  **Test Case: GetBoard (Verify the Board Creation)**

   - This test case is designed to **verify that the board created** in Trello is successfully retrievable via the API. 
     It sends a **GET** request to retrieve the board details and checks that the boardId matches the expected value.

   **Test Method:**
   - **GetBoard():** Sends a **GET** request to the Trello API to fetch the details of a board by using the `boardId`.

   **Test Steps:**

   1. The test begins by sending a **GET** request to the Trello API to retrieve the board details using the `boardId`.
   2. The API response is validated:
      1. The status code should be 200, indicating successful retrieval of the board.
      2. The response body should contain the correct `boardId`.
      3. The board name in the response should match the expected boardName.
   3. The test logs the API request, response, and verifies the values of `boardId` and `boardName`.
   4. The test checks if the actual `boardId` and `boardName` match the expected values.
   5. If both values match, the test passes successfully.

   **Expected Result:**

       1. The boardId from the response should match the expected boardId.
       2. The boardName from the response should match the expected boardName.

___

4. ### **Test Case: CreateList/s (Create Lists in Trello Board)**

   - This test case is designed to **create one or more lists** in a Trello board using a **POST** request to the Trello API. 
     It checks that the lists are created with the correct names and associates each list with the board by passing the boardId.
     The number of lists created depends on the input parameter `json file (queryParm) CreateList.`

   **Test Method:**

   - **CreateList_s():** Sends a **POST** request to the Trello API to **create multiple lists** on a specified board. 
     Each list's name is passed from the input parameter, and the API response is validated

   **Test Steps:**
   1. The test case starts by iterating over a list of names to create the corresponding number of lists.
   2. For each list, it sends a POST request to the Trello API with the following parameters:
      1. `idBoard` (the ID of the board to which the list will be added).
      2. `name` (the name of the list to be created).
   3. The API response is validated:
      1. The status code should be 200, indicating successful creation of the list.
      2. The response body should contain the correct `name` and `id` for each created list.
      3. The board name in the response should match the expected `boardName`.
   4. The created list's `id` and `name` are logged and stored for later use.
   5. Each created list's `name` is validated to match the name passed in the request.
   6. The test logs the request, response, and the validation results for each list.

   **Expected Outcome:**

       1. The correct number of lists should be created with the expected names.
       2. The id and name of each list should match the values passed in the request.
       3. The test should pass if all lists are created successfully and the names are as expected

___

5. ### **Test Case: GetList/s (Get Lists in Trello Board)**

   - This test case is designed to **verify that the lists created** in the Trello board are successfully retrievable via the API. 
     It sends a **GET** request to retrieve each list's details by its listId, checking that the listId, listName, and idBoard match the expected values.

   **Test Method:**
   - **GetList_s():** Sends a **GET** request to the Trello API to fetch the details of the lists by using their listId.

   **Test Steps:**
   1. The test case begins by iterating over the list of created lists and sending a GET request for each list using its `listId`.
   2. For each list, the following validations are performed:
      1.  The status code should be 200, indicating successful retrieval of the list.
      2.  The response body should contain the correct `id` (matching the expected `listId`).
      3.  The `name` of the list should match the expected `listName`.
      4.  The `idBoard` of the list should match the expected `boardId` to ensure that the list exists on the correct board.
   3. The created list's `id`, `name`, and `idBoard` are logged and validated against the expected values.
   4. The test logs the request, response, and assertion results for each list.

   **Expected Result:**

       1. The listId and listName from the response should match the expected listId and listName values.
       2. The idBoard from the response should match the expected boardId to confirm that the list belongs to the correct board.
       3. The test should pass if all lists are retrieved successfully with the correct details.

___

6. ### **Test Case: CreateCard/s (Create Cards in a List on Trello Board)**

   - This test case is designed to **create one or more cards** in a specified list within a Trello board. 
   It sends a **POST** request to the Trello API to create each card, with the card's name passed from the query parameter. 
   The created cards are associated with the specified listId, which refers to an existing list on the board 
   (`The last list added/created "existing in the queryParm file"`).

   **Test Method:**
   - **CreateCard_s():** Sends a **POST** request to the Trello API to create multiple cards in a specified list within a Trello board. 
     Each card's name is passed from the input parameter, and the API response is validated.

   **Test Steps:**
   1. The test case begins by iterating over a list of card names `json file (queryParm) CreateCard` and sending a **POST** request for each card to create it within a specified list on the Trello board.
   2. The request parameters include:
      1.  `idList` (The `Id` of the list in which the card will be created).(`The last list created/entered in the queryParam file`).
      2. `name` (The `name` of the card to be created).(`The name of all the card entered in the queryParam with key 'CreateCard'`).
   3. The API response is validated:
      1. The status code should be 200, indicating successful creation of the card.
      2. The response body should contain the correct name and id for each created card.
   4. The created `card's id` and `name` are logged and stored.
   5. Each created `card's name` is validated to match the name passed in the request.
   6. The test logs the request, response, and validation results for each card.

   **Expected Result:**

       1. The correct number of cards should be created with the expected names.
       2. The id and name of each card should match the values passed in the request.
       3. The test should pass if all cards are created successfully and the names are as expected.

___

7. ### **Test Case: GetCard/s (Get Cards from Trello Board)**

   - This test case is designed to verify that the cards created on a Trello board are successfully retrievable via the API. 
   It sends a GET request to retrieve each card's details by its cardId, and checks that the id, name, and idList (`the list the card belongs to`) match the expected values.

   **Test Method:**
   - **GetCard_s():** Sends a **GET** request to the Trello API to fetch the details of the cards using their `cardId`.

   **Test Steps:**
   1. The test case begins by iterating over a list of cardsList (which contains the identifiers of the created cards) and sending a GET request for each card using its cardId.
   2. For each card, the following validations are performed:
      1. The status code should be 200, indicating successful retrieval of the card details.
      2. The `id` of the card in the response body should match the expected `cardId`.
      3. The `name` of the card in the response body should match the expected `cardName`.
      4. The `idList` (the list to which the card belongs) should match the expected `listId` to ensure the card exists in the correct list.
   3. The retrieved card's id, name, and idList are logged and validated against the expected values.
   4. The test logs the request, response, and assertion results for each card.

   **Expected Result:**

       1. The cardId and cardName from the response should match the expected cardId and cardName values.
       2. The idList from the response should match the expected listId to confirm that the card belongs to the correct list.
       3. The test should pass if all cards are retrieved successfully with the correct details.
   
___

8. ### **Test Case: CreateChecklist/s (Verify Creation of Checklists for Cards)**

   - This test case is designed to **create checklists for cards** in a Trello board by sending a **POST** request to the Trello API. 
    Each checklist's name is specified as a query parameter for the card.`CardNameChecklists`

   **Test Method:**
   - **CreateChecklist_s():** Sends a **POST** request to create checklists for each card in the Trello board.

   **Test Steps:**
   1. Loop through the list of checklists `CardNameChecklists`.
   2. For each checklist, retrieve the corresponding `card ID` by calling getListOrCardId().
   3. For each card, loop through the list of `checklist names` and send a POST request to create each checklist.
   4. For each checklist, the following validations are performed:
      1. Check that the status code of the response is 200, indicating successful creation.
      2. Ensure the response contains the correct name of the checklist.
   5. The created checklist’s `name` and `id` are logged and validated against the expected values.
   6. The test logs the request, response, and assertion results for each checklist.

   **Expected Result:**

       1. The correct number of checklists will be created for each card.
       2. Each checklist will have the correct name as specified in the request.
       3. The test should pass if all checklists are created successfully, and their names match the expected values.

___

9. ### **Test Case: CreateChecklist/s (Verify Creation of Checklists for Cards)**

   - This test case is designed to **retrieve checklists from cards** on a Trello board to verify that the **checklists have been successfully created in the specified card**.
     It sends a **GET** request for each checklist by its `checklistId` and validates the response.

   **Test Method:**
   - **GetChecklist_s():** Sends a GET request to the Trello API to fetch the details of each checklist using the checklistId.

   **Test Steps:**
   1. Loop through the checklists stored in `checkList.`
   2. For each checklist, retrieve the corresponding `cardId` and `checklistId` from the `checklistsNamesIds` map.
   3. For each checklist, send a GET request using the `checklistId` to retrieve its details.
   4. For each checklist, the following validations are performed:
      1. Check that the status code of the response is 200, indicating successful retrieval.
      2. Ensure the response body contains the correct `id`, `name`, and `idCard` values.
   5. Validate that the `id`, `name`, and `idCard` from the response match the expected values.
   6. The test logs the request, response, and assertion results for each checklist.

   **Expected Result:**

       1. The correct id, name, and idCard should be retrieved for each checklist.
       2. The response should contain the expected `id`, `name`, and `idCard` values.
       3. The test passes if all checklists are successfully retrieved with the correct values and associations.

___


10.  ###  **Test Case: CreateCheckItem/s (Verify Creation of CheckItems for Checklists)**
     - This test case is designed to **create checkItems on a specific checklist depending on the chosen number and attached names**. 
       It sends **POST** requests to create checkItems and validates their creation by checking the response.

     **Test Method:**
     - **createCheckItem/sOnChecklist/s():** Sends a **POST** request to create checkItems on a checklist and verifies the created checkItem's name.

     **Test Steps:**
     1. Loop through the list of checkItems `checkItemsList` to create checkItems for `each checklist`.
     2. For each checklist, retrieve the corresponding `checklistId` from the `checklistsNamesIds` map.
     3. For each checkItem:
        1. Send a **POST** request to create the checkItem on the checklist using the `checklistId`.
        2. The request includes the checkItem's name from the list.
     4. For each checkItem, the following validations are performed:
        1. Check that the response status code is 200, indicating successful creation.
        2. Ensure the response body contains the correct `name` of the checkItem.
        3. Validate that the `name` of the created checkItem matches the expected name.
        5. The test logs the request, response, and assertion results for each checkItem.

     **Expected Result:**

         1. The correct name of the checkItem should be created for each checklist.
         2. The response should return the expected name for each checkItem.
         3. The test passes if all checkItems are successfully created with the correct names.

___


11.  ###  **Test Case: GetCheckItem/s on Checklist/s (Verify Retrieval of CheckItems for Checklists)**
      - This test case is designed to **verify the successful retrieval of checkItems for specific checklists**. 
      It sends **GET** requests to fetch the checkItems and validates that the checkItems are correctly returned based on the checklist ID.

      **Test Method:**
      - **GetCheckItem_sOnChecklist_s():** Sends a **GET** request to retrieve checkItems from a checklist and verifies the retrieved checkItem's properties, such as ID, name, and checklist ID.

      **Test Steps:**
      1. Loop through the list of `checkItemsList` to retrieve **checkItems** for each checklist.
      2. For each checkItem, obtain the corresponding `checklistId` from the `checklistsNamesIds` map.
      3. For each checkItem in the list:
         1. Send a **GET** request to fetch the `checkItem` based on the checklistId.
         2. The request URL is constructed by appending the `checklist` and `checkItem` **endpoints** along with the `checklistId` and `checkItemId`.
      4. For each checkItem, the following checks are performed:
         1. Validate that the response status code is 200, indicating a successful retrieval.
         2. Ensure the response contains the correct `checkItemId`, `checkItem name`, and `checklistId`.
         3. Verify that the retrieved `checkItemId` matches the expected ID. .
         4. Ensure that the name of the retrieved `checkItem` matches the expected name.
      5. The test logs the request, response, and assertion results for each checkItem. 

      **Expected Result:**

          1. The correct checkItem should be successfully retrieved for each checklist.
          2. The retrieved checkItem's details (Id, name, checklistId) should match the expected values.
          3. The test should pass if all checkItems are successfully retrieved with the correct properties and the response returns the expected values for each checkItem.

___

12.  ###  **Test Case: Move Cards to Lists (Verify Moving Cards to Specified Lists)**
      - This test case is designed to **verify that cards are moved successfully to specific lists**. 
        It sends **PUT** requests to move cards from one list to another and validates that the card is correctly moved.

      **Test Method:**
      - **MoveCardsToLists():** Sends a **PUT** request to move cards to specific lists and verifies the successful move by checking the response data.
        
      **Test Steps:**
      1. Loop through the `moveToList` list to identify which list the cards should be moved to.
      2. For each list in the `moveToList` list, retrieve the corresponding `listId` from the `listsNamesIds` map.
      3. For each card in the list, retrieve the corresponding `cardId` from the `cardsNamesIds` map.
      4. Send PUT request to move each card:
          1. For each card, send a **PUT** request to move the **card to the desired list**.
          2. The request URL is constructed by appending the `cardsEndPoint` with the `cardId`, and the query parameter `idList` is set to the target `listId`.
      5. For each moved card, the following checks are performed:
          1. Validate that the response status code is 200, indicating the card was moved successfully.
          2. Ensure the response contains the correct `cardId` and `listId`.
          3. Verify that the `idList` in the response matches the expected target `listId`, confirming that the card has been moved.
      6. Log information about the card and list to confirm the correct move has occurred.
      
      **Expected Result:**

          1. The correct card should be moved to the desired list.
          2. The response body should contain the expected `cardId` and `listId` values.
          3. The test should pass if all cards are successfully moved to the correct lists with the expected properties.
___

## TestData

- The test data are divided mainly into two separated json files :
1. The first json file `queryParamKeyToken.json` which contains the key & token required to be connected to the trello user API.

Ex:

```json
{
  "KeyAndToken": {
    
    "key": "EnterYourTrelloKey",
    "token": "EnterYourTrelloToken"
  }
}
```
2. The second json file `queryParam.json` which mainly contains the request data which can be fully manipulated by the user, 
   The user is able to modify **values** in every section in the json file.
   
- Preserving Key Structure:

   Certain keys, such as `CreateList`, `CreateCard`, `Checklists`, `CheckItems`, and `moveTo`, should not be altered. 
   These keys must retain their names exactly as they are.
   While modifying values under these keys, you are allowed to adjust the content/values (such as names of lists, `cards`, `checklists`, and `items`) but the key names themselves must remain intact.
   
- Maintaining Correspondence:

  When creating or modifying `cards`, `checklists`, or `checkItems`, **the corresponding keys for these items should be updated with the new values.**
  This means that if you add a new card or checklist, the corresponding key (e.g., `CreateCard`, `CreateList`, etc.) must reflect this new addition.
  For example, if a new card is added under `CreateCard`, the key should capture the name of this new card, ensuring that the structure remains consistent with previous items.

Ex :

```json
{
  
  "CreateBoard" :{"name": "Courses Board", "defaultLists": false},

  "CreateList1" :{"name" : "Done"},
  "CreateList2" :{"name" : "ToDo"},
  "CreateList3" :{"name" : "Doing"},
  "CreateList4" :{"name" : "ToBeDone"},
  "CreateList5" :{"name" : "Later"},
  "CreateList6" :{"name" : "Archive"},


  "CreateCard1" :{"name" : "Selenium Course"},
  "CreateCard2" :{"name" : "RestAssured Course"},
  "CreateCard3" :{"name" : "Java Course"},
  "CreateCard4" :{"name" : "Appium Course"},
  "CreateCard5" :{"name" : "Manual Course"},
  "CreateCard6" :{"name" : "API Course"},
  "CreateCard7" :{"name" : "Jmeter Course"},
  "CreateCard8" :{"name" : "Cypress Course"},


  "Selenium CourseChecklists"   :["LocatorsSelenium","Functions","DataDriven","Actions","Assertions"],
  "RestAssured CourseChecklists":["Headers","Body"],
  "Java CourseChecklists"       :["OOP","DesignPattern"],
  "Appium CourseChecklists"     :["Locators","Capabilities","Simulations"],
  "Manual CourseChecklists"     :["ISTQB-CTFL"],
  "API CourseChecklists"        :["Postman", "Newman-CLI"],
  "Jmeter CourseChecklists"     :["Controllers","Looping"],
  "Cypress CourseChecklists"    :["LocatorCypress","Methods","Injection"],


  "LocatorsSeleniumCheckItems"  :["Ids", "xPaths"],
  "FunctionsCheckItems"         :["Functionality Testing", "Boundary Conditions", "Negative Testing"],
  "DataDrivenCheckItems"        :["External Data Files", "Dynamic Inputs"],
  "ActionsCheckItems"           :["MoveToElement", "Select"],
  "AssertionsCheckItems"        :["True/False", "Element Text", "Visibility"],
  "HeadersCheckItems"           :["Content-Type", "Authorization", "Accept"],
  "BodyCheckItems"              :["Request Payload", "Response Body"],
  "OOPCheckItems"               :["Encapsulation", "Inheritance"],
  "DesignPatternCheckItems"     :["Singleton", "Factory"],
  "LocatorsCheckItems"          :["CSS Selectors", "Classes"],
  "CapabilitiesCheckItems"      :["Browser Compatibility", "Platform Support"],
  "SimulationsCheckItems"       :["Load Testing", "Stress Testing"],
  "ISTQB-CTFLCheckItems"        :["TestingTechniques", "TestingProcess"],
  "PostmanCheckItems"           :["Collections", "Environment Variables"],
  "Newman-CLICheckItems"        :["Run Collections", "Export Results"],
  "ControllersCheckItems"       :["Action Methods", "View Controllers"],
  "LoopingCheckItems"           :["For Loops", "While Loops"],
  "LocatorCypress"              :[],
  "MethodsCheckItems"           :["Test Initialization", "Test Execution", "Test Cleanup"],
  "InjectionCheckItems"         :["Dependency Injection", "Service Injection", "Constructor Injection"],


  "moveToDone"     : ["Selenium Course","RestAssured Course","Manual Course"],
  "moveToToDo"     : ["Appium Course","API Course"],
  "moveToDoing"    : ["Cypress Course","Jmeter Course"],
  "moveToToBeDone" : ["Java Course"],
  "moveToLater"    : []
  
}

```

___

## Configuration

1. config.properties :

- The configuration for API endpoints and request data **Json paths** is managed via the `ConfigLoader` class. The URLs and JSON file paths are loaded from a properties file.

### Example Configuration

```properties
#JsonFilesPaths
#--------------#

queryParamKeyTokenPath=src/main/resources/queryParamKeyToken.json
queryParamPath=src/main/resources/queryParam.json

#URL
#---#

url = https://api.trello.com/1

#endPoints
#---------#

boards=/boards/
lists=/lists/
cards=/cards/
checklists=/checklists/
checkItems=/checkItems/


```

2. updated.properties :

- The file is used to store data, specifically the last `boardId` created. This allows for efficient test case execution by keeping track of the board created in previous runs.
- If the board is deleted manually from Trello, the corresponding `boardId` saved in the file should also be removed.

```properties
#Updated properties
#Sun Jan 12 00:46:11 EET 2025
boardId=6782f4b333bc4f87ab6bd4d0
```
___

## How to Run
   1. Clone the repository:
   ```bash
      git clone https://github.com/HossamAtef11/TrelloAPIProject.git
   ```
   2. Navigate to the project directory:
   ```bash   
      cd TrelloProject
   ```
   3. Install dependencies (if using Maven):
   ```bash
      mvn clean install
   ```
   4. Run the tests using TestNG:
   Running the bat file which contains maven command line execution
   ```bash
      RunScript.bat
   ```
___

## Test Reporting

- The tests are integrated with Allure for reporting. 
- To generate and view the Allure report, use the following commands:

1. Generate the Allure report:
   Running the bat file which contains allure command line execution

      ```bash
      AllureRun.bat
      ```
2. Open the Allure report in your browser.
___

## Logging
- The tests use Log4j for logging. Logs will be generated in the default log file configured in log4j2.xml.
- The logging is included also in the allure report.
___
___
