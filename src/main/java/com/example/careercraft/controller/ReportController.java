package com.example.careercraft.controller;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.ApiResponse;
import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.ReportDto;
import com.example.careercraft.exception.PdfGenerationException;
import com.example.careercraft.req.PaymentRequest;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.PdfService;
import com.example.careercraft.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("reports")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final AuthService authService;

    private final PdfService pdfService;

    private  final BraintreeGateway braintreeGateway;



    @GetMapping("")
    @Secured("USER")

    public ResponseEntity<List<AggregatedReportDto>> generateReports(
            @RequestHeader(value = "Aut" +
                    "" +
                    "horization") String authHeader,
            @RequestParam Long categoryId) {
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);
        List<AggregatedReportDto> reports = reportService.generateReportForSkills(customerInfo.getId(), categoryId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/generate-reports")
    @Secured("USER")
    public ResponseEntity<ApiResponse<List<AggregatedReportDto>>> generateReports(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam Long categoryId,
            @RequestBody PaymentRequest paymentRequest) {
        // 1. Получаем детали клиента
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);

        // 2. Обрабатываем платеж
        TransactionRequest request = new TransactionRequest()
                .amount(paymentRequest.getAmount()) // Сумма к оплате
                .paymentMethodNonce(paymentRequest.getPaymentMethodNonce()) // Полученный nonce
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = braintreeGateway.transaction().sale(request);

        // 3. Проверяем результат транзакции
        if (!result.isSuccess()) {
            ApiResponse<List<AggregatedReportDto>> response = new ApiResponse<>();
            response.setMessage("Payment failed: " + result.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 4. Если платеж успешен, генерируем отчет
        List<AggregatedReportDto> reports = reportService.generateReportForSkills(customerInfo.getId(), categoryId);
        ApiResponse<List<AggregatedReportDto>> response = new ApiResponse<>();
        response.setMessage("Reports generated successfully");
        response.setData(reports);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aggregated")
    @Secured("USER")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<AggregatedReportDto> getAggregatedReportForCategory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long categoryId) {
        AggregatedReportDto aggregatedReportDto = reportService.getAggregatedReportForCategory(authHeader, categoryId);
        return ResponseEntity.ok(aggregatedReportDto);
    }

    @GetMapping("/category/{categoryId}")
    @Secured("USER")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<List<ReportDto>> getAllReportsForCategoryAndCustomer(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long categoryId) {

        // Получение всех отчетов по категории для определенного пользователя
        List<ReportDto> reports = reportService.getAllReportsForCategoryAndCustomer(authHeader, categoryId);

        return ResponseEntity.ok(reports);
    }


    @GetMapping("/download")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public void downloadReportsAsPdf(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long categoryId,
            HttpServletResponse response) {
        AggregatedReportDto aggregatedReport = reportService.getAggregatedReportForCategory(authHeader, categoryId);
        List<ReportDto> reports = reportService.getAllReportsForCategoryAndCustomer(authHeader, categoryId);
        pdfService.generateAndWritePdf(reports, aggregatedReport, response);
    }

}



