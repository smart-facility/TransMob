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
package core.preprocessing;

/**
 * This class define the structure of output files: .PERSONS.
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * @deprecated
 */

@Deprecated
public class Persons {

	/**
	 * @return
	 *  name="hhold"
	 */
	public Integer getHhold() {
		return hhold;
	}

	/**
	 * @return
	 *  name="person"
	 */
	public Integer getPerson() {
		return person;
	}

	/**
	 * @return
	 *  name="relate"
	 */
	public Integer getRelate() {
		return relate;
	}

	/**
	 * @return
	 *  name="age"
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * @return
	 *  name="gender"
	 */
	public Integer getGender() {
		return gender;
	}

	/**
	 * @return
	 *  name="work"
	 */
	public Integer getWork() {
		return work;
	}

	/**
	 * @return
	 *  name="drive"
	 */
	public Integer getDrive() {
		return drive;
	}

	/**
	 * @param hhold
	 *  name="hhold"
	 */
	public void setHhold(int hhold) {
		this.hhold = hhold;
	}

	/**
	 * @param person
	 *  name="person"
	 */
	public void setPerson(int person) {
		this.person = person;
	}

	/**
	 * @param relate
	 *  name="relate"
	 */
	public void setRelate(int relate) {
		this.relate = relate;
	}

	/**
	 * @param age
	 *  name="age"
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @param gender
	 *  name="gender"
	 */
	public void setGender(int gender) {
		this.gender = gender;
	}

	/**
	 * @param work
	 *  name="work"
	 */
	public void setWork(int work) {
		this.work = work;
	}

	/**
	 * @param drive
	 *  name="drive"
	 */
	public void setDrive(int drive) {
		this.drive = drive;
	}

	/**
	 *  name="hhold"
	 */
	private int hhold;
	/**
	 *  name="person"
	 */
	private int person;
	/**
	 *  name="relate"
	 */
	private int relate;
	/**
	 *  name="age"
	 */
	private int age;
	/**
	 *  name="gender"
	 */
	private int gender;
	/**
	 *  name="work"
	 */
	private int work;
	/**
	 *  name="drive"
	 */
	private int drive;
}
