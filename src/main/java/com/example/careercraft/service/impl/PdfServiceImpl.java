package com.example.careercraft.service.impl;


import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.ReportDto;
import com.example.careercraft.exception.PdfGenerationException;
import com.example.careercraft.service.PdfService;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {




//    @Override
//    public byte[] generatePdf(List<ReportDto> reports, AggregatedReportDto aggregatedReport) {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            PdfWriter writer = new PdfWriter(baos);
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf);
//
//            // Добавляем агрегированный отчет
//            document.add(new Paragraph("Aggregated Report"));
//            document.add(new Paragraph("Total Score: " + aggregatedReport.getTotalScore()));
//            document.add(new Paragraph("Average Percentage Correct: " + aggregatedReport.getAveragePercentageCorrect()));
//            document.add(new Paragraph("Skill Level: " + aggregatedReport.getSkillLevel()));
//
//            // Добавляем отчеты
//            document.add(new Paragraph("Reports:"));
//            for (ReportDto report : reports) {
//                document.add(new Paragraph("Score: " + report.getScore()));
//                document.add(new Paragraph("Percentage Correct: " + report.getPercentageCorrect()));
//                document.add(new Paragraph("Skill Level: " + report.getSkillLevel()));
//                document.add(new Paragraph("Skill Name: " + report.getSkillName()));
//                document.add(new Paragraph("Category Name: " + report.getCategoryName()));
//                document.add(new Paragraph(" "));
//            }
//
//            document.close();
//            return baos.toByteArray();
//        } catch (IOException e) {
//            throw new PdfGenerationException("Failed to generate PDF document.");
//        }
//
//    }

    public void generateAndWritePdf(List<ReportDto> reports, AggregatedReportDto aggregatedReport, HttpServletResponse response) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Настройка шрифтов
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // Добавляем заголовок
            document.add(new Paragraph("Aggregated Report")
                    .setFont(font)
                    .setFontSize(18)
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            // Добавляем данные агрегированного отчета
            Table aggregatedReportTable = new Table(new float[] {1, 1});
            aggregatedReportTable.setWidth(UnitValue.createPercentValue(100));
            aggregatedReportTable.addHeaderCell(new Cell().add(new Paragraph("Metric")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            aggregatedReportTable.addHeaderCell(new Cell().add(new Paragraph("Value")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            aggregatedReportTable.addCell("Total Score:");
            aggregatedReportTable.addCell(String.valueOf(aggregatedReport.getTotalScore()));
            aggregatedReportTable.addCell("Average Percentage Correct:");
            aggregatedReportTable.addCell(String.valueOf(aggregatedReport.getAveragePercentageCorrect()));
            aggregatedReportTable.addCell("Skill Level:");
            aggregatedReportTable.addCell(aggregatedReport.getSkillLevel());
            document.add(aggregatedReportTable);

            // Добавляем заголовок для отчетов
            document.add(new Paragraph("Reports:")
                    .setFont(font)
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20));

            // Создаем таблицу для отчетов
            Table reportTable = new Table(UnitValue.createPercentArray(new float[] {1, 1, 1, 1, 1}));
            reportTable.setWidth(UnitValue.createPercentValue(100));
            reportTable.addHeaderCell("Score");
            reportTable.addHeaderCell("Percentage Correct");
            reportTable.addHeaderCell("Skill Level");
            reportTable.addHeaderCell("Skill Name");
            reportTable.addHeaderCell("Category Name");

            // Добавляем отчеты в таблицу
            for (ReportDto report : reports) {
                reportTable.addCell(String.valueOf(report.getScore()));
                reportTable.addCell(String.valueOf(report.getPercentageCorrect()));
                reportTable.addCell(String.valueOf(report.getSkillLevel()));
                reportTable.addCell(report.getSkillName());
                reportTable.addCell(report.getCategoryName());
            }
            document.add(reportTable);

            document.close();

            // Настраиваем заголовки ответа
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reports.pdf");
            response.setContentLength(baos.size());

            // Записываем PDF в ответ
            try (OutputStream outputStream = response.getOutputStream()) {
                baos.writeTo(outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new PdfGenerationException("Failed to generate or send PDF document.");
        }
    }


}
