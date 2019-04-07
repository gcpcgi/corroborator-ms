package com.corroborator.rest.domain;

public class ResponseStatus {
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	private String statusCode;

	public ResponseStatus() {
		super();
	}

	public ResponseStatus(String statusCode) {
		super();
		this.statusCode = statusCode;
	}



	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}
}
