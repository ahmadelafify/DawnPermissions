package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class PermissionAlreadyAdded extends Exception {

    public PermissionAlreadyAdded() {
        super(Language.getChatString("error.permission_already_added"));
    }

    public PermissionAlreadyAdded(String message) {
        super(message);
    }
}
