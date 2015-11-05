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
/**
 * 
 */
package core.preprocessing.geotools;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * 
 */
public class CRSHelperTest {

	/**
	 *  name="logger"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
    private static final Logger logger = Logger.getLogger(CRSHelper.class);


	/**
	 * Test method for
	 * {@link core.preprocessing.geotools.CRSHelper#toMga56(java.lang.String)}.
	 */
	@Test @Ignore
	public void testToMga56() {
		String mga56 = "POINT (338261.5 6238639.2)";
		String wgs84 = "POINT (151.24909999999977 -33.98036899912515)";
		WKTReader fromText = new WKTReader();
		try {
			logger.debug("fromText.read(mga56): " + fromText.read(mga56));
			logger.debug("crsHelper.toMga56(wgs84): "
					+ CRSHelper.toMga56(CRSHelper.swap(wgs84)));
			assertEquals(
					0,
					CRSHelper.toMga56(CRSHelper.swap(wgs84)).distance(
							fromText.read(mga56)), 0.1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link core.preprocessing.geotools.CRSHelper#toWgs84(java.lang.String)}.
	 */
	@Test @Ignore
	public void testToWgs84() {
		String mga56 = "POINT (337985.0	6238653.2)";
		String wgs84 = "POINT (0 0)";
		WKTReader fromText = new WKTReader();
		try {
			logger.debug("fromText.read(wgs84): " + fromText.read(wgs84));
			logger.debug("crsHelper.toWgs84(mga56): "
					+ CRSHelper.toWgs84(mga56));
			assertEquals(0,
					CRSHelper.toWgs84(mga56).distance(fromText.read(wgs84)),
					0.001);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link core.preprocessing.geotools.CRSHelper#swap(java.lang.String)} .
	 */
	@Test @Ignore
	public void testSwap() {
		String wgs84 = "POINT (12 34)";
		logger.debug(CRSHelper.swap(wgs84));
		assertEquals("POINT (34 12)", CRSHelper.swap(wgs84));
	}

}
