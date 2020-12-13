package bloomFilter

import bitset.Bitset
import kotlin.math.*

@Suppress("unused")

const val ANSI_RESET = "\u001B[0m"
const val ANSI_YELLOW = "\u001B[33m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_RED = "\u001B[31m"


class BloomFilter<T : Any>(private val M: Int, private val N: Int = -1) : Set<T> {

    init {
        if (N < 1)
            println(ANSI_YELLOW + "WARNING: Number of elements setting is recommended" + ANSI_RESET)
    }

    private val bitset = Bitset(M)
    private var hashFunctionsSet = false

    inner class Hashes(vararg val hashes: (T) -> Int)

    private lateinit var hashes: Hashes

    fun setHashes(vararg hashes: (T) -> Int, doNotChange: Boolean = false) {
        this.setHashFunctions(*hashes, doNotChange = doNotChange)
    }

    fun setHashes(hashes: List<(T) -> Int>, doNotChange: Boolean = false) {
        this.setHashFunctions(*hashes.toTypedArray(), doNotChange = doNotChange)
    }

    private fun setHashFunctions(vararg hashes: (T) -> Int, doNotChange: Boolean) {
        check(hashes.isNotEmpty())
        this.hashes = if (N > 0) {
            val o = optimalHashesNumber()
            if (hashes.size != o)
                println(ANSI_YELLOW + "WARNING: The number of specified functions is not optimal. Specified " + hashes.size + " but optimal is " + o + ANSI_RESET)
            if (hashes.size > o && !doNotChange) {
                println(ANSI_YELLOW + "WARNING: The number of specified functions is more than optimal. Discarded to " + o + " explicitly" + ANSI_RESET)
                Hashes(*hashes.sliceArray(0 until o))
            } else {
                println(ANSI_GREEN + "Functions set" + ANSI_RESET)
                Hashes(*hashes)
            }
        } else {
            Hashes(*hashes)
        }
        this.hashFunctionsSet = true
    }


    fun add(e: T) {
        check(this.hashFunctionsSet) { "Hash functions are not set" }
        for (hash in this.hashes.hashes)
            this.bitset.add(abs(hash(e)) % this.size)
    }

    fun addAll(elements: Collection<T>) {
        for (e in elements)
            this.add(e)
    }

    fun mightContains(e: T): Boolean {
        check(this.hashFunctionsSet) { "Hash functions are not set" }
        for (hash in this.hashes.hashes)
            if (!this.bitset.contains(abs(hash(e)) % this.size))
                return false
        return true
    }

    fun optimalHashesNumber(): Int {
        check(N > 0) { "Elements number is not set or set incorrectly" }
        var n = round(ln(2.0) * this.size.toDouble() / N).toInt()
        if (n < 1)
            n = 1
        return n
    }

    val countHashes: Int
        get() = this.hashes.hashes.size

    override val size: Int
        get() = M

    override fun contains(element: T): Boolean = this.mightContains(element)

    override fun containsAll(elements: Collection<T>): Boolean {
        for (e in elements)
            if (!this.contains(e))
                return false
        return true
    }

    override fun isEmpty(): Boolean {
        for (i in this.bitset)
            if (i == true)
                return false
        return true
    }

    override fun iterator(): Iterator<T> {
        throw NotImplementedError()
    }
}


