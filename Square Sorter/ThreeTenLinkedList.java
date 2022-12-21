import java.util.Comparator;

/**
 * A class to sort Linked lists and check if a list is sorted.
 * @author Jafar Moukalled
 * 
 * @param <T> Type of object
 */
class ThreeTenLinkedList<T> {
	/**
	 * Validates if a given doulby linked list is sorted or not.
	 * @param <X> type for object
	 * @param pairs head/tail combo
	 * @param comp comparator variable
	 * @return true if sorted, false otherwise
	 */
	static <X> boolean isSorted(NodePair<X> pairs, Comparator<X> comp) {
		
		// check for null comparator or null pairs
		if(comp == null) {
			throw new IllegalArgumentException("Comparator is null!");
		}

		if(pairs == null) {
			throw new IllegalArgumentException("Node pairs are null!");
		}

		// check for empty list
		if((pairs.head == null) && (pairs.tail == null)) {
			return true;
		}

		Node<X> temp = pairs.head; // to iterate through the list

		while(temp != null) {
			if(comp.compare(temp.data, temp.next.data) > 0) {
				// if true for any node, then it is not sorted
				return false;
			}
			temp = temp.next;
		}
		// if reached here, then it is sorted
		return true;
	}
	
	/**
	 * Sorts a pair of head/tail nodes of a doubly linked list using a custom comparator variable.
	 * 
	 * @param <X> The type of object
	 * @param pairs	head/tail combo
	 * @param comp comparator variable
	 * @return sorted pair
	 */
	static <X> NodePair<X> sort(NodePair<X> pairs, Comparator<X> comp) {
		
		// check for null comparator or null pairs
		if(comp == null) {
			throw new IllegalArgumentException("Comparator is null!");
		}

		if(pairs == null) {
			throw new IllegalArgumentException("Node pairs are null!");
		}

		// check for empty list
		if((pairs.head == null) && (pairs.tail == null)) {
			return pairs;
		}

		Node<X> temp = pairs.head; // to iterate through the list
		X d = null; // to replace values for sorting

		// sort
		while(true) {
			if(temp.next == null) {
				// done sorting, reached end of list
				break;
			}
			else if(comp.compare(temp.data, temp.next.data) > 0) {
				// shift temp.next.data to the left
				d = temp.data;
				temp.data = temp.next.data;
				temp.next.data = d;
				
				// go back to the start of the list
				temp = pairs.head;
			}
			else {
				temp = temp.next;
			}
		}

		// list is sorted here!
		return pairs;
	}
	
	/**
	 * Creates a pair of nodes. (head/tail)
	 * 
	 * @param <Y> Object Type
	 */
	public static class NodePair<Y> {
		/**
		 * Head of the list.
		 */
		public Node<Y> head;

		/**
		 * Tail of the list.
		 */
		public Node<Y> tail;

		/**
		 * Constructor for pair of nodes.
		 * @param head Tail of the list.
		 * @param tail Head of the list.
		 */
		public NodePair(Node<Y> head, Node<Y> tail) {
			this.head = head;
			this.tail = tail;
		}
	}
}