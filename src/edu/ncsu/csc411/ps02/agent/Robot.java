package edu.ncsu.csc411.ps02.agent;

import java.util.*;

import edu.ncsu.csc411.ps02.environment.Action;
import edu.ncsu.csc411.ps02.environment.Environment;
import edu.ncsu.csc411.ps02.environment.Position;

import static edu.ncsu.csc411.ps02.environment.TileStatus.*;

/**
	Represents an intelligent agent moving through a particular room.	
	The robot only has two sensors - the ability to retrieve
 	status of all its neighboring tiles, including itself, and the
	ability to retrieve to location of the TARGET tile.
	
	Your task is to modify the getAction method below so that it reaches
	TARGET with a minimal number of steps.
*/

public class Robot {
	private Environment env;

	private class Node {
		Position position;
		Node parent;
		double g;
		double h;

		Node(Position position, Node parent, double g, double h) {
			this.position = position;
			this.parent = parent;
			this.g = g;
			this.h = h;
		}

		double f() {
			return g + h;
		}
	}
	
	/** Initializes a Robot on a specific tile in the environment. */
	public Robot (Environment env) {
		this.env = env;
	}
	
	/**
    Problem Set 02 - Modify the getAction method below in order to simulate
    the passage of a single time-step. At each time-step, the Robot decides
    which tile to move to.
    
    Your task for this Problem Set is to modify the method below such that
    the Robot agent is able to reach the TARGET tile on a given Environment. 
    5 out of the 10 graded test cases, with explanations on how to create new
    Environments, are available under the test package.
    
    This method should return a single Action from the Action class.
    	- Action.DO_NOTHING
    	- Action.MOVE_UP
    	- Action.MOVE_DOWN
    	- Action.MOVE_LEFT
    	- Action.MOVE_RIGHT
	 */

	/**
			1. Implement a Node class to store position, 
	 		cost of path from starting node to itself (g), 
	 		estimated cost of path from itself to the target node (h),
	 		and total cost (f), as well as a reference to the previous node in path.
	 		2. Create two sets of nodes, one containing nodes to be examined, and the other containing nodes already examined.
	 		3. Whenever the set of nodes to be examined is not exmpty, find the node with the lowest total cost
	 		and remove that node.
	 		4. For all neighbor nodes of the current node, calculate their f, 
	 		and add them to the set of nodes to be examined if the current path to the neighbor is shorter or if they are not in there already.
	 		5. If by the time the set of nodes to be examined is emptied, and a path to the target node is not found,
	 		there is no valid path to the target.
	 		6. When a path is found, the agent moves from the target back to the start node using the references stored,
	 		and return the action that is used to move to the next node in path.
	 */
	public Action getAction () {
		Position start = env.getRobotPosition(this);
		Position target = env.getTarget();
		List<Node> openList = new ArrayList<>();
		List<Node> closedList = new ArrayList<>();
		openList.add(new Node(start, null, 0, calculateDistance(start, target)));
		while (!openList.isEmpty()) {
			Node currentNode = findLowestCost(openList);
			if (currentNode.position.equals(target)) {
				return getMovement(getPath(currentNode));
			}
			openList.remove(currentNode);
			closedList.add(currentNode);
			for (Map.Entry<String, Position> entry : env.getNeighborPositions(currentNode.position).entrySet()) {
				Position neighborPosition = entry.getValue();
				if (neighborPosition == null || findPosInList(closedList, neighborPosition) || env.getPositionTile(neighborPosition).getStatus() == IMPASSABLE) {
					continue;
				}
				Node neighborNode = new Node(neighborPosition, currentNode, currentNode.g + 1, calculateDistance(neighborPosition, target));
				if (!findPosInList(openList, neighborPosition)) {
					openList.add(neighborNode);
				} else if (neighborNode.g < findNodeInList(openList, neighborPosition).g) {
					openList.remove(findNodeInList(openList, neighborPosition));
					openList.add(neighborNode);
				}
			}
		}
		return Action.DO_NOTHING;
	}

	private double calculateDistance(Position a, Position b) {
		return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
	}
	private Node findLowestCost(List<Node> list) {
		Node lowestfNode = list.get(0);
		for (Node node : list) {
			if (node.f() < lowestfNode.f()) {
				lowestfNode = node;
			}
		}
		return lowestfNode;
	}

	private Node findNodeInList(List<Node> list, Position position) {
		for (Node node : list) {
			if (node.position.equals(position)) {
				return node;
			}
		}
		return null;
	}

	private boolean findPosInList(List<Node> list, Position position) {
		for (Node node : list) {
			if (node.position.equals(position)) {
				return true;
			}
		}
		return false;
	}

	private Action getMovement(List<Node> path) {
		if (path.size() > 1) {
			Position start = path.get(0).position;
			Position next = path.get(1).position;

			if (next.getRow() > start.getRow()) {
				return Action.MOVE_DOWN;
			} else if (next.getRow() < start.getRow()) {
				return Action.MOVE_UP;
			} else if (next.getCol() > start.getCol()) {
				return Action.MOVE_RIGHT;
			} else if (next.getCol() < start.getCol()) {
				return Action.MOVE_LEFT;
			}
		}
		return Action.DO_NOTHING;
	}

	private List<Node> getPath(Node endNode) {
		List<Node> path = new ArrayList<>();
		Node currentNode = endNode;
		while (currentNode != null) {
			path.add(currentNode);
			currentNode = currentNode.parent;
		}
		Collections.reverse(path);
		return path;
	}
}