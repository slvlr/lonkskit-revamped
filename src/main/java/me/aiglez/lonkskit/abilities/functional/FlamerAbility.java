package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.helpers.FlamerListeners;
import me.lucko.helper.Services;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;

import java.io.IOException;

public class FlamerAbility extends FunctionalAbility {

    public FlamerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("flamer", configurationLoader);
        new FlamerListeners(Services.load(KitPlugin.class));
    }

    @Override
    public void registerListeners() { }
}
