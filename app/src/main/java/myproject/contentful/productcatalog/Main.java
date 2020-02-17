package myproject.contentful.productcatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Starting Contentful-to-Neo4j  Application ...");

        SpringApplication.run(Main.class, args);

        logger.info("Staring Contentful-to-Neo4j Application Completes ...");

    }
}
