package com.type2labs.nevernote.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private List<String> messages = new ArrayList<>();

    public ValidationException(List<String> messages) {
        this.messages = messages;
    }


    public List<String> getMessages() {
        return messages;
    }
}
