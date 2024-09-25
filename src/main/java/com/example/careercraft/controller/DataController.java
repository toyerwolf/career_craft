package com.example.careercraft.controller;

import com.example.careercraft.service.impl.DataService;
import com.example.careercraft.service.impl.ExcelExportService;
import com.example.careercraft.service.impl.WebScrapingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@AllArgsConstructor
@Slf4j
public class DataController {

    private final WebScrapingService webScrapingService;
    private final ExcelExportService excelExportService;






    @GetMapping("/scrape")
    public ResponseEntity<String> scrapeData(@RequestParam String url, @RequestParam String cssQuery) {
        try {

            Elements elements = webScrapingService.getDataFromWebsite(url, cssQuery);
            List<String> data = new ArrayList<>();
            elements.forEach(element -> data.add(element.text()));
            log.info("Data scraped: {}", data);
            String excelFileName = "scraped_data.xlsx"; // Убедитесь, что путь к файлу корректен
            excelExportService.exportDataToExcel(data, excelFileName);log.info("Data saved to Excel file: {}", excelFileName);

            return ResponseEntity.ok("Data scraped and saved to Excel file: " + excelFileName);
        } catch (IOException e) {
            log.error("Error while scraping data or saving to Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
}


    @GetMapping("/scrape/filter")
    public ResponseEntity<String> scrapeAndFilterData(@RequestParam String url, @RequestParam String cssQuery, @RequestParam String productName) {
        try {
            String excelFileName = webScrapingService.scrapeAndSaveData(url, cssQuery, productName);
            return ResponseEntity.ok("Data scraped and saved to Excel file: " + excelFileName);
        } catch (IOException e) {
            log.error("Error while scraping data or saving to Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



}