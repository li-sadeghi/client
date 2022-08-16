package config;

import constants.Constants;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Properties;

public class Config extends Properties {
    private static Config config;

    private Config(String address) {
        try {
            Reader fileReader = new FileReader(address);
            this.load(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config getConfig() {
        if (config == null) {
            config = new Config(Constants.CONFIG_ADDRESS);
        }
        return config;
    }

    public <E> E getProperty(Class<E> c, String propertyName) {
        return getObject(c, getProperty(propertyName));
    }

    public <E> Optional<E> getOptionalProperty(Class<E> c, String propertyName) {
        if (containsKey(propertyName)) {
            return Optional.of(getObject(c, getProperty(propertyName)));
        } else {
            return Optional.empty();
        }
    }

    private <E> E getObject(Class<E> c, String value) {
        E e = null;
        try {
            Constructor<E> constructor = c.getConstructor(String.class);
            e = constructor.newInstance(value);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
        }
        return e;
    }
}
