package me.ae.dawn.Exceptions;

public class EmptyArgument extends Exception {

    // for when an argument is left null
    public EmptyArgument(String message) {
        super(message);
    }
}