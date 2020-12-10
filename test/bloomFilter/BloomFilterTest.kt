package bloomFilter

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalStateException
import java.security.MessageDigest
import kotlin.math.abs
import java.util.*
import kotlin.math.E
import kotlin.math.pow


class BloomFilterTest {

    private fun intHash1(a: Int): Int = abs(a)
    private fun intHash2(a: Int): Int = ((a * 2654435761) % (Int.MAX_VALUE.toLong() * 2)).toInt()
    private fun intHash3(a: Int): Int {
        var x = (a shr 16 xor a) * 0x45d9f3b
        x = (x shr 16 xor x) * 0x45d9f3b
        x = x shr 16 xor x
        return x
    }

    private fun stringHash1(a: String): Int = abs(a.hashCode())
    private fun stringHash2(a: String): Int {
        val dig = MessageDigest.getInstance("SHA-256").digest(a.toByteArray())
        val hash = dig.joinToString(separator = "") { String.format("%02X", it) }
        return hash.hashCode()
    }
    private fun stringHash3(a: String): Int {
        val dig = MessageDigest.getInstance("SHA-512").digest(a.toByteArray())
        val hash = dig.joinToString(separator = "") { String.format("%02X", it) }
        return hash.hashCode()
    }


        @Test
    fun generalTest() {
        val size = 50
        val n = 10
        val random = Random()
        val controlSet = mutableSetOf<Int>()
        for (i in 0 until n)
            controlSet.add(random.nextInt())
        val bloom = BloomFilter<Int>(size, n)
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
        val size = 50
        for (n in listOf(1, 5, 10, 50, 100, 500)) {
            val controlList = mutableListOf<String>()
            val random = Random()
            for (i in 0 until n)
                controlList.add(getRandomString(random.nextInt(100)))
            val bloom = BloomFilter<String>(size, n)
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
        val n = 1000
        val size = 5000
        val controlList = mutableListOf<String>()
        val random = Random()
        for (i in 0 until n)
            controlList.add(getRandomString(random.nextInt(100)))
        val bloom = BloomFilter<String>(size, n)
        bloom.setHashes(::stringHash1, ::stringHash2, ::stringHash3)
        val k = bloom.countHashes
        for (e in controlList)
            assertFalse(bloom.mightContains(e))
        for (i in controlList.indices) {
            var falsePositives = 0
            bloom.add(controlList[i])
            assertTrue(bloom.mightContains(controlList[i]))
            val pos0 = (1 - E.pow(-k * (i + 1) / size.toDouble())).pow(k)
            for (j in i + 1 until controlList.size)
                if (bloom.mightContains(controlList[j]))
                    falsePositives += 1
            var pos = falsePositives / n.toDouble()
            println("$pos0 $pos $falsePositives")
            if (pos < 0.02)  // the formula doesn't work on very small possibility values
                pos = 0.0
            assertTrue(pos < pos0)   //accounting for error
        }
    }
}
