package hello;

import java.io.*;

/** CompactPrefixTree class, implements Dictionary ADT and
 *  several additional methods. Can be used as a spell checker.
 *  Fill in code and feel free to add additional methods as needed.
 *  S19 */
public class CompactPrefixTree implements Dictionary {

    private Node root; // the root of the tree
    private int numSuggestions;
    private String[] suggestions;
    private String prefix = "";
    private int curr = 0;
    private int indent = -2;

    /** Default constructor.
     * Creates an empty "dictionary" (compact prefix tree).
     * */
    public CompactPrefixTree(){
        root = new Node();
    }

    /**
     * Creates a dictionary ("compact prefix tree")
     * using words from the given file.
     * @param filename the name of the file with words
     */
    public CompactPrefixTree(String filename) {
        // FILL IN CODE:
        // Read each word from the file, add it to the tree
        root = new Node();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while((line = br.readLine()) != null) {
                add(line);
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    /** Adds a given word to the dictionary.
     * @param word the word to add to the dictionary
     */
    public void add(String word) {
        root = add(word.toLowerCase(), root); // Calling private add method
    }

    /**
     * Checks if a given word is in the dictionary
     * @param word the word to check
     * @return true if the word is in the dictionary, false otherwise
     */
    public boolean check(String word) {
        return check(word.toLowerCase(), root); // Calling private check method
    }

    /**
     * Checks if a given prefix is stored in the dictionary
     * @param prefix The prefix of a word
     * @return true if this prefix is a prefix of any word in the dictionary,
     * and false otherwise
     */
    public boolean checkPrefix(String prefix) {
        return checkPrefix(prefix.toLowerCase(), root); // Calling private checkPrefix method
    }


    /**
     * Prints all the words in the dictionary, in alphabetical order,
     * one word per line.
     */
    public void print() {
        print("", root); // Calling private print method
    }

    /**
     * Print out the nodes of the compact prefix tree, in a pre-order fashion.
     * First, print out the root at the current indentation level
     * (followed by * if the node's valid bit is set to true),
     * then print out the children of the node at a higher indentation level.
     */
    public void printTree(Node curr) {
        // FILL IN CODE
        indent++;
        String tab = "";
        for(int i = 0; i < indent; i++) {
            tab+="\t";
        }
        System.out.print(tab);
        System.out.print(curr.prefix);
        if(curr.isWord) {
            System.out.println("*");
        }
        else {
            System.out.println("");
        }
        for(int i = 0; i < 26; i++) {
            Node child = curr.getChild(i);
            if(child != null) {
                printTree(child);
            }
        }
        indent--;
    }

    /**
     * Print out the nodes of the tree to a file, using indentations to specify the level
     * of the node.
     * @param filename the name of the file where to output the tree
     */
    public void printTree(String filename) {
        System.out.println("inside print tree");
        System.out.println("the filename is: " + filename);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            printTreeHelper(bw, root);
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    /**
     * helper method used by printTree to file
     * @param bw BufferedWriter object
     * @param curr current node in traversal
     */
    private void printTreeHelper(BufferedWriter bw, Node curr) {
        try {
            indent++;
            String tab = " ";
            for(int i = 0; i < indent; i++) {
                tab+=" ";
            }
            bw.write(tab);
            bw.write(curr.prefix);
            if(curr.isWord) {
                bw.write("*");
            }
            bw.newLine();
            for(int i = 0; i < 26; i++) {
                Node child = curr.getChild(i);
                if(child != null) {
                    printTreeHelper(bw, child);
                }
            }
            indent--;
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }


    /**
     * Return an array of the entries in the dictionary that are as close as possible to
     * the parameter word.  If the word passed in is in the dictionary, then
     * return an array of length 1 that contains only that word.  If the word is
     * not in the dictionary, then return an array of numSuggestions different words
     * that are in the dictionary, that are as close as possible to the target word.
     * Implementation details are up to you, but you are required to make it efficient
     * and make good use ot the compact prefix tree.
     *
     * @param word The word to check
     * @param numSuggestions The length of the array to return.  Note that if the word is
     * in the dictionary, this parameter will be ignored, and the array will contain a
     * single world.
     * @return An array of the closest entries in the dictionary to the target word
     */
    public String[] suggest(String word, int numSuggestions) {
        boolean bool = check(word, root);

        if(bool) {
            this.suggestions = new String[1];
            this.suggestions[0] = word;
            return suggestions;
        }

        this.numSuggestions = numSuggestions;
        this.suggestions = new String[numSuggestions];
        curr = 0;
        prefix = "";
        suggest(word, root);

        return suggestions;
    }

    /**
     * recursive suggest function that finds the best suggestions for the given word
     * @param word the word to find the best suggestions for. This word keeps reducing in
     *             size as more common prefixes are found, the prefix is removed from the word
     * @param node the current node at to find the suggestions from
     */
    public void suggest(String word, Node node) {
        int idx;
        if(node == root) {
            idx = getIndex(word);
            if(node.getChild(idx) == null) {
                getLCSNode(node, "");
                return;
            }
            suggest(word, node.getChild(idx));
            return;
        }

        if(node == null) {
            getLCSNode(root, prefix);
            return;
        }

        String lcs = getLCS(word, node.prefix);
        int lcsLen = lcs.length();
        if(!lcs.equals("") && (lcsLen == node.prefix.length())){
            this.prefix += node.prefix;
            if(lcsLen != word.length()) {
                word = word.substring(lcsLen);
                idx = getIndex(word);
                Node child = node.getChild(idx);
                suggest(word, child);
                return;
            }
        }
            getLCSNode(root, prefix);
    }

    /**
     * Retreives the node at which the largest common string ends
     * So the node after which there are no more common prefixes between the given string
     * @param curr the current node to start the traversal from
     * @param s the string to compare with and find the largest common string prefix
     * @return the node after which there are no more common prefixes compared to the string s
     */
    private Node getLCSNode(Node curr, String s) {
        int idx = getIndex(s);
        Node child;
        if(curr == root) {
            child = root.getChild(idx);
            getLCSNode(child, s);
        }

        String lcs = getLCS(s, curr.prefix);
        //if lcs is same as prefix
        if(lcs.equals(curr.prefix)) {
            s = s.substring(lcs.length());
            if(s.length() > 0) {
                idx = getIndex(s);
                Node innerChild = curr.getChild(idx);
                if(innerChild == null) {
                    return curr;
                }
                else {
                    return getLCSNode(innerChild, s);
                }
            }
        }
        if(s.length() == 0) {
            getKids(prefix, curr);
        }
        return curr;
    }

    /**
     * Once we have the last node of the largest common string
     * We pass it to this function and retrieve all of its kids
     * @param s the prefix of the node that is added to the children
     * @param node the node to begin from
     * @return array of suggestions
     */
    private String[] getKids(String s, Node node) {
        for(int i = 0; i < 26; i++) {
            Node child = node.getChild(i);
            if(child != null) {
                if(child.isWord) {
                    if(curr == numSuggestions) return suggestions;
                    if((s+child.prefix).equals(suggestions[0])) {
                        prefix = "";
                        suggest(s.substring(0, s.length() - 1), root);
                        break;
                    }
                    suggestions[curr] = s+child.prefix;
                    curr++;
                }
                getKids(s+child.prefix, child);
            }
        }
        return suggestions;
    }

    /**
     * Gets the largest common string prefix between two strings
     * @param s1 String one
     * @param s2 String two
     * @return the largest common prefix string
     */
    private String getLCS(String s1, String s2) {
        String subStr = "";
        int len = Math.min(s1.length(), s2.length());

        for(int i = 0; i < len; i++) {
            char curr = s1.charAt(i);
            if(curr == s2.charAt(i)) {
                subStr += curr;
            }
            else {
                return subStr;
            }
        }
        return subStr;
    }

    /**
     * Gets the suffix of a string
     * @param s1 the given string
     * @param prefix the prefix
     * @return the remaining string without the prefix
     */
    private String getSuffix(String s1, String prefix) {
        return s1.substring(prefix.length());
    }

    // ---------- Private helper methods ---------------

    /**
     * Gets the index of a given string based on it's first letter
     * @param s the string
     * @return the corresponding index of the string
     */
    private int getIndex(String s) {
        char c = s.charAt(0);
        return (int)c - (int)'a';
    }

    /**
     * Adds a string to the given node
     * @param s The string to add
     * @param node the node the string will be a child off
     */
    private void setChild(String s, Node node) {
        int cIdx = getIndex(s);
        Node returnedNode = add(s, node.getChild(cIdx));
        node.setChild(cIdx, returnedNode);
    }

    /**
     *  A private add method that adds a given string to the tree
     * @param s the string to add
     * @param node the root of a tree where we want to add a new string

     * @return a reference to the root of the tree that contains s
     */
    private Node add(String s, Node node) {
        // FILL IN CODE

        //if node is root
        if(node == root) {
            node.setPrefix("");
            node.setIsWord(false);
//            System.out.println("root node is: " + node.prefix);
            setChild(s, node);
            return node;
        }

        Node newNode = new Node();
        // if node is null
        if(node == null) {
            newNode.setIsWord(true);
            newNode.setPrefix(s);
            return newNode;
        }

        // if the prefix is the same as current node value
        if(s.equals(node.getPrefix())) {
            if(!node.getIsWord()) {
                node.setIsWord(true);
            }
            return node;
        }

        else {
            String prefix = getLCS(s, node.prefix);
            String suffix = getSuffix(node.prefix, prefix);
            String suffixWord = getSuffix(s, prefix);
            int newIndex = getIndex(suffixWord);
            if(suffix.equals("")) {
                Node returnedNode = add(suffixWord, node.getChild(newIndex));
                node.setChild(newIndex, returnedNode);
                return node;
            }

            newNode.setPrefix(prefix);
            newNode.setIsWord(false);

            node.setPrefix(suffix);
            int index = getIndex(suffix);
            newNode.setChild(index, node);

            Node returnedNode = add(suffixWord, newNode.getChild(newIndex));
            newNode.setChild(newIndex, returnedNode);
            return newNode;
        }
    }


    /** A private method to check whether a given string is stored in the tree.
     *
     * @param s the string to check
     * @param node the root of a tree
     * @return true if the prefix is in the dictionary, false otherwise
     */
    private boolean check(String s, Node node) {
        // FILL IN CODE
        int index;
        //if node is root
        if(node == null) {
            return false;
        }

        if(node.prefix.equals("")) {
            if(s.equals("")) return true;
            index = getIndex(s);
            return check(s, node.getChild(index));
        }

        if(s.equals(node.prefix)) return node.isWord;

        String lcs = getLCS(s, node.prefix);
        if(lcs.equals("")) return false;
        else {
            if(node.prefix.length() > s.length()) return false;
            String suffix = s.substring(lcs.length());
            if(suffix.equals("")) {
                return node.isWord;
            }
            index = getIndex(suffix);
            return check(suffix, node.getChild(index));
        }
    }

    /**
     * A private recursive method to check whether a given prefix is in the tree
     *
     * @param prefix the prefix
     * @param node the root of the tree
     * @return true if the prefix is in the dictionary, false otherwise
     */
    private boolean checkPrefix(String prefix, Node node) {
        if(prefix.equals("")) return true;
        int idx;
        if(node == root) {
            idx = getIndex(prefix);
            Node child = node.getChild(idx);
            return checkPrefix(prefix, child);
        }

        if(node == null) return false;

        String lcs = getLCS(prefix, node.prefix);

        if(lcs.equals(prefix)) return true;

        if(!lcs.equals("") && lcs.equals(node.prefix)) {
            prefix = prefix.substring(lcs.length());
            idx = getIndex(prefix);
            return checkPrefix(prefix, node.getChild(idx));
        }

        return false;
    }

    /**
     * Outputs all the words stored in the dictionary
     * to the console, in alphabetical order, one word per line.
     * @param s the string obtained by concatenating prefixes on the way to this node
     * @param node the root of the tree
     */
    private void print(String s, Node node) {
        for(int i = 0; i < 26; i++) {
            Node child = node.getChild(i);
            if(child != null) {
                if(child.isWord) {
                    System.out.println(s + child.prefix);
                }
                print(s+child.prefix, child);
            }
        }
    }

    // FILL IN CODE: add a private suggest method. Decide which parameters
    // it should have

    // --------- Private class Node ------------
    // Represents a node in a compact prefix tree
    private class Node {
        String prefix; // prefix stored in the node
        Node children[]; // array of children (26 children)
        boolean isWord; // true if by concatenating all prefixes on the path from the root to this node, we get a valid word

        Node() {
            isWord = false;
            prefix = "";
            children = new Node[26]; // initialize the array of children
        }

        /**
         * sets node prefix value
         * @param s prefix string
         */
        public void setPrefix(String s) {
            prefix = s;
        }

        /**
         * sets is word value
         * @param val boolean
         */
        public void setIsWord(boolean val) {
//            System.out.println("setting is word for: " + prefix + " val is: " + val);

            isWord = val;
//            System.out.println("new set is word: " + isWord);
        }

        /**
         * getter for node prefix
         * @return the prefix
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * getter for isWord
         * @return boolean value isWord
         */
        public boolean getIsWord() {
            return isWord;
        }

        /**
         * Sets the child of a given node to the given node
         * @param index the index of the child in the children array
         * @param newNode the new node to be set as the current nodes child
         */
        public void setChild(int index, Node newNode) {
            children[index] = newNode;
        }

        /**
         * Gets a particular child of the given node
         * @param index the index of the child to get
         * @return the child at the given index
         */
        public Node getChild(int index) {
            return children[index];
        }

        // FILL IN CODE: Add other methods to class Node as needed
    }

}
