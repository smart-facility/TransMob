package hibernate;

import hibernate.postgres.TransimsHouseholdLocationDAO;
import hibernate.postgres.TransimsHouseholdLocationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import core.ApplicationContextHolder;
import core.HardcodedData;
import core.synthetic.dwelling.DwellingControl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A class for data accessing and operating through Hibernare.
 * 
 * @author <a href="mailto:qun@uow.edu.au">Qun CHEN</a><br>
 *         qun@uow.edu.au
 *
 *         TODO JH. This class should be refactored out to appropriate class for managing household locations.
 *         HibernateUtility is not an appropriate class name.
 */
public class HibernateUtility {

	private static final Logger logger = Logger.getLogger(HibernateUtility.class);
	private TransimsHouseholdLocationDAO transimsHouseholdLocationDAO;

	public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContextHolder holder = ctx.getBean(ApplicationContextHolder.class);

        HibernateUtility hibernateUtility = new HibernateUtility();
		DwellingControl dwellingControl = new DwellingControl();

		// ===========update household id in the transims_household_location
        HardcodedData.prepend();

        hibernateUtility.clearTransimsHouseholdLocationBonding();
		Map<Integer, ArrayList<TransimsHouseholdLocationEntity>> tzTransimsHouseholdLocation = hibernateUtility
				.getTZTransimsHouseholdLocation();
		for (int i = HardcodedData.START_YEAR; i <= 2025; i++) {

			hibernateUtility.setTransimsHouseholdLocation(i, tzTransimsHouseholdLocation, "dwelling", dwellingControl);
		}
	}







	public void clearTransimsHouseholdLocationBonding() {

		transimsHouseholdLocationDAO = ApplicationContextHolder.getBean(TransimsHouseholdLocationDAO.class);
		transimsHouseholdLocationDAO.clearTransimsHouseholdLocationBonding();
	}

	public void setTransimsHouseholdLocation(int year,
			Map<Integer, ArrayList<TransimsHouseholdLocationEntity>> tzLocations,
			String schema, DwellingControl dwellingControl) {

		dwellingControl.getLimit(year, schema);

		DwellingControl dwellingControlPrevious = new DwellingControl();

		if (year != HardcodedData.START_YEAR) {
			dwellingControlPrevious.getLimit(year - 1, true);
		}

		Transaction transaction = transimsHouseholdLocationDAO.getCurrentSession()
				.beginTransaction();

		for (Entry<Integer, ArrayList<TransimsHouseholdLocationEntity>> entry : tzLocations
				.entrySet()) {

			int tzID = entry.getKey();
			ArrayList<TransimsHouseholdLocationEntity> transimsHouseholdLocations = entry
					.getValue();

			Collections.sort(transimsHouseholdLocations, new IdComparator());
			logger.debug(transimsHouseholdLocations.size() + ","
					+ transimsHouseholdLocations.get(0).getDwellingIndex());
			// System.exit(0);

			int oneBedroom = 0;
			int twoBedroom = 0;
			int threeBedroom = 0;
			int fourBedroom = 0;
			if (year != HardcodedData.START_YEAR) {
				int[] is = dwellingControlPrevious
						.getCurrentDwellingsCapacity().get(tzID);
				if (is == null) {
					continue;
				}
				oneBedroom = is[0];
				twoBedroom = is[1];
				threeBedroom = is[2];
				fourBedroom = is[3];
			}

			if (dwellingControl.getCurrentDwellingsCapacity().get(tzID) == null) {
				continue;
			}

			int numberOfOneBedroom = dwellingControl.getCurrentDwellingsCapacity().get(tzID)[0]- oneBedroom;

			int numberOfTwoBedroom = dwellingControl.getCurrentDwellingsCapacity().get(
					tzID)[1]
					- twoBedroom;
			int numberOfThreeBedroom = dwellingControl.getCurrentDwellingsCapacity().get(
					tzID)[2]
					- threeBedroom;
			int numberOfFourBedroom = dwellingControl.getCurrentDwellingsCapacity().get(
					tzID)[3]
					- fourBedroom;

			logger.debug("TZ: " + tzID + "--" + numberOfOneBedroom + "," + numberOfTwoBedroom + ","
					+ numberOfThreeBedroom + "," + numberOfFourBedroom);

			int count = 1;

			// Collections.shuffle(transimsHouseholdLocations,
			// HardcodedData.random);

			for (TransimsHouseholdLocationEntity transimsHouseholdLocation : transimsHouseholdLocations) {

				if (count > numberOfOneBedroom) {
					break;
				}

				if ((transimsHouseholdLocation.getYearAvailable()) == 0) {

					transimsHouseholdLocation.setYearAvailable(year);
					transimsHouseholdLocation.setBedroomsNumber(1);
					transimsHouseholdLocationDAO.attachDirty(transimsHouseholdLocation);
					count++;

				}
			}

			logger.debug("finish 1 bedroom");

			count = 1;
			for (TransimsHouseholdLocationEntity transimsHouseholdLocation : transimsHouseholdLocations) {

				if (count > numberOfTwoBedroom) {
					break;
				}

				if (transimsHouseholdLocation.getYearAvailable() == 0) {

					count++;
					transimsHouseholdLocation.setYearAvailable(year);
					transimsHouseholdLocation.setBedroomsNumber(2);
					transimsHouseholdLocationDAO
							.attachDirty(transimsHouseholdLocation);

				}
			}

			logger.debug("finish 2 bedroom");

			count = 1;
			for (TransimsHouseholdLocationEntity transimsHouseholdLocation : transimsHouseholdLocations) {

				if (count > numberOfThreeBedroom) {
					break;
				}
				if (transimsHouseholdLocation.getYearAvailable() == 0) {

					count++;
					transimsHouseholdLocation.setYearAvailable(year);
					transimsHouseholdLocation.setBedroomsNumber(3);
					transimsHouseholdLocationDAO
							.attachDirty(transimsHouseholdLocation);

				}
			}

			logger.debug("finish 3 bedroom");

			count = 1;
			for (TransimsHouseholdLocationEntity transimsHouseholdLocation : transimsHouseholdLocations) {

				if (count > numberOfFourBedroom) {
					break;
				}
				if (transimsHouseholdLocation.getYearAvailable() == 0) {

					count++;
					transimsHouseholdLocation.setYearAvailable(year);
					transimsHouseholdLocation.setBedroomsNumber(4);
					transimsHouseholdLocationDAO
							.attachDirty(transimsHouseholdLocation);
				}

			}
		}
		transaction.commit();
		transimsHouseholdLocationDAO.getCurrentSession().close();
	}


	public Map<Integer, ArrayList<TransimsHouseholdLocationEntity>> getTZTransimsHouseholdLocation() {

		Map<Integer, ArrayList<TransimsHouseholdLocationEntity>> tzLocations = new HashMap<Integer, ArrayList<TransimsHouseholdLocationEntity>>();

		for (int tzID : HardcodedData.travelZonesLiveable) {
			ArrayList<TransimsHouseholdLocationEntity> transimsHouseholdLocations = (ArrayList<TransimsHouseholdLocationEntity>) transimsHouseholdLocationDAO
					.findByTravelZoneId(tzID);
			if (transimsHouseholdLocations != null) {
				tzLocations.put(tzID, transimsHouseholdLocations);
			}

		}

		return tzLocations;
	}

}