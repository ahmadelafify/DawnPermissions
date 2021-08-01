package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class InvalidSubcommand extends Exception {

    public InvalidSubcommand() {
        super(Language.getChatString("error.invalid_subcommand"));
    }

    public InvalidSubcommand(String message) {
        super(message);
    }
}