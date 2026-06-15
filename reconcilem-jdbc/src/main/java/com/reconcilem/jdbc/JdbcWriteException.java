package com.reconcilem.jdbc;

public class JdbcWriteException extends RuntimeException {

    public JdbcWriteException(String message) {
        super(message);
    }

    public JdbcWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
