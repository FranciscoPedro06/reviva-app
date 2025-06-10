package com.reviva.app.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {

    }

    /**
     * Formata um objeto Date para uma String no formato especificado.
     * @param date O objeto Date a ser formatado.
     * @param format O formato desejado (ex: "dd/MM/yyyy").
     * @return A data formatada como String.
     */
    public static String formatDateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Converte uma String de data para um objeto Date.
     * @param dateString A String da data.
     * @param format O formato da String da data.
     * @return O objeto Date correspondente.
     * @throws ParseException Se a String não puder ser parseada.
     */
    public static Date convertStringToDate(String dateString, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.parse(dateString);
    }

    /**
     * Verifica se uma data específica é igual ou anterior à data atual.
     * Útil para saber se uma memória pode ser "aberta".
     * @param targetDate A data a ser verificada.
     * @return true se a data alvo for igual ou anterior à data atual, false caso contrário.
     */
    public static boolean isPastOrPresent(Date targetDate) {
        Date currentDate = new Date(); // Data e hora atual
        return targetDate.before(currentDate) || targetDate.equals(currentDate);
    }

    /**
     * Adiciona um determinado número de dias a uma data.
     * @param date A data inicial.
     * @param days O número de dias a adicionar.
     * @return A nova data.
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    /**
     * Retorna a data atual como um objeto Date.
     * @return A data atual.
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    // Você pode adicionar mais métodos aqui conforme a necessidade,
    // como comparar datas, obter componentes de data (dia, mês, ano), etc.
}