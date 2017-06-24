package flatmapIteratorImprove;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import io.faststream.query.db.nodes.stream.intstream.SI_Filter;
import io.faststream.query.db.nodes.stream.stream.SO_FlatMapToInt;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.runtime.StreamUtil;

public class FlatMapToIntFilterAsLongStreamIterator extends ArrayList.Processor {

    public Object process(Object[] ar, int listSize, TerminalQueryOperationNode node) {
        // Query: C_FLAT_MAP_TO_INT->C_FILTER->C_AS_LONG_STREAM->CT_TO_ITERATOR

        // Extract all functions from a linked list of query objects
        SI_Filter on = (SI_Filter) node.previous().previous();
        IntPredicate intPredicate = on.getIntPredicate();

        Function mapper = ((SO_FlatMapToInt) on.previous()).getMapper();

        return new ResultIterator(ar, 0, listSize, mapper, intPredicate);
    }

    static class ResultIterator implements java.util.PrimitiveIterator.OfLong {

        final Object[] a;

        int start;

        int end;

        int cursor;

        final Function mapper;

        final IntPredicate intPredicate;

        IntStream s0;
        PrimitiveIterator.OfInt s0Iterator;
        long next;

        ResultIterator(Object[] a, int start, int end, Function mapper, IntPredicate intPredicate) {
            this.a = a;
            this.start = start;
            this.cursor = start;
            this.mapper = mapper;
            this.intPredicate = intPredicate;
            this.cursor = start - 1;
            this.end = end - 1;
            advance();
        }

        public void advance() {

            // Vi skal have fundet naeste element inde vi stopper
            Object o = null;
            for (;;) {
                PrimitiveIterator.OfInt s0Iterator = this.s0Iterator;
                if (s0Iterator == null) {
                    this.s0 = s0 = (IntStream) mapper.apply(o);
                    if (s0 != null) {
                        s0Iterator = StreamUtil.fromIntStream(s0);
                    }
                }
                if (intPredicate.test(s0Iterator.nextInt())) {
                    next = s0Iterator.nextInt();
                    return;
                }
                if (cursor >= end) {
                    cursor = end + 1;
                }
                o = a[++cursor];
            }

        }

        public void remove() {
            throw new UnsupportedOperationException("remove is not supported");
        }

        public boolean hasNext() {
            return cursor <= end;
        }

        public long nextLong() {
            if (cursor > end) {
                throw new NoSuchElementException();
            }
            long next = this.next;
            advance();
            return next;
        }

        public Long next() {
            return nextLong();
        }
    }

    static class SplitHelper {

    }
}
