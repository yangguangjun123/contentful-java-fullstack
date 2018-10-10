package myproject.meetup.contentful.productcatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Starting Contentful Java Fullstack Application ...");

        SpringApplication.run(Main.class, args);

        logger.info("Staring Contentful Java Fullstack Application Completes ...");

    }
}
