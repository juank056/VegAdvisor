package com.vegadvisor.client.bo;

// Generated 23-oct-2015 19:39:18 by Hibernate Tools 4.3.1

/**
 * Chdmensa generated by hbm2java
 */
public class Chdmensa implements java.io.Serializable, AbstractBO<ChdmensaId> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ChdmensaId id;
    private String mchmensaf;
    private String mchientsf;
    // Nombre del usuario que ha enviado el mensaje
    private String senderName;

    public Chdmensa() {
    }

    public Chdmensa(ChdmensaId id, String mchmensaf, String mchientsf) {
        this.id = id;
        this.mchmensaf = mchmensaf;
        this.mchientsf = mchientsf;
    }

    public ChdmensaId getId() {
        return this.id;
    }

    public void setId(ChdmensaId id) {
        this.id = id;
    }

    public String getMchmensaf() {
        return this.mchmensaf;
    }

    public void setMchmensaf(String mchmensaf) {
        this.mchmensaf = mchmensaf;
    }

    public String getMchientsf() {
        return this.mchientsf;
    }

    public void setMchientsf(String mchientsf) {
        this.mchientsf = mchientsf;
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName the senderName to set
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public ChdmensaId getPrimaryKey() {
        return id;
    }

    @Override
    public void cleanObject() {
    }
}
