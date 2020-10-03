package me.aiglez.lonkskit.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.aiglez.lonkskit.KitPlugin;
import org.junit.After;
import org.junit.Before;

import java.io.File;

public class MainTests {

    private ServerMock server;
    private KitPlugin plugin;
    private File testDirectory;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = (KitPlugin) MockBukkit.load(KitPlugin.class);
        testDirectory = new File("D:" + File.separator + "Spigot"
                + File.separator + "Projects"
                + File.separator + "LonksKit-Revamped"
                + File.separator + "src"
                + File.separator + "test"
                + File.separator + "resources");
    }


    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

}
