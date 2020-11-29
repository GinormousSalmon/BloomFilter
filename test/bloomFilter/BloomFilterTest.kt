package bloomFilter

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalStateException
import kotlin.math.abs
import java.security.MessageDigest
import java.util.*


class BloomFilterTest {

    private val size = 50

    private fun intHash1(a: Int): Int = abs(a) % size
    private fun intHash2(a: Int): Int = (abs(a) / size) % size
    private fun intHash3(a: Int): Int = (a / (Int.MAX_VALUE / size) / 2) + size / 2

    @Test
    fun addTest() {
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
        var falsePositives = 0
        for (e in controlSet)
            bloom.add(e)
        for (e in controlSet)
            assertTrue(bloom.mightContains(e))
    }
}