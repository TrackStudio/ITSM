package scripts.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesUtil {
    public static final Log log = LogFactory.getLog(PropertiesUtil.class);

    public static Properties getProperties(String fileName) {
        Properties properties = new Properties();
        try {
            File propertiesFile = new File(fileName);
            log.error("Read file : " + new File("./").getAbsolutePath() + " exists : " + propertiesFile.exists());
            if (propertiesFile.exists()) {
                properties.load(new FileReader(propertiesFile));
            }
        } catch (IOException e) {
            log.error("Error", e);
        }
        return properties;
    }
}
