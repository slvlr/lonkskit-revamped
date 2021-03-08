package me.aiglez.lonkskit.tests;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.text.NumberFormatter;
import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class StringTests {

    @Test
    public void testReplaceable() throws IOException {
        float kd = (float) 1 / (float) 1000;
        System.out.println(kd);
        System.out.println(new DecimalFormat("#0.00").format(kd));


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
