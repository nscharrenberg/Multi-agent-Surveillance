package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.structures.FibonacciHeap;

public class Fibonacci{

    //Node with the minimum key
    private Node minNode;
    //Size of the heap, how many node in the heap
    private int size;
    //Sets the minimum node
    private void setMinNode(Node x){
        this.minNode = x;
    }


    //Inserting to the heap
    public void insert(Node node){
        if(minNode == null)
            setMinNode(node);
        addToRootList(node);
        //If inserted node has a key less then a minimum node, we change the min node
        if(minNode.key > node.key)
            setMinNode(node);
        size++;
    }

    //Decreasing the key of the node
    public void decreaseKey(Node node, int key){
        //Set a new key to the node
        node.key = key;
        //Get a parent of the node
        Node parentX = node.parent;
        //Check if node has a parent, and if parent has a key bigger than a key of the node
        if(parentX != null && node.key < parentX.key){
            cut(node, parentX);
            cutCascade(parentX);
        }

        if(minNode.key > node.key)
            setMinNode(node);
    }

    private void cut(Node node, Node parent){
        removeFromChildList(node, parent);
        parent.degree -= 1;
        addToRootList(node);
        node.parent = null;
        node.mark = false;
    }
    private void cutCascade(Node x){
        Node parent = x.parent;
        if(parent != null){
            if(!x.mark)
                x.mark = true;
            else{
                cut(x, parent);
                cutCascade(parent);
            }
        }
    }
    public Node extractMin(){
        Node min = minNode;
        if(min != null){

            //If min node has children, we put their on the root
            Node minChild = min.child;
            if(minChild != null) {
                int childrenNum = min.degree;
                for (int i = 0; i < childrenNum; i++) {
                    Node childN = minChild;
                    minChild = minChild.right;
                    addToRootList(childN);
                    childN.parent = null;
                }
            }
            removeFromRootList(min);

            if(min.right == min) {
                setMinNode(null);
            }else{
                setMinNode(min.right);
                consolidate();
            }
            size--;
        }
        assert min != null;
        return min;
    }
    private void heapLink(Node parent, Node child){
        removeFromRootList(child);
        addToChildList(child, parent);
        //Plus one to the parent, degree means how many child has this node
        parent.degree++;
        child.parent = parent;
        child.mark = false;
    }
    private void removeFromChildList(Node node, Node parent){
        if(parent.child == parent.child.right)
            parent.child = null;
        else if(parent.child == node) {
                /*
            First IF  "if(parent.child == parent.child.right)"
                      PARENT                               PARENT
                        ||                 ====>             ||
            CHILD1 <- CHILD1 -> CHILD1                      NULL
            =======================================================
            Second IF  "if(parent.child == node)"
                     PARENT                             PARENT
                       ||                  =====>         ||
            CHILD1 <- NODE -> CHILD1                    CHILD1
             */
            parent.child = node.right;
        }
        //Remove node
        node.left.right = node.right;
        node.right.left = node.left;
    }
    private void addToChildList(Node node, Node parent){
        if(parent.child == null)
            node.right = node.left = parent.child = node;
        else {
                /*
                **Every child has the same parent

                  PARENT                                          PARENT
              /     ||     \                                   /    ||    \
        CHILD1 <- CHILD1 -> CHILD1             (NODE) <- CHILD1 <- NODE -> CHILD1 -> (NODE)
        ============================================================================================================================
                              PARENT                                                             PARENT
                          /     ||     \                                                      /    ||    \
        (CHILD1) <- CHILD2 <- CHILD1 -> CHILD2 -> (CHILD1)          (NODE) <- CHILD2 <- CHILD1 <- NODE -> CHILD2 -> CHILD1 -> (NODE)
        =============================================================================================================================================================
                                        PARENT                                                                          PARENT
                                    /     ||     \                                                                   /    ||    \
        (CHILD3) <- CHILD2 <- CHILD1 <- CHILD3 -> CHILD2 -> CHILD1 -> (CHILD3)   (NODE) <- CHILD2 <- CHILD1 <- CHILD3 <- NODE -> CHILD2 -> CHILD1 -> CHILD3 -> (NODE)
                 */
            node.right = parent.child.right;
            node.left = parent.child;
            parent.child.right.left = parent.child.right = node;
        }
    }
    private void addToRootList(Node node){
        //Add node on the right side of the minimum node
        node.right = minNode.right;
        node.left = minNode;
        //Connect the right node of a minimum node with the node, and the right node of a minimum node is the node
        minNode.right.left = minNode.right =node;
    }
    private void removeFromRootList(Node node){
        if(node == minNode)
            setMinNode(node.right);

        //Left node connect with the Right node
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private void consolidate() {
        //Optimal solution for size()  Can be improve by Tom Cormen formula
        Node[] array = new Node[(int) (Math.log(size) * 2)+1];
        Node stop = minNode;
        Node nextR = minNode;
        boolean flag = true;
        while (stop != nextR || flag){
            //Maintain the condition of a while loop
            flag = false;
            Node next = nextR;
            Node save = nextR.right;


            int degree = next.degree;

            //Combine one node with other node (make children)
            while (array[degree] != null) {
                Node a = array[degree];
                if (next.key > a.key) {
                    Node tmp_Node = next;
                    next = a;
                    a = tmp_Node;
                }

                //Maintain the condition of a while loop
                if(a == stop)
                    stop = stop.right;
                if(save == a)
                    save = save.right;

                //Make a heap (link between two nodes) (make a child)
                heapLink(next, a);
                array[degree] = null;
                degree++;
            }
            array[degree] = next;
            nextR = save;
            if(array[degree].key < minNode.key)
                setMinNode(array[degree]);
        }
    }
    public boolean isEmpty(){
        return minNode == null;
    }
}
