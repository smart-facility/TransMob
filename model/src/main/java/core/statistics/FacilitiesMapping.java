package core.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.ApplicationContextHolder;
import hibernate.postgis.FacilitiesMappingDAO;
import hibernate.postgis.FacilitiesMappingEntity;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

/**
 * A mapping class contains types and facilities
 * 
 * @author qun
 * 
 */
public class FacilitiesMapping {

    private static final Logger logger = Logger.getLogger(FacilitiesMapping.class);

    private final FacilitiesMappingDAO facilitiesMappingDAO;

	public static void main(String[] args) {
		FacilitiesMapping facilitiesMapping = new FacilitiesMapping();
		// logger.debug(facilitiesMapping.getMapping().get("EDUCATION - SCHOOL"));
		facilitiesMapping.mappingTest();
	}

	public FacilitiesMapping() {
        facilitiesMappingDAO = ApplicationContextHolder.getBean(FacilitiesMappingDAO.class);
		mapping = new HashMap<String, ArrayList<String>>();
		
		String fileName = "facilityMapping.csv";
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/facilityMapping.csv"
		
		initialMapping(new File(fileName));
	}

	/**
	 * reads in mapping file
	 * 
	 * @param mappingFile
	 *            the selected mapping file
	 */
	public final void initialMapping(File mappingFile) {
		String facility, category;
		// ArrayList<String> categoryArrayList = new ArrayList<String>();
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new BufferedReader(new FileReader(mappingFile)));
			String[] nextLine;
			while ((nextLine = csvReader.readNext()) != null) {
				facility = nextLine[1];
				category = nextLine[3];
				createMapping(facility, category);
			}
		} catch (FileNotFoundException e) {
            logger.error("Exception caught", e);
		} catch (IOException e) {
            logger.error("Exception caught", e);
		} finally {
			try {
				if (csvReader != null) {
                    csvReader.close();
                }
			} catch (IOException e) {
                logger.error("Exception caught", e);
			}
		}
	}

	/**
	 * initial mapping from database
	 */
	public void initialMapping() {
		try {
            List<FacilitiesMappingEntity> entities = facilitiesMappingDAO.findAll();

            for (FacilitiesMappingEntity entity : entities) {
				createMapping(entity.getFacility(), entity.getCategory());
			}
		} catch (Exception e) {
            logger.error("Exception caught", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void createMapping(String facility, String category) {
		ArrayList<String> categoryArrayList;
		if (mapping.containsKey(facility)) {

			categoryArrayList = mapping.get(facility);
			categoryArrayList.add(category);
			mapping.put(facility, (ArrayList<String>) categoryArrayList.clone());
		} else {
			categoryArrayList = new ArrayList<String>();
			categoryArrayList.add(category);
			mapping.put(facility, (ArrayList<String>) categoryArrayList.clone());
		}
		// logger.debug(mapping.size());
		categoryArrayList.clear();
	}

    /**
     * JH This is test code that doesn't belong in production classes.
     */
	public void mappingTest() {
		String strLine;
		String fileNameType = "types.txt";
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/types.txt"
		String fileNameCategories = "categories.txt";
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/categories.txt"
		
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileNameType)));
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileNameCategories),true))) {			
			while ((strLine = bufferedReader.readLine()) != null) {
				String typesCountString = strLine;
				typesCountString = typesCountString.trim().substring(1)
						.substring(0, typesCountString.length() - 2);
				String[] types = typesCountString.split(",");
				String[] count;
				HashMap<String, Integer> categoryCountHashMap = new HashMap<String, Integer>();
				int num;

				for (String type : types) {
					count = type.split("=");
					// logger.debug(count[0]);
					if (count[0].startsWith(" ")) {
						count[0] = count[0].substring(1);
					}
					// logger.debug(count[0]);
					ArrayList<String> categoryList = mapping.get(count[0]);
					// logger.debug(categoryList);
					for (String categoty : categoryList) {
						if (categoryCountHashMap.containsKey(categoty)) {
							num = categoryCountHashMap.get(categoty)
									+ Integer.valueOf(count[1]);
							categoryCountHashMap.put(categoty, num);
						} else {
							categoryCountHashMap.put(categoty,
									Integer.valueOf(count[1]));
						}
					}
				}
				logger.debug(categoryCountHashMap);
				bufferedWriter.write(categoryCountHashMap + "\n");
			}
		} catch (NumberFormatException e) {
            logger.error("Exception caught", e);
		} catch (IOException e) {
            logger.error("Exception caught", e);
		}
	}

	public Map<String, ArrayList<String>> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, ArrayList<String>> mapping) {
		this.mapping = mapping;
	}

	private Map<String, ArrayList<String>> mapping;

}
