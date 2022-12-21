import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

/**
 *  A list of squares within a single window.
 *  
 *  <p>Adapterion of Nifty Assignment (http://nifty.stanford.edu/) by
 *  Mike Clancy in 2001. Original code by Mike Clancy. Updated Fall
 *  2022 by K. Raven Russell.</p>
 *  
 *  Updated Fall 2022 by Jafar Moukalled
 */
public class SquareList {

	/**
	 * Head of the square list.
	 */
	private Node<Square> head;

	/**
	 * Tail of the square list.
	 */
	private Node<Square> tail;

	/**
	 *  Initialize an empty list of squares.
	 */
	public SquareList() {
		head = null;
		tail = head;
	}
	
	/**
	 * Gets the head of the square list.
	 * @return head of list
	 */
	public Node<Square> getHead() {
		return this.head;
	}
	
	/**
	 * Gets the tail of the square list.
	 * @return tail of list
	 */
	public Node<Square> getTail() {
		return this.tail;
	}
	
	/**
	 * Determines how many squares there are in the list.
	 * @return number of squares
	 */
	public int numSquares() {		
		int num = 0; // initialize num to iterate
		Node<Square> temp = this.head; // initialize temp node to iterate through list

		while(temp != null) {
			num++;
			temp = temp.next;
		}

		return num;
	}
	
	/**
	 * Adds a square to the end of the list.
	 * 
	 * @param sq a new square
	 */
	public void add(Square sq) {
		// error check
		if(sq == null) {
			throw new IllegalArgumentException("Square is invalid!");
		}

		// instantiate new node
		Node<Square> node = new Node<>(sq);

		// check if there are no nodes in the list
		if(head == null) {
			// no nodes so head and tail are the same for now
			head = node;
			tail = node;
			head.prev = null;
			tail.next = null;
		}
		else {
			// in any other scenario, add to the back of the list
			tail.next = node;
			node.prev = tail;
			tail = node;
			tail.next = null;
		}
	}
	
	/**
	 * Handler for clicking on a square.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if successful, false if not
	 */
	public boolean handleClick (int x, int y) {
		// instantiate new temp node to interate through list
		Node<Square> temp = this.head;

		// to label if a square has been removed
		int n = 0;

		// check validity of x and y
		if(x < 0) {
			System.err.println("X must be greater than 0!");
			return false;
		}

		if(y < 0) {
			System.err.println("Y must be greater than 0!");
			return false;
		}

		// if one square in the list
		if(this.numSquares() == 1) {	
			// check if contained
			if(head.data.contains(x, y)) {
				this.head = null;
				this.tail = null;
				return true;
			}
			else {
				return false;
			}
		}

		// if no squares in the list
		if(this.numSquares() == 0) {
			// do nothing
			return false;
		}
		

		// iterate through list until found a square that is contained in (x,y)
		while(temp != null) {

			if(this.head.data.contains(x,y)) {
				// if head needs to be removed
				this.head = this.head.next;
				this.head.prev = null;
				n++;
			}
			else if(this.tail.data.contains(x,y)) {
				// if tail needs to be removed
				this.tail = this.tail.prev;
				this.tail.next = null;
				n++;
			}
			else if(temp.data.contains(x,y)) {
				// is contained so remove this node
				temp.prev.next = temp.next;
				temp.next.prev = temp.prev;
				n++;
			}
			
			temp = temp.next;
		} // end while

		if(n > 0) {
			// if square(s) have been deleted
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *  Gets an iterator for the list of squares.
	 *  Squares are returned in the order added.
	 *  
	 *  @return the iterator requested
	 */
	public Iterator<Square> elements() {
		return new Iterator<Square>() {
			/**
			 *  The current node pointed to by the
			 *  iterator (containing the next value
			 *  to be returned).
			 */
			private Node<Square> current = getHead();
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Square next() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				Square ret = current.data;
				current = current.next;
				return ret;
			}
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return (current != null);
			}
		};
	}
	
	/**
	 * Sorts the list by creation time using the id.
	 */
	public void sortCreation() {
		// make comparator var
		Comparator<Square> comp = new Comparator<Square>() {
			public int compare(Square s1, Square s2) {
				return(s1.id() - s2.id());
			}
		};

		// make pair of squares
		ThreeTenLinkedList.NodePair<Square> pair = new ThreeTenLinkedList.NodePair<>(this.getHead(), this.getTail());

		// sort the list
		ThreeTenLinkedList.NodePair<Square> sorted = ThreeTenLinkedList.sort(pair, comp);

		// set the head and tail of the list
		this.head = sorted.head;
		this.tail = sorted.tail;
	}
	
	/**
	 * Sorts the list by location by the upper left point of the square.
	 */
	public void sortLoc() {
		// make comparator var
		Comparator<Square> comp = new Comparator<Square>() {
			public int compare(Square s1, Square s2) {
				if((s1.getUpperLeftX() - s2.getUpperLeftX()) == 0) {
					return (s1.getUpperLeftY() - s2.getUpperLeftY());
				}
				else {
					return (s1.getUpperLeftX() - s2.getUpperLeftX());
				}
			}
		};

		// make pair of squares
		ThreeTenLinkedList.NodePair<Square> pair = new ThreeTenLinkedList.NodePair<>(this.getHead(), this.getTail());

		// sort the list
		ThreeTenLinkedList.NodePair<Square> sorted = ThreeTenLinkedList.sort(pair, comp);

		// set the head and tail of the list
		this.head = sorted.head;
		this.tail = sorted.tail;
	}
}
