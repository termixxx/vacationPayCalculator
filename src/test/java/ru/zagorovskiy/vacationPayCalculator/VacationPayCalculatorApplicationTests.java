package ru.zagorovskiy.vacationPayCalculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.zagorovskiy.vacationPayCalculator.controller.VacationPayCalculatorController;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        var result = BigDecimal.valueOf(1020, 2);

        // when
        var resultController = controller.calculatePay(averageSalary, vacationDays, null, null);

        // then
        assertThat(resultController).isEqualTo(result);
    }

    @Test
    void vacationPayWithStartAndEndDates() {
        // given
        var averageSalary = BigDecimal.valueOf(360);
        var result = BigDecimal.valueOf(1734, 2);

        // when
        var resultController = controller.calculatePay(averageSalary, null,
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));

        // then
        assertThat(resultController).isEqualTo(result);
    }

    @Test
    void vacationPayWithStartDate() {
        // given
        var averageSalary = BigDecimal.valueOf(360);
        var result = BigDecimal.valueOf(1734, 2);

        // when
        var resultController = controller.calculatePay(averageSalary, 31,
                LocalDate.of(2023, 1, 1), null);

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
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 6));

        // then
        assertThat(resultController).isEqualTo(result);
    }
}
