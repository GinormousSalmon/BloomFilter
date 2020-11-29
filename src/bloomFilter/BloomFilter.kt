package bloomFilter

import bitset.Bitset

@Suppress("unused")


class BloomFilter<T : Any>(private val N: Int) : Set<T> {
    private val bitset = Bitset(N)
    private var hashFunctionsSet = false

    inner class Hashes(private val hash1: (T) -> Int, private val hash2: (T) -> Int, private val hash3: (T) -> Int) {
        fun hash1(e: T) = hash1.invoke(e)
        fun hash2(e: T) = hash2.invoke(e)
        fun hash3(e: T) = hash3.invoke(e)
    }

    private lateinit var hashes: Hashes


    fun setHashes(hash1: (T) -> Int, hash2: (T) -> Int, hash3: (T) -> Int) {
        this.hashes = Hashes(hash1, hash2, hash3)
        this.hashFunctionsSet = true
    }


    fun add(e: T) {
        check(this.hashFunctionsSet) { "Hash functions are not set" }
        bitset.add(this.hashes.hash1(e))
        bitset.add(this.hashes.hash2(e))
        bitset.add(this.hashes.hash3(e))
    }

    fun mightContains(e: T): Boolean {
        check(this.hashFunctionsSet) { "Hash functions are not set" }
        return bitset.contains(this.hashes.hash1(e)) && bitset.contains(this.hashes.hash2(e)) &&
                bitset.contains(this.hashes.hash3(e))
    }

    override val size: Int
        get() = N

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun iterator(): Iterator<T> {
        TODO("Not yet implemented")
    }

    override fun contains(element: T): Boolean {
        TODO("Not yet implemented")
    }
}


