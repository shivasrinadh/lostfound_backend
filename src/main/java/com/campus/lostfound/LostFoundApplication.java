package com.campus.lostfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.function.Supplier;

@SpringBootApplication
public class LostFoundApplication {

    public static void main(String[] args) {

        // Load .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

        mapDatasourceProperties();

        SpringApplication.run(LostFoundApplication.class, args);
    }

    private static void mapDatasourceProperties() {
        setIfMissing("SPRING_DATASOURCE_URL", () -> firstNonBlank(
                valueOf("JDBC_DATABASE_URL"),
                valueOf("DATABASE_URL"),
                valueOf("DB_URL"),
                valueOf("url"),
                buildJdbcUrlFromMySqlParts()));

        setIfMissing("SPRING_DATASOURCE_USERNAME", () -> firstNonBlank(
                valueOf("JDBC_DATABASE_USERNAME"),
                valueOf("DATABASE_USERNAME"),
                valueOf("DB_USERNAME"),
                valueOf("username"),
                valueOf("MYSQLUSER")));

        setIfMissing("SPRING_DATASOURCE_PASSWORD", () -> firstNonBlank(
                valueOf("JDBC_DATABASE_PASSWORD"),
                valueOf("DATABASE_PASSWORD"),
                valueOf("DB_PASSWORD"),
                valueOf("password"),
                valueOf("MYSQLPASSWORD")));
    }

    private static String buildJdbcUrlFromMySqlParts() {
        String host = firstNonBlank(valueOf("MYSQLHOST"), valueOf("DB_HOST"));
        String port = firstNonBlank(valueOf("MYSQLPORT"), valueOf("DB_PORT"));
        String database = firstNonBlank(valueOf("MYSQLDATABASE"), valueOf("DB_NAME"));

        if (isBlank(host) || isBlank(database)) {
            return null;
        }

        String resolvedPort = isBlank(port) ? "3306" : port;
        return "jdbc:mysql://" + host + ":" + resolvedPort + "/" + database
                + "?useSSL=true&requireSSL=true&serverTimezone=UTC";
    }

    private static void setIfMissing(String key, Supplier<String> fallbackSupplier) {
        if (isBlank(valueOf(key))) {
            String fallback = fallbackSupplier.get();
            if (!isBlank(fallback)) {
                System.setProperty(key, fallback.trim());
            }
        }
    }

    private static String valueOf(String key) {
        String systemValue = System.getProperty(key);
        if (!isBlank(systemValue)) {
            return systemValue;
        }
        return System.getenv(key);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}