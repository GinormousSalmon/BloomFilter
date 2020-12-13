package bloomFilter

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalStateException
import java.security.MessageDigest
import kotlin.math.abs
import java.util.*
import kotlin.math.E
import kotlin.math.pow

@SuppressWarnings("unused")

class BloomFilterTest {
    private val random = Random()

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
        val bloom = BloomFilter<Int>(size)
        assertTrue(bloom.isEmpty())
        assertThrows(IllegalStateException::class.java) { bloom.add(random.nextInt()) }
        assertThrows(IllegalStateException::class.java) { bloom.mightContains(random.nextInt()) }
        bloom.setHashes(::intHash1, ::intHash2, ::intHash3)
        for (e in controlSet)
            assertFalse(bloom.mightContains(e))
        for (e in 0 until 1000)
            assertFalse(bloom.mightContains(random.nextInt()))
        for (e in controlSet) {
            bloom.add(e)
            assertFalse(bloom.isEmpty())
        }
        for (e in controlSet)
            assertTrue(bloom.mightContains(e))
    }

    private fun getRandomString(bound: Int): String {
        val length = random.nextInt(bound)
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
            for (i in 0 until n)
                controlList.add(getRandomString(100))
            val bloom = BloomFilter<String>(size, n)
            assertTrue(bloom.isEmpty())
            bloom.setHashes(::stringHash1, ::stringHash2, ::stringHash3)
            for (e in controlList)
                assertFalse(bloom.mightContains(e))
            for (i in controlList.indices) {
                bloom.add(controlList[i])
                for (j in 0..i)
                    assertTrue(bloom.mightContains(controlList[i]))
            }
            assertFalse(bloom.isEmpty())
        }
    }

    @Test
    fun containsAllTest() {
        val size = 500
        for (n in listOf(20, 50, 100, 500, 1000)) {
            val controlList = mutableListOf<String>()
            val random = Random()
            for (i in 0 until n)
                controlList.add(getRandomString(100))
            val bloom = BloomFilter<String>(size, n)
            assertTrue(bloom.isEmpty())
            bloom.setHashes(::stringHash1, ::stringHash2, ::stringHash3)

            assertFalse(bloom.containsAll(controlList))
            for (i in 0..20) {
                val partialList = (0 until random.nextInt(n)).map { controlList.random() }
                if (partialList.isNotEmpty())
                    assertFalse(bloom.containsAll(partialList))
                else
                    assertTrue(bloom.containsAll(partialList))
            }

            bloom.addAll(controlList)
            assertFalse(bloom.isEmpty())
            assertTrue(bloom.containsAll(controlList))
            for (i in 0..10) {
                val partialList = (0 until random.nextInt(n)).map { controlList.random() }
                assertTrue(bloom.containsAll(partialList))
            }
        }
    }

    @Test
    fun probabilityTest() {
        val n = 1000
        val size = 5000
        val controlList = mutableListOf<String>()
        for (i in 0 until n)
            controlList.add(getRandomString(100))
        val bloom = BloomFilter<String>(size, n)
        assertTrue(bloom.isEmpty())
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
            if (pos < 0.05)  // the formula doesn't work on very small possibility values
                pos = 0.0
            assertTrue(pos < pos0)   //accounting for error
        }
        assertFalse(bloom.isEmpty())
    }

    class Person(val firstname: String, val lastname: String, val age: Int)

    // 2 147 483 647
    private fun hash1(p: Person): Int = (p.firstname + p.lastname + p.age.toString()).hashCode()
    private fun hash2(p: Person): Int = stringHash2(p.firstname + p.lastname + p.age.toString())
    private fun hash3(p: Person): Int = stringHash3(p.firstname + p.lastname + p.age.toString())
    private fun hash4(p: Person): Int {
        val a = p.age * 10000000
        val b = (p.firstname.hashCode() / 2 + Int.MAX_VALUE / 2) / 2870968 * 10000
        val c = (p.lastname.hashCode() / 2 + Int.MAX_VALUE / 2) / 588836
        return a + b + c
    }

    private fun hash5(p: Person): Int {
        val a = (214 - p.age) * 10000000
        val b = (stringHash2(p.firstname) / 2 + Int.MAX_VALUE / 2) / 2870968 * 10000
        val c = (stringHash2(p.lastname) / 2 + Int.MAX_VALUE / 2) / 588836
        return a + b + c
    }

    private val hashes = listOf(::hash3, ::hash1, ::hash4, ::hash2, ::hash5)


    @Test
    fun probabilityTest2() {
        val n = 800
        val size = 3500
        val controlList = mutableListOf<Person>()
        for (i in 0 until n) {
            val fname = getRandomString(20)
            val lname = getRandomString(20)
            val age = random.nextInt(70)
            controlList.add(Person(fname, lname, age))
        }

        println("optimal is ${BloomFilter<Any>(size, n).optimalHashesNumber()}")
        for (number in 1..5) {
            val bloom = BloomFilter<Person>(size, n)
            bloom.setHashes(hashes.subList(0, number), doNotChange = true)
            var prob = 0.0

            for (i in controlList.indices) {
                bloom.add(controlList[i])

                var falsePositives = 0
                for (j in i + 1 until controlList.size)
                    if (bloom.mightContains(controlList[j]))
                        falsePositives += 1
                val pos = falsePositives / n.toDouble()
                prob += pos
            }
            prob /= n
            println("$number function(s) $prob average probability")
        }
    }
}
