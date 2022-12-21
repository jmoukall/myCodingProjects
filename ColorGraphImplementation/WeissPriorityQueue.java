import java.util.Iterator;
import java.util.Comparator;
import java.util.NoSuchElementException;

import java.util.HashMap;

/**
 * PriorityQueue class implemented via the binary heap.
 * From the Weiss textbook. Updated to support a HashMap.
 * @param <AnyType> custom AnyTpe object
 * @author Jafar Moukalled
 */
public class WeissPriorityQueue<AnyType> extends WeissAbstractCollection<AnyType>
{
	/**
	 * Default capacity of heap.
	 */
	private static final int DEFAULT_CAPACITY = 100;

	/**
	 * Number of elements in the heap.
	 */
	private int currentSize;   // Number of elements in heap

	/**
	 * The heap array.
	 */
	private AnyType [ ] array; // The heap array

	/**
	 * The comparator object for the compare method.
	 */
	private Comparator<? super AnyType> cmp;

	/**
	 * HashMap to store AnyType-index pairs for easy access.
	 */
	private HashMap<AnyType, Integer> indexMap;
	
	/**
	 * Gets the index of an item in the priority queue.
	 * @param x item in the queue
	 * @return index of the item
	 */
	public int getIndex(AnyType x) {
		// use indexMap to get index
		Integer index = this.indexMap.get(x);

		if(index == null) {return -1;}
		
		return index;
	} // end getIndex
	
	/**
	 * Updates the priority of an item in the queue.
	 * @param x item to get new priority
	 * @return true if successful/ false if otherwise
	 */
	public boolean update(AnyType x) {
		// check if null
		if(x == null) {return false;}

		// record the index of x in the heap
		int index = this.getIndex(x);

		// replace value at index
		this.array[index] = x;

		// establish order
		this.buildHeap();

		return true;
	} // end update
	
	/**
	 * Construct an empty PriorityQueue.
	 */
	@SuppressWarnings("unchecked")
	public WeissPriorityQueue( )
	{
		this.currentSize = 0;
		this.cmp = null;
		this.array = (AnyType[]) new Object[ DEFAULT_CAPACITY + 1 ];
		this.indexMap = new HashMap<AnyType, Integer>();
	}
	
	/**
	 * Construct an empty PriorityQueue with a specified comparator.
	 * @param c comparator object
	 */
	@SuppressWarnings("unchecked")
	public WeissPriorityQueue( Comparator<? super AnyType> c )
	{
		this.currentSize = 0;
		this.cmp = c;
		this.array = (AnyType[]) new Object[ DEFAULT_CAPACITY + 1 ];
		this.indexMap = new HashMap<AnyType, Integer>();
	}
	
	 
	/**
	 * Construct a PriorityQueue from another Collection.
	 * @param coll collection containing items
	 */
	@SuppressWarnings("unchecked")
	public WeissPriorityQueue( WeissCollection<? extends AnyType> coll )
	{
		this.cmp = null;
		this.currentSize = coll.size( );
		this.array = (AnyType[]) new Object[ ( currentSize + 2 ) * 11 / 10 ];
		this.indexMap = new HashMap<AnyType, Integer>();
		
		int i = 1;
		for( AnyType item : coll )
		{
			this.indexMap.put(item, i);
			array[ i++ ] = item;
		}
		buildHeap( );
	}
	
	/**
	 * Compares lhs and rhs using comparator if
	 * provided by cmp, or the default comparator.
	 * @param lhs item to be compared
	 * @param rhs	item to be compared
	 * @return <0 if less, 0 if equal, and >0 if greater
	 */
	@SuppressWarnings("unchecked")
	private int compare( AnyType lhs, AnyType rhs )
	{
		if( cmp == null )
			return ((Comparable)lhs).compareTo( rhs );
		else
			return cmp.compare( lhs, rhs );	
	} // end compare
	
	/**
	 * Adds an item to this PriorityQueue.
	 * @param x any object.
	 * @return true.
	 */
	public boolean add( AnyType x )
	{
		if( currentSize + 1 == array.length )
			doubleArray( );

		// Percolate up
		int hole = ++currentSize;
		array[ 0 ] = x;
		
		for( ; compare( x, array[ hole / 2 ] ) < 0; hole /= 2 ) {
			array[ hole ] = array[ hole / 2 ];
			this.indexMap.remove(hole, array[hole]);
			this.indexMap.replace(array[hole/2], hole/2, hole);
		}

		array[ hole ] = x;
		this.indexMap.remove(hole, array[hole]);
		this.indexMap.put(x, hole);
		
		return true;
	} // end add
	
	/**
	 * Returns the number of items in this PriorityQueue.
	 * @return the number of items in this PriorityQueue.
	 */
	public int size( )
	{
		return currentSize;
	} // end size
	
	/**
	 * Make this PriorityQueue empty.
	 */
	public void clear( )
	{
		currentSize = 0;
	} // end clear
	
	/**
	 * Returns an iterator over the elements in this PriorityQueue.
	 * The iterator does not view the elements in any particular order.
	 * @return an iterator
	 */
	public Iterator<AnyType> iterator( )
	{
		return new Iterator<AnyType>( )
		{
			int current = 0;
			
			public boolean hasNext( )
			{
				return current != size( );
			}
			
			@SuppressWarnings("unchecked")
			public AnyType next( )
			{
				if( hasNext( ) )
					return array[ ++current ];
				else
					throw new NoSuchElementException( );
			}
			
			public void remove( )
			{
				throw new UnsupportedOperationException( );
			}
		};
	} // end iterator
	 
	/**
	 * Returns the smallest item in the priority queue.
	 * @return the smallest item.
	 * @throws NoSuchElementException if empty.
	 */
	public AnyType element( )
	{
		if( isEmpty( ) )
			throw new NoSuchElementException( );
		return array[ 1 ];
	} // end element
	
	/**
	 * Removes the smallest item in the priority queue.
	 * @return the smallest item.
	 * @throws NoSuchElementException if empty.
	 */
	public AnyType remove( )
	{
		AnyType minItem = element( );
		array[ 1 ] = array[ currentSize-- ];
		percolateDown( 1 );
		this.indexMap.remove(array[1]);

		return minItem;
	} // end remove


	/**
	 * Establish heap order property from an arbitrary
	 * arrangement of items. Runs in linear time.
	 */
	private void buildHeap( )
	{
		for( int i = currentSize / 2; i > 0; i-- )
			percolateDown( i );
	} // end buildHeap

	/**
	 * Internal method to percolate down in the heap.
	 * @param hole the index at which the percolate begins.
	 */
	private void percolateDown( int hole )
	{
		int child;
		AnyType tmp = array[ hole ];

		for( ; hole * 2 <= currentSize; hole = child )
		{
			child = hole * 2;
			if( child != currentSize &&
					compare( array[ child + 1 ], array[ child ] ) < 0 )
				child++;
			if( compare( array[ child ], tmp ) < 0 ) {
				array[ hole ] = array[ child ];
				this.indexMap.remove(hole, array[hole]);
				this.indexMap.replace(array[child], child, hole);
			}
			else
				break;
		}
		array[ hole ] = tmp;
		this.indexMap.remove(hole, array[hole]);
		this.indexMap.put(tmp, hole);
	} // end percolateDown
	
	/**
	 * Internal method to extend array.
	 */
	@SuppressWarnings("unchecked")
	private void doubleArray( )
	{
		AnyType [ ] newArray;

		newArray = (AnyType []) new Object[ array.length * 2 ];
		for( int i = 0; i < array.length; i++ )
			newArray[ i ] = array[ i ];
		array = newArray;
	} // end doubleArray
}// end WeissPriorityQueue class
