package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class InvalidPlayer extends Exception {

    public InvalidPlayer() {
        super(Language.getChatString("error.invalid_player"));
    }

    public InvalidPlayer(String message) {
        super(message);
    }
}