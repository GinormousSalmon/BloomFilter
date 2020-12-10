package bloomFilter

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalStateException
import kotlin.math.abs
import java.util.*
import kotlin.math.E
import kotlin.math.pow


class BloomFilterTest {

    private var size = 50
    private val k = 3  // number of hash functions

    private fun intHash1(a: Int): Int = abs(a) % size
    private fun intHash2(a: Int): Int = (abs(a) / size) % size
    private fun intHash3(a: Int): Int = (a / (Int.MAX_VALUE / size) / 2) + size / 2

    private fun stringHash1(a: String): Int = a.length % size
    private fun stringHash2(a: String): Int = abs(a.hashCode()) % size
    private fun stringHash3(a: String): Int = abs(a.toSet().toString().hashCode()) % size

    @Test
    fun generalTest() {
        val controlSet = mutableSetOf<Int>()
        val random = Random()
        for (i in 0 until 10)
            controlSet.add(random.nextInt())
        val bloom = BloomFilter<Int>(size)
        assertThrows(IllegalStateException::class.java) { bloom.add(random.nextInt()) }
        assertThrows(IllegalStateException::class.java) { bloom.mightContains(random.nextInt()) }
        bloom.setHashes(::intHash1, ::intHash2, ::intHash3)
        for (e in controlSet)
            assertFalse(bloom.mightContains(e))
        for (e in 0 until 1000)
            assertFalse(bloom.mightContains(random.nextInt()))
        for (e in controlSet)
            bloom.add(e)
        for (e in controlSet)
            assertTrue(bloom.mightContains(e))
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @Test
    fun containsTest() {
        for (n in listOf(1, 10, 50, 100, 500)) {
            val controlList = mutableListOf<String>()
            val random = Random()
            for (i in 0 until n)
                controlList.add(getRandomString(random.nextInt(100)))
            val bloom = BloomFilter<String>(size)
            bloom.setHashes(::stringHash1, ::stringHash2, ::stringHash3)
            for (e in controlList)
                assertFalse(bloom.mightContains(e))
            for (i in controlList.indices) {
                bloom.add(controlList[i])
                for (j in 0..i)
                    assertTrue(bloom.mightContains(controlList[i]))
            }
        }
    }

    @Test
    fun probabilityTest() {
        val n = 3000
        size = 5000
        val controlList = mutableListOf<String>()
        val random = Random()
        for (i in 0 until n)
            controlList.add(getRandomString(random.nextInt(100)))
        val bloom = BloomFilter<String>(size)
        bloom.setHashes(::stringHash1, ::stringHash2, ::stringHash3)
        for (e in controlList)
            assertFalse(bloom.mightContains(e))
        for (i in controlList.indices) {
            var falsePositives = 0
            bloom.add(controlList[i])
            for (j in 0..i)
                assertTrue(bloom.mightContains(controlList[i]))
            val pos0 = (1 - E.pow(-k * (i + 1) / size.toDouble())).pow(k)
            for (j in i + 1 until controlList.size)
                if (bloom.mightContains(controlList[j]))
                    falsePositives += 1
            var pos = falsePositives / n.toDouble()
            println("$pos0 $pos $falsePositives")
            if (pos < 0.1)  // the formula doesn't work on very small possibility values
                pos = 0.0
            assertTrue(pos * 0.95 < pos0)   //accounting for error
        }
    }
}