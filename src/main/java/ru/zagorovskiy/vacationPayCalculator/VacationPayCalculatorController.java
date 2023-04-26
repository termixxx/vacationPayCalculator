package ru.zagorovskiy.vacationPayCalculator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@RestController
public class VacationPayCalculatorController {
    private final Logger logger = LogManager.getLogger(VacationPayCalculatorController.class);

    @GetMapping("/calculate")
    public BigDecimal calculatePay(@RequestParam(name = "averageSalary") BigDecimal averageSalary,
                                   @RequestParam(name = "vacationDays") Integer vacationDays,
                                   @RequestParam(name = "startDateStr", required = false) String startDateStr, // yyyy-mm-dd
                                   @RequestParam(name = "endDateStr", required = false) String endDateStr) throws DateTimeException {
        BigDecimal vacationPay;
        final var salaryPerDay = averageSalary.divide(BigDecimal.valueOf(360), 2, RoundingMode.HALF_UP);

        if (startDateStr != null && endDateStr != null) {
            var curDate = LocalDate.parse(startDateStr);
            var endDate = LocalDate.parse(endDateStr);
            if (curDate.isAfter(endDate)) {
                logger.error("Start date should go before the end date");
                throw new DateTimeException("StartDate > EndDate");
            }

            var holidayDays = getHolidayDaysList(curDate.getYear());
            int vacationDaysWithHolidays = getVacationDaysWithHolidays(curDate, endDate, holidayDays);

            vacationPay = salaryPerDay.multiply(BigDecimal.valueOf(vacationDaysWithHolidays));
        } else {
            vacationPay = salaryPerDay.multiply(BigDecimal.valueOf(vacationDays));
        }
        return vacationPay;
    }

    private static int getVacationDaysWithHolidays(LocalDate curDate, LocalDate endDate, List<LocalDate> holidayDays) {
        int vacationDaysWithHolidays = 0;

        while (!curDate.equals(endDate)) {
            // подсчёт кол-ва отпускных дней с учётом праздников и выходных

            var dayOfWeek = curDate.getDayOfWeek().getValue();
            if (dayOfWeek == 6 || dayOfWeek == 7) { // пропуск субботы и воскресенья
                curDate = curDate.plusDays(1);
                continue;
            }
            if (holidayDays.contains(curDate)) { // пропускаем праздники
                curDate = curDate.plusDays(1);
                continue;
            }
            vacationDaysWithHolidays++; // рабочий день ++
            curDate = curDate.plusDays(1);
        }
        return vacationDaysWithHolidays;
    }

    private static List<LocalDate> getHolidayDaysList(int year) {
        int nextYear = year + 1;
        return List.of(
                LocalDate.of(year, Month.JANUARY, 1),
                LocalDate.of(year, Month.JANUARY, 2),
                LocalDate.of(year, Month.JANUARY, 3),
                LocalDate.of(year, Month.JANUARY, 4),
                LocalDate.of(year, Month.JANUARY, 5),
                LocalDate.of(year, Month.JANUARY, 6),
                LocalDate.of(year, Month.JANUARY, 8),
                LocalDate.of(year, Month.FEBRUARY, 23),
                LocalDate.of(year, Month.MARCH, 8),
                LocalDate.of(year, Month.MAY, 1),
                LocalDate.of(year, Month.MAY, 9),
                LocalDate.of(year, Month.JUNE, 12),
                LocalDate.of(year, Month.NOVEMBER, 4),

                LocalDate.of(nextYear, Month.JANUARY, 1), // если отпуск начался в одном году, а закончился в другом
                LocalDate.of(nextYear, Month.JANUARY, 2),
                LocalDate.of(nextYear, Month.JANUARY, 3),
                LocalDate.of(nextYear, Month.JANUARY, 4),
                LocalDate.of(nextYear, Month.JANUARY, 5),
                LocalDate.of(nextYear, Month.JANUARY, 6),
                LocalDate.of(nextYear, Month.JANUARY, 8),
                LocalDate.of(nextYear, Month.FEBRUARY, 23),
                LocalDate.of(nextYear, Month.MARCH, 8),
                LocalDate.of(nextYear, Month.MAY, 1),
                LocalDate.of(nextYear, Month.MAY, 9),
                LocalDate.of(nextYear, Month.JUNE, 12),
                LocalDate.of(nextYear, Month.NOVEMBER, 4)
        );
    }
}
