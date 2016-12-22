package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private final Comparator<E> comparator;
    private int size;
    private final Node nil = new Node(BLACK, null);
    private Node root;

    public RedBlackTree() {

        this.comparator = null;
        size=0;
        root=nil;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public String toString() {
        return "RBT{" + root + "}";
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
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
        if (curr == nil) {
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
        return root == null || root==nil;
    }

    @Override
    public boolean contains(E value) {

        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null && root!=nil) {
            Node curr = root;
            while (curr != null && curr!=nil) {
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
        if (contains(value)) return false;
        root = add(root, value);
        root.color = BLACK;
        size++;
        return true;
    }

    private Node add(Node node, E value) {
        if (node == nil || node==null) return new Node(RED,value);

        int cmp = value.compareTo(node.val);
        if (cmp < 0) node.left = add(node.left, value);
        else if (cmp > 0) node.right = add(node.right, value);

        if (isRed(node.right) && !isRed(node.left))  node=aleftRotate(node);
        if (isRed(node.left) && isRed(node.left.left)) node= arightRotate(node);
        if (isRed(node.left) && isRed(node.right)) flipColor(node);
        return node;
    }

    private Node aleftRotate(Node node) {
        Node x = node.right;
        node.right = x.left;
        x.left = node;
        x.color = node.color;
        node.color = RED;
        return x;
    }

    private Node arightRotate(Node node) {
        Node x = node.left;
        node.left = x.right;
        x.right = node;
        x.color = node.color;
        node.color = RED;
        return x;
    }

    @Override
    public boolean remove(E value) {
       if (value == null) {
            throw new NullPointerException("value is null");
        }

        Node current=root;
        while (current!=nil){
            int cmp=value.compareTo(current.val);
            if (cmp==0) {
                removeNode(current);
                size--;
                return true;
            }
            else current=(cmp<0)? current.left:current.right;
        }
        return false;
    }

    private void removeNode(Node z){
        Node x,y;
        if(z==null||z==nil) return;
        if (z.left==nil || z.right==nil) y=z;
        else{
            y=z.right;
            while (y.left!=nil) y=y.left;
        }

        if(y.left!=nil) x=y.left;
        else x=y.right;

        x.parent=y.parent;
        if(y.parent != null){
            if (y==y.parent.left) y.parent.left=x;
            else y.parent.right=x;
        }else root=x;

        if(y!=z) z.val=y.val;
        if(y.color==BLACK) removeFixUp(x);
    }

    private void removeFixUp(Node x){
        while (x!=root && x.color==BLACK){
            if (x==x.parent.left){
                Node z=x.parent.right;
                if(z.color==RED){
                    z.color=BLACK;
                    x.parent.color=RED;
                    leftRotate(x.parent);
                    z=x.parent.right;
                }
                if (z.left.color==BLACK&&z.right.color==BLACK){
                    z.color=RED;
                    x=x.parent;
                }else{
                    if (z.right.color==BLACK){
                        z.left.color=BLACK;
                        z.color=RED;
                        rightRotate(z);
                        z=x.parent.right;
                    }
                    z.color=x.parent.color;
                    x.parent.color=BLACK;
                    z.right.color=BLACK;
                    leftRotate(x.parent);
                    x=root;
                }
            }else{
                Node z=x.parent.left;
                if(z.color==RED){
                    z.color=BLACK;
                    x.parent.color=RED;
                    rightRotate(x.parent);
                    z=x.parent.left;
                }
                if(z.right.color==BLACK && z.left.color==BLACK){
                    z.color=RED;
                    x=x.parent;
                }else{
                    if(z.left.color==BLACK){
                        z.right.color=BLACK;
                        z.color=RED;
                        leftRotate(z);
                        z=x.parent.left;
                    }
                    z.color=x.parent.color;
                    x.parent.color=BLACK;
                    z.left.color=BLACK;
                    rightRotate(x.parent);
                    x=root;
                }
            }
        }
        x.color=BLACK;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }


    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != nil) y.left.parent = x;

        if (x.parent != null) {
            if (x == x.parent.left) x.parent.left = y;
            else x.parent.right = y;
        } else  root = y;
        y.left = x;
        if (x != nil) x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != nil) y.right.parent = x;

        if (y != nil) y.parent = x.parent;
        if (x.parent != null) {
            if (x == x.parent.right) x.parent.right = y;
            else x.parent.left = y;
        } else root = y;
        y.right = x;
        if (x != nil) x.parent = y;
    }

    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    private void flipColor(Node node) {
        node.color = RED;
        node.left.color = BLACK;
        node.right.color = BLACK;
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
