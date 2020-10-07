package me.aiglez.lonkskit.players;

public interface LocalMessager {

    void msg(String message);

    void msg(Iterable<String> messages);

    void msg(String message, Object... replacements);

}
