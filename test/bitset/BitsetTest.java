package bitset;


import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BitsetTest {
    Random rand = new Random();

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
                } else {
                    assertThrows(IllegalArgumentException.class, () -> bitset.add(random));
                }
            }
        }
    }
}