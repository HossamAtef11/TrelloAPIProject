package reuse;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import static java.lang.invoke.MethodHandles.lookup;

@SuppressWarnings("ALL")
@Listeners(AllureLog4jListener.class)
public class BaseTest {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private final Type type = new TypeToken<Map<String, Object>>(){}.getType();
    protected String boardsEndPoint;
    protected String listsEndPoint;
    protected String cardsEndPoint;
    protected String checklistEndPoint;
    protected String checkItemsEndPoint;
    protected String queryParamKeyTokenPath;
    protected String queryParamPath;
    protected Map <String,Object> queryParamMap;
    protected Map <String,Object> queryParamKeyToken;
    protected Map <String ,String> keyTokenMap ;
    protected ConfigLoader propertyRead;
    protected ConfigLoader propertyUpdate;
    protected String url;
    protected List<String> lists ;
    protected List<String> cardsList;
    protected List<String> checkList;
    protected List<String> checkItemsList;
    protected List<String> moveToList;



    @BeforeClass
    public void setUP() throws FileNotFoundException {
        try (FileWriter fileWriter = new FileWriter("./logs/application.log",false))
        {
            fileWriter.write("");
            log.info("The log file is cleared");
        }
        catch (IOException exception){
            exception.getStackTrace();
            log.error("The file isn't found and the message is ",exception);
        }
        log.info("Starting the setUp...");
        propertyRead = new ConfigLoader(false,"./src/main/resources/config.properties");
        log.debug("Loading configuration properties...");
        propertyUpdate = new ConfigLoader(true,"./src/main/resources/updated.properties");
        log.debug("Loading configuration properties...");
        queryParamKeyTokenPath = propertyRead.getProperty("queryParamKeyTokenPath");
        log.debug("Query Param Key & Token Path: {}", queryParamKeyTokenPath);
        queryParamPath = propertyRead.getProperty("queryParamPath");
        log.debug("Query Param Path: {}", queryParamPath);
        queryParamKeyToken = new Gson().fromJson(new FileReader(queryParamKeyTokenPath),type);
        log.info("Query Param Key & Token Path is retrieved successfully...");
        queryParamMap = new Gson().fromJson(new FileReader(queryParamPath), type);
        log.info("Query Parameters map is retrieved successfully...");
        url = propertyRead.getProperty("url");
        log.info("Get Url:  {}",url);
        boardsEndPoint = propertyRead.getProperty("boards");
        log.info("Get Boards endpoint: {}",boardsEndPoint);
        listsEndPoint = propertyRead.getProperty("lists");
        log.info("Get Lists endpoint: {}",listsEndPoint);
        cardsEndPoint = propertyRead.getProperty("cards");
        log.info("Get Cards endpoint: {}",cardsEndPoint);
        checklistEndPoint = propertyRead.getProperty("checklists");
        log.info("Get Checklist endpoint: {}",checklistEndPoint);
        checkItemsEndPoint = propertyRead.getProperty("checkItems");
        log.info("Get Checklist Items endpoint: {}",checkItemsEndPoint);
        lists =getKeys(queryParamMap,"CreateList");
        log.info("Lists to be created is/are retrieved successfully... {}",lists);
        cardsList =getKeys(queryParamMap,"CreateCard");
        log.info("Cards to be created is/are retrieved successfully... {}", cardsList);
        checkList =getKeys(queryParamMap,"checklists");
        log.info("CheckLists to be created is/are retrieved successfully... {}", checkList);
        checkItemsList =getKeys(queryParamMap,"checkItems");
        log.info("Checklist Items to be created is/are retrieved successfully... {}", checkItemsList);
        moveToList =getKeys(queryParamMap,"MoveTo");
        log.info("MoveTo list to be created is/are retrieved successfully... {}", moveToList);
        keyTokenMap = getMapByKey(queryParamKeyToken,"KeyAndToken");
        if (keyTokenMap.get("key").isEmpty() || keyTokenMap.get("token").isEmpty()) {
            Assert.fail("Please enter your Trello's Key & Token!");
        }
        log.info("Key & Token are retrieved successfully... {}", keyTokenMap);
    }


    protected static Map<String, String> getMapByKey(Map<String, Object> queryParamMap, String key) {
        if (queryParamMap.containsKey(key)) {
            log.info("Sent key is existing in the json: {}",key);
            Object value = queryParamMap.get(key);
            log.debug("Value of the key: {}",value);
            Map <String, Object> innerMap = (Map<String, Object>) value;
            if (innerMap.isEmpty()) {
                log.warn("The map is empty: {}", innerMap);
                return null;
            }
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<String, Object> entry : innerMap.entrySet())
            {
                result.put(entry.getKey(), entry.getValue().toString());
            }
            log.info("The map is created: {}",result);
            return result;
        }
        log.warn("The key entered doesn't exist in the Json: {}", key);
        return null;
    }
    protected static List<String> getListByKey(Map<String, Object> queryParamMap, String key) {
        if (queryParamMap.containsKey(key)) {
            log.info("Sent key is existing in the json: {}", key);
            List<String> value = (List) queryParamMap.get(key);
            log.info("The list of {} are/is retrieved: {}", key,value);
            return value;
        }
        log.warn("The key entered doesn't exist in the Json: {}", key);
        return null;
    }
    protected static List<String> getKeys(Map<String, Object> queryParamMap, String keyPrefix) {
        List<String> keys = new ArrayList<>();
        for (String key : queryParamMap.keySet()) {
            if (key.toLowerCase().replaceAll("\\s+", "").contains(keyPrefix.toLowerCase().replaceAll("\\s+", ""))) {
                keys.add(key);}
        }
        if (!keys.isEmpty()) {
            log.info("The list of keys containing {} are/is retrieved: {} " ,keyPrefix ,keys);
            return keys;
        } else {
            log.info("The key prefix entered doesn't match any keys in the JSON: {}" ,keyPrefix);
            return null;
        }
    }
    protected String getCheckListId(Map<String , String> checklistsNamesIds, String checkItems){
        checkItems = checkItems.replaceAll("(.*)Check.*", "$1").replaceAll("\\s+", "");

        for (String key : checklistsNamesIds.keySet() ) {
            if (key.equals(checkItems)) {

                log.info("The Checklist id is gotten successfully: {}",checklistsNamesIds.get(key));
                return checklistsNamesIds.get(key);

            }
        }

        log.warn("The sent item id isn't exist {}",checkItems);
        return null;
    }
    protected String getObjectId(Map<String , String> nameIdsMap, String objectName , String objectType){
        if (objectType.equalsIgnoreCase("list")) {
            objectName = objectName.replaceAll(".*moveTo(.*)", "$1").replaceAll("\\s+", "");
            log.info("The List name is selected successfully: '{}'",objectName);
        }
        else if (objectType.equalsIgnoreCase("card")) {
            objectName = objectName.replaceAll("(.*)Checklists.*", "$1");
            log.info("The Card name is selected successfully: '{}'",objectName);
        }
        else if (objectType.equalsIgnoreCase("checklist")) {
            objectName = objectName.replaceAll("\\s+", "");

            log.info("The Checklist name is selected successfully: '{}'",objectName);
        }
        else if (objectType.equalsIgnoreCase("checkItem")) {
            objectName = objectName.replaceAll("\\s+", "");

            log.info("The Checklist name is selected successfully: '{}'",objectName);
        }
        for (String key : nameIdsMap.keySet() ) {
            if (key.replaceAll("\\s+", "").equals(objectName.replaceAll("\\s+", ""))) {
                log.info("The Value is the sent key: '{}' gotten successfully: '{}'",key,key.equals(objectName));
                return nameIdsMap.get(key);
            }
        }

        return null;
    }




}






