package bitset;

import java.util.*;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")


public class Bitset implements Iterable<Boolean> {
    private final int length;
    private final byte[] data;


    public Bitset(int N) {
        if (N <= 0)
            throw new IllegalArgumentException();

        this.length = N;
        this.data = new byte[(int) Math.ceil((double) N / 8)];
        Arrays.fill(this.data, (byte) -128);
    }


    public int length() {
        return this.length;
    }

    public void add(int n) {
        if (n < 0 || n >= this.length)
            throw new IllegalArgumentException();

        int byteIndex = n / 8;
        this.data[byteIndex] = (byte) (((this.data[byteIndex] + 128) | (int) Math.pow(2, 7 - n % 8)) - 128);
    }


    public boolean contains(int n) {
        if (n < 0 || n >= this.length)
            throw new IllegalArgumentException();
        return ((this.data[n / 8] + 128) / (int) Math.pow(2, 7 - n % 8)) % 2 == 1;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        int index = 1;
        for (Boolean b : this)
            result.append(b ? "1" : "0").append((index++ % 8 == 0) ? "." : "");
        return result.toString();
    }

    @NotNull
    public Iterator<Boolean> iterator() {
        return new BitsetIterator();
    }

    public class BitsetIterator implements Iterator<Boolean> {
        private int currentIndex;

        private BitsetIterator() {
            currentIndex= 0;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < length();
        }

        @Override
        public Boolean next() {
            return contains(currentIndex++);
        }

        @Override
        public void remove() {
            //TODO
            throw new NotImplementedError();
        }
    }
}