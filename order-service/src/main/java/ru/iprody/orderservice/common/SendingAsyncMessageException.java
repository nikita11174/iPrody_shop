package ru.iprody.orderservice.common;

public class SendingAsyncMessageException extends RuntimeException {

    public SendingAsyncMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
