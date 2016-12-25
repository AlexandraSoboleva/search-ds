package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private final Comparator<E> comparator;
    private final Node nil = new Node(null);
    private int size;
    private Node root;

    public RedBlackTree() {

        this.comparator = null;
        size = 0;
        root = null;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new RedBlackTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new RedBlackTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });

        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }

    @Override
    public String toString() {
        return "RBT{" + root + "}";
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != nil) {
            curr = curr.left;
        }
        return curr.val;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != nil) {
            curr = curr.right;
        }
        return curr.val;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null || curr == nil) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.val);
        inorderTraverse(curr.right, list);

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {

        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != nil) {
                int cmp = compare(curr.val, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }

        Node z = new Node(RED, value);

        if (root == null) {
            root = z;
            root.color = BLACK;
            root.parent = nil;
            size++;
            return true;
        }

        Node y = nil;
        Node x = root;
        while (x != nil) {
            y = x;
            int cmp = compare(z.val, x.val);
            if (cmp == 0) return false;
            else if (cmp < 0) x = x.left;
            else x = x.right;
        }
        z.parent = y;
        if (y == nil) root = z;
        else {
            int cmp = compare(z.val, y.val);
            if (cmp == 0) return false;
            else if (cmp < 0) y.left = z;
            else y.right = z;
        }
        z.left = nil;
        z.right = nil;
        addFixUp(z);

        size++;
        return true;
    }

    private void addFixUp(Node z) {
        Node y;
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                y = z.parent.parent.right;
                if (y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                y = z.parent.parent.left;
                if (y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }

            }
        }
        root.color = BLACK;
        root.parent = nil;
    }


    @Override
    public boolean remove(E value) {
        return false;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }


    private void leftRotate(Node x) {
        if (x.right != nil) {
            Node y = x.right;
            x.right = y.left;
            if (y.left != nil) y.left.parent = x;
            y.parent = x.parent;

            if (x.parent == nil) root = y;
            else {
                if (x == x.parent.left) x.parent.left = y;
                else x.parent.right = y;
            }
            y.left = x;
            x.parent = y;
        }
    }

    private void rightRotate(Node x) {
        if (x.left != nil) {
            Node y = x.left;
            x.left = y.right;
            if (y.right != nil) y.right.parent = x;

            y.parent = x.parent;

            if (x.parent == nil) root = y;
            else {
                if (x == x.parent.right) x.parent.right = y;
                else x.parent.left = y;
            }
            y.right = x;
            x.parent = y;
        }
    }


    private class Node {
        Node left, right, parent;
        boolean color;
        E val;

        public Node(boolean color, E val) {
            this.color = color;
            this.val = val;
            left = nil;
            right = nil;
            parent = null;
        }

        public Node(E val) {
            left = null;
            right = null;
            parent = null;
            color = BLACK;
            this.val = val;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(val);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }
}
