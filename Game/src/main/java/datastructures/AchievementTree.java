package datastructures;

import java.util.function.Consumer;

public class AchievementTree {

    public static class Achievement implements Comparable<Achievement> {
        private final String name;
        private final String description;
        private final int points;

        public Achievement(String name, String description, int points) {
            this.name = name;
            this.description = description;
            this.points = points;
        }

        // === GETTERS NUEVOS ===
        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getPoints() {
            return points;
        }

        @Override
        public int compareTo(Achievement o) {
            return Integer.compare(this.points, o.points);
        }

        @Override
        public String toString() {
            return name + " (" + points + " pts): " + description;
        }
    }

    private static class Node {
        Achievement value;
        Node left, right;
        Node(Achievement v) { value = v; }
    }

    private Node root;

    public void insert(Achievement a) {
        root = insertRec(root, a);
    }

    private Node insertRec(Node n, Achievement a) {
        if (n == null) return new Node(a);
        int cmp = a.compareTo(n.value);
        if (cmp < 0) n.left = insertRec(n.left, a);
        else if (cmp > 0) n.right = insertRec(n.right, a);
        return n;
    }

    // === lo que ya tenías ===
    public String inOrderString() {
        StringBuilder sb = new StringBuilder();
        traverseInOrder(root, sb);
        return sb.toString();
    }

    private void traverseInOrder(Node n, StringBuilder sb) {
        if (n == null) return;
        traverseInOrder(n.left, sb);
        sb.append(n.value).append("\n");
        traverseInOrder(n.right, sb);
    }


    public void forEachInOrder(Consumer<Achievement> consumer) {
        traverseInOrder(root, consumer);
    }

    private void traverseInOrder(Node n, Consumer<Achievement> consumer) {
        if (n == null) return;
        traverseInOrder(n.left, consumer);
        consumer.accept(n.value);
        traverseInOrder(n.right, consumer);
    }

    public Achievement findByName(String name) {
        return findByNameRec(root, name);
    }

    private Achievement findByNameRec(Node node, String name) {
        if (node == null) {
            return null;
        }

        // Coincide este nodo
        if (node.value.name.equalsIgnoreCase(name)) {
            return node.value;
        }

        // Buscamos primero en la izquierda
        Achievement left = findByNameRec(node.left, name);
        if (left != null) {
            return left;
        }

        // Si no estaba a la izquierda, buscamos en la derecha
        return findByNameRec(node.right, name);
    }


    public boolean isEmpty() {
        return root == null;
    }

}
