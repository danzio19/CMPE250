import java.util.ArrayList;

public class BinarySearchTree {

    private class Node { // nested node class to use inside bst

        private String ip;
        private Node left;
        private Node right;


        public Node(String ip, Node left, Node right) {
            this.ip = ip;
            this.left = left;
            this.right = right;
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
    }


    private Node root;
    private ArrayList<String> log = new ArrayList<>(); // all logging operations is stored in an arraylist

    public BinarySearchTree(String rootIp) {
        root = new Node(rootIp, null, null);
    }

    public ArrayList<String> getLog() {
        return log;
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

    public Node addNode(String ip) { // method for the user
        return addNode(ip, root);
    }

    private Node addNode(String ip, Node root) { // goes down recursively in the tree and adds the node
        if (root == null)
            return new Node(ip, null, null) ;

        int compareResult = ip.compareTo(root.getIp()); // each node's value is compared
        log.add(addNodeLog(ip, root));
        if (compareResult < 0) { // ip is smaller, move to the left subtree
            root.setLeft(addNode(ip, root.getLeft()));
        }
        else if (compareResult > 0) { // ip is bigger, move to the right subtree
            root.setRight(addNode(ip, root.getRight()));
        }
        else // duplicate, do nothing
            ;
        return root; // return root for backtracking
    }
    private String addNodeLog(String ip, Node currentNode) { // logging method for adding nodes
        if (currentNode != null) {
            return currentNode.getIp() + ": New node being added with IP:" + ip;
        }
        return "";
    }

    public Node removeNode(String ip) { // method for the user
        return removeNode(ip, root, null, true);
    }
    // goes down recursively until the node to be deleted is found
    // uses a boolean for logging for the case with removing a node with two children
    // while replacing the node with two children with the smallest node from the right subtree
    // we remove the smallest node, but it shouldn't be logged as an extra operation => logging = false
    // only logs when logging is true
    private Node removeNode(String ip, Node root, Node parent, boolean logging) {
        if (root == null) // not found
            return root;

        int compareResult = ip.compareTo(root.getIp()); // compare currentNode ip

        if (compareResult < 0) // move to the left subtree
            root.setLeft(removeNode(ip, root.getLeft(), root, logging));
        else if (compareResult > 0) // move to the right subtree
            root.setRight(removeNode(ip, root.getRight(), root, logging));
        else if (root.getLeft() != null && root.getRight() != null) { // node with two children to be deleted
            root.setIp(findMin(root.getRight()).getIp());
            if(logging)
                log.add(removeNonLeafLog(ip, root.getIp(), parent.getIp()));
            root.setRight(removeNode(root.getIp(), root.getRight(), root, false));
        }
        else if (root.getLeft() != null || root.getRight() != null) { // node with single child
            if (logging)
                log.add(removeSingleChildLog(ip, parent.getIp()));
            root = (root.getLeft() != null) ? root.getLeft() : root.getRight();
        }
        else { // leaf node
            if(logging)
                log.add(removeLeafLog(ip, parent.getIp()));
            root = null;
        }
        return root;

    }

    private String removeLeafLog(String ip, String parentIp) { // logging methods
        return parentIp + ": Leaf Node Deleted: " + ip;
    }

    private String removeNonLeafLog(String removedIp, String replacedIp, String parentIp) {
        return parentIp + ": Non Leaf Node Deleted; removed: " + removedIp + " replaced: " + replacedIp;
    }
    private String removeSingleChildLog(String removedIp, String parentIp) {
        return parentIp + ": Node with single child Deleted: " + removedIp;
    }
    // for send method there are 3 possible cases
    // 1- the sender is one of the ancestors of the receiver - there is a straight path
    // 2- the receiver is one of the ancestors of the sender - there is a straight path
    // 3- the sender and the receiver share a common ancestor - the path changes direction in the common ancestor

    public void send(String senderIp, String receiverIp) { // general method for all 3 cases
        Node sendRoot = findSendRoot(senderIp, receiverIp, root);
        if (sendRoot.getIp().equals(senderIp)) { // case 1
            log.add(senderLog(senderIp, receiverIp)); // sending log
            sendMessageSender(senderIp, receiverIp, sendRoot); // sending message down to the receiver
        }
        else if (sendRoot.getIp().equals(receiverIp)) { // case 2
            sendMessageReceiver(senderIp, receiverIp, sendRoot); // sending message up to the receiver
            log.remove(log.size() - 1); // remove last log(transmitter log), since it should be the receiver log
            log.add(receiverLog(senderIp, receiverIp));
        }
        else { // case 3
            sendMessage(senderIp, receiverIp, sendRoot);
        }

    }
    // finds the root (the highest node) of the path between the receiver and the sender
    // for all 3 cases - the root can be the sender(case 1), the receiver(case 2) or another node(case 3)
    private Node findSendRoot(String senderIp, String receiverIp, Node currentNode) {
        while(currentNode != null) {
            int compareSender = currentNode.getIp().compareTo(senderIp); // compare with both values
            int compareReceiver = currentNode.getIp().compareTo(receiverIp);

            if (compareSender < 0 && compareReceiver < 0) { // both of them are in the left subtree
                currentNode = currentNode.getLeft();
                continue;
            }
            else if (compareSender > 0 && compareReceiver > 0) { // both of them are in the right subtree
                currentNode = currentNode.getRight();
                continue;
            }
            // the root for the message path is found
            // either the sender and the receiver are in the different subtrees, or the current node is one of them
            else if ((compareSender <= 0 && compareReceiver >= 0) || (compareSender >= 0 && compareReceiver <= 0)) {
                return currentNode;
            }
        }
        return null;
    }
    // case 1
    private void sendMessageSender(String senderIp, String receiverIp, Node currentNode) {
        Node previousNode;
        while (!currentNode.getIp().equals(receiverIp)) { // move down in the subtree until the receiver is found
            int compareResult = receiverIp.compareTo(currentNode.getIp());
            previousNode = currentNode;
            currentNode = (compareResult < 0) ? currentNode.getLeft() : currentNode.getRight();
            log.add(transmitterLog(senderIp, receiverIp, previousNode.getIp(),  currentNode.getIp()));
        }
        log.remove(log.size() - 1); // remove the last transmitter log
        log.add(receiverLog(senderIp, receiverIp));
    }
    // case 2
    private void sendMessageReceiver(String senderIp, String receiverIp, Node currentNode) {
        if (currentNode == null)
            return;

        int compareResult = senderIp.compareTo(currentNode.getIp());

        if (compareResult < 0) { // move down the subtree until the sender is found, log while going back up
            sendMessageReceiver(senderIp, receiverIp, currentNode.getLeft());
            log.add(transmitterLog(senderIp, receiverIp, currentNode.getLeft().getIp(), currentNode.getIp()));
        }
        else if (compareResult > 0) {
            sendMessageReceiver(senderIp, receiverIp, currentNode.getRight());
            log.add(transmitterLog(senderIp, receiverIp, currentNode.getRight().getIp(), currentNode.getIp()));
        }
        else { // sender is found
            log.add(senderLog(senderIp, receiverIp));
            return;
        }

    }
    // case 3, combines sendMessageSender and sendMessageReceiver methods
    private void sendMessage(String senderIp, String receiverIp, Node currentNode) {
        sendMessageReceiver(senderIp, receiverIp, currentNode); // go down to the sender, carry the message from the sender to the root
        log.remove(log.size() - 1); // remove the receiver log, we are only in the root, not the receiver

        // add the transmitter log for the root node
        log.add((receiverIp.compareTo(currentNode.getIp()) < 0) ? // transmitted from right to left?
                transmitterLog(senderIp, receiverIp, currentNode.getRight().getIp(), currentNode.getIp()) : // right to left
                transmitterLog(senderIp, receiverIp, currentNode.getLeft().getIp(), currentNode.getIp())); // left to right
        sendMessageSender(senderIp, receiverIp,currentNode); // send message from root to the receiver

    }
    // logging methods for sendMessage
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
