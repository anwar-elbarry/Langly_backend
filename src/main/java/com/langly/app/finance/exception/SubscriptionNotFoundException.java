package com.langly.app.finance.exception;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(String message) {
        super(message);
    }

    public SubscriptionNotFoundException(String field, String value) {
        super(String.format("Subscription not found with %s: %s", field, value));
    }
}
