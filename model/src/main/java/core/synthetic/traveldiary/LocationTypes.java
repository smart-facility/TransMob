package core.synthetic.traveldiary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.ApplicationContextHolder;
import core.FacilityType;
import hibernate.postgres.FacilityCategoryDAO;
import hibernate.postgres.FacilityCategoryEntity;
import org.apache.log4j.Logger;

/**
 * A class which lists locations/facilities associated with each purpose
 * 
 * @author ageorge
 * 
 */
public class LocationTypes {

    private static final Logger logger = Logger.getLogger(LocationTypes.class);
    private final FacilityCategoryDAO facilityCategoryDao;

    /**
	 * The key is the purpose and values are list of facilities associated with
	 * that purpose
	 */
	private Map<Integer, ArrayList<FacilityType>> purposeLocations;

	public LocationTypes() {
		this.purposeLocations = new HashMap<>();
        this.facilityCategoryDao = ApplicationContextHolder.getBean(FacilityCategoryDAO.class);
	}

	public Map<Integer, ArrayList<FacilityType>> getPurposeLocations() {
		return this.purposeLocations;
	}

	/**
	 * get location types from database
	 */
	public void readLocationTypes() {

        List<FacilityCategoryEntity> facilityCategories = facilityCategoryDao.findAll();

        for (FacilityCategoryEntity facilityCategory : facilityCategories) {
            String category = facilityCategory.getCategory();
            int purpose = getPurposeCodeHTS(category);

            FacilityType facility = facilityCategory.retrieveFacilityAsType();

            if (!purposeLocations.containsKey(purpose)) {
                purposeLocations.put(purpose, new ArrayList<FacilityType>());
                logger.trace("Adding purpose: " + purpose);
            }

            purposeLocations.get(purpose).add(facility);
            logger.trace("Adding facility type: " + facility.getName() + " to purpose: " + purpose);
        }

	}


    /** FIXME JH - Make this an enum
    // ========================================================================================================
    /**
     * Get the purpose code according to HTS purpose format
     *
     * @param purpose
     * @return HTS purpose code as 1 Change mode 2 Home 3 Go to work 4 Return to
     *         work 5 Work related business 6 Education 7 Shopping 8 Personal
     *         business/services 9 Social/recreation 10 Serve passenger 11 Other
     *
     * @author Vu Lam Cao
     */
    private int getPurposeCodeHTS(String purpose) {
        int purposeCode = 2;

        purpose = purpose.toLowerCase().trim();
        purpose = purpose.replace(" ", "_");
        purpose = purpose.replace("-", "_");
        purpose = purpose.replace("/", "_");
        purpose = purpose.replace("___", "_");

        switch (purpose) {
            case "change_mode":
                purposeCode = 1;
                break;
            case "home":
                purposeCode = 2;
                break;
            case "go_to_work":
                purposeCode = 3;
                break;
            case "return_to_work":
                purposeCode = 4;
                break;
            case "work_related_business":
                purposeCode = 5;
                break;
            case "education":
                purposeCode = 6;
                break;
            case "shopping":
                purposeCode = 7;
                break;
            case "personal_business_services":
                purposeCode = 8;
                break;
            case "social_recreation":
                purposeCode = 9;
                break;
            case "serve_passenger":
                purposeCode = 10;
                break;
            default:
                purposeCode = 11;
                break;
        }

        return purposeCode;
    }

}
