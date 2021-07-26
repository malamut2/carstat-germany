package com.github.malamut2.carstat_germany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.LinkedHashMap;

import static com.github.malamut2.carstat_germany.DateUtils.isValidDate;
import static com.github.malamut2.carstat_germany.DateUtils.monthBefore;

@Component
public class Retriever {

    private static final Logger logger = LoggerFactory.getLogger(Retriever.class);

    @Value("${kba.base-url:https://www.kba.de/SharedDocs/Publikationen/DE/Statistik/Fahrzeuge/FZ/}")
    protected String baseUrl;

    @Value("${data.dir:data}")
    protected String dataDirString;

    @Value("${kba.request.timeoutMillis:20000}")
    protected long kbaRetrieveTimeoutMillis;

    protected File dataDir;

    protected WebClient webclient;

    @PostConstruct
    protected void init() {
        dataDir = new File(dataDirString);
        if (!dataDir.isDirectory()) {
            if (!dataDir.mkdirs()) {
                throw new RuntimeException("data directory " + dataDir.getAbsolutePath()
                        + " does not exist and cannot be created.");
            }
        }
        webclient = WebClient
                .builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(32 * 1024 * 1024))  // we need to be able to keep a full Excel file in memory
                        .build())
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Downloads all files of type FZ10 and FZ11 in the given time frame (inclusive).
     * @param fromDate the earliest date to retrieve, in format yyyyMM
     * @param toDate the latest date to retrieve, in format yyyyMM
     * @param refresh if true, retrieve files from remote site even if they already reside in our data folder
     * @return all downloaded files, including files which already existed before download. The files are organized
     *   in a linked hash map, using yyyyMM-type format as the key, in descending order.
     */
    public LinkedHashMap<String, File> downloadMonthlyAdditions(String fromDate, String toDate, boolean refresh) {
        LinkedHashMap<String, File> result = new LinkedHashMap<>();
        for (String date = toDate; isValidDate(date) && date.compareTo(fromDate) >= 0; date = monthBefore(date)) {
            File fz10 = download(KBADocumentType.fz10, date, refresh);
            File fz11 = download(KBADocumentType.fz11, date, refresh);
            if (fz10 != null && fz11 != null) {  // only accept months with complete data
                result.put(date + "-fz10", fz10);
                result.put(date + "-fz11", fz11);
            }
        }
        return result;
    }

    protected File download(KBADocumentType docType, String date, boolean refresh) {

        String year = date.substring(0, 4);
        String month = date.substring(4);
        String localFz10Name = KBADocumentType.fz10.getLocalName(year, month);
        String localFz11Name = KBADocumentType.fz11.getLocalName(year, month);
        File localFz10 = new File(dataDir, localFz10Name);
        File localFz11 = new File(dataDir, localFz11Name);
        File localFile = docType == KBADocumentType.fz10 ? localFz10 : localFz11;

        if (!refresh) {
            if (localFz10.isFile() && localFz11.isFile()) {
                return localFile;
            }
        }

        String remoteName = docType.getRemoteName(year, month);
        return download(localFile, remoteName);

    }

    // returns dst in case of success, null otherwise
    private File download(File dst, String src) {
        try {
            byte[] contents = webclient.get().uri(src).retrieve().bodyToMono(byte[].class).block(Duration.ofMillis(kbaRetrieveTimeoutMillis));
            if (contents == null) {
                logger.warn("Could not download " + src + " from KBA website.");
                return null;
            }
            Files.write(Paths.get(dst.getAbsolutePath()), contents);
            logger.debug("Successfully downloaded File " + dst.getAbsolutePath() + "from " + baseUrl + src);
            return dst;
        } catch (Exception e) {
            logger.warn("Could not download " + src + " from KBA website", e);
            return null;
        }
    }

}
