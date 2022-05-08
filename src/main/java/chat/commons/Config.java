package chat.commons;

import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final String fileName = "/config_file.cfg";

    @Produces
    public Properties getPropertiesFromConfigFile() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}