package core.synthetic;

/**
 * A statistical pool contains a mediator which could gathering the distribution
 * data.
 * 
 * @author qun
 * 
 */
public abstract class StatisticalPool extends Pool implements PoolControlable {



	@Override
	public abstract void add(Object o);

	@Override
	public abstract void remove(Object o);

	@Override
	public abstract void clearPool();

	@Override
	public abstract int getPoolNumber();

}
