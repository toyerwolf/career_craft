package com.example.careercraft.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element; // Импортируем правильный класс Element
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebScrapingService {

    private final ExcelExportService excelExportService;

    // Конструктор для внедрения зависимостей
    public WebScrapingService(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    // Метод для получения данных с веб-страницы
    public Elements getDataFromWebsite(String url, String cssQuery) throws IOException {
        Document document = Jsoup.connect(url).get();
        return document.select(cssQuery);
    }

    // Метод для скрапинга данных и сохранения в Excel
    public String scrapeAndSaveData(String url, String cssQuery, String productName) throws IOException {
        // Получаем данные с веб-страницы
        Elements elements = getDataFromWebsite(url, cssQuery);

        // Извлекаем текст из элементов, фильтруя по названию продукта
        List<String> filteredData = elements.stream()
                .map(Element::text) // Здесь вызываем метод text() на элементе
                .filter(text -> text.contains(productName)) // Фильтруем по названию продукта
                .collect(Collectors.toList());

        // Логирование извлеченных данных
        log.info("Data scraped: {}", filteredData);

        // Сохраняем данные в Excel файл
        String excelFileName = "scraped_data.xlsx"; // Убедитесь, что путь к файлу корректен
        excelExportService.exportDataToExcel(filteredData, excelFileName);

        log.info("Data saved to Excel file: {}", excelFileName);

        return excelFileName;
    }
}