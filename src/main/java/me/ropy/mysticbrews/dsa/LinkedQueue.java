package me.ropy.mysticbrews.dsa;

public class LinkedQueue<T> {

    private Node<T> head;
    private Node<T> tail;
    private int length;

    public LinkedQueue() {
        this.head = null;
        this.length = 0;
    }

    // ----- enqueue -----
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            this.head = newNode;
            this.tail = newNode;
            this.length += 1;
        } else {
            this.tail.next = newNode;
            this.tail = newNode;
            this.length += 1;
        }
    }

    // ---- dequeue ----
    public T dequeue() {
        if (this.head == null) {
            this.tail = null;
            return null;
        }

        T returnData = this.head.data;
        this.head = this.head.next;
        this.length--;
        return returnData;
    }

    // ---- SEARCH ----

    public boolean search(T target) {
        if (this.head == null) return false;
        Node<T> curr = this.head;
        while (curr != null) {
            if (curr.data.equals(target)) return true;
            curr = curr.next;
        }
        return false;
    }

    public void remove(T target) {
        if (this.head == null) return;
        if (this.head.data == target){
            this.head = this.head.next;
            return;
        }
        Node<T> prev = this.head;
        Node<T> curr = this.head.next;
        while (curr != null) {
            if (curr.data.equals(target)) {
                prev.next = curr.next;
                return;
            }
            prev = curr;
            curr = curr.next;
        }
    }

    // ---- UTILS ----
    public boolean isEmpty() {
        return this.head == null;
    }

    public int getLength() {
        return this.length;
    }

    private static class Node<T> {
        public T data;
        public Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
}
