/** A class that represents a path via pursuit curves. */
public class Path {

    // TODO
    /**
    * This is the instance attribute of the class Path.
    */
    public Point curr = new Point();
    public Point next;

    /**
    * Define a constructor
    */
    public Path(double x, double y){
    	next = new Point(x, y);
    }

    public void iterate(double dx, double dy){
    	curr = next;
    	next = new Point(curr.x + dx, curr.y + dy);
    }
}
