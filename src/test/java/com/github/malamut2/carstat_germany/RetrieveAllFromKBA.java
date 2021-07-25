package com.github.malamut2.carstat_germany;

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

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RetrieveAllFromKBA.class, args);
        Retriever retriever = context.getBean(Retriever.class);
        LinkedHashMap<String, File> map = retriever.downloadMonthlyAdditions("202106", "202106", true);
        System.out.println("Downloaded " + map.size() + " files.");
        context.close();
        System.exit(0);
    }

}
