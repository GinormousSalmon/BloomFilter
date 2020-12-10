package bloomFilter

import bitset.Bitset

@Suppress("unused")


class BloomFilter<T : Any>(private val N: Int) : Set<T> {
    private val bitset = Bitset(N)
    private var hashFunctionsSet = false

    inner class Hashes(vararg val hashes: (T) -> Int)

    private lateinit var hashes: Hashes


    fun setHashes(vararg hashes: (T) -> Int) {
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
            if(!bitset.contains(hash(e))){
                result = false
                break
            }

        return result
    }

    override val size: Int
        get() = N

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


