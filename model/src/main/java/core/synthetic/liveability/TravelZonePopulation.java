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
package core.synthetic.liveability;

import core.ApplicationContextHolder;
import hibernate.postgis.TravelZonesRandwickGSDAO;
import hibernate.postgis.TravelZonesRandwickGSEntity;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class calculates travel zone population
 * 
 * @author qun
 * 
 */
public class TravelZonePopulation {

    private static final Logger logger = Logger.getLogger(TravelZonePopulation.class);

	private final TravelZonesRandwickGSDAO travelZonesRandwickGSDAO;
	private Map<Integer, Integer> populationIn500Radius;

	public TravelZonePopulation() {
		// TODO Auto-generated constructor stub

		travelZonesRandwickGSDAO = ApplicationContextHolder.getBean(TravelZonesRandwickGSDAO.class);
		populationIn500Radius = new HashMap<>();

	}

	/**
	 * Gets population by density in travel zones than project to a 500m circle
	 * 
	 * @param travelZonePopulation
	 * @return
	 */
	public Map<Integer, Integer> get500MPopulation(
			Map<Integer, Integer> travelZonePopulation) {
		double circleArea = 500 * 500 * Math.PI;
		double ratio;
		double travelArea;
		int travelZoneId;
		int population;

		for (Map.Entry<Integer, Integer> entry : travelZonePopulation
				.entrySet()) {

			travelZoneId = entry.getKey();
			logger.trace(travelZoneId);

			List<TravelZonesRandwickGSEntity> findByTz06 = travelZonesRandwickGSDAO.findByTz06(travelZoneId);
			if (!findByTz06.isEmpty()) {
				travelArea = findByTz06.get(0).getAreaSqm().doubleValue();
				ratio = circleArea / travelArea;

				population = (int) (ratio * entry.getValue());

				populationIn500Radius.put(travelZoneId, population);
			} else {
				logger.warn("Missing travel zone id: " + travelZoneId);
				populationIn500Radius.put(travelZoneId, 1);
			}
		}

		return populationIn500Radius;
	}

	public Map<Integer, Integer> getNewPopulation() {
		return populationIn500Radius;
	}

	@Override
	public String toString() {
		return "TravelZonePopulation [travelZonesRandwickGSDAO="
				+ travelZonesRandwickGSDAO + ", populationIn500Radius="
				+ populationIn500Radius + "]";
	}

}
