package ru.zagorovskiy.vacationPayCalculator.data;

import java.time.Month;
import java.util.List;
import java.util.Map;


public class HolidaysData {
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
    public static Map<Month, List<Integer>> getHolidayDaysList() {
        return Map.of(Month.JANUARY, List.of(1, 2, 3, 4, 5, 6, 8),
                Month.FEBRUARY, List.of(23),
                Month.MARCH, List.of(8),
                Month.MAY, List.of(1, 9),
                Month.JUNE, List.of(12),
                Month.NOVEMBER, List.of(4));
    }
}
