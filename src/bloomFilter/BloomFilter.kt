package bloomFilter

import bitset.Bitset

class BloomFilter(N: Int) {
    val bitset = Bitset(N)

}

fun main() {
    val a = Bitset(10)
    for (i in a)
        println(i)
}