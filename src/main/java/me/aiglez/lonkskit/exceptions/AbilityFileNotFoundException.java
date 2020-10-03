package me.aiglez.lonkskit.exceptions;

public class AbilityFileNotFoundException extends RuntimeException {


    public AbilityFileNotFoundException(String name) {
        super("Couldn't find the file (" + name + ".yml" + ") in abilities folder.");
    }

}
