package com.example.careercraft.service.impl;


import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.ReportDto;
import com.example.careercraft.exception.PdfGenerationException;
import com.example.careercraft.service.PdfService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
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

            // Добавляем агрегированный отчет
            document.add(new Paragraph("Aggregated Report"));
            document.add(new Paragraph("Total Score: " + aggregatedReport.getTotalScore()));
            document.add(new Paragraph("Average Percentage Correct: " + aggregatedReport.getAveragePercentageCorrect()));
            document.add(new Paragraph("Skill Level: " + aggregatedReport.getSkillLevel()));

            // Добавляем отчеты
            document.add(new Paragraph("Reports:"));
            for (ReportDto report : reports) {
                document.add(new Paragraph("Score: " + report.getScore()));
                document.add(new Paragraph("Percentage Correct: " + report.getPercentageCorrect()));
                document.add(new Paragraph("Skill Level: " + report.getSkillLevel()));
                document.add(new Paragraph("Skill Name: " + report.getSkillName()));
                document.add(new Paragraph("Category Name: " + report.getCategoryName()));
                document.add(new Paragraph(" "));
            }

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
