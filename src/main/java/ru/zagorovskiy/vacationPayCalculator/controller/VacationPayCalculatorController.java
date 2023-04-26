package ru.zagorovskiy.vacationPayCalculator.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zagorovskiy.vacationPayCalculator.service.CalculatorService;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;

@RestController
public class VacationPayCalculatorController {
    private final Logger logger = LogManager.getLogger(VacationPayCalculatorController.class);
    private final CalculatorService calculatorService;

    public VacationPayCalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/calculate")
    public BigDecimal calculatePay(@RequestParam(name = "averageSalary") BigDecimal averageSalary, // среднюю зарплату за 12 месяцев
                                   @RequestParam(name = "vacationDays") Integer vacationDays,
                                   @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, // yyyy-mm-dd
                                   @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws DateTimeException {
        BigDecimal vacationPay;

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                logger.error("Start date should go before the end date");
                throw new DateTimeException("StartDate > EndDate");
            }
            vacationPay = calculatorService.calculateVacationPayWithDates(averageSalary, startDate, endDate);
        } else if (startDate != null) {
            vacationPay = calculatorService.calculateVacationPayWithStartDay(averageSalary, vacationDays, startDate);
        } else {
            vacationPay = calculatorService.calculateVacationPay(averageSalary, vacationDays);
        }
        return vacationPay;
    }


}
