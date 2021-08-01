package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class NoPermission extends Exception {

    public NoPermission() {
        super(Language.getChatString("error.no_permission"));
    }

    public NoPermission(String message) {
        super(message);
    }
}