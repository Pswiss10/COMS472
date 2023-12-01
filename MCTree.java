package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;



public class MCTree<E> {

	private final int CVal = 2;
    private MCNode<E> root;;
	private HashMap<E, Double> playouts;
    private HashMap<E, Double> wins;
	private HashMap<MCNode<E>, List<MCNode<E>>> gameTree;

    public MCTree(MonteCarloTreeSearch monteCarloTreeSearch) {
        this.root = null;
		this.gameTree = new HashMap<>();
		wins = new HashMap<>();
		playouts = new HashMap<>();
    }

	public void addRoot(E state) {
		this.root = new MCNode<>(state);
		gameTree.put(root, new ArrayList<>());
		playouts.put(state, 0.0);
		wins.put(state, 0.0);
    }


    public MCNode<E> getRoot() {
        return root;
    }

    public List<E> getVisitedChildren(MCNode<E> parent) {
        List<E> visitedChildren = new ArrayList<>();
		if (gameTree.containsKey(parent)){
			for (MCNode<E> child : gameTree.get(parent)) {
				visitedChildren.add(child.getState());
			}
		}
        return visitedChildren;
    }

    public MCNode<E> addChild(MCNode<E> parent, E child) {
        MCNode<E> newChild = new MCNode<>(child);
		List<MCNode<E>> children = successors(parent);
		children.add(newChild);
		gameTree.put(parent,children);
		wins.put(child, 0.0);
		playouts.put(child, 0.0);
        return newChild;
    }

    public MCNode<E> getParent(MCNode<E> node) {
        MCNode<E> parent = null;
		for (MCNode<E> key : gameTree.keySet()) {
			List<MCNode<E>> children = successors(key);
			for (MCNode<E> child : children) {
				if (child.getState() == node.getState()) {
					parent = key;
					break;
				}
			}
			if (parent != null) break;
		}
		return parent;
    }

	public List<MCNode<E>> successors(MCNode<E> node) {
		if (gameTree.containsKey(node)){
		 	return gameTree.get(node);
		}
		else {
			return new ArrayList<>();
		}
	}

	public void updateStats(boolean result, MCNode<E> node) {
		playouts.put(node.getState(), playouts.get(node.getState()) + 1);
		if (result) wins.put(node.getState(), wins.get(node.getState()) + 1);
	}

    public MCNode<E> getChildWithMaxUCT(MCNode<E> node) {
		List<MCNode<E>> best_children = new ArrayList<>();
		double max_uct = Double.NEGATIVE_INFINITY;
		for (MCNode<E> child : successors(node)) {
			double uct = ((wins.get(child.getState())) / (playouts.get(child.getState()))) + Math.sqrt((CVal / playouts.get(child.getState())) * (Math.log(playouts.get(node.getState()))));
			if (uct > max_uct) {
				max_uct = uct;
				best_children = new ArrayList<>();
				best_children.add(child);
			} else if (uct == max_uct) {
				best_children.add(child);
			}
		}
		
		Random rand = new Random();
		return best_children.get(rand.nextInt(best_children.size()));
	}

    public MCNode<E> getChildWithMaxPlayouts(MCNode<E> node) {
		List<MCNode<E>> best_children = new ArrayList<>();
		double max_playouts = Double.NEGATIVE_INFINITY;
		for (MCNode<E> child : successors(node)) {
			double playouts = (this.playouts.get(child.getState()));
			if (playouts > max_playouts) {
				max_playouts = playouts;
				best_children = new ArrayList<>();
				best_children.add(child);
			} else if (playouts == max_playouts) {
				best_children.add(child);
			}
		}
		Random rand = new Random();
		return best_children.get(rand.nextInt(best_children.size()));
    }
}