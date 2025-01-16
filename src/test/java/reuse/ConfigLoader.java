package reuse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Listeners;
import java.io.*;
import java.util.Properties;

@SuppressWarnings("ALL")
@Listeners(AllureLog4jListener.class)
public class ConfigLoader {

    protected static final Logger log = LogManager.getLogger(ConfigLoader.class);
    private Properties properties;
    private Properties temporaryProperties;
    private final String filepath;

    // Constructor to initialize the loader with file path and action flag
    public ConfigLoader(boolean action, String filepath) {
        this.filepath = filepath;
        loadProperties(action);
    }

    // Load properties based on the action (read or write)
    private void loadProperties(boolean action) {
        try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
            if (action) {
                temporaryProperties = new Properties();
                temporaryProperties.load(fileInputStream);
                log.debug("Loaded properties for modification from file: {}", filepath);
            } else {
                properties = new Properties();
                properties.load(fileInputStream);
                log.debug("Loaded properties from file: {}", filepath);
            }
        } catch (IOException e) {
            log.error("Failed to load properties from file: {}", filepath, e);
        }
    }

    // Retrieve the property value by key with optional default value from config
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        log.info("Returned value for key '{}': {}", key, value);
        return value;
    }

    // Retrieve the property value by key with optional default value from updated
    public String getPropertyUpdate(String key) {
        String value = temporaryProperties.getProperty(key);
        log.info("Returned value for key '{}': {}", key, value);
        return value;
    }

    // Set a new property and save it to the properties file
    public void setProperty(String key, String value) throws IOException {
        if (temporaryProperties == null) {
            temporaryProperties = new Properties();
        }

        temporaryProperties.setProperty(key, value);
        savePropertiesToFile();
        log.info("Updated properties file with key '{}' and value '{}'", key, value);
    }

    // Save properties to the specified file path
    private void savePropertiesToFile() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filepath)) {
            temporaryProperties.store(fileOutputStream, "Updated properties");
        } catch (IOException e) {
            log.error("Failed to save properties to file: {}", filepath, e);
            throw e;
        }
    }
}