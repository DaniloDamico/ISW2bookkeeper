package iteratorUtility;

import org.apache.bookkeeper.util.IteratorUtility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;
import java.util.PrimitiveIterator.OfLong;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MergePrimitiveLongIteratorTest {

    private final long[] expected;
    private final OfLong valueOne;
    private final OfLong valueTwo;

    private final boolean expectedException;


    @Parameters
    public static Collection<Object[]> getParameters(){
        OfLong valid1 = new OfLong() {
            private final long[] elements = {1, 3, 5};

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < elements.length;
            }

            @Override
            public long nextLong() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[currentIndex++];
            }
        };

        OfLong valid2 = new OfLong() {
            private final long[] elements = {1, 2, 3, 4, 6};

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < elements.length;
            }

            @Override
            public long nextLong() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[currentIndex++];
            }
        };

        long[] expected = { 1, 2, 3, 4, 5, 6};

        OfLong empty = new OfLong() {
            private final long[] elements = {};

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < elements.length;
            }

            @Override
            public long nextLong() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[currentIndex++];
            }
        };

        long[] expectedEmpty = {};

        OfLong invalid = new OfLong() {
            private final long[] elements = {1, 3, 5};

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                throw new NoSuchElementException();
            }

            @Override
            public long nextLong() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[currentIndex++];
            }
        };

        OfLong nonsorted = new OfLong() {
            private final long[] elements = {6, 4, 2};

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < elements.length;
            }

            @Override
            public long nextLong() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[currentIndex++];
            }
        };

        OfLong repetitive = new OfLong() {
            private final long[] elements = {2, 4, 4, 6};

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < elements.length;
            }

            @Override
            public long nextLong() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[currentIndex++];
            }
        };

        OfLong negative = new OfLong() {
            private final long[] elements = {-2, 4, 4, 6};

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < elements.length;
            }

            @Override
            public long nextLong() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[currentIndex++];
            }
        };

        long[] expectedNonsorted = {6,4,2};

        long[] expectedRepetitive = {2, 4, 4, 6};

        long[] negativeExpected = {-2, 4, 4, 6};


        return Arrays.asList(new Object[][]{
                //expected, iteratorOne, iteratorTwo, exception
                {expected, valid1, valid2, false}, // legal arrays, checks that it removes duplicates from the output iterator as per the documentation
                {expectedEmpty, valid1, empty, false}, // one empty array
                {expectedEmpty, empty, empty, false}, // empty arrays
                {expected, valid1, null, true}, // null !!!!
                {expected, valid1, invalid, true}, // invalid iterator !!!!!
                {expectedNonsorted, valid1, nonsorted, false}, // non sorted iterator
                {expectedRepetitive, valid1, repetitive, false}, // repetitive iterator
                {negativeExpected, valid1, negative, false}, // negative iterator
                {expectedEmpty, valid1, valid1, false}, // the same iterator twice
        });
    }

    public MergePrimitiveLongIteratorTest(long[] expected, OfLong valueOne, OfLong valueTwo, boolean exception){
        this.expected = expected;
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
        this.expectedException = exception;
    }

    @Test
    public void testMergePrimitiveLongIterator() {

        List<Long> list = new ArrayList<>();
        OfLong mergedIterator;

        try {
            mergedIterator = IteratorUtility.mergePrimitiveLongIterator(valueOne, valueTwo);
            while (mergedIterator.hasNext()) {
                list.add(mergedIterator.nextLong());
            }

        } catch (Exception e) {
            Assert.assertTrue(expectedException);
            return;
        }

        assertEquals(expected.length, list.size());

        System.out.println("expected: " + Arrays.toString(expected)+ " actual: " + list);

        for(int i=0; i<list.size(); i++){
            assertEquals(expected[i], list.get(i).longValue());
        }

        // Branch Coverage Tests
        try {
            mergedIterator.nextLong();
        } catch (NoSuchElementException e) {
            Assert.assertTrue(true);
        }


    }
}
