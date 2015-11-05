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
package core;

/**
 * Enumerated type for facilities.
 */
public enum FacilityType {

//    AIRPORT("airport", 0), /* OUT OF STUDY AREA */
    AMBULANCE_STATION("ambulance_station", 1),
    APARTMENTS("apartments", 2),
    AUSTRALIA_POST("australila_post", 3),
    BANK("bank", 4),
    BAR("bar", 5),
    BAYS_AND_BEACHES("bays_and_beaches", 6),
//    BOAT_RAMP("boat_ramp", 7), /* OUT OF STUDY AREA */
    BOWLING_CLUB("bowling_club", 8),
//    BUNNINGS("bunnings", 9), /* OUT OF STUDY AREA */
    BUS_STATION("bus_station", 10),
    BUSINESS_PARK("business_park", 11),
    CAFE("cafe", 12),
    CARPARK("carpark", 13),
    CEMETARY("cemetery", 14),
    CHILD_CARE_CENTRE("child_care_centre", 15),
    CHURCH("church", 16),
    CLUB("club", 17),
    EDUCATION("education", 18),
    EDUCATION_PRIMARY("education_primary", 19),
    EDUCATION_SCHOOL("education_school", 20),
    EDUCATION_UNIVERSITY("education_university", 21),
    FAST_FOOD_OUTLET("fast_food_outlet", 22),
    FIRE_STATION("fire_station", 23),
    GOLF("golf", 24),
    GUIDES_HALL("guides_hall", 25),
    HEALTH("health", 26),
    HOSPITAL("hospital", 27),
    HOTEL("hotel", 28),
    KINDERGARTEN("kindergarten", 29),
    LIBRARY("library", 30),
    LIGHT_RAIL_STATION("light_rail_station", 31),
    LOCAL_GOVERNMENT("local_government", 32),
    MOTOR_REGISTRY("motor_registry", 33),
    NEWSAGENCY("newsagency", 34),
    NURSING_HOME("nursing_home", 35),
    OTHER("other", 36),
    PARK("park", 37),
    PLACES_INTEREST("places_interest", 38),
    POLICE_STATION("police_station", 39),
    RACECOURSE("racecourse", 40),
    RAILWAY_STATION("railway_station", 41),
    RESERVE("reserve", 42),
//    RESTAURANT("restaurant", 43), /* OUT OF STUDY AREA */
    RETIREMENT_VILLAGE("retirement_village", 44),
    SCOUT("scout", 45),
    SERVICE_STATION("service_station", 46),
    SHOPPING_CENTRE("shopping_centre", 47),
//    SPEEDCAM("speedcam", 48),
    SPORTS("sports", 49),
    SUPERMARKET("supermarket", 50),
    SWIMMING("swimming", 51),
    THEATRE("theatre", 52),
    WEIGHBRIDGE("weighbridge", 53);


    private final String name;
    private final int value;


    private FacilityType(String name, int value) {
        this.name = name;
        this.value = value;

    }

    /**
     * Return the TravelMode enum that matches the TravelMode string.
     * @param modeString TravelMode string
     * @return The matching TravelMode.
     */
    public static FacilityType getFacilityType(String modeString) {
        for (FacilityType mode : FacilityType.values()) {
            if (mode.name.equalsIgnoreCase(modeString.trim())) {
                return mode;
            }
        }

        return OTHER;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
