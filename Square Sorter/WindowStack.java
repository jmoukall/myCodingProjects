import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

/**
 *  A stack of windows within the window.
 *  
 *  <p>Adapterion of Nifty Assignment (http://nifty.stanford.edu/) by
 *  Mike Clancy in 2001. Original code by Mike Clancy. Updated Fall
 *  2022 by K. Raven Russell.</p>
 *
 *  Updated Fall 2022 by Jafar Moukalled
 */
public class WindowStack {

	/**
	 * The head of the stack.
	 */
	private Node<Window> head;
	
	/**
	 * The tail of the stack.
	 */
	private Node<Window> tail;

	/**
	 * Default constructor.
	 */
	public WindowStack() {
		//Any initialization code you need.
		
		//O(1)
		// set head and tail to null
		this.head = null;
		this.tail = null;
	}
	
	/**
	 * 	Gets the head of the stack.
	 * 
	 * @return head of stack
	 */
	public Node<Window> getHead() {
		return this.head;
	}
	
	/**
	 * Gets the tail of the stack.
	 * 
	 * @return tail of stack
	 */
	public Node<Window> getTail() {
		return this.tail;
	}
	
	/**
	 * Gets the number of windows in the stack.
	 * 
	 * @return number of windows
	 */
	public int numWindows() {
		int num = 0; // initialize num to iterate
		Node<Window> temp = this.head; // initialize temp node to iterate through stack

		while(temp != null) {
			num++;
			temp = temp.next;
		}

		return num;
	}
	
	/**
	 * Adds a window to the top of the stack.
	 * 
	 * @param r new window
	 */
	public void add(Window r) {
		// instantiate new node
		Node<Window> node = new Node<>(r);
		
		// check for null window
		if(r == null) {
			throw new IllegalArgumentException("Window is invalid!");
		}

		// check if stack is empty
		if(this.head == null) {
			head = node;
			tail = node;
			head.prev = null;
			tail.next = null;

		}
		else {
			// add to the top of the stack
			node.next = head;
			node.prev = null;

			head.prev = node;
			head = node;

			// un-select previous head
			head.next.data.setSelected(false);
		}

		// make the top selected
		this.head.data.setSelected(true);
	}
	
	/**
	 * Handler for clicking inside a window.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param leftClick if leftClick or rightClick
	 * @return true if successful, false if not
	 */
	public boolean handleClick (int x, int y, boolean leftClick) {
		// check validity of x and y
		if(x < 0) {
			System.err.println("X must be greater than 0!");
			return false;
		}

		if(y < 0) {
			System.err.println("Y must be greater than 0!");
			return false;
		}

		// check if left click or right click
		if(leftClick == true) {
			//left click
			return handleLeftClick(x, y);
		}
		else {
			// right click
			return handleRightClick(x, y);
		}
	}

	/**
	 * Handler for left clicking inside a window.
	 * 
	 * @param x x-coordinate
	 * @param y	y-coordinate
	 * @return	true if successful, false if not
	 */
	private boolean handleLeftClick(int x, int y) {
		// instantiate new temp node to interate through list
		Node<Window> temp = this.head;

		// check if there is a window in the coordinates
		while(temp != null) {
			// check if the window is contained
			if(temp.data.contains(x, y)) {
				break;
			}

			// iterate
			temp = temp.next;
		}

		// return false if no windows selected
		if(temp == null) {
			return false;
		}
		else if(temp == head) { // check if selected window is at the top
			temp.data.handleClick(x,y);
			return true;
		}
		else if(temp == tail) { // check if selected window is the tail
			// remove tail from stack
			tail = tail.prev;
			tail.next = null;

			// move temp node to top
			temp.next = head;
			temp.prev = null;

			head.prev = temp;
			head = temp;

			// ensure isSelected is false for head.next
			head.next.data.setSelected(false);

			// set new top to selected and handle click
			head.data.setSelected(true);
			return true;
		}
		else { 
			// move node to top of stack

			// remove temp from stack
			temp.next.prev = temp.prev;
			temp.prev.next = temp.next;

			// now add temp to the top
			temp.next = head;
			temp.prev = null;

			head.prev = temp;
			head = temp;

			// ensure isSelected is false for head.next
			if(head.next.data.getSelected() == true) {
				// set to false
				head.next.data.setSelected(false);
			}

			// set new top to selected and handle click
			head.data.setSelected(true);
			return true;
		}
	}

	/**
	 * Handler for right clicking inside a window.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return	true if successful, false if not
	 */
	private boolean handleRightClick(int x, int y) {
		// instantiate new temp node to interate through list
		Node<Window> temp = this.head;

		// check if there is only one window
		if(this.numWindows() == 1) {
			this.head = null;
			this.tail = null;
			return true;
		}

		// check if there is a window in the coordinates
		while(temp != null) {
			// check if the window is contained
			if(temp.data.contains(x, y)) {
				break;
			}

			// iterate
			temp = temp.next;
		}

		// return false if no windows selected
		if(temp == null) {
			return false;
		}
		else if(temp == head) { // check if selected window is at the top
			head = head.next;
			head.prev = null;

			// select new top
			head.data.setSelected(true);
			return true;
		}
		else {
			// remove this node
			temp.next.prev = temp.prev;
			temp.prev.next = temp.next;
			return true;
		}
	}

	/**
	 *  Gets an iterator for the stack of windows.
	 *  Windows are returned from bottom to top.
	 *  
	 *  @return the iterator requested
	 */
	public Iterator<Window> windows() {
		//Note that this method uses your linked list!
		//so if the iterator doesn't work, that's on you...
		
		return new Iterator<Window>() {
			/**
			 *  The current node pointed to by the
			 *  iterator (containing the next value
			 *  to be returned).
			 */
			private Node<Window> current = getTail();
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Window next() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				Window ret = current.data;
				current = current.prev;
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
	 * Sorts windows in the stack by area of window.
	 */
	public void sortSize() {
		//unselect the top window
		this.getHead().data.setSelected(false);
		
		//create a way to compare windows by area
		Comparator<Window> comp = new Comparator<Window>() {
			public int compare(Window w1, Window w2) {
				return (w1.getWidth()*w1.getHeight())-(w2.getWidth()*w2.getHeight());
			}
		};
		
		//create a pair of nodes to pass into the sort function
		ThreeTenLinkedList.NodePair<Window> pair = new ThreeTenLinkedList.NodePair<>(getHead(), getTail());
		
		//call the sort function with the comparator
		ThreeTenLinkedList.NodePair<Window> ret = ThreeTenLinkedList.sort(pair, comp);
		
		//make the returned list the head and tail of this list
		//this is for PART 5 of the project... don't try to do this
		//before you complete ThreeTenLinkedList.sort()!
		
		this.head = ret.head;
		this.tail = ret.tail;	

		//re-select the top of the stack
		this.getHead().data.setSelected(true);
	}
	
	/**
	 * Sorts windows in the stack by location of the upper left point of the window.
	 */
	public void sortLoc() {
		//unselect the top window
		this.getHead().data.setSelected(false);

		// create new comparator var
		Comparator<Window> comp = new Comparator<Window>() {
			public int compare(Window w1, Window w2) {
				if(w1.getUpperLeftX() < w2.getUpperLeftX()) {
					return -1;
				}
				else if(w1.getUpperLeftX() > w2.getUpperLeftX()) {
					return 1;
				}
				else {
					if(w1.getUpperLeftY() < w2.getUpperLeftY()) {
						return -1;
					}
					else if(w1.getUpperLeftY() > w2.getUpperLeftY()) {
						return 1;
					}
					else {
						return 0;
					}
				}
			}
		};

		// create node pair
		ThreeTenLinkedList.NodePair<Window> pair = new ThreeTenLinkedList.NodePair<>(this.getHead(), this.getTail());

		// sort the pair
		ThreeTenLinkedList.NodePair<Window> sorted = ThreeTenLinkedList.sort(pair, comp);

		// set head and tail
		this.head = sorted.head;
		this.tail = sorted.tail;

		// set top to selected again
		this.head.data.setSelected(true);
	}
}

