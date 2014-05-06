package core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
public class DeepCopyTest implements Serializable {

    private static final Logger logger = Logger.getLogger(DeepCopyTest.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Room room1, room2;
	private House house1;

	@Before
	public void setUp() throws Exception {

		room1 = new Room();
		room2 = new Room();
		house1 = new House();

		room1.roomID = 1;
		room2.roomID = 2;
		house1.houseName = "a";
		house1.rooms = new ArrayList<Room>(2);
		house1.rooms.add(room1);
		house1.rooms.add(room2);

	}


	@Test
	public void testCopyBySerialize() {

		House house2 = (House) DeepCopy.copyBySerialize(house1);
		house2.houseName = "b";
		house2.rooms.remove(0);
		assertEquals("a", house1.houseName);
		assertEquals(2, house1.rooms.size());
		//
        assertEquals("b", house2.houseName);
		assertEquals(1, house2.rooms.size());
		//
		HashMap<Integer, String> hashMap1 = new HashMap<Integer, String>();
		//
		String aString = "a";
		String bString = "b";
		String cString = "c";
		//
		hashMap1.put(1, aString);
		hashMap1.put(2, bString);
		//
		HashMap<Integer, String> hashMap2;
		hashMap2 = (HashMap<Integer, String>) DeepCopy.copyBySerialize(hashMap1);
		//
		logger.debug(hashMap1.toString());
		logger.debug(hashMap2.toString());
		//
		assertTrue(hashMap2.get(1).equals(aString));
		assertTrue(hashMap2.get(2).equals(bString));
		assertTrue(hashMap1.get(1).equals(aString));
		assertTrue(hashMap1.get(2).equals(bString));
		//
		hashMap2.put(1, cString);
		logger.debug(hashMap1.toString());
		logger.debug(hashMap2.toString());
		assertTrue(hashMap2.get(1).equals(cString));
		assertTrue(hashMap2.get(2).equals(bString));
		assertTrue(hashMap1.get(1).equals(aString));
		assertTrue(hashMap1.get(2).equals(bString));

	}

	public class Room implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int roomID;
	}

	public class House implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String houseName;
		public ArrayList<Room> rooms;
	}
}
