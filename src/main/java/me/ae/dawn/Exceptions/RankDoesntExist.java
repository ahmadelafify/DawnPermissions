package me.ae.dawn.Exceptions;

import me.ae.dawn.Resources.Language;

public class RankDoesntExist extends Exception {

    public RankDoesntExist() {
        super(Language.getChatString("error.rank_doesnt_exist"));
    }

    public RankDoesntExist(String message) {
        super(message);
    }
}
