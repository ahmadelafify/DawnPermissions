package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class PermissionNotFound extends Exception {

    public PermissionNotFound() {
        super(Language.getChatString("error.permission_not_found"));
    }

    public PermissionNotFound(String message) {
        super(message);
    }
}
