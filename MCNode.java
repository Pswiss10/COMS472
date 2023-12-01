package edu.iastate.cs472.proj2;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode<E>
{
    private E state;
    private MCNode<E> parent;

    public MCNode(E board) {
        this.state = board;
        this.parent = null;
    }

    public E getState() {
        return state;
    }

    public MCNode<E> getParent() {
        return parent;
    }

    public boolean isRootNode() {
      return parent == null;
    }
    
}

