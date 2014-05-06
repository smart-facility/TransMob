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
