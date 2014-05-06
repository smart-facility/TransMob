package core.synthetic.household;

import core.HardcodedData;
import core.synthetic.HouseholdPool;
import core.synthetic.dwelling.DwellingAllocator;
import core.synthetic.individual.Individual;
import core.synthetic.travel.mode.TravelModes;
import core.synthetic.traveldiary.TravelDiaryColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for setting up the vehicle list for the model and for amending the vehicle IDs for the households.
 */
public class VehicleManager {

    /**
     * Private constructor for utility class.
     */
    private VehicleManager() {
        // Utility class private constructor.
    }

    /**
     * amendvehicleID revised 19 March 2013 by NH
     * @param scenarioName The name of the scenario being run.
     * @param households The current household pool.
     *
     */
    public static void amendVehicleID(String scenarioName, HouseholdPool households) {
        /*
         * Travel Diaries' columns are
         * [travel_id][individual_id][household_id][age][gender][income]
         * [origin][destination][start_time][end_time][duration]
         * [travel_mode][purpose][vehicle_id][trip_id]
         */
        int modeCarDriver = TravelModes.CarDriver.getIntValue();
        int modeTaxi = TravelModes.Taxi.getIntValue();
        int modeBus = TravelModes.Bus.getIntValue();
        int modeTrain = TravelModes.LightRail.getIntValue();
        int colMode = TravelDiaryColumns.TravelMode_Col.getIntValue();
        int colVehID = TravelDiaryColumns.VehicleID_Col.getIntValue();

        for (Household hhold : households.getHouseholds().values()) {
            int nCarThisHhold = 0;
            for (Individual individual : hhold.getResidents()) {
                int [][] travelDiary = individual.getTravelDiariesWeekdays();

                // if this individual doesn't have travel diaries
                if (travelDiary == null) {
                    continue;
                }

                int newCarID = -1;
                int newTaxiID = -1;


                for (int[] trip : travelDiary) {
                    // NOT set location for travel diary does NOT travel (stay
                    // at home)
                    if (trip[6] == -1 // origin
                            || trip[7] == -1 // destination
                            || trip[8] == -1 // start_time
                            || trip[9] == -1 // end_time
                            || trip[10] == -1 // duration
                            || trip[11] == -1 // travel_mode
                    ) {
                        continue;
                    }

                    // if the mode of this trip is CarDriver or Taxi and number
                    // of vehicles used by this households is >= 9,
                    // then switch the mode of this trip to public transport
                    if ((trip[colMode] == modeCarDriver || trip[colMode] == modeTaxi) && nCarThisHhold > 9) {
                        trip[colVehID] = 0; /* vehicelID */
                        if ("base".equals(scenarioName)) {
                            trip[colMode] = modeBus;
                        } else {
                            trip[colMode] = HardcodedData.random
                                    .nextInt(2) == 0 ? modeBus : modeTrain;
                        }
                        continue;
                    }

                    // if the mode is CarDriver
                    if (trip[colMode] == modeCarDriver) {
                        if (newCarID == -1) {
                            nCarThisHhold += 1;
                            newCarID = nCarThisHhold;
                        }
                        // set vehicleID for this trip to newCarID
                        trip[colVehID] = newCarID;
                        continue;
                    }

                    // else if the mode is Taxi
                    if (trip[colMode] == modeTaxi) {
                        nCarThisHhold += 1;
                        newTaxiID = nCarThisHhold;

                        // set vehicleID for this trip to newTaxiID
                        trip[colVehID] = newTaxiID;
                    }
                }
            }
        }
    }

    /**
     * Create a list of vehicle location and assume 2 cars (type=1, subtype=0
     * and subtype=1) and 7 taxis (type=1, subtype=2) can park in the household
     * location
     *
     * @param dwellingAllocator The dwelling allocator.
     * @param households The current household pool.
     * @return an array list of Vehicles retrieved from database
     *
     */
    public static List<Vehicle> createVehicleList(DwellingAllocator dwellingAllocator, HouseholdPool households) {

        List<Vehicle> vehicleLocation = new ArrayList<>();

        for (Household household : households.getHouseholds().values()) {

            for (int i = 0; i < 9; i++) {
                Vehicle vehicle = new Vehicle();

                vehicle.setVehicleId(i + 1); /* vehicle */
                vehicle.sethHold(household.getId()); /* household */

                /* location */
                vehicle.setLocation(dwellingAllocator.getDwellingPool()
                        .getDwellings().get(household.getDwellingId())
                        .getActivityLocation());

                vehicle.setType(1); /* type */
                vehicle.setSubType(1); /* subtype */

                vehicleLocation.add(vehicle);
            }
        }

        return vehicleLocation;
    }
}
