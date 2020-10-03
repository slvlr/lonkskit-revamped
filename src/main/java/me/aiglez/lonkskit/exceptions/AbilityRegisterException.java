package me.aiglez.lonkskit.exceptions;

public class AbilityRegisterException extends RuntimeException {

    public AbilityRegisterException(String abilityName, String reason) {
        super("Couldn't load the ability with name: " + abilityName + "\n" +
                "Reason: " + reason);
    }
}
