package me.aiglez.lonkskit.abilities.functional.johan;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;

import java.io.IOException;

public class FlamerAbility extends FunctionalAbility {

    public FlamerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("flamer", configurationLoader);
    }

    @Override
    public void registerListeners() {

    }
}
