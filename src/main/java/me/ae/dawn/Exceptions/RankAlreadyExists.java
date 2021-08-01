package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class RankAlreadyExists extends Exception {

    public RankAlreadyExists() {
        super(Language.getChatString("error.rank_already_exists"));
    }

    public RankAlreadyExists(String message) {
        super(message);
    }
}
