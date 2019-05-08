package com.tg.async.exception;

/**
 * Created by twogoods on 2018/5/1.
 */
public class UnsupportTypeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportTypeException() {
	}

	public UnsupportTypeException(String message) {
		super(message);
	}

	public UnsupportTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportTypeException(Throwable cause) {
		super(cause);
	}
}
