import bloomFilter.BloomFilter
import kotlin.math.abs


const val size = 20

fun hash1(a: Int): Int = abs(a) % size
fun hash2(a: Int): Int = (abs(a) / size) % size
fun hash3(a: Int): Int = (a / (Int.MAX_VALUE / size) / 2) + size / 2

// расчет оптим кол-ва хэш-функций  // done
// произвольное кол-во хеш-ф   // done
// исследование зависимости вероятности от количества хэш-ф
fun main() {
    val bl = BloomFilter<Int>(size, 10)

    bl.setHashes(::hash1, ::hash2)
    println(bl.mightContains(6))
    bl.add(6)
    println(bl.mightContains(6))
    println(bl.mightContains(7))
}