package core.synthetic;

/**
 * The interface for pool to add and remove instances
 * @author qun
 *
 */
public interface Command {
	
	public void add(Object o);
	
	public void remove(Object o);
}
