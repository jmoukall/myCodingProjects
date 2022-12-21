/**
 * Holds bare nodes for use in other classes.
 * 
 * @param <T> Type of object
 */
class Node<T> {
	/**
	 * Data for the node.
	 */
	public T data;

	/**
	 * Next pointer for a list.
	 */
	public Node<T> next;

	/**
	 * Previous pointer for a list.
	 */
	public Node<T> prev;
	
	/**
	 * Default constructor.
	 */
	public Node() {
		
	}
	
	/**
	 * Constructor passing data as a parameter.
	 * @param data data to be inserted in node
	 */
	public Node(T data) {
		this.data = data;
	}
}