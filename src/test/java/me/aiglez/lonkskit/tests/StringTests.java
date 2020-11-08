package me.aiglez.lonkskit.tests;

import me.aiglez.lonkskit.messages.Replaceable;
import org.junit.Assert;
import org.junit.Test;

public class StringTests {

    @Test
    public void testReplaceable() {
        String test = "Hello {1}, welcome at {0}, {zd}, {8}";
        String handled = Replaceable.handle(test, "AigleZ", "Server.com");
        Assert.assertNotNull(handled);
        System.out.println(handled);
    }

    @Test
    public void distance() {
        final double x1 = -10.125558D;
        final double y1 = 80.899998D;
        final double z1 = 25.26D;


        final double x2 = 10010.012328D;
        final double y2 = -1580D;
        final double z2 = 2.255D;

        final double squared = distanceSquared(x1, y1, z1, x2, y2, z2);
        System.out.println("Distance squared: " + squared);

        final double sqrt = distance(x1, y1, z1, x2, y2, z2);
        System.out.println("Distance: " + sqrt);
        Assert.assertNotEquals(0, squared);

    }

    private double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        return square(x1 - x2)
                + square(y1 - y2)
                + square(z1 - z2);
    }

    private double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    public double square(double num) {
        return num * num;
    }
}
