package me.aiglez.lonkskit.controllers;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.struct.LoginItemStack;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlayerController {

    private final Map<String, LoginItemStack> loginItems;

    public PlayerController() {
        this.loginItems = Maps.newHashMap();
    }

    public void loadLoginItems() {
        final ConfigurationNode config = KitPlugin.getSingleton().getConf().getNode("hotbar");

        for (final Object childName : config.getChildrenMap().keySet()) {
            final ConfigurationNode child = config.getNode(childName);

            final String name = child.getNode("name").getString("");
            final Material material = Material.matchMaterial(child.getNode("material").getString("STONE"));

            try {
                final List<String> lore = child.getNode("lore").getList(new TypeToken<String>() {});
                final List<String> playerCommands = child.getNode("lore").getList(new TypeToken<String>() {});
                final List<String> consoleCommands = child.getNode("lore").getList(new TypeToken<String>() {});

                final LoginItemStack loginItemStack = new LoginItemStack(
                        ItemStackBuilder.of(material).name(name).lore(lore).build(),
                        playerCommands,
                        consoleCommands
                );

                loginItems.put((String) childName, loginItemStack);

            } catch (NullPointerException | ObjectMappingException e) {
                Logger.severe("Couldn't load a login itemstack, skipping this one.");
                e.printStackTrace();
            }
        }
    }

    public Collection<LoginItemStack> getLoginItems() {
        return Collections.unmodifiableCollection(loginItems.values());
    }
}
