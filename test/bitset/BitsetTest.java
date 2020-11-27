package bitset;


import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BitsetTest {
    Random rand = new Random();

//    public static void println(Object line) {
//        System.out.println(line);
//    }
//
//    public static void print(Object line) {
//        System.out.print(line);
//    }

    @Test
    void addRemoveTest() {
        for (int i = 0; i < 50; i++) {
            int size = (int) (rand.nextFloat() * 50000);
            Bitset bitset = new Bitset(size);
            for (int j = 0; j < 10000; j++) {
                int random = (int) (rand.nextFloat() * 1000000);
                if (random < size) {
                    bitset.add(random);
                    assertTrue(bitset.contains(random));
                    bitset.remove(random);
                    assertFalse(bitset.contains(random));
                } else {
                    assertThrows(IllegalArgumentException.class, () -> bitset.add(random));
                    assertThrows(IllegalArgumentException.class, () -> bitset.remove(random));
                }
            }
        }
    }

    @Test
    void differenceTest() {
        for (int a = 0; a < 100; a++) {
            int size = (int) (rand.nextFloat() * 50000);
            Bitset bitset = new Bitset(size);
            ArrayList<Integer> trueBits = new ArrayList<>();
            for (int j = 0; j < 10000; j++) {
                int random = (int) (rand.nextFloat() * 1000000);
                if (random < size) {
                    trueBits.add(random);
                    bitset.add(random);
                }
            }
            bitset.difference();
            for (int i = 0; i < size; i++)
                if (trueBits.contains(i))
                    assertFalse(bitset.contains(i));
                else
                    assertTrue(bitset.contains(i));
        }
    }

    @Test
    void unionTest() {
        for (int a = 0; a < 100; a++) {
            int size1 = (int) (rand.nextFloat() * 50000);
            int size2 = (int) (rand.nextFloat() * 50000);
            Bitset bitset1 = new Bitset(size1);
            Bitset bitset2 = new Bitset(size2);
            ArrayList<Integer> trueBits1 = new ArrayList<>();
            ArrayList<Integer> trueBits2 = new ArrayList<>();
            for (int j = 0; j < 10000; j++) {
                int random = (int) (rand.nextFloat() * 1000000);
                if (random < size1) {
                    trueBits1.add(random);
                    bitset1.add(random);
                }
            }
            for (int j = 0; j < 10000; j++) {
                int random = (int) (rand.nextFloat() * 1000000);
                if (random < size2) {
                    trueBits2.add(random);
                    bitset2.add(random);
                }
            }
            Bitset result = bitset1.union(bitset2);
            for (int i = 0; i < result.length(); i++)
                if (trueBits1.contains(i) || trueBits2.contains(i))
                    assertTrue(result.contains(i));
                else
                    assertFalse(result.contains(i));
        }
    }

    @Test
    void intersectTest() {
        for (int a = 0; a < 100; a++) {
            int size1 = (int) (rand.nextFloat() * 50000);
            int size2 = (int) (rand.nextFloat() * 50000);
            Bitset bitset1 = new Bitset(size1);
            Bitset bitset2 = new Bitset(size2);
            ArrayList<Integer> trueBits1 = new ArrayList<>();
            ArrayList<Integer> trueBits2 = new ArrayList<>();
            for (int j = 0; j < 10000; j++) {
                int random = (int) (rand.nextFloat() * 1000000);
                if (random < size1) {
                    trueBits1.add(random);
                    bitset1.add(random);
                }
            }
            for (int j = 0; j < 10000; j++) {
                int random = (int) (rand.nextFloat() * 1000000);
                if (random < size2) {
                    trueBits2.add(random);
                    bitset2.add(random);
                }
            }
            Bitset result = bitset1.intersect(bitset2);
            for (int i = 0; i < result.length(); i++)
                if (trueBits1.contains(i) && trueBits2.contains(i))
                    assertTrue(result.contains(i));
                else
                    assertFalse(result.contains(i));
        }
    }
}