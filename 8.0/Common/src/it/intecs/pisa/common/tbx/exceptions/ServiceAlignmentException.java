/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.common.tbx.exceptions;

/**
 *
 * @author massi
 */
public class ServiceAlignmentException extends Exception{
    private String detail;
	
	public ServiceAlignmentException()
    {

    }

	public ServiceAlignmentException(String det)
    {
		detail=det;
    }
	
	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDetail() {
		return detail;
	}
}
