package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PigAbility extends FunctionalAbility {

    public PigAbility(ConfigurationNode configuration) {
        super("pig", configuration);
    }

    @Override
    public void handleListeners() {
    }
}
