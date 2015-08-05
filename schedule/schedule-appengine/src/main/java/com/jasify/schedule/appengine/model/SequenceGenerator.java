package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.SequenceMeta;
import org.slim3.datastore.Datastore;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Persistence generator for incremental sequences
 *
 * @author krico
 * @since 05/08/15.
 */
public class SequenceGenerator {
    private final SequenceMeta meta = SequenceMeta.get();
    private final Key name;
    private final int increment;
    private Iterator<Long> range = Collections.emptyIterator();

    public SequenceGenerator(String name) {
        this(name, 1);
    }

    public SequenceGenerator(String name, int increment) {
        this.name = Datastore.createKey(meta, name);
        this.increment = increment;
        Preconditions.checkArgument(increment > 0, "increment <= 0");
    }

    private void loadRange() {
        range = TransactionOperator.execute(new TransactionOperation<Iterator<Long>, RuntimeException>() {
            @Override
            public Iterator<Long> execute(Transaction tx) throws RuntimeException {
                Sequence sequence = Datastore.getOrNull(SequenceMeta.get(), name);
                if (sequence == null) {
                    sequence = new Sequence();
                    sequence.setName(name);
                    sequence.setNext(1L);
                }
                final Long next = sequence.getNext() == null ? 1L : sequence.getNext();
                sequence.setNext(next + increment);
                Datastore.put(tx, sequence);
                tx.commit();
                return new LongRangeIterator(next, next + increment);
            }
        });
    }

    public long next() {
        Preconditions.checkState(Datastore.getCurrentTransaction() == null, "Cannot call next() within a transaction");
        if (!range.hasNext()) {
            loadRange();
        }
        return range.next();
    }

    static class LongRangeIterator implements Iterator<Long> {
        private final long end;
        private long next;

        public LongRangeIterator(long start, long end) {
            this.next = start;
            this.end = end;
        }

        @Override
        public boolean hasNext() {
            return next < end;
        }

        @Override
        public Long next() {
            if (hasNext()) return next++;
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
