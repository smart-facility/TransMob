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
