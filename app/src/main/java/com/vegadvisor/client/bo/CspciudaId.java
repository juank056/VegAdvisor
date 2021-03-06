package com.vegadvisor.client.bo;

// Generated 23-oct-2015 19:39:18 by Hibernate Tools 4.3.1

/**
 * CspciudaId generated by hbm2java
 */
public class CspciudaId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String paicpaiak;
	private String ciucciuak;

	public CspciudaId() {
	}

	public CspciudaId(String paicpaiak, String ciucciuak) {
		this.paicpaiak = paicpaiak;
		this.ciucciuak = ciucciuak;
	}

	public String getPaicpaiak() {
		return this.paicpaiak;
	}

	public void setPaicpaiak(String paicpaiak) {
		this.paicpaiak = paicpaiak;
	}

	public String getCiucciuak() {
		return this.ciucciuak;
	}

	public void setCiucciuak(String ciucciuak) {
		this.ciucciuak = ciucciuak;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof CspciudaId))
			return false;
		CspciudaId castOther = (CspciudaId) other;

		return ((this.getPaicpaiak() == castOther.getPaicpaiak()) || (this
				.getPaicpaiak() != null && castOther.getPaicpaiak() != null && this
				.getPaicpaiak().equals(castOther.getPaicpaiak())))
				&& ((this.getCiucciuak() == castOther.getCiucciuak()) || (this
						.getCiucciuak() != null
						&& castOther.getCiucciuak() != null && this
						.getCiucciuak().equals(castOther.getCiucciuak())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getPaicpaiak() == null ? 0 : this.getPaicpaiak().hashCode());
		result = 37 * result
				+ (getCiucciuak() == null ? 0 : this.getCiucciuak().hashCode());
		return result;
	}

}
