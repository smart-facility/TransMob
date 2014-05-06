package core.preprocessing.geotools;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import core.HardcodedData;

/**
 * A CRS helper class for switching the format of geometry between MGA56 and
 * WGS56.
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 * 
 */
public class CRSHelper {

    private static final Logger logger = Logger.getLogger(CRSHelper.class);

	static {
		try {
			crsMga56 = CRS.decode("EPSG:28356");
			crsWgs84 = CRS.decode("EPSG:4326");
			// crsGoogle=CRS.decode("EPSG:3785");
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			logger.error("Exception caught", e);
		}
	}

    /**
     * Private constructor.
     */
    private CRSHelper() {
        // Private constructor.
    }

    /**
	 * Transforms a well-known text in WGS84 format to a geometry in MGA56
	 * format.
	 * 
	 * @param wgs84WKT
	 *            the well-known text in WGS84 format.
	 * @return the Geometry in MGA56 format transformed from WGS84 format.
	 */
	public static Geometry toMga56(String wgs84WKT) {
		Geometry targetGeometry = null;
		try {
            MathTransform transform = CRS.findMathTransform(crsWgs84, crsMga56);
			WKTReader fromText = new WKTReader();
            Geometry sourceGeometry = fromText.read(wgs84WKT);
			sourceGeometry.setSRID(HardcodedData.streetWGS84SRID);
			targetGeometry = JTS.transform(sourceGeometry, transform);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (TransformException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		}
		return targetGeometry;
	}

	/**
	 * Transforms a well-known text in MGA56 format to a geometry in WGS84
	 * format.
	 * 
	 * @param mga56WKT
	 *            the well-known text in MGA56 format.
	 * @return the Geometry in WGS84 format transformed from MGA56 format.
	 */
	public static Geometry toWgs84(String mga56WKT) {
		Geometry targetGeometry = null;
		try {
            MathTransform transform = CRS.findMathTransform(crsMga56, crsWgs84);
			WKTReader fromText = new WKTReader();
			Geometry sourceGeometry = fromText.read(mga56WKT);
			targetGeometry = JTS.transform(sourceGeometry, transform);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		} catch (TransformException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		}
		return targetGeometry;
	}

    /**
     * Transforms a well-known text in MGA56 format to a geometry in WGS84
     * format.
     *
     * @param mga56WKT
     *            the well-known text in MGA56 format.
     * @return the Geometry in WGS84 format transformed from MGA56 format.
     */
    public static Geometry toWgs84(Geometry mga56WKT) {
        Geometry targetGeometry = null;
        try {
            MathTransform transform = CRS.findMathTransform(crsMga56, crsWgs84);
            targetGeometry = JTS.transform(mga56WKT, transform);
        } catch (FactoryException e) {
            // TODO Auto-generated catch block
            logger.error("Exception caught", e);
        } catch (TransformException e) {
            // TODO Auto-generated catch block
            logger.error("Exception caught", e);
        }
        return targetGeometry;
    }

	/**
	 * Swaps the latitude and longitude of single MultiLineString.
	 * 
	 * @param source
	 * @return
	 */
	public static String swap(String source) {
		WKTReader fromText = new WKTReader();
		Geometry sourceGeometry = null;
		try {
			sourceGeometry = fromText.read(source);

            Coordinate[] coordinates = sourceGeometry.getCoordinates();

            for (Coordinate coordinate : coordinates) {
                double temp = coordinate.x;
                coordinate.x = coordinate.y;
                coordinate.y = temp;
            }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
            logger.error("Exception caught", e);
		}

		return sourceGeometry.toString();
	}

	/**
	 * name="crsMga56"
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private static CoordinateReferenceSystem crsMga56;
	/**
	 * name="crsWgs84"
	 * 
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private static CoordinateReferenceSystem crsWgs84;
}
