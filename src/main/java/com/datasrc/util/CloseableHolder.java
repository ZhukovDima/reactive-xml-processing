package com.datasrc.util;

import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The CloseableHolder class is responsible for managing a collection of AutoCloseable objects
 * and providing a mechanism to close them in a controlled manner.
 */
public class CloseableHolder implements Runnable {
    private final Deque<AutoCloseable> toClose = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        toClose.descendingIterator().forEachRemaining(it -> {
            try {
                it.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Adds an AutoCloseable object to the collection of objects to be closed.
     *
     * @param t   the AutoCloseable object to be added
     * @param <T> the type of the AutoCloseable object
     * @return the added AutoCloseable object
     */
    public <T extends AutoCloseable> T add(T t) {
        toClose.add(Objects.requireNonNull(t));
        return t;
    }
}
