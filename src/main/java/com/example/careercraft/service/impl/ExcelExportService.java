package com.example.careercraft.service.impl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public void exportDataToExcel(List<String> data, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Scraped Data");

        // Добавляем заголовок
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Extracted Data");

        // Добавляем данные в Excel
        int rowNum = 1;
        for (String line : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(line);
        }

        // Записываем данные в файл
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            workbook.close();
            System.out.println("Excel file created: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing Excel file: " + e.getMessage());
            throw e;
        }
    }

}