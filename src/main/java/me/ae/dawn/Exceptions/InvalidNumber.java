package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class InvalidNumber extends Exception {

    public InvalidNumber() {
        super(Language.getChatString("error.invalid_integer"));
    }

    public InvalidNumber(String message) {
        super(message);
    }
}