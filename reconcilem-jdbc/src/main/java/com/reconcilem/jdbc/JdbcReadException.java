package com.reconcilem.jdbc;

public class JdbcReadException extends RuntimeException
{
    public JdbcReadException(String message)
    {
        super(message);
    }

    public JdbcReadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
