/**
 *
 */
package com.vegadvisor.client.bo;


import com.vegadvisor.client.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos estadisticos de un establecimiento
 *
 * @author JuanCamilo
 */
public class EstablishmentStatistic {

    /**
     * Id del establecimiento
     */
    private int establishmentId;

    /**
     * Nombre del establecimiento
     */
    private String establishmentName;

    /**
     * Fecha del dia de consulta
     */
    private String day;

    /**
     * Numero de checkins del dia
     */
    private int checkins;

    /**
     * Lista de numeros de estrellas en opiniones
     */
    private List<Integer> opinionStars;

    /**
     * Constructor
     */
    public EstablishmentStatistic() {
        // Inicia lista de opiniones
        this.opinionStars = new ArrayList<Integer>();
        // Checkins en cero
        this.checkins = 0;
        // Id y nombre a default
        this.establishmentId = 0;
        this.establishmentName = Constants.BLANKS;
    }

    /**
     * @return the establishmentId
     */
    public int getEstablishmentId() {
        return establishmentId;
    }

    /**
     * @param establishmentId the establishmentId to set
     */
    public void setEstablishmentId(int establishmentId) {
        this.establishmentId = establishmentId;
    }

    /**
     * @return the establishmentName
     */
    public String getEstablishmentName() {
        return establishmentName;
    }

    /**
     * @param establishmentName the establishmentName to set
     */
    public void setEstablishmentName(String establishmentName) {
        this.establishmentName = establishmentName;
    }

    /**
     * @return the day
     */
    public String getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * @return the checkins
     */
    public int getCheckins() {
        return checkins;
    }

    /**
     * @param checkins the checkins to set
     */
    public void setCheckins(int checkins) {
        this.checkins = checkins;
    }

    /**
     * @return the opinionStars
     */
    public List<Integer> getOpinionStars() {
        return opinionStars;
    }

    /**
     * @param opinionStars the opinionStars to set
     */
    public void setOpinionStars(List<Integer> opinionStars) {
        this.opinionStars = opinionStars;
    }

    @Override
    public String toString() {
        return "EstablishmentStatistic{" +
                "establishmentId=" + establishmentId +
                ", establishmentName='" + establishmentName + '\'' +
                ", day='" + day + '\'' +
                ", checkins=" + checkins +
                ", opinionStars=" + opinionStars +
                '}';
    }

    /**
     * Calcula promedio de estrellas
     *
     * @return
     */
    public float getAverageStars() {
        //Inicia promedio en cero
        float avg = 0;
        if (opinionStars.size() == 0)/*No hay promedio*/
            return avg;
        //Suma elementos de la lista
        for (Integer stars : opinionStars) {
            //Suma
            avg += stars;
        }
        //Divide suma por el numero de elementos
        return avg / opinionStars.size();
    }
}
