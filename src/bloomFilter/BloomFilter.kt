package bloomFilter

import bitset.Bitset
import kotlin.math.ln
import kotlin.math.round

@Suppress("unused")

const val ANSI_RESET = "\u001B[0m"
const val ANSI_YELLOW = "\u001B[33m"


class BloomFilter<T : Any>(private val M: Int, private val N: Int = -1) : Set<T> {
    private val bitset = Bitset(M)
    private var hashFunctionsSet = false

    inner class Hashes(vararg val hashes: (T) -> Int)

    private lateinit var hashes: Hashes

    fun setHashes(vararg hashes: (T) -> Int) {
        if (N > 0) {
            val o = optimalHashesNumber()
            if (hashes.size != o)
                println(ANSI_YELLOW + "WARNING: The number of specified functions is not optimal. Specified " + hashes.size + " but optimal is " + o + ANSI_RESET)
        }
        this.hashes = Hashes(*hashes)
        this.hashFunctionsSet = true
    }


    fun add(e: T) {
        check(this.hashFunctionsSet) { "Hash functions are not set" }

        for (hash in this.hashes.hashes)
            bitset.add(hash(e))
    }

    fun mightContains(e: T): Boolean {
        check(this.hashFunctionsSet) { "Hash functions are not set" }

        var result = true
        for (hash in this.hashes.hashes)
            if (!bitset.contains(hash(e))) {
                result = false
                break
            }

        return result
    }

    fun optimalHashesNumber(): Int {
        check(N > 0) { "Elements number is not set or set incorrectly" }
        var n = round(ln(2.0) * this.size / N).toInt()
        if (n < 1)
            n = 1
        return n
    }

    override val size: Int
        get() = M

    override fun contains(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun iterator(): Iterator<T> {
        TODO("Not yet implemented")
    }
}


