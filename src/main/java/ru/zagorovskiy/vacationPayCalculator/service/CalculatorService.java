package ru.zagorovskiy.vacationPayCalculator.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static ru.zagorovskiy.vacationPayCalculator.data.HolidaysData.getHolidayDaysList;

@Service
public class CalculatorService {
    private final HolidaysService holidaysService;


    public CalculatorService(HolidaysService holidaysService) {
        this.holidaysService = holidaysService;
    }

    /**
     * Считает зарплаты за 1 рабочий день
     *
     * @param averageSalary зарплата за 12 месяцев
     * @return зп за рабочий день
     */
    private BigDecimal calculateSalaryPerDay(BigDecimal averageSalary) {
        return averageSalary
                .divide(java.math.BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                .divide(java.math.BigDecimal.valueOf(29.3), 2, RoundingMode.HALF_UP);
        // среднюю зп за 12 месяцев делим на 12, и делим на среднее кол-во дней в месяце 29,3
        // 29.3 взято из статьи https://www.sberbank.ru/ru/s_m_business/pro_business/raschet-srednemesjachnoj-zarabotnoj-platy/
    }

    /**
     * Расчёт отпускных, когда известна дата начала и конца отпуска
     *
     * @param averageSalary зарплата за 12 месяцев
     * @param startDate     дата начала отпуска
     * @param endDate       дата конца отпуска
     * @return сумма отпускных
     */
    public BigDecimal calculateVacationPayWithDates(BigDecimal averageSalary, LocalDate startDate, LocalDate endDate) {
        var salaryPerDay = calculateSalaryPerDay(averageSalary);
        var holidayDays = getHolidayDaysList();

        int vacationDaysWithHolidays = holidaysService.getVacationDaysWithHolidaysAndDates(startDate, endDate, holidayDays);

        return salaryPerDay.multiply(BigDecimal.valueOf(vacationDaysWithHolidays));
    }

    /**
     * Расчёт отпускных, когда известна дата начала и кол-во дней отпуска
     *
     * @param averageSalary зарплата за 12 месяцев
     * @param vacationDays  кол-во отпускных дней
     * @param startDate     дата начала отпуска
     * @return сумма отпускных
     */
    public BigDecimal calculateVacationPayWithStartDay(BigDecimal averageSalary, Integer vacationDays, LocalDate startDate) {
        var salaryPerDay = calculateSalaryPerDay(averageSalary);
        var holidayDays = getHolidayDaysList();

        int vacationDaysWithHolidays = holidaysService.getVacationDaysWithHolidaysAndStartDay(startDate, vacationDays, holidayDays);

        return salaryPerDay.multiply(BigDecimal.valueOf(vacationDaysWithHolidays));
    }

    /**
     * Расчёт отпускных, когда известно только кол-во отпускных дней
     *
     * @param averageSalary зарплата за 12 месяцев
     * @param vacationDays  кол-во отпускных дней
     * @return сумма отпускных
     */
    public BigDecimal calculateVacationPay(BigDecimal averageSalary, Integer vacationDays) {
        var salaryPerDay = calculateSalaryPerDay(averageSalary);
        return salaryPerDay.multiply(BigDecimal.valueOf(vacationDays));
    }
}
