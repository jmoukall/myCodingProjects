import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.awt.Color;

import javax.swing.JPanel;

import java.util.Collection;
import java.util.NoSuchElementException;

import java.util.LinkedList;

/**
 *  Simulation of our coloring algorithm.
 *  Author: Jafar Moukalled
 *	    Katherine (Raven) Russell
 */
class ThreeTenColor implements ThreeTenAlg {
	/**
	 *  The graph the algorithm will run on.
	 */
	Graph<GraphNode, GraphEdge> graph;
	
	/**
	 *  The priority queue of nodes for the algorithm.
	 */
	WeissPriorityQueue<GraphNode> queue;
	
	/**
	 *  The stack of nodes for the algorithm.
	 */
	LinkedList<GraphNode> stack;
	
	/**
	 *  Whether or not the algorithm has been started.
	 */
	private boolean started = false;
	
	/**
	 *  Whether or not the algorithm is in the coloring stage or not.
	 */	
	private boolean coloring = false;	

	/**
	 *  The color when a node has "no color".
	 */
	public static final Color COLOR_NONE_NODE = Color.WHITE;
	
	/**
	 *  The color when an edge has "no color".
	 */
	public static final Color COLOR_NONE_EDGE = Color.BLACK;
		
	/**
	 *  The color when a node is inactive.
	 */
	public static final Color COLOR_INACTIVE_NODE = Color.LIGHT_GRAY;

	/**
	 *  The color when an edge is inactive.
	 */
	public static final Color COLOR_INACTIVE_EDGE = Color.LIGHT_GRAY;
	
	/**
	 *  The color when a node is highlighted.
	 */
	public static final Color COLOR_HIGHLIGHT = new Color(255,204,51);
	
	/**
	 *  The color when a node is in warning.
	 */
	public static final Color COLOR_WARNING = new Color(255,51,51);

			
	/**
	 *  The colors used to assign to nodes.
	 */
	public static final Color[] COLORS = 
		{Color.PINK, Color.GREEN, Color.CYAN, Color.ORANGE, 
		Color.MAGENTA, Color.YELLOW, Color.DARK_GRAY, Color.BLUE};
	
	/**
	 *  {@inheritDoc}
	 */
	public EdgeType graphEdgeType() {
		return EdgeType.UNDIRECTED;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void reset(Graph<GraphNode, GraphEdge> graph) {
		this.graph = graph;
		started = false;
		coloring = false;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public boolean isStarted() {
		return started;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void start() {
		this.started = true;
		
		//create an empty stack
		stack = new LinkedList<>();
		
		//create an empty priority queue
		queue = new WeissPriorityQueue<>();
		
		for(GraphNode v : graph.getVertices()) {
			
			//Set the cost of each node to be its degree
			v.setCost(graph.degree(v));
			
			//Set each node to be active
			//This enables the display of cost for the node
			v.setActive();
		
			//add node into queue
			queue.add(v);
		}
		
		//highlight the current node with max priority 
		highlightNextMax();
			
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void finish() {
	
		// Coloring completed. Set all edges back to "no color".
		for (GraphEdge e: graph.getEdges()){
			e.setColor(COLOR_NONE_EDGE);
		}
		
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void cleanUpLastStep() {
		// Unused. Required by the interface.		
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public boolean setupNextStep() {
	
		// Whole algorithm done. 
		if (coloring && stack.size() == 0)
			return false;
						
		// First stage done when all nodes are pushed into stack.
		// Change the flag to start the coloring stage.
		if (!coloring && graph.getVertexCount() == stack.size()){
			coloring = true;
		}
		
		//Return true to indicate more steps to continue.
		return true;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void doNextStep() {
	
		if (!coloring){
			//Stage 1: pushing nodes into stack one by one & update record
			
			// maxNode is the active node with the highest priority
			// Remove the maxNode from priority queue and push it into stack
			GraphNode maxNode = findMax();
			
			//Update the cost of all nodes that is a neighbor of the maxNode
			updateNeighborCost(maxNode);
			
			//Identify and highlight the next max node in the updated priority queue
			highlightNextMax();
			
								
		}
		else{
			//Stage 2: pop nodes from stack one by one and choose a color for each
			
			//Pop off stack top 
			GraphNode node = stack.pop();
			
			//For the node popped off, pick a color that is different from all
			//neighbors who has got assigned a color so far			
			Color newColor = chooseColor(node);
			
			//Inform all neighbors of this node the selected color
			updateColor(node, newColor);
			
		}
		
	} // end doNextStep
	
	/**
	 * Find the current max node in the priority queue and highlight it.
	 */
	public void highlightNextMax(){
		if(this.queue.isEmpty()) {return;}
		this.queue.element().color = COLOR_HIGHLIGHT;
	} // end highlightNextMax

	/**
	 * Find and remove the node with max priority from the priority queue.
	 * @return max node in priority queue
	 */
	public GraphNode findMax(){
		GraphEdge temp = null;
		Collection<GraphEdge> incidentEdges = null;
		int i = 0;

		// check if queue is empty
		if(this.queue.isEmpty()) {return null;}

		// return node with max prio and remove it
		GraphNode maxNode = this.queue.element();
		this.queue.remove();

		// push the node into the stack
		this.stack.push(maxNode);

		// set max node to be inactive and change color
		maxNode.unsetActive();
		maxNode.color = COLOR_INACTIVE_NODE;

		// get all incident edges of the maxNode
		incidentEdges = this.graph.getIncidentEdges(maxNode);

		// set the color of all the incident edges to COLOR_INACTIVE_EDGE
		incidentEdges.forEach((node) -> {node.color = COLOR_INACTIVE_EDGE;});

		return maxNode;
	} // end findMax

	/**
	 * Update the cost for all active neighbors of given node.
	 * @param maxNode node with highest degree
	 */
	public void updateNeighborCost(GraphNode maxNode){
		// Update the cost for all active neighbors of maxNode.
		// Note that the cost of a node is equal to the number of its *active* neighbors.

		// go through all neighbors and update the cost
		this.graph.getNeighbors(maxNode).forEach((neighbor) -> {
			// one less because removing the node
			neighbor.setCost(neighbor.getCost() - 1);
		});
	} // end updateNeighborCost
	
	/**
	 * Picks a color based on certain criteria that follows Chaitlin's Algorithm.
	 * @param node node to change color
	 * @return the color to be picked
	 */
	public Color chooseColor(GraphNode node){
		// check node if null
		if(node == null) {return null;}

		int i = 0;
		Color c = null;
		
		// go through all colors until one satisfies all conditions
		for(i = 0; i < COLORS.length; i++) {
			// check if neighbors have this color
			if(!node.nbrHasColor(i)) {
				c = COLORS[i];
				break;
			}
		}// end for

		// if the color is not found
		if(c == null) {
			return COLOR_WARNING;	
		}

		// set constant to use in forEach loop
		final int index = i;

		// set nbrColors for neighbors of this node
		this.graph.getNeighbors(node).forEach((n) -> {
			n.setNbrColor(index);
		});

		// return selected color	
		return c;
	} // end chooseColor
	
	/**
	 * Sets the color of a node to be the new color and updates any incident edges accordingly.
	 * @param node node of color to be changed
	 * @param newColor	new color to be switched to
	 */
	public void updateColor(GraphNode node, Color newColor){
		// set node to new color
		if(node == null) {return;}
		if(newColor == null) {return;}
		node.color = newColor;

		// check each edge, if color isnt set then  set it to new color
		this.graph.getIncidentEdges(node).forEach((edge) -> {
			if(edge.color.equals(Color.WHITE)) {
				edge.setColor(newColor);
			}
		});
	} // end updateColor
} // end ThreeTenColor
