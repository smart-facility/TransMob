package core.synthetic.liveability;

import hibernate.postgis.TravelZonesFacilitiesEntity;

import java.util.Comparator;
import java.util.Map;

/**
 * Compares travel zones by prices
 * 
 * @author qun
 * 
 */
public class ValueComparator implements Comparator<Object> {

	private final Map<Integer, Double> baseMap;

	private final Map<Integer, TravelZonesFacilitiesEntity> travelZonesFacilitiesMap;

	private final int numberOfBedrooms;

	public ValueComparator(Map<Integer, Double> baseMap,
			Map<Integer, TravelZonesFacilitiesEntity> travelZonesFacilitiesMap,
			int numberOfBedrooms) {
		super();
		this.baseMap = baseMap;
		this.travelZonesFacilitiesMap = travelZonesFacilitiesMap;
		this.numberOfBedrooms = numberOfBedrooms;
	}

	/**
	 * compare 2 dwelling according to their prices]
	 * 
	 * @param arg0
	 *            first dwelling
	 * @param arg1
	 *            second dwelling
	 * @return compare results
	 */
	@Override
	public int compare(Object arg0, Object arg1) {

		double value1 = 0, value2 = 0;

		value1 = this.baseMap.get(arg0);
		value2 = this.baseMap.get(arg1);
		// TODO Auto-generated method stub
		if (value1 < value2) {
			return 1;
		} else if (value1 == value2) {
			TravelZonesFacilitiesEntity travelZonesFacilities1 = this.travelZonesFacilitiesMap
					.get(arg0);
			TravelZonesFacilitiesEntity travelZonesFacilities2 = this.travelZonesFacilitiesMap
					.get(arg1);

			double price1 = 0, price2 = 0;

			switch (this.numberOfBedrooms) {
			case 1:
				price1 = travelZonesFacilities1.getPrice(true, 1);
				price2 = travelZonesFacilities2.getPrice(true, 1);

				break;

			case 2:

				price1 = travelZonesFacilities1.getPrice(true, 2);
				price2 = travelZonesFacilities2.getPrice(true, 2);
				break;

			case 3:
				price1 = travelZonesFacilities1.getPrice(true, 3);
				price2 = travelZonesFacilities2.getPrice(true, 3);
				break;
			case 4:
				price1 = travelZonesFacilities1.getPrice(true, 4);
				price2 = travelZonesFacilities2.getPrice(true, 4);
				break;

			}

			if (price1 < price2) {

				return -1;
			} else {
				return 1;
			}

		} else {
			return -1;
		}
	}

}
