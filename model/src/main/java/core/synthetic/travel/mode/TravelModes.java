package core.synthetic.travel.mode;

public enum TravelModes {
    Walk(1, "Walk", "WALK"),
	CarDriver(2, "Car driver", "CAR_DRIVER"),
    Bus(3, "Bus", "BUS"),
    LightRail(4, "Light rail", "LIGHTRAIL"),
    Bike(7, "Bycycle", "BICYCLE"),
    CarPassenger(11, "Car passenger", "CAR_PASSENGER"),
    Taxi(12, "Taxi", "TAXI"),
    Other(8, "Other", "OTHER");


    private final int value;
    private final String internalName;
    private final String transimsName;


    /**
     * Private constructor.
     * @param value
     * @param name
     */
    private TravelModes(int value, String name, String transimsName) {
        this.value = value;
        this.internalName = name;
        this.transimsName = transimsName;
    }

    /**
     * Return the TravelMode enum that matches the TravelMode integer.
     * @param modeInt TravelMode integer
     * @return The matching TravelMode.
     */
    public static TravelModes classify(int modeInt) {
        for (TravelModes mode : TravelModes.values()) {
            if (mode.value == modeInt) {
                return mode;
            }
        }

        return Other;
    }

    /**
     * Return the TravelMode enum that matches the TravelMode string.
     * @param modeString TravelMode string
     * @return The matching TravelMode.
     */
    public static TravelModes getTravelMode(String modeString) {
        for (TravelModes mode : TravelModes.values()) {
            if (mode.internalName.equalsIgnoreCase(modeString.trim())) {
                return mode;
            }
        }

        return Other;
    }

    /**
     * Return the TravelMode enum that matches the TravelMode TRANSSIMS string.
     * @param modeString TravelMode string
     * @return The matching TravelMode.
     */
    public static TravelModes getTravelModeFromTransims(String modeString) {
        for (TravelModes mode : TravelModes.values()) {
            if (mode.transimsName.equalsIgnoreCase(modeString.trim())) {
                return mode;
            }
        }

        return Other;
    }

    public int getIntValue() {
        return value;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getTranssimsName() {
        return transimsName;
    }

    /**
     * @param mode
     * @return the Travel mode code according to version 4 TRANSIMS' Trips mode:
     *         1 = Walk / 2 = Drive alone / 3 = Transit (bus) / 4 = Transit
     *         (rail) / 5 = Park-&-ride outbound / 6 = Park-&-ride inbound / 7 =
     *         Bicycle / 8 = Magic move / 9 = School bus / 10 = 2 person carpool
     *         11 = 3 person carpool (carpassenger) / 12 = 4+ person carpool
     *         (taxi)/ 13 = Kiss-&-ride outbound / 14 = Kiss-&-ride inbound
     *
     * @author Vu Lam Cao
     */
    public static int getTravelModeCode(String mode) {

        mode = mode.toLowerCase().trim();
        mode = mode.replace(" ", "_");
        mode = mode.replace("-", "_");
        mode = mode.replace("/", "_");
        mode = mode.replace("___", "_");

        if ("carpassenger".equals(mode)) {
            return TravelModes.CarPassenger.getIntValue();
        }

        for (TravelModes travelMode : TravelModes.values()) {
            if (travelMode.transimsName.equalsIgnoreCase(mode)) {
                return travelMode.getIntValue();
            }
        }

        return TravelModes.Other.getIntValue();
    }

}
