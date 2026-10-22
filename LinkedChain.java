/**
 * A generic chain of linked nodes. Use it as the storage inside your
 * own classes, and extend or modify it as your project needs.
 *
 * This class owns storage only: nodes, the head reference, and the
 * count. It contains no application logic. A class that needs a
 * collection should hold a LinkedChain field and call its methods.
 *
 * add() places the new entry at the FRONT of the chain, so entries added
 * with add() come back from toArray() in reverse order of insertion.
 * addToBack() preserves insertion order.
 *
 * Time complexity, where n is the number of entries:
 *   add              O(1)
 *   addToBack        O(1)
 *   removeFirst      O(1)
 *   remove(T)        O(n)
 *   cycleFrontToBack O(1)
 *   clear            O(1)
 *   getEntry         O(n)
 *   getHead/getTail  O(1)
 *   contains         O(n)
 *   count            O(n)
 *   size             O(1)
 *   isEmpty          O(1)
 *   toArray          O(n)
 *
 * @author CS 2114 Staff
 * @version September 2026
 *
 * @param <T> the type of entries stored in the chain
 */
import java.util.NoSuchElementException;
public class LinkedChain<T>
{
    private Node<T> head;   // reference to the first node, or null
    private Node<T> tail;
    private int numberOfEntries; // number of nodes in the chain

    /**
     * Creates an empty chain.
     */
    public LinkedChain()
    {
        head = null;
        tail = null;
        numberOfEntries = 0;
    }

    /**
     * Adds a new entry to the front of this chain.
     *
     * @param newEntry the object to be added; must not be null
     * @return true
     * @throws IllegalArgumentException if newEntry is null
     */
    public boolean add(T newEntry)
    {
        requireNonNull(newEntry);
        head = new Node<T>(newEntry, head);
        numberOfEntries++;
        if(numberOfEntries == 1) {
            tail = head;
        }
        return true;
    }

    /**
     * Adds a new entry to the end of this chain.
     *
     * @param newEntry the object to be added; must not be null
     * @return true
     * @throws IllegalArgumentException if newEntry is null
     */
    public boolean addToBack(T newEntry)
    {
        requireNonNull(newEntry);
        Node<T> newNode = new Node<T>(newEntry);
        if (isEmpty())
        {
            head = newNode;
        }
        else
        {
            tail.next = newNode;
        }
        tail = newNode;
        numberOfEntries++;
        return true;
    }

    /**
     * Removes and returns the first entry in this chain.
     *
     * @return the removed entry
     * @throws NoSuchElementException if the chain is empty
     */
    public T removeFirst()
    {
        if (numberOfEntries == 0)
        {
            throw new NoSuchElementException("Empty chain");
        }
        T result = head.data;
        head = head.next;
        if (head == null)
        {
            tail = null;
        }
        numberOfEntries--;
        return result;
    }

    /**
     * Removes the first occurrence of a given entry from this chain.
     *
     * @param anEntry the entry to be removed
     * @return true if the removal was successful, or false if not
     */
    public boolean remove(T anEntry)
    {
        if (anEntry == null || head == null)
        {
            return false;
        }
        if (anEntry.equals(head.data))
        {
            head = head.next;
            if (head == null)
            {
                tail = null;
            }
            numberOfEntries--;
            return true;
        }
        Node<T> previous = head;
        Node<T> current = head.next;
        while (current != null)
        {
            if (anEntry.equals(current.data))
            {
                if (current == tail)
                {
                    tail = previous;
                }
                previous.next = current.next;
                numberOfEntries--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    /**
     * Removes all entries from this chain.
     */
    public void clear()
    {
        head = null;
        tail = null;
        numberOfEntries = 0;
    }

    /**
     * Gets the entry at the given position without removing it.
     *
     * @param index the zero-based position
     * @return the entry at that position
     * @throws IndexOutOfBoundsException if index is negative or
     *         index is greater than or equal to getNumberOfEntries()
     */
    public T getEntry(int index)
    {
        if (index < 0 || index >= numberOfEntries)
        {
            throw new IndexOutOfBoundsException(
                "Index " + index + " out of bounds for size "
                + numberOfEntries);
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++)
        {
            current = current.next;
        }
        return current.data;
    }
    
    /**
     * Gets the head of the chain.
     * @return the head of the chain
     * @throws NoSuchElementException if chain is empty
     */
    public T getHead() {
        if(numberOfEntries == 0) {
            throw new NoSuchElementException("Empty chain");
        }
        return head.data;
    }
    
    /**
     * Gets the tail of the chain.
     * @return the tail of the chain
     * @throws NoSuchElementException if chain is empty
     */
    public T getTail() {
        if(numberOfEntries == 0) {
            throw new NoSuchElementException("Empty chain");
        }
        return tail.data;
    }

    /**
     * Tests whether this chain contains a given entry.
     *
     * @param anEntry the entry to locate
     * @return true if the chain contains anEntry, or false if not
     */
    public boolean contains(T anEntry)
    {
        return count(anEntry) > 0;
    }

    /**
     * Counts the number of times a given entry appears in this chain.
     *
     * @param anEntry the entry to be counted
     * @return the number of times anEntry appears in the chain
     */
    public int count(T anEntry)
    {
        if (anEntry == null)
        {
            return 0;
        }
        int frequency = 0;
        Node<T> current = head;
        while (current != null)
        {
            if (anEntry.equals(current.data))
            {
                frequency++;
            }
            current = current.next;
        }
        return frequency;
    }
    
    /**
     * Moves the first entry to the back of the chain. A chain with one
     * entry is left unchanged. O(1)
     *
     * @throws NoSuchElementException if the chain is empty
     */
    public void cycleFrontToBack() {
        if(numberOfEntries == 0) {
            throw new NoSuchElementException("Empty chain");
        }
        if(head == tail) {
            return;
        }
        Node<T> first = head;
        head = head.next;
        first.next = null;
        tail.next = first;
        tail = first;
    }

    /**
     * Gets the number of entries currently in this chain.
     *
     * @return the integer number of entries in the chain
     */
    public int getNumberOfEntries()
    {
        return numberOfEntries;
    }

    /**
     * Sees whether this chain is empty.
     *
     * @return true if the chain has no entries, or false if not
     */
    public boolean isEmpty()
    {
        return numberOfEntries == 0;
    }

    /**
     * Copies every entry in this chain into the given array, first entry
     * first.
     *
     * @param values an array of the correct type to fill; it must have
     *               length at least getNumberOfEntries()
     * @return the array containing all entries in the chain
     * @throws IllegalArgumentException if values is null or too small
     */
    public T[] toArray(T[] values)
    {
        if (values == null)
        {
            throw new IllegalArgumentException("Array must not be null");
        }
        if (values.length < numberOfEntries)
        {
            throw new IllegalArgumentException(
                "Array of length " + values.length
                + " is too small for " + numberOfEntries + " entries");
        }
        int index = 0;
        Node<T> current = head;
        while (current != null)
        {
            values[index] = current.data;
            index++;
            current = current.next;
        }
        return values;
    }

    /**
     * Returns a readable listing of the chain, first entry first,
     * e.g. "[a, b, c]".
     *
     * @return a string representation of this chain
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null)
        {
            sb.append(current.data);
            if (current.next != null)
            {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Rejects null entries.
     *
     * @param entry the entry to check
     * @throws IllegalArgumentException if entry is null
     */
    private void requireNonNull(T entry)
    {
        if (entry == null)
        {
            throw new IllegalArgumentException("Cannot add null to chain");
        }
    }

    /**
     * A node in the chain. Holds one entry and a link to the next node.
     *
     * @param <E> the type of data held in the node
     */
    private static class Node<E>
    {
        private E data;        // entry stored in this node
        private Node<E> next;  // link to the next node, or null

        /**
         * Creates a node with the given data and no next node.
         *
         * @param dataPortion the entry to store
         */
        private Node(E dataPortion)
        {
            this(dataPortion, null);
        }

        /**
         * Creates a node with the given data and next link.
         *
         * @param dataPortion the entry to store
         * @param nextNode the node that follows this one
         */
        private Node(E dataPortion, Node<E> nextNode)
        {
            data = dataPortion;
            next = nextNode;
        }
    }
}
