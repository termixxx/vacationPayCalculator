package ru.zagorovskiy.vacationPayCalculator.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

@Service
public class HolidaysService {

    /**
     * Считает кол-во рабочих дней в промежутке между датами начала и конца
     *
     * @param startDate   дата ухода в отпуск
     * @param endDate     дата возращения из отпуска
     * @param holidayDays мапа праздничных дней
     * @return количество дней
     */
    public int getVacationDaysWithHolidaysAndDates(LocalDate startDate, LocalDate endDate, Map<Month, List<Integer>> holidayDays) {
        int vacationDaysWithHolidays = 0;
        endDate = endDate.plusDays(1);// включительно
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
     * Считает кол-во рабочих дней с начальной даты по кол-ву выходных дней
     *
     * @param startDate    дата ухода в отпуск
     * @param vacationDays кол-во выходных дней
     * @param holidayDays  мапа праздничных дней
     * @return количество дней
     */
    public int getVacationDaysWithHolidaysAndStartDay(LocalDate startDate, Integer vacationDays, Map<Month, List<Integer>> holidayDays) {
        int vacationDaysWithHolidays = 0;

        while (vacationDays != 0) {
            // подсчёт кол-ва отпускных дней с учётом праздников и выходных
            var dayOfWeek = startDate.getDayOfWeek().getValue();

            if (dayOfWeek == 6 || dayOfWeek == 7) { // пропуск субботы и воскресенья
                vacationDays--;
                startDate = startDate.plusDays(1);
                continue;
            }
            if (holidayDays.get(startDate.getMonth()) // лист праздничных дней по текущему месяцу
                    .contains(startDate.getDayOfMonth())) { // пропускаем праздники
                vacationDays--;
                startDate = startDate.plusDays(1);
                continue;
            }
            vacationDaysWithHolidays++; // рабочий день ++
            vacationDays--;
            startDate = startDate.plusDays(1);
        }
        return vacationDaysWithHolidays;
    }
}
