import bloomFilter.BloomFilter


fun hash1(e: Int): Int = e * 2
fun hash2(e: Int): Int = e / 3
fun hash3(e: Int): Int = e -5


fun main() {
    val bl = BloomFilter<Int>(20)
    bl.setHashes(::hash1, ::hash2, ::hash3)
    println(bl.mightContains(6))
    bl.add(6)
    println(bl.mightContains(6))
    println(bl.mightContains(7))
}