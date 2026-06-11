package com.reconcilem.csv;

public class CsvReadException extends RuntimeException
{
    public CsvReadException(String message){
        super(message);
    }

    public CsvReadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
