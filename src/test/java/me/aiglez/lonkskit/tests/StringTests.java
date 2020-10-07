package me.aiglez.lonkskit.tests;

import junit.framework.TestCase;
import me.aiglez.lonkskit.players.messages.Replaceable;
import org.junit.Assert;

public class StringTests extends TestCase {

    public void testReplaceable() {
        String test = "Hello {1}, welcome at {0}, {zd}, {8}";
        String handled = Replaceable.handle(test, "AigleZ", "Server.com");
        Assert.assertNotNull(handled);
        System.out.println(handled);
    }
}
