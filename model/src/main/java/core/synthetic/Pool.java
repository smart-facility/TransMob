package core.synthetic;

/**
 * The abstract pool for general usage.
 * 
 * @author qun
 * 
 */
public abstract class Pool implements Command {

	protected int maxId;

	public Pool(int maxId) {
		super();
		this.maxId = maxId;
	}

	public Pool() {
		super();
	}

	@Override
	public abstract void add(Object o);

	@Override
	public abstract void remove(Object o);

	public int getMaxId() {
		return this.maxId;
	}

	public abstract int getPoolNumber();

	public abstract Object getPoolComponent();

	@Override
	public String toString() {
		return "Pool [maxId=" + maxId + "]";
	}

}
