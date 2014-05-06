package core.synthetic.traveldiary;

public enum ActivityLocationColumns {
	FacilityId_Col(0),
	ActivityID_Col(1),
	Type_Col(2),
	HospotId_Col(3),
	NoteBus_Col(4),
	NoteTrain_Col(5),
	TravelZone_Col(6),
	NoteEntry_Col(7),
	;
    
    private final int value;
    
    /**
     * Private constructor.
     * @param value
     */
    private ActivityLocationColumns(int value) {
        this.value = value;
    }
    
    public int getIntValue() {
        return value;
    }

}
