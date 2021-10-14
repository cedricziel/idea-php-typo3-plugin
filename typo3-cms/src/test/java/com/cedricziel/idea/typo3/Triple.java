package com.cedricziel.idea.typo3;

public class Triple<T, U, V> {
    private final T left;
    private final U middle;
    private final V right;

    public Triple(T left, U middle, V right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public U getMiddle() {
        return middle;
    }

    public V getRight() {
        return right;
    }
}
