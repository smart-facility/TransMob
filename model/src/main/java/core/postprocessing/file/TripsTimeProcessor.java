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
package core.postprocessing.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import core.DeepCopy;

/**
 * A class read and process work trips time information
 * 
 * @author qun
 * 
 */
public class TripsTimeProcessor {

	private final LinkInTravelZone linkInTravelZone;

	public TripsTimeProcessor(String linkListFilePath) {
		super();
		linkInTravelZone = LinkInTravelZone.getInstance(linkListFilePath);
	}

	public Map<Integer, Double> calculateNormalizedTravelZoneAverageTripsTime(
			Map<Integer, List<Double>> linksTripsTime) {
		return normalise(getTravelZoneAverageTripsTime(getTravelZoneAllTripsTime(linksTripsTime)));
	}

	/**
	 * In replace of the congestion value for a travel zone, we compute the
	 * average over all of the travel times for trips with a work purpose,
	 * averaged over the travel zones. S 2b. Store these results in the
	 * database, for later use. 3. Find the maximum average travel time by TZ
	 * and the minimum, then normalise results by calculating (maximum - average
	 * for TZ)/(maximum - minimum)
	 * 
	 * @param travelZoneAverageTripsTime
	 * @return
	 */
	public Map<Integer, Double> normalise(
			Map<Integer, Double> travelZoneAverageTripsTime) {

		Map<Integer, Double> normalizedTravelZoneAverageTripsTime = null;
		normalizedTravelZoneAverageTripsTime = (Map<Integer, Double>) DeepCopy
				.copyBySerialize(travelZoneAverageTripsTime);

		double max = Collections.max(normalizedTravelZoneAverageTripsTime.values());

		double min = Collections.min(normalizedTravelZoneAverageTripsTime
				.values());

		for (Entry<Integer, Double> tripsTime : normalizedTravelZoneAverageTripsTime
				.entrySet()) {
			double time = tripsTime.getValue();
			time = (max - time) / (max - min);
			normalizedTravelZoneAverageTripsTime.put(tripsTime.getKey(), time);
		}

		return normalizedTravelZoneAverageTripsTime;

	}


	public Map<Integer, List<Double>> getTravelZoneAllTripsTime(
			Map<Integer, List<Double>> linksTripsTime) {

		Map<Integer, List<Double>> travelZoneTripsTime = new HashMap<Integer, List<Double>>();

		int linkId;
		List<Double> tripsTime;

		for (Entry<Integer, List<Double>> entry : linksTripsTime.entrySet()) {
			linkId = entry.getKey();
			tripsTime = entry.getValue();

			List<Integer> travelZoneIds = linkInTravelZone
					.getLinkInTravelZoneMap().get(linkId);

			if (travelZoneIds != null) {
				for (Integer travelZoneId : travelZoneIds) {

					List<Double> storedTripsTime = travelZoneTripsTime
							.get(travelZoneId);

					if (storedTripsTime != null) {
						storedTripsTime.addAll(tripsTime);
						travelZoneTripsTime.put(travelZoneId, storedTripsTime);
					} else {
						// List<Double> temp = new ArrayList<Double>();
						// temp.add(tripsTime);
						travelZoneTripsTime.put(travelZoneId, tripsTime);
					}
				}
			}
		}

		return travelZoneTripsTime;
	}

	public Map<Integer, Double> getTravelZoneAverageTripsTime(
			Map<Integer, List<Double>> travelZoneTripsTime) {
		Map<Integer, Double> travelZoneAverageTripsTime = new HashMap<Integer, Double>();

		double averageCongestion;

		for (Entry<Integer, List<Double>> entry : travelZoneTripsTime
				.entrySet()) {
			averageCongestion = 0;

			// travelZoneId = entry.getKey();
			// result[i][0] = travelZoneId;

			for (Double d : entry.getValue()) {
				averageCongestion += d;
			}

			averageCongestion /= entry.getValue().size();

			// result[i][1] = averageCongestion;

			travelZoneAverageTripsTime.put(entry.getKey(), averageCongestion);

			// i++;
		}

		return travelZoneAverageTripsTime;
	}

}
