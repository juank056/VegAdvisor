package com.vegadvisor.client.bo;

// Generated 23-oct-2015 19:39:18 by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.List;

/**
 * Fomhilfo generated by hbm2java
 */
public class Fomhilfo implements java.io.Serializable, AbstractBO<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer hifchifnk;
	private String usucusuak;
	private Date hiffregff;
	private String hifhoratf;
	private String hiftituaf;
	private String hifdetaaf;
	private Date hiffecuff;
	// Nombre del usuario que registra el hilo
	private String userName;
	// Lista de respuestas al hilo
	private List<Fodreshi> responses;

	public Fomhilfo() {
	}

	public Fomhilfo(String usucusuak, Date hiffregff, String hifhoratf,
			String hiftituaf, String hifdetaaf, Date hiffecuff) {
		this.usucusuak = usucusuak;
		this.hiffregff = hiffregff;
		this.hifhoratf = hifhoratf;
		this.hiftituaf = hiftituaf;
		this.hifdetaaf = hifdetaaf;
		this.hiffecuff = hiffecuff;
	}

	public Integer getHifchifnk() {
		return this.hifchifnk;
	}

	public void setHifchifnk(Integer hifchifnk) {
		this.hifchifnk = hifchifnk;
	}

	public String getUsucusuak() {
		return this.usucusuak;
	}

	public void setUsucusuak(String usucusuak) {
		this.usucusuak = usucusuak;
	}

	public Date getHiffregff() {
		return this.hiffregff;
	}

	public void setHiffregff(Date hiffregff) {
		this.hiffregff = hiffregff;
	}

	public String getHifhoratf() {
		return this.hifhoratf;
	}

	public void setHifhoratf(String hifhoratf) {
		this.hifhoratf = hifhoratf;
	}

	public String getHiftituaf() {
		return this.hiftituaf;
	}

	public void setHiftituaf(String hiftituaf) {
		this.hiftituaf = hiftituaf;
	}

	public String getHifdetaaf() {
		return this.hifdetaaf;
	}

	public void setHifdetaaf(String hifdetaaf) {
		this.hifdetaaf = hifdetaaf;
	}

	public Date getHiffecuff() {
		return this.hiffecuff;
	}

	public void setHiffecuff(Date hiffecuff) {
		this.hiffecuff = hiffecuff;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the responses
	 */
	public List<Fodreshi> getResponses() {
		return responses;
	}

	/**
	 * @param responses
	 *            the responses to set
	 */
	public void setResponses(List<Fodreshi> responses) {
		this.responses = responses;
	}

	@Override
	public Integer getPrimaryKey() {
		return hifchifnk;
	}

	@Override
	public void cleanObject() {

	}
}
