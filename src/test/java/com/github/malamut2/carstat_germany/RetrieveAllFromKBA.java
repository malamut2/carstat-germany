package com.github.malamut2.carstat_germany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * This small tool just retrieves all files from KBA, and then exits.
 */
@SpringBootApplication
public class RetrieveAllFromKBA {

    private static final Logger logger = LoggerFactory.getLogger(RetrieveAllFromKBA.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RetrieveAllFromKBA.class, args);
        Retriever retriever = context.getBean(Retriever.class);
        LinkedHashMap<String, File> map = retriever.downloadMonthlyAdditions("200901", "202106", false);
        logger.info("We have " + map.size() + " files available.");
        context.close();
        System.exit(0);
    }

}
