import java.util.ArrayList;

public class AVLTree {

    private class Node {
        // the working principle of the addNode, deleteNode and the send method are the same for the avl and the bst
        private String ip;
        private Node left;
        private Node right;
        private int height;

        public Node(String ip, Node left, Node right, int height) {
            this.ip = ip;
            this.left = left;
            this.right = right;
            this.height = height;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }


    private Node root;
    private ArrayList<String> log = new ArrayList<>();
    private static final int MAX_ALLOWED_IMBALANCE = 1; // maximum allowed imbalance of the subtrees of a node

    public AVLTree(String rootIp) {
        root = new Node(rootIp, null, null, 0);
    }

    public ArrayList<String> getLog() {
        return log;
    }
    public int height(Node currentNode) { // height outside node class to avoid nullPointerException
        if (currentNode == null)
            return -1;
        return currentNode.getHeight();
    }

    public Node findMin() {
        return findMin(root);
    }
    private Node findMin(Node root) {
        if (root == null)
            return null;
        else if (root.getLeft() == null)
            return root;
        return findMin(root.left);
    }

    public Node findMax() {
        return findMax(root);
    }
    private Node findMax(Node root) {
        if (root == null)
            return null;
        else if (root.getRight() == null)
            return root;
        return findMax(root.getRight());
    }

    public Node addNode(String ip) {
        return addNode(ip, root);
    }

    private Node addNode(String ip, Node root) {
        if (root == null)
            return new Node(ip, null, null, 0) ;

        int compareResult = ip.compareTo(root.getIp());
        log.add(addNodeLog(ip, root));
        if (compareResult < 0) {
            root.setLeft(addNode(ip, root.getLeft()));

        }
        else if (compareResult > 0) {
            root.setRight(addNode(ip, root.getRight()));
        }
        else
            ;
        return balance(root); // instead of directly returning the node, we are checking the balance of the node
        // and correcting the tree with rotations if necessary
    }
    private String addNodeLog(String ip, Node currentNode) {
        if (currentNode != null) {
            return currentNode.getIp() + ": New node being added with IP:" + ip;
        }
        return "";
    }

    public Node removeNode(String ip) {
        return removeNode(ip, root, null, true);
    }
    // uses a boolean for logging for the case with removing a node with two children
    // while replacing the node with two children with the smallest node from the right subtree
    // we remove the smallest node, but it shouldn't be logged as an extra operation => logging = false
    // only logs when logging is true
    private Node removeNode(String ip, Node root, Node parent, boolean logging) {
        if (root == null)
            return root;

        int compareResult = ip.compareTo(root.getIp());

        if (compareResult < 0)
            root.setLeft(removeNode(ip, root.getLeft(), root, logging));
        else if (compareResult > 0)
            root.setRight(removeNode(ip, root.getRight(), root, logging));
        else if (root.getLeft() != null && root.getRight() != null) {
            root.setIp(findMin(root.getRight()).getIp());
            if(logging)
                log.add(removeNonLeafLog(ip, root.getIp(), parent.getIp()));
            root.setRight(removeNode(root.getIp(), root.getRight(), root, false)); // set logging to false while replacing
        }
        else if (root.getLeft() != null || root.getRight() != null) {
            if (logging)
                log.add(removeSingleChildLog(ip, parent.getIp()));
            root = (root.getLeft() != null) ? root.getLeft() : root.getRight();
        }
        else {
            if(logging)
                log.add(removeLeafLog(ip, parent.getIp()));
            root = null;
        }
        return balance(root); // instead of directly returning the node, we are checking the balance of the node
        // and correcting the tree with rotations if necessary

    }
    private Node balance(Node currentNode) { // a method for rotating the tree if necessary
        if (currentNode == null)
            return currentNode;
        if (height(currentNode.getLeft()) - height(currentNode.getRight()) > MAX_ALLOWED_IMBALANCE) { // left rotation
            if (height(currentNode.getLeft().getLeft()) > height(currentNode.getLeft().getRight())) {// left-left
                log.add("Rebalancing: right rotation");
                return leftSingleRotation(currentNode);
            }
            else { // left-right
                log.add("Rebalancing: left-right rotation");
                return leftDoubleRotation(currentNode);
            }
        }
        else if (height(currentNode.getRight()) - height(currentNode.getLeft()) > MAX_ALLOWED_IMBALANCE) { // right rotation
            if (height(currentNode.getRight().getRight()) > height(currentNode.getRight().getLeft())) {// right-right
                log.add("Rebalancing: left rotation");
                return rightSingleRotation(currentNode);
            }
            else {
                log.add("Rebalancing: right-left rotation"); // right-left
                return rightDoubleRotation(currentNode);
            }
        }
        // adjust the height of the node
        currentNode.setHeight(1 + Math.max(height(currentNode.getLeft()), height(currentNode.getRight())));
        return currentNode;
    }

    private Node leftSingleRotation(Node currentNode) { // single rotation with the left child
        Node leftChild = currentNode.getLeft();
        currentNode.setLeft(leftChild.getRight());
        leftChild.setRight(currentNode);
        currentNode.setHeight((1 + Math.max(height(currentNode.getLeft()), height(currentNode.getRight()))));
        leftChild.setHeight(1 + Math.max(height(leftChild.getLeft()), height(leftChild.getRight())));
        return leftChild;
    }
    private Node rightSingleRotation(Node currentNode) { // single rotation with the right child
        Node rightChild = currentNode.getRight();
        currentNode.setRight(rightChild.getLeft());
        rightChild.setLeft(currentNode);
        currentNode.setHeight((1 + Math.max(height(currentNode.getLeft()), height(currentNode.getRight()))));
        rightChild.setHeight(1 + Math.max(height(rightChild.getLeft()), height(rightChild.getRight())));
        return rightChild;
    }
    private Node leftDoubleRotation(Node currentNode) { // double rotation with the left child
        currentNode.setLeft(rightSingleRotation(currentNode.getLeft()));
        return leftSingleRotation(currentNode);
    }
    private Node rightDoubleRotation(Node currentNode) { // double rotation with the right child
        currentNode.setRight(leftSingleRotation(currentNode.getRight()));
        return rightSingleRotation(currentNode);
    }

    private String removeLeafLog(String ip, String parentIp) {
        return parentIp + ": Leaf Node Deleted: " + ip;
    }

    private String removeNonLeafLog(String removedIp, String replacedIp, String parentIp) {
        return parentIp + ": Non Leaf Node Deleted; removed: " + removedIp + " replaced: " + replacedIp;
    }
    private String removeSingleChildLog(String removedIp, String parentIp) {
        return parentIp + ": Node with single child Deleted: " + removedIp;
    }


    public void send(String senderIp, String receiverIp) {
        Node sendRoot = findSendRoot(senderIp, receiverIp, root);
        if (sendRoot.getIp().equals(senderIp)) {
            log.add(senderLog(senderIp, receiverIp));
            sendMessageSender(senderIp, receiverIp, sendRoot);
        }
        else if (sendRoot.getIp().equals(receiverIp)) {
            sendMessageReceiver(senderIp, receiverIp, sendRoot);
            log.remove(log.size() - 1);
            log.add(receiverLog(senderIp, receiverIp));
        }
        else {
            sendMessage(senderIp, receiverIp, sendRoot);
        }

    }
    private void sendMessageSender(String senderIp, String receiverIp, Node currentNode) {
        //log.add(senderLog(senderIp, receiverIp));
        Node previousNode;
        while (!currentNode.getIp().equals(receiverIp)) {
            int compareResult = receiverIp.compareTo(currentNode.getIp());
            previousNode = currentNode;
            currentNode = (compareResult < 0) ? currentNode.getLeft() : currentNode.getRight();
            log.add(transmitterLog(senderIp, receiverIp, previousNode.getIp(),  currentNode.getIp()));
        }
        log.remove(log.size() - 1);
        log.add(receiverLog(senderIp, receiverIp));
    }
    private void sendMessageReceiver(String senderIp, String receiverIp, Node currentNode) {
        if (currentNode == null)
            return;

        int compareResult = senderIp.compareTo(currentNode.getIp());

        if (compareResult < 0) {
            sendMessageReceiver(senderIp, receiverIp, currentNode.getLeft());
            log.add(transmitterLog(senderIp, receiverIp, currentNode.getLeft().getIp(), currentNode.getIp()));
        }
        else if (compareResult > 0) {
            sendMessageReceiver(senderIp, receiverIp, currentNode.getRight());
            log.add(transmitterLog(senderIp, receiverIp, currentNode.getRight().getIp(), currentNode.getIp()));
        }
        else {
            log.add(senderLog(senderIp, receiverIp));
            return;
        }

    }
    private Node findSendRoot(String senderIp, String receiverIp, Node currentNode) {
        while(currentNode != null) {
            int compareSender = currentNode.getIp().compareTo(senderIp);
            int compareReceiver = currentNode.getIp().compareTo(receiverIp);

            if (compareSender < 0 && compareReceiver < 0) {
                currentNode = currentNode.getLeft();
                continue;
            }
            else if (compareSender > 0 && compareReceiver > 0) {
                currentNode = currentNode.getRight();
                continue;
            }
            else if ((compareSender <= 0 && compareReceiver >= 0) || (compareSender >= 0 && compareReceiver <= 0)) {
                return currentNode;
            }
        }
        return null;
    }
    private void sendMessage(String senderIp, String receiverIp, Node currentNode) {
        sendMessageReceiver(senderIp, receiverIp, currentNode);
        log.remove(log.size() - 1);
        //log.add(receiverLog(senderIp, receiverIp));

        log.add((receiverIp.compareTo(currentNode.getIp()) < 0) ?
                transmitterLog(senderIp, receiverIp, currentNode.getRight().getIp(), currentNode.getIp()) :
                transmitterLog(senderIp, receiverIp, currentNode.getLeft().getIp(), currentNode.getIp()));
        sendMessageSender(senderIp, receiverIp,currentNode);

    }

    private String senderLog(String senderIp, String receiverIp) {
        return senderIp + ": Sending message to: " + receiverIp;
    }
    private String receiverLog(String senderIp, String receiverIp) {
        return receiverIp + ": Received message from: " + senderIp;
    }
    private String transmitterLog(String senderIp, String receiverIp, String transmitterIp, String loggingIp) {
        return loggingIp + ": Transmission from: " + transmitterIp + " receiver: " + receiverIp + " sender:" + senderIp;
    }

}
