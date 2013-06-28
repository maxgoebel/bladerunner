package at.tuwien.prip.ocr;

/**
 * 
 * Coordinate.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 30, 2012
 */
public class Coordinate {

	int x;
	int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Coordinate)
		{
			Coordinate other = (Coordinate) obj;
			return x==other.x && y==other.y;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
}
