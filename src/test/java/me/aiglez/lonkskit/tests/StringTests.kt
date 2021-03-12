package me.aiglez.lonkskit.tests

import be.seeseemelk.mockbukkit.MockBukkit
import me.aiglez.lonkskit.KitPlugin
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.swing.text.html.HTML


class StringTests {

    @Test
    fun testReplaceable() {

        val x = 9
        println(x.unaryPlus())
        println(x)

    }

    override fun toString(): String {
        return "mok"
    }
    infix fun String.o(u: Int){}

    fun dfs(graph: Int) {
        val visited = HashSet<Int>()
        fun df1s(current: Int) {

        }

    }

    @After
    fun distance() {
        val x1 = -10.125558
        val y1 = 80.899998
        val z1 = 25.26
        val x2 = 10010.012328
        val y2 = -1580.0
        val z2 = 2.255
        val squared = distanceSquared(x1, y1, z1, x2, y2, z2)
        println("Distance squared: $squared")
        val sqrt = distance(x1, y1, z1, x2, y2, z2)
        println("Distance: $sqrt")
        Assert.assertNotEquals(0, squared)
    }

    public fun distanceSquared(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
        return (square(x1 - x2)
                + square(y1 - y2)
                + square(z1 - z2))
    }

    private fun distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
        return Math.sqrt(distanceSquared(x1, y1, z1, x2, y2, z2))
    }

    fun square(num: Double): Double {
        return num * num
    }
}