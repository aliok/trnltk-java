package org.trnltk.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class AppProperties {
    private static Properties properties;

    public static String largeFilesFolder() {
        return getString("app.data.folder.large.files");
    }

    public static String oneMillionSentencesFolder() {
        return getString("app.data.folder.1msentences");
    }

    private static String getString(String key) {
        return (String) getObject(key);
    }

    private static Object getObject(String key) {
        if (properties == null)
            loadProperties();

        final Object value = properties.get(key);
        if (value == null)
            throw new RuntimeException("Cannot find key in properties file! Key : " + key + " properties file: " + properties.toString());
        return value;
    }

    private static void loadProperties() {
        final ClassLoader classLoader = AppProperties.class.getClassLoader();
        final InputStream stream = classLoader.getResourceAsStream("trnltk.apps.properties");
        final Properties props = new Properties();
        try {
            props.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AppProperties.properties = props;
    }
}
