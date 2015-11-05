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
package core;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A utility class implement serialized deep copy
 * 
 * @author qun
 * 
 */
public final class DeepCopy {

    private static final Logger logger = Logger.getLogger(DeepCopy.class);

    private DeepCopy() {

	}

	/**
	 * copies object though serialization.
	 * 
	 * @param src
	 *            the source object
	 * @return the copied object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object copyBySerialize(Object src) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
			oos = new ObjectOutputStream(bos); // B
			// serialize and pass the object
			oos.writeObject(src); // C
			oos.flush(); // D
			ByteArrayInputStream bin = new ByteArrayInputStream(
					bos.toByteArray()); // E
			ois = new ObjectInputStream(bin); // F
			// return the new object
			// return ois.readObject(); // G
		} catch (Exception e) {
			logger.error("Exception in ObjectCloner = ", e);
		} finally {
			try {
			//	oos.close();
				if (ois != null) {
                    ois.close();
                }
			} catch (IOException e) {
                logger.error("Exception in ObjectCloner = ", e);
            }

		}
		try {
			return ois.readObject();
		} catch (Exception e) {
            logger.error("Exception in ObjectCloner = ", e);
        }

		return ois;
	}
}
