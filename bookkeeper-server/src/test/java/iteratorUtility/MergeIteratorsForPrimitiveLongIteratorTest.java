package iteratorUtility;

import org.apache.bookkeeper.util.IteratorUtility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.PrimitiveIterator.OfLong;
import java.util.function.ToLongFunction;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MergeIteratorsForPrimitiveLongIteratorTest {

    private final long[] expected;
    private final Iterator<Integer> valueOne;
    private final Iterator<Integer> valueTwo;
    private final Comparator<Integer> comparator;
    private final ToLongFunction<Integer> function;
    private final boolean expectedException;

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters(){

        List<Integer> validList1 = Arrays.asList(1, 3, 5, 7, 9);
        List<Integer> validList2 = Arrays.asList(1, 2, 4, 6, 8, 10);
        List<Integer> nonSortedList = Arrays.asList(6, 4, 2, 8, 10);
        List<Integer> repetitiveList = Arrays.asList(2, 4, 4, 8, 10);
        List<Integer> emptyList = Collections.emptyList();
        List<Integer> negativeList = Arrays.asList(-1, 3, 5, 7, 9);

        Iterator<Integer> valid1 = validList1.iterator();
        Iterator<Integer> valid2 = validList2.iterator();
        Iterator<Integer> nonsorted = nonSortedList.iterator();
        Iterator<Integer> repetitive = repetitiveList.iterator();
        Iterator<Integer> empty = emptyList.iterator();
        Iterator<Integer> negative = negativeList.iterator();

        long[] expected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        long[] expectedEmpty = {};

        Iterator<Integer> invalid = new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                throw new UnsupportedOperationException("Invalid iterator");
            }

            @Override
            public Integer next() {
                throw new UnsupportedOperationException("Invalid iterator");
            }
        };

        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");

        Iterator<String> stringIterator = names.iterator();

        long[] expectedNonsorted = {6, 4, 2, 8, 10};
        long[] expectedRepetitive = {2, 4, 4, 8, 10};
        long[] expectedNegative = {-1, 3, 5, 7, 9};

        Comparator<Integer> validComparator = Comparator.naturalOrder();
        Comparator<Integer> invalidComparator = (a, b) -> {
            throw new UnsupportedOperationException("Invalid comparator");};

        ToLongFunction<Integer> validFunction = Integer::longValue;
        ToLongFunction<Integer> invalidFunction =  obj -> {
            throw new UnsupportedOperationException("Invalid function");};

        return Arrays.asList(new Object[][]{
                //expected,         iteratorOne,    iteratorTwo,    comparator,         function,       expectedFailure
                {expected,          valid1,         valid2,         validComparator,    validFunction,  false}, // legal arrays, checks that it removes duplicates from the output iterator as per the documentation
                {expectedEmpty,     valid1,         empty,          validComparator,    validFunction,  false}, // an empty array and a non empty array
                {expectedEmpty,     empty,          empty,          validComparator,    validFunction,  false}, // empty arrays
                {expected,          valid1,         null,           validComparator,    validFunction,  true}, // null
                {expected,          null,           null,           validComparator,    validFunction,  true}, // null
                {expected,          valid1,         invalid,        validComparator,    validFunction,  true}, // invalid iterator
                {expected,          valid1,         stringIterator, validComparator,    validFunction,  true}, // iterator of different type
                {expectedNonsorted, valid1,         nonsorted,      validComparator,    validFunction,  false}, // non sorted iterator
                {expectedRepetitive,valid1,         repetitive,     validComparator,    validFunction,  false}, // repetitive iterator
                {expectedNegative,  valid1,         negative,       validComparator,    validFunction,  false}, // negative iterator

                {expectedEmpty,     valid1,         valid2,         invalidComparator,  validFunction,  true}, // invalid comparator
                {expectedEmpty,     valid1,         valid2,         null,               validFunction,  true}, // null comparator
                {expectedEmpty,     valid1,         valid2,         validComparator,    invalidFunction,true}, // invalid function
                {expectedEmpty,     valid1,         valid2,         validComparator,    null,           true}, // null function

        });
    }

    public MergeIteratorsForPrimitiveLongIteratorTest(long[] expected, Iterator<Integer> valueOne, Iterator<Integer> valueTwo,
                                                      Comparator<Integer> comparator, ToLongFunction<Integer> function, boolean exception){
        this.expected = expected;
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
        this.comparator = comparator;
        this.function = function;
        this.expectedException = exception;
    }

    @Test
    public void testMergeIteratorsForPrimitiveLongIterator(){

        List<Long> list = new ArrayList<>();
        OfLong mergedIterator;

        try {
            mergedIterator = IteratorUtility.mergeIteratorsForPrimitiveLongIterator(valueOne, valueTwo, comparator, function);
            while (mergedIterator.hasNext()) {
                list.add(mergedIterator.nextLong());
            }
        } catch (Exception e) {
            Assert.assertTrue(expectedException);
            return;
        }

        assertEquals(expected.length, list.size());
        System.out.println("expected: " + Arrays.toString(expected) + " actual: " + list);

        for(int i=0; i<list.size(); i++){
            assertEquals(expected[i], list.get(i).longValue());
        }



    }
}
