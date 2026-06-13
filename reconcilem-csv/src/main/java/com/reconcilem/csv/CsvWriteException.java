package com.reconcilem.csv;

public class CsvWriteException extends RuntimeException
{
    public CsvWriteException(String message)
    {
        super(message);
    }
    public CsvWriteException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
