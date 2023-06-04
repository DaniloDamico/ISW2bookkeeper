import org.apache.bookkeeper.util.IteratorUtility;
import org.junit.Test;

import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfLong;

import static org.junit.Assert.assertEquals;

public class IteratorUtilityTest {

    @Test
    public void testMergePrimitiveLongIterator() {
        OfLong iter1 = new PrimitiveIterator.OfLong() {
            private final long[] elements = new long[] { 1, 2, 3, 4, 5 };
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < elements.length;
            }

            @Override
            public long nextLong() {
                return elements[index++];
            }
        };

        OfLong iter2 = new PrimitiveIterator.OfLong() {
            private final long[] elements = new long[] { 2, 4, 6, 8, 10 };
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < elements.length;
            }

            @Override
            public long nextLong() {
                return elements[index++];
            }
        };

        OfLong mergedIter = IteratorUtility.mergePrimitiveLongIterator(iter1, iter2);
        long[] expectedElements = new long[] { 1, 2, 3, 4, 5, 6, 8, 10 };
        int index = 0;
        while (mergedIter.hasNext()) {
            assertEquals(expectedElements[index++], mergedIter.nextLong());
        }
    }
}
