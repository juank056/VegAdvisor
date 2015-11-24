/**
 *
 */
package com.vegadvisor.client.util;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Clase para manejar utilidades de fechas
 *
 * @author ESFDES27
 */
public class DateUtils {

    /**
     * Constructor
     */
    private DateUtils() {
    }

    /**
     * Obtiene la fecha en string YYYY-MM-DD
     *
     * @param date la fecha a obtener
     * @return fecha en String
     */
    @SuppressWarnings("deprecation")
    public static String getDateString(Date date) {
        String ret = Constants.BLANKS + (date.getYear() + 1900);
        ret += date.getMonth() + 1 > 9 ? Constants.MINUS
                + (date.getMonth() + 1) : Constants.MINUS_ZERO
                + (date.getMonth() + 1);
        ret += date.getDate() > 9 ? Constants.MINUS + date.getDate()
                : Constants.MINUS_ZERO + date.getDate();
        return ret;
    }

    /**
     * Obtiene la hora en string HH:MM:SS
     *
     * @param date la hora a obtener
     * @return hora en String
     */
    @SuppressWarnings("deprecation")
    public static String getTimeString(Date date) {
        String ret = date.getHours() > 9 ? Constants.BLANKS + date.getHours()
                : Constants.ZERO + date.getHours();
        ret += date.getMinutes() > 9 ? Constants.TWO_POINTS + date.getMinutes()
                : Constants.TWO_POINTS_ZERO + date.getMinutes();
        ret += date.getSeconds() > 9 ? Constants.TWO_POINTS + date.getSeconds()
                : Constants.TWO_POINTS_ZERO + date.getSeconds();
        return ret;
    }

    /**
     * Obtiene la fecha en string YYYYMMDD
     *
     * @param date la fecha a obtener
     * @return fecha en String
     */
    @SuppressWarnings("deprecation")
    public static String getDateStringYYYYMMDD(Date date) {
        String ret = Constants.BLANKS + (date.getYear() + 1900);
        ret += date.getMonth() + 1 > 9 ? Constants.BLANKS
                + (date.getMonth() + 1) : Constants.ZERO
                + (date.getMonth() + 1);
        ret += date.getDate() > 9 ? Constants.BLANKS + date.getDate()
                : Constants.ZERO + date.getDate();
        return ret;
    }

    /**
     * Obtiene la hora en string HHMMSS
     *
     * @param date la hora a obtener
     * @return hora en String
     */
    @SuppressWarnings("deprecation")
    public static String getTimeStringHHMMSS(Date date) {
        String ret = date.getHours() > 9 ? Constants.BLANKS + date.getHours()
                : Constants.ZERO + date.getHours();
        ret += date.getMinutes() > 9 ? Constants.BLANKS + date.getMinutes()
                : Constants.ZERO + date.getMinutes();
        ret += date.getSeconds() > 9 ? Constants.BLANKS + date.getSeconds()
                : Constants.ZERO + date.getSeconds();
        return ret;
    }

    /**
     * Metodo para obtener una fecha dado un String en formato YYYYMMDD
     *
     * @param date el String de la fecha a obtener
     * @return El objeto de fecha
     */
    @SuppressWarnings("deprecation")
    public static Date getDateDateYYYYMMDD(String date) {
        Date fech = new Date();
        fech.setYear(Integer.valueOf(date.substring(0, 4)) - 1900);
        fech.setMonth(Integer.valueOf(date.substring(4, 6)) - 1);
        fech.setDate(Integer.valueOf(date.substring(6, 8)));
        return fech;
    }

    /**
     * Metodo para obtener una hora dado un String en formato HHMMSS
     *
     * @param time el String de la fecha a obtener
     * @return El objeto de fecha
     */
    @SuppressWarnings("deprecation")
    public static Date getTimeDateHHMMSS(String time) {
        return new Time(Integer.valueOf(time.substring(0, 2)),
                Integer.valueOf(time.substring(2, 4)), Integer.valueOf(time
                .substring(4, 6)));
    }

    /**
     * Metodo para obtener una Hora dado un String en formato HHMMSS
     *
     * @param time el String de la hora a obtener
     * @return El objeto de fecha/hora
     */
    @SuppressWarnings("deprecation")
    public static Date getTimeHHMMSS(String time) {
        Date fech = new Time(0);
        fech.setHours(Integer.valueOf(time.substring(0, 2)));
        fech.setMinutes(Integer.valueOf(time.substring(2, 4)));
        fech.setSeconds(Integer.valueOf(time.substring(4, 6)));
        return fech;
    }

    /**
     * Obtiene la fecha actual en SQL DATE
     *
     * @return Fecha actual en Sql
     */
    public static java.sql.Date getCurrentSQLDate() {
        return new java.sql.Date(new Date().getTime());
    }

    /**
     * Obtiene la fecha actual en UTIL DATE
     *
     * @return Fecha actual en UTIL
     */
    public static Date getCurrentUtilDate() {
        return new Date();
    }

    /**
     * Obtiene la hora actual en sql time
     *
     * @return Hora actual en Sql Time
     */
    public static Time getCurrentSQLTime() {
        return new Time(new Date().getTime());
    }

    /**
     * Realiza el corrimiento de una fecha en un numero de dias determinados
     *
     * @param date la fecha a la que se le va a realizar el corrimiento
     * @param days el numero de dias que se va a correr (positivo o negativo)
     * @return Fecha con el corrimiento de dias
     */
    @SuppressWarnings("deprecation")
    public static Date moveUtilDate(Date date, int days) {
        // Crea fecha de retorno
        Date ret = new Date(date.getTime());
        // Retaliza corrimiento
        ret.setDate(ret.getDate() + days);
        // retorna
        return ret;
    }

    /**
     * Obtiene la fecha actual en formato YYMM Numerico
     *
     * @return Fecha actual en formato yymm numerico
     */
    public static int getCurrentYYMMDate() {
        // A�o actual
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        // Mes actual
        int currMonth = Calendar.getInstance().get(Calendar.MONTH);
        // A�o en String
        String year = (Constants.BLANKS + currYear).substring(2);
        // Mes en string
        String month = currMonth + 1 < 10 ? Constants.ZERO + (currMonth + 1)
                : Constants.BLANKS + (currMonth + 1);
        return Integer.valueOf(year + month);
    }

    /**
     * Obtiene dia del mes actual
     *
     * @return Dia del mes entre 1 y 31
     */
    public static int getDayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Obtiene dia de la semana
     *
     * @return Dia de la semana entre 1 y 7
     */
    public static int getDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Obtiene Fechs y hora actual del sistema en formato YYYY-MM-DD HH:MM:SS
     *
     * @return
     */
    public static String getCurrentDateTime() {
        // Fecha actual
        Date currDate = getCurrentUtilDate();
        // Retorna
        return getDateString(currDate) + Constants.BLANK_SPACE
                + getTimeString(currDate);
    }

    /**
     * Obtiene Fecha/Hora en String
     *
     * @return Fecha hora en string
     */
    public static String getDateTimeString(Date date, String separator) {
        // Hora (pone hora actual)
        Date time = getCurrentSQLTime();
        // Retorna
        return getDateString(date) + separator + getTimeString(time);
    }

    /**
     * Convierte de objeto util date a date en formato xml
     *
     * @param utilDate Fecha util a convertir
     * @return Fecha util convertida a formato xml
     * @throws DatatypeConfigurationException Error de fecha
     */
    public static XMLGregorianCalendar utilDateToXMLDate(Date utilDate)
            throws DatatypeConfigurationException {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(utilDate);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar xmlDate = datatypeFactory
                .newXMLGregorianCalendar(gregorianCalendar);
        return xmlDate;
    }

}
