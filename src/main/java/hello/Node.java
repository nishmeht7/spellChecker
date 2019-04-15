package hello;

public class Node {
    String prefix;
    Node children[];
    boolean isValid = false;

    public Node(String prefix, boolean isValid) {
        this.prefix = prefix;
        this.children = new Node[26];
        this.isValid = isValid;
    }

}
