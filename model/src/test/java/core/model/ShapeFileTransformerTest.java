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
package core.model;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>qun@uow.edu.au
 *
 */
public class ShapeFileTransformerTest {

	/**
	 *   name="shapeFileTransformer"
	 * @uml.associationEnd  
	 */
	private ShapeFileTransformer shapeFileTransformer;
	/**
	 *   name="shapeFilePath"
	 */
	private String shapeFilePath;
	/**
	 *   name="batchPath"
	 */
	private String batchPath;
	/**
	 *   name="tableName"
	 */
	private String tableName;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		shapeFilePath="C:/Documents and Settings/qun/Desktop/";
		batchPath="C:/Temp/pgsql2shp.bat";
		tableName="streets_mga56";
		shapeFileTransformer=new ShapeFileTransformer(shapeFilePath, batchPath, tableName);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		shapeFileTransformer=null;
	}

	/**
	 * Test method for {@link core.model.ShapeFileTransformer#run()}.
	 */
	@Test @Ignore
	public void testRun() {
		shapeFileTransformer.run();
		assertTrue(new File(shapeFilePath+tableName+".shp").exists());
	}

}
