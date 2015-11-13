/**
 * 
 */
package com.vegadvisor.client.bo;

/**
 * @author Juan Camilo
 * Interfaz default de un Bussiness Object de la base de datos
 * K: llave primaria del objeto
 */ 
public interface AbstractBO<K> {

	/**
	 * Metodo para obtener la llave primaria del objeto
	 * @return la llave primaria del objeto
	 */
	public K getPrimaryKey();
	
	/**
	 * Metodo para limpiar el objeto
	 */
	public void cleanObject();
	
}
