package dev.tagless.craft.closedblocks.api.util.data;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * An optional list behaves like a java {@link java.util.ArrayList}
 * with the only difference it can be transformed into other type
 * of collections, can be mapped with default values if empty, will
 * throw if iterated in any way while empty and is immutable
 * @param <T> the list type
 */
@Immutable @SuppressWarnings("unused")
public final class OptionalList<T> extends AbstractList<T> implements List<T>, RandomAccess {

    private final T[] elements;

    @SafeVarargs
    private OptionalList(final T... elements) {
        this.elements = elements;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return this.elements.length;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return this.elements.length == 0;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(final Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     */
    @Override
    public int indexOf(final Object o) {
        return indexOfRange(o);
    }

    int indexOfRange(final Object o) {
        if (o == null) {
            for (int i = 0; i < this.elements.length; i++) {
                if (this.elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < this.elements.length; i++) {
                if (o.equals(this.elements[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     */
    @Override
    public int lastIndexOf(final Object o) {
        return lastIndexOfRange(o, this.elements.length);
    }

    int lastIndexOfRange(final Object o, final int end) {
        if (o == null) {
            for (int i = end - 1; i >= 0; i--) {
                if (this.elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = end - 1; i >= 0; i--) {
                if (o.equals(this.elements[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    T elementData(final int index) {
        return this.elements[index];
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public T get(final int index) {
        Objects.checkIndex(index, this.elements.length);
        return elementData(index);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchElementException {@inheritDoc}
     * @since 21
     */
    @Override
    public T getFirst() {
        if (this.elements.length == 0) {
            throw new NoSuchElementException();
        } else {
            return elementData(0);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchElementException {@inheritDoc}
     * @since 21
     */
    @Override
    public T getLast() {
        int last = this.elements.length - 1;
        if (last < 0) {
            throw new NoSuchElementException();
        } else {
            return elementData(last);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Collection)) {
            return false;
        }

        return (o.getClass() == OptionalList.class)
            ? equalsOptionalList((OptionalList<?>) o)
            : equalsRange((Collection<?>) o, 0, this.elements.length);
    }

    boolean equalsRange(final Collection<?> other, int from, final int to) {
        var oit = other.iterator();
        for (; from < to; from++) {
            if (!oit.hasNext() || !Objects.equals(this.elements[from], oit.next())) {
                return false;
            }
        }

        return !oit.hasNext();
    }

    private boolean equalsOptionalList(final OptionalList<?> other) {
        final int s = this.elements.length;
        if (s == other.elements.length) {
            for (int i = 0; i < s; i++) {
                if (!Objects.equals(this.elements[i], other.elements[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (Object o : this.elements) {
            hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
        }
        return hashCode;
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @see #listIterator(int)
     */
    @Override
    public @NotNull ListIterator<T> listIterator() {
        this.checkNotEmpty("listIterator");
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public @NotNull Iterator<T> iterator() {
        this.checkNotEmpty("iterator");
        return new Itr();
    }

    /**
     * {@inheritDoc}
     *
     * @param index
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @implSpec This implementation returns a straightforward implementation of the
     * {@code ListIterator} interface that extends the implementation of the
     * {@code Iterator} interface returned by the {@code iterator()} method.
     * The {@code ListIterator} implementation relies on the backing list's
     * {@code get(int)}, {@code set(int, E)}, {@code add(int, E)}
     * and {@code remove(int)} methods.
     *
     * <p>Note that the list iterator returned by this implementation will
     * throw an {@link UnsupportedOperationException} in response to its
     * {@code remove}, {@code set} and {@code add} methods unless the
     * list's {@code remove(int)}, {@code set(int, E)}, and
     * {@code add(int, E)} methods are overridden.
     *
     * <p>This implementation can be made to throw runtime exceptions in the
     * face of concurrent modification, as described in the specification for
     * the (protected) {@link #modCount} field.
     */
    @Override
    public @NotNull ListIterator<T> listIterator(final int index) {
        this.checkNotEmpty("listIterator");
        return super.listIterator(index);
    }

    /**
     * Creates a {@link Spliterator} over the elements in this list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED} and
     * {@link Spliterator#ORDERED}.  Implementations should document the
     * reporting of additional characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @implSpec The default implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em>
     * spliterator as follows:
     * <ul>
     * <li>If the list is an instance of {@link RandomAccess} then the default
     *     implementation creates a spliterator that traverses elements by
     *     invoking the method {@link List#get}.  If such invocation results or
     *     would result in an {@code IndexOutOfBoundsException} then the
     *     spliterator will <em>fail-fast</em> and throw a
     *     {@code ConcurrentModificationException}.
     *     If the list is also an instance of {@link AbstractList} then the
     *     spliterator will use the list's {@link AbstractList#modCount modCount}
     *     field to provide additional <em>fail-fast</em> behavior.
     * <li>Otherwise, the default implementation creates a spliterator from the
     *     list's {@code Iterator}.  The spliterator inherits the
     *     <em>fail-fast</em> of the list's iterator.
     * </ul>
     * @implNote The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     * @since 1.8
     */
    @Override
    public @NotNull Spliterator<T> spliterator() {
        this.checkNotEmpty("spliterator");
        return super.spliterator();
    }

    /**
     * {@inheritDoc}
     *
     * @return a reverse-ordered view of this collection, as a {@code List}
     * @implSpec The implementation in this interface returns a reverse-ordered List
     * view. The {@code reversed()} method of the view returns a reference
     * to this List. Other operations on the view are implemented via calls to
     * public methods on this List. The exact relationship between calls on the
     * view and calls on this List is unspecified. However, order-sensitive
     * operations generally delegate to the appropriate method with the opposite
     * orientation. For example, calling {@code getFirst} on the view results in
     * a call to {@code getLast} on this List.
     * @since 21
     */
    @Override
    public List<T> reversed() {
        this.checkNotEmpty("reversed");
        return super.reversed();
    }

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     *
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     *
     * @return a sequential {@code Stream} over the elements in this collection
     * @implSpec The default implementation creates a sequential {@code Stream} from the
     * collection's {@code Spliterator}.
     * @since 1.8
     */
    @Override
    public @NotNull Stream<T> stream() {
        this.checkNotEmpty("stream");
        return super.stream();
    }

    /**
     * Returns a possibly parallel {@code Stream} with this collection as its
     * source.  It is allowable for this method to return a sequential stream.
     *
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     *
     * @return a possibly parallel {@code Stream} over the elements in this
     * collection
     * @implSpec The default implementation creates a parallel {@code Stream} from the
     * collection's {@code Spliterator}.
     * @since 1.8
     */
    @Override
    public @NotNull Stream<T> parallelStream() {
        this.checkNotEmpty("parallelStream");
        return super.parallelStream();
    }

    private class Itr implements Iterator<T> {
        int cursor;
        int lastRet = -1;

        Itr() {}

        @Override
        public boolean hasNext() {
            return cursor != OptionalList.this.elements.length;
        }

        @Override
        public T next() {
            int i = cursor;
            if (i >= OptionalList.this.elements.length)
                throw new NoSuchElementException();

            cursor = i + 1;
            return OptionalList.this.elements[lastRet = i];
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    private class ListItr extends Itr implements ListIterator<T> {

        ListItr(final int index) {
            super();
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(final T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(final T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T previous() {
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();

            cursor = i;
            return OptionalList.this.elements[lastRet = i];
        }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        this.checkNotEmpty("forEach");

        Objects.requireNonNull(action);
        for (T obj : this.elements) {
            action.accept(obj);
        }
    }

    /**
     * Obtains a safe to use optional list from the
     * provided values
     * @param first the first value
     * @param elements any other additional value
     * @return the optional list
     */
    @SafeVarargs
    public final OptionalList<T> orElse(final T first, final T... elements) {
        T[] c = Arrays.copyOf(elements, elements.length + 1);
        System.arraycopy(elements, 0, c, 1, elements.length);
        c[0] = first;

        return new OptionalList<>(c);
    }

    /**
     * Obtains a safe to use optional list from the
     * provided values
     * @param other the values to read from
     * @return the optional list
     */
    @SuppressWarnings("unchecked")
    public OptionalList<T> orElse(final Collection<T> other) {
        T[] c = (T[]) other.toArray();
        return new OptionalList<>(c);
    }

    /**
     * Obtains a safe to use optional list from the
     * provided dynamic values
     * @param other the values to read from
     * @return the optional list
     */
    @SuppressWarnings("unchecked")
    public OptionalList<T> orElseGet(final Supplier<Collection<T>> other) {
        T[] c = (T[]) other.get().toArray();
        return new OptionalList<>(c);
    }

    /**
     * Throw if the list is empty
     * @param message the error message
     * @return the current list instance
     */
    public OptionalList<T> orElseThrow(final String message) {
        if (this.isEmpty()) {
            throw new IllegalStateException(message);
        }

        return this;
    }

    /**
     * Throw if the list is empty
     * @param exception the exception provider
     * @return the current list instance
     */
    public <E extends Throwable> OptionalList<T> orElseThrow(final Supplier<E> exception) throws E {
        if (this.isEmpty()) {
            throw exception.get();
        }

        return this;
    }

    private void checkNotEmpty(final String operation) {
        if (this.isEmpty()) {
            throw new IllegalStateException(String.format("Cannot perform %s on OptionalList because it's empty",
                    operation));
        }
    }

    /**
     * Collect the elements into any kind of data
     * structure. Please be aware this is the only method
     * exempt from empty-check, in order to allow developers
     * to "gracefully" work with empty sets if they allow
     * it.
     * @param collector the collector
     * @return the collector instance
     * @param <A> the mutable accumulation type of the reduction operation (often hidden as an implementation detail)
     * @param <R> the result type of the reduction operation
     */
    @NotNull
    public <A, R> R collect(final @NotNull Collector<? super T, A, R> collector) {
        return Stream.of(this.elements).collect(collector);
    }

    /**
     * Create an empty optional list
     * @return the optional list
     * @param <T> the list type
     */
    public static <T> OptionalList<T> empty() {
        return new OptionalList<>();
    }

    /**
     * Create a singleton optional list
     * @param value the list single value
     * @return the optional list
     * @param <T> the list type
     */
    public static <T> OptionalList<T> single(final T value) {
        return new OptionalList<>(value);
    }

    /**
     * Create an optional list from any kind
     * of collection
     * @param collection the collection to read from
     * @return the optional list (or the same collection if it's an instance of
     * OptionalList)
     * @param <T> the list type
     */
    @SuppressWarnings("unchecked")
    public static <T> OptionalList<T> collect(final Collection<T> collection) {
        if (collection instanceof OptionalList<T> l) {
            return l;
        }

        return new OptionalList<>(((T[]) collection.toArray()));
    }
}