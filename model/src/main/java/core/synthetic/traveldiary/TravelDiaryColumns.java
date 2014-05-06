package core.synthetic.traveldiary;

public enum TravelDiaryColumns {
	    TravelID_Col(0),
		IndividualID_Col(1),
		HouseholdID_Col(2),
		Age_Col(3),
		Gender_Col(4),
		Income_Col(5),
		Origin_Col(6),
		Destination_Col(7),
		StartTime_Col(8),
		EndTime_Col(9),
		Duration_Col(10),
		TravelMode_Col(11),
		Purpose_Col(12),
		VehicleID_Col(13),
		TripID_Col(14),
		;
	    
	    private final int value;
	    
	    /**
	     * Private constructor.
	     * @param value
	     */
	    private TravelDiaryColumns(int value) {
	        this.value = value;
	    }
	    
	    public int getIntValue() {
	        return value;
	    }

}
