import java.awt.Color;
import java.awt.Graphics;

/**
 *  A single square in a window.
 *  
 *  <p>Adapterion of Nifty Assignment (http://nifty.stanford.edu/) by
 *  Mike Clancy in 2001. Original code by Mike Clancy. Updated Fall
 *  2022 by K. Raven Russell.</p>
 */
public class Square {
	/**
	 *  The next id when a new square is created.
	 */
	private static int nextID = 0;
	
	/**
	 *  The X position of the upper left corner of this square.
	 */
	private int upperLeftX;
	
	/**
	 *  The Y position of the upper left corner of this square.
	 */
	private int upperLeftY;
	
	/**
	 *  The size of this square.
	 */
	private int size;
	
	/**
	 *  The color this square.
	 */
	private Color color;
	
	/**
	 *  The id of this square.
	 */
	private int id;
	
	/**
	 *  Initialize a square with the given position (specified 
	 *  relative to the WindowManager in which all this is being
	 *  run), dimensions, and color.
	 *  
	 *  @param centerX the x position of the center of this square
	 *  @param centerY the y position of the center of this square
	 *  @param size the size of this square
	 *  @param c the color of this square
	 */
	public Square (int centerX, int centerY, int size, Color c) {
		this.upperLeftX = centerX - size/2;
		this.upperLeftY = centerY - size/2;
		this.size = size;
		this.color = c;
		this.id = nextID;
		nextID++;
	}

	/**
	 * This method checks whether a specific point is contained in the current square object.
	 * 
	 * @param x x coordinate
	 * @param y	y coordinate
	 * @return true if is contained, false if not
	 */
	public boolean contains (int x, int y) {
		//Returns whether or not a given x and y
		//position are contained within this square.

		// find max bounds for current square
		int maxBoundX = this.upperLeftX + this.size;
		int maxBoundY = this.upperLeftY + this.size;

		// check validity of x and y
		if(x < 0) {
			System.err.println("X must be greater than 0!");
			return false;
		}

		if(y < 0) {
			System.err.println("Y must be greater than 0!");
			return false;
		}

		// now to check if point is in a square
		if(((x >= upperLeftX) && (x <= maxBoundX)) && ((y >= upperLeftY) && (y <= maxBoundY))) {
			// point is in the square
			return true;
		}
		else {
			// point is not in the square
			return false;
		}
	}

	/**
	 *  Fetches the id of this square.
	 *  
	 *  @return the id of this square
	 */
	public int id() {
		return id;
	}
	
	/**
	 *  Gets the upper left x position of the square.
	 *  
	 *  @return the upper left x position
	 */
	public int getUpperLeftX() {
		return upperLeftX;
	}
	
	/**
	 *  Gets the upper left y position of the square.
	 *  
	 *  @return the upper left y position
	 */
	public int getUpperLeftY() {
		return upperLeftY;
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals (Object o) {
		if(o instanceof Square) {
			Square sq = (Square) o;
			return upperLeftX == sq.upperLeftX && 
				upperLeftY == sq.upperLeftY && 
				size == sq.size;
		}
		return false;
	}
	
	/**
	 *  Draw the square, surrounded by a border.
	 *  
	 *  @param g the graphics to paint on, assumed not null
	 */
	public void paint (Graphics g) {
		// set the color to black for the border
		g.setColor(Color.BLACK);

		// draw the border
		g.drawRect(this.upperLeftX, this.upperLeftY, this.size, this.size);

		// set the color to whatever specified
		g.setColor(this.color);

		// fill the drawn rectangle
		g.fillRect(this.upperLeftX, this.upperLeftY, this.size, this.size);
	}
}
