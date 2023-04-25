package ru.zagorovskiy.vacationPayCalculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VacationPayCalculatorApplicationTests {

    @Autowired
    private VacationPayCalculatorController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    void vacationPayWithoutStartAndEndDates() {
        // given
        var averageSalary = BigDecimal.valueOf(360);
        int vacationDays = 10;
        var result = BigDecimal.valueOf(1000, 2);

        // when
        var resultController = controller.calculatePay(averageSalary, vacationDays, null, null);

        // then
        assertThat(resultController).isEqualTo(result);
    }

    @Test
    void vacationPayWithStartAndEndDates() {
        // given
        var averageSalary = BigDecimal.valueOf(360);
        var result = BigDecimal.valueOf(1600, 2);

        // when
        var resultController = controller.calculatePay(averageSalary, null,
                "2023-01-01", "2023-01-31");

        // then
        assertThat(resultController).isEqualTo(result);
    }

    @Test
    void vacationPayPerOnlyHolidays() {
        // given
        var averageSalary = BigDecimal.valueOf(360);
        var result = BigDecimal.valueOf(0, 2);

        // when
        var resultController = controller.calculatePay(averageSalary, null,
                "2023-01-01", "2023-01-06");

        // then
        assertThat(resultController).isEqualTo(result);
    }
}
