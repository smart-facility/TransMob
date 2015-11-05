/* This file is part of TransMob.

   TransMob is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   TransMob is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser Public License for more details.

   You should have received a copy of the GNU Lesser Public License
   along with TransMob.  If not, see <http://www.gnu.org/licenses/>.

*/
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
