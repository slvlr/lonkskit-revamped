package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;

public class WolfAbility extends FunctionalAbility {

    public WolfAbility(ConfigurationNode configuration) {
        super("wolf", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(KitSelectEvent.class)
                .filter(e -> e.getKit() != null)
                .filter(e -> e.getKit().getBackendName().toUpperCase().contains("WOLF"))
                .handler(e -> DisguiseAPI.disguiseToAll(e.getLocalPlayer().toBukkit(), new MobDisguise(DisguiseType.WOLF)));

    }
}
