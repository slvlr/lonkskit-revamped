package me.aiglez.lonkskit.exceptions;

public class AbilityFileNotFoundException extends RuntimeException {

    public AbilityFileNotFoundException(String name) {
        super("Couldn't find the file (" + name == null ? "unknown" : name + ".yml" + ") in abilities folder. DO NOT REPORT THIS TO AIGLEZ!");
    }

}
