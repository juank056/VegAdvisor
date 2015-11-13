package com.vegadvisor.client.bo;

// Generated 23-oct-2015 19:39:18 by Hibernate Tools 4.3.1

/**
 * FodreshiId generated by hbm2java
 */
public class FodreshiId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int hifchifnk;
	private int rhfcrhfnk;

	public FodreshiId() {
	}

	public FodreshiId(int hifchifnk, int rhfcrhfnk) {
		this.hifchifnk = hifchifnk;
		this.rhfcrhfnk = rhfcrhfnk;
	}

	public int getHifchifnk() {
		return this.hifchifnk;
	}

	public void setHifchifnk(int hifchifnk) {
		this.hifchifnk = hifchifnk;
	}

	public int getRhfcrhfnk() {
		return this.rhfcrhfnk;
	}

	public void setRhfcrhfnk(int rhfcrhfnk) {
		this.rhfcrhfnk = rhfcrhfnk;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof FodreshiId))
			return false;
		FodreshiId castOther = (FodreshiId) other;

		return (this.getHifchifnk() == castOther.getHifchifnk())
				&& (this.getRhfcrhfnk() == castOther.getRhfcrhfnk());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getHifchifnk();
		result = 37 * result + this.getRhfcrhfnk();
		return result;
	}

}
