package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.messages.Messages;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.ValueType;

public interface LocalMessager {

    void msg(String message);

    void msg(Iterable<String> messages);

    void msg(String message, Object... replacements);

    void msg(Messages message, Object... replacements);

    default void msg(ConfigurationNode node, Object... replacements) {
        if(node == null || node.getValueType() != ValueType.SCALAR) return;
        if(node.getString() == null || node.getString().isEmpty()) return;
        msg(node.getString(""), replacements);
    }
}
