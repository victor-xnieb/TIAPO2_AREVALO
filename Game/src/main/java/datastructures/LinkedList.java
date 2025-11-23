package datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;


public class LinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T v) { value = v; }
    }

    private Node<T> head;
    private int size;

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = node;
        } else {
            Node<T> cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = node;
        }
        size++;
    }

    public boolean remove(T value) {
        if (head == null) return false;
        if (head.value.equals(value)) {
            head = head.next;
            size--;
            return true;
        }
        Node<T> cur = head;
        while (cur.next != null && !cur.next.value.equals(value)) {
            cur = cur.next;
        }
        if (cur.next != null) {
            cur.next = cur.next.next;
            size--;
            return true;
        }
        return false;
    }

    public boolean isEmpty() { return size == 0; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> cur = head;
            @Override
            public boolean hasNext() { return cur != null; }
            @Override
            public T next() {
                if (cur == null) throw new NoSuchElementException();
                T v = cur.value;
                cur = cur.next;
                return v;
            }
        };
    }

    public void removeIf(Predicate<T> predicate) {
        // eliminar del inicio mientras cumpla el predicado
        while (head != null && predicate.test(head.value)) {
            head = head.next;
            size--;
        }
        // eliminar en el resto
        Node<T> cur = head;
        while (cur != null && cur.next != null) {
            if (predicate.test(cur.next.value)) {
                cur.next = cur.next.next;
                size--;
            } else {
                cur = cur.next;
            }
        }
    }

    public void clear() {
        head = null;
        size = 0;
    }

}
