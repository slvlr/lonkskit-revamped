package me.aiglez.lonkskit.players;

public interface LocalMessager {

    void msg(String message);

    void msg(Iterable<String> messages);

    void msg(String message, Object... replacements);

    /*
        default void msg(String message) {
        Preconditions.checkNotNull(message, "message may not be null");
        if(toBukkit() != null) toBukkit().sendMessage(Text.colorize(message));
    }

    default void msg(Iterable<String> messages) {
        if(toBukkit() != null) {
            messages.forEach(this::msg);
        }
    }

    default void msg(String message, Object... replacements) {
        msg(Replaceable.handle(message, replacements));
    }
     */
}
