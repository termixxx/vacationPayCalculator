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
import java.util.Map;

@RestController
public class VacationPayCalculatorController {
    private final Logger logger = LogManager.getLogger(VacationPayCalculatorController.class);

    @GetMapping("/calculate")
    public BigDecimal calculatePay(@RequestParam(name = "averageSalary") BigDecimal averageSalary, // среднюю зарплату за 12 месяцев
                                   @RequestParam(name = "vacationDays") Integer vacationDays,
                                   @RequestParam(name = "startDateStr", required = false) String startDateStr, // yyyy-mm-dd
                                   @RequestParam(name = "endDateStr", required = false) String endDateStr) throws DateTimeException {
        BigDecimal vacationPay;

        final var salaryPerDay = averageSalary
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(29.3), 2, RoundingMode.HALF_UP);
        // среднюю зп за 12 месяцев делим на 12, и делим на среднее кол-во дней в месяце 29,3
        // 29.3 взято из статьи https://www.sberbank.ru/ru/s_m_business/pro_business/raschet-srednemesjachnoj-zarabotnoj-platy/

        if (startDateStr != null && endDateStr != null) {
            var curDate = LocalDate.parse(startDateStr);
            var endDate = LocalDate.parse(endDateStr);
            if (curDate.isAfter(endDate)) {
                logger.error("Start date should go before the end date");
                throw new DateTimeException("StartDate > EndDate");
            }

            var holidayDays = getHolidayDaysList();
            int vacationDaysWithHolidays = getVacationDaysWithHolidays(curDate, endDate, holidayDays);

            vacationPay = salaryPerDay.multiply(BigDecimal.valueOf(vacationDaysWithHolidays));
        } else {
            vacationPay = salaryPerDay.multiply(BigDecimal.valueOf(vacationDays));
        }
        return vacationPay;
    }

    /**
     * Считает кол-во дней отпуска
     *
     * @param startDate   дата ухода в отпуск
     * @param endDate     дата возращения из отпуска
     * @param holidayDays мапа праздничных дней
     * @return количество дней
     */
    private static int getVacationDaysWithHolidays(LocalDate startDate, LocalDate endDate, Map<Month, List<Integer>> holidayDays) {
        int vacationDaysWithHolidays = 0;

        while (!startDate.equals(endDate)) {
            // подсчёт кол-ва отпускных дней с учётом праздников и выходных

            var dayOfWeek = startDate.getDayOfWeek().getValue();
            if (dayOfWeek == 6 || dayOfWeek == 7) { // пропуск субботы и воскресенья
                startDate = startDate.plusDays(1);
                continue;
            }
            if (holidayDays.get(startDate.getMonth()) // лист праздничных дней по текущему месяцу
                    .contains(startDate.getDayOfMonth())) { // пропускаем праздники
                startDate = startDate.plusDays(1);
                continue;
            }
            vacationDaysWithHolidays++; // рабочий день ++
            startDate = startDate.plusDays(1);
        }
        return vacationDaysWithHolidays;
    }

    /**
     * 1, 2, 3, 4, 5, 6 и 8 января — Новогодние каникулы;<br/>
     * 7 января — Рождество Христово;<br/>
     * 23 февраля — День защитника Отечества;<br/>
     * 8 марта — Международный женский день;<br/>
     * 1 мая — Праздник Весны и Труда;<br/>
     * 9 мая — День Победы;<br/>
     * 12 июня — День России;<br/>
     * 4 ноября — День народного единства.<br/>
     *
     * @return мапу с праздничными днями для месяцев
     */
    private static Map<Month, List<Integer>> getHolidayDaysList() {
        return Map.of(Month.JANUARY, List.of(1, 3, 4, 5, 6, 8),
                Month.FEBRUARY, List.of(23),
                Month.MARCH, List.of(8),
                Month.MAY, List.of(1, 9),
                Month.JUNE, List.of(12),
                Month.NOVEMBER, List.of(4));
    }
}
