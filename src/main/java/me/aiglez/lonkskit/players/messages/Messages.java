package me.aiglez.lonkskit.players.messages;

public enum Messages {

    HEY("Hey");


    private String defaultMessage, path;
    Messages(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    public String toString(Object... replacements) {
        //TODO: implement in messages.yml
        return Replaceable.handle(defaultMessage, replacements);
    }
}
