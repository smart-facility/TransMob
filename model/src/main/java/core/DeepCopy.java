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
