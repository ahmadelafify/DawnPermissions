package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class InvalidRankName extends Exception {

    // for when a rank name argument is left null
    public InvalidRankName() {
        super(Language.getChatString("error.invalid_rankname"));
    }

    public InvalidRankName(String message) {
        super(message);
    }
}