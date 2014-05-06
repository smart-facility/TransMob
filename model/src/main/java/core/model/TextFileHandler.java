package core.model;

import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.StringValueTransformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import core.ModelMain;
import core.synthetic.household.Household;
import core.synthetic.individual.Individual;

public class TextFileHandler {

    private static final Logger logger = Logger.getLogger(TextFileHandler.class);

	public TextFileHandler() {
	}



	/**
	 * concates 2 matrices tmp1[r1][c] and tmp2[r2][c]. The result is a matrix
	 * of size (r1+r2) x c. Note that 2 matrices tmp1 and tmp2 must have the
	 * same number of columns.
	 */
	@SuppressWarnings("unused")
	private static double[][] concatematrix(double[][] tmp1, double[][] tmp2) {
		double[][] tmpsum;
		boolean firstUpdate = true;
		if (tmp1.length == 1) {
			for (int i = 0; i <= tmp1[0].length - 1; i++) {
				if (tmp1[0][i] != 0) {
					firstUpdate = false;
					break;
				}
            }
		} else {
			firstUpdate = false;
		}

		if (firstUpdate) {
			tmpsum = tmp2;
        } else {
			tmpsum = new double[tmp1.length + tmp2.length][tmp1[0].length];
			for (int i = 0; i <= tmpsum.length - 1; i++) {
				if (i <= tmp1.length - 1) {
                    System.arraycopy(tmp1[i], 0, tmpsum[i], 0, tmp1[0].length);
                } else {
                    System.arraycopy(tmp2[i - tmp1.length], 0, tmpsum[i], 0, tmp2[0].length);
                }
			}
		}
		return tmpsum;
	}

	public void writeToCSV(String filename, double[][] dataout) {
        CSVWriter csvWriter = null;

        try {
            csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(filename)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

            for (double [] row : dataout) {
                Double [] rowAsObj = ArrayUtils.toObject(row);
                Collection<String> stringValues = CollectionUtils.collect(Arrays.asList(rowAsObj), StringValueTransformer.getInstance());
                csvWriter.writeNext(stringValues.toArray(new String[stringValues.size()]));
            }

            csvWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to write CSV file: " + filename, e);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close CSV file after writing: " + filename, e);
                }
            }
        }
	}

    /**
     * Write an array of ints to a CSV file. File is overwritten if it exists.
     * @param filename The name of the file.
     * @param dataout The data to be written.
     */
	public static void writeToCSV(String filename, int[][] dataout) {
		writeToCSV(filename, dataout, false);
	}

    /**
     * Write an array of ints to a CSV file.
     * @param filename The name of the file.
     * @param dataout The data to be written.
     * @param append True to append the data to the end of the file. False to overwite.
     */
    public static void writeToCSV(String filename, int[][] dataout, boolean append) {
        CSVWriter csvWriter = null;

        try {
            csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(filename, append)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

            for (int [] row : dataout) {
                Integer [] rowAsObj = ArrayUtils.toObject(row);
                Collection<String> stringValues = CollectionUtils.collect(Arrays.asList(rowAsObj), StringValueTransformer.getInstance());
                csvWriter.writeNext(stringValues.toArray(new String[stringValues.size()]));
            }

            csvWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to write CSV file: " + filename, e);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close CSV file after writing: " + filename, e);
                }
            }
        }
    }

    /**
     * Write an array of strings to a CSV file.
     * @param filename The name of the file.
     * @param dataout The data to be written.
     * @param append True to append the data to the end of the file. False to overwite.
     */
    public static void writeToCSV(String filename, String[][] dataout, boolean append) {
        CSVWriter writer = null;

        try {
            writer = new CSVWriter(new BufferedWriter(new FileWriter(filename, append)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

            for (String [] row : dataout) {
                writer.writeNext(row);
            }

            writer.flush();
        } catch (IOException e) {
            logger.error("Failed to write CSV file: " + filename, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to close CSV file after writing: " + filename, e);
                }
            }
        }
    }


    /**
     * Write a list of String arrays to CSV file. File is overwritten.
     * @param filename Name of the file to write.
     * @param dataout  Data to be written
     */
    public static void writeToCSV(String filename, List<String[]> dataout) {

        CSVWriter csvWriter = null;

        try {
            csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(filename)));

            csvWriter.writeAll(dataout);

            csvWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to write CSV file: " + filename, e);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + filename, e);
                }
            }
        }
    }

    //==================================================================================
    /***
     * Write a list of String arrays to CSV file. File is overwritten.
     * @param filename Name of the file to write.
     * @param header The header to write at the top of the file.
     * @param dataout  Data to be written
     *
     * @author vlcao
     */
    public static void writeToCSV(String filename, String[] header, List<String[]> dataout) {
        CSVWriter csvWriter = null;
        try {
            Writer fw = new BufferedWriter(new FileWriter(filename));
            csvWriter = new CSVWriter(fw, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

            csvWriter.writeNext(header);
            csvWriter.writeAll(dataout);

            csvWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to write content to file:" + filename, e);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + filename, e);
                }
            }
        }
    }

    /**
     * Write a list of String arrays to tab delimited file. File is overwritten.
     * @param filename Name of the file to write.
     * @param header The header to write at the top of the file.
     * @param dataout  Data to be written
     */
	public static void writeTabLimited(String filename, String[] header, List<String []> dataout) {
        CSVWriter csvWriter = null;
        try {
            Writer fw = new BufferedWriter(new FileWriter(filename));
            csvWriter = new CSVWriter(fw, '\t', CSVWriter.NO_QUOTE_CHARACTER);

			csvWriter.writeNext(header);
            csvWriter.writeAll(dataout);

            csvWriter.flush();
		} catch (IOException e) {
			logger.error("Failed to write content to file:" + filename, e);
		}  finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + filename, e);
                }
            }
        }
	}

    /**
     * Write a list of String arrays to tab delimited file. File is appended.
     * @param filename Name of the file to write.
     * @param header The header to write at the top of the file.
     * @param dataout  Data to be written
     */
    public static void appendTabLimited(String filename, String[] header, List<String []> dataout) {
        CSVWriter csvWriter = null;
        try {
            Writer fw = new BufferedWriter(new FileWriter(filename, true));
            csvWriter = new CSVWriter(fw, '\t', CSVWriter.NO_QUOTE_CHARACTER);

            csvWriter.writeNext(header);
            csvWriter.writeAll(dataout);

            csvWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to write content to file:" + filename, e);
        }  finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + filename, e);
                }
            }
        }
    }


	// ===========================================================================
	/**
	 * Write the network file Proccess_Link_2 into text file for TRANSIMS.
	 * 
	 * @param filename
	 * @param header
	 * 
	 * @author vlcao
	 */
	public static void writeTabLimited(String filename, String[] header, ModelMain main) {

        CSVWriter writer = null;
		try {
			writer = new CSVWriter(new BufferedWriter(new FileWriter(filename)), '\t', CSVWriter.NO_QUOTE_CHARACTER);
			writer.writeNext(header);

			int accessID = 1;
			for (int i = 0; i < main.getProcessLink().size(); i++) {
                String [] dataRow = new String[header.length];

				for (int j = 0; j < 2; j++) {
					dataRow[0] = String.valueOf(accessID++);

					switch (j) {
					case 0:
                        dataRow[1] = String.valueOf(i + 1);
						dataRow[2] = "ACTIVITY";
						dataRow[3] = main.getProcessLink().get(i).toString();
						dataRow[4] = "PARKING";
						break;

					case 1:
                        dataRow[1] = main.getProcessLink().get(i).toString();
						dataRow[2] = "PARKING";
						dataRow[3] = String.valueOf(i + 1);
						dataRow[4] = "ACTIVITY";
						break;
					}

					dataRow[5] = "86400";
					dataRow[6] = "0";
					dataRow[7] = "Parking Access";

                    writer.writeNext(dataRow);
				}
			}

			writer.flush();

		} catch (IOException e) {
			logger.error("Failed to write file: " + filename, e);
		}  finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + filename, e);
                }
            }
        }
	}

	// @SuppressWarnings("unused")
	public void testIntConversion(int[][] intfreq, double[][] freq) {
		int nrow = intfreq.length;
		int ncol = intfreq[0].length;
		double[] tstdouble = new double[nrow];
		int[] tstint = new int[nrow];

		for (int i = 0; i <= nrow - 1; i++) {
			tstdouble[i] = 0;
			tstint[i] = 0;
			for (int j = 0; j <= ncol - 1; j++) {
				tstdouble[i] += freq[i][j];
				tstint[i] += intfreq[i][j];
			}
			logger.debug("tstdouble = " + tstdouble[i] + "; tstint = "
					+ tstint[i]);
		}

	}

	// ======================================================================================
	/**
	 * write Travel Diary into CSV file.
	 * 
	 * @param fileName
	 * 
	 * @author vlcao
	 */
	public static void writeTravelDiaryToCSV(String fileName, ModelMain main) {

        final String[] header = {"Travel_ID", "Agent ID", "Hhold ID", "Age", "Gender", "Income",
                "Travel_Origin", "Travel_Destination", "Start_Time", "End_Time", "Duration",
                "Mode_of_Transport", "Purpose", "Vehicle_ID", "Trip_ID"};

		CSVWriter csvWriter = null;
		try {
            csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fileName)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			csvWriter.writeNext(header);

			// Iterates the data and write it out to the .csv file
			for (Household hhold : main.getHouseholdPool().getHouseholds().values()) {
				for (Individual individual : hhold.getResidents()) {
					int[][] diaries = individual.getTravelDiariesWeekdays();
					if (diaries == null) {
						continue;
                    }
                    for (int[] diary : diaries) {

                        //NOT write to csv file for travel diary does NOT travel (stay at home)
                        if ((diary[6] == -1 //origin
                                && diary[7] == -1 //destination
                                && diary[8] == -1 //start_time
                                && diary[9] == -1 //end_time
                                && diary[10] == -1 //duration
                                && diary[11] == -1) //travel_mode
                                || (diary[6] == 0 //origin
                                && diary[7] == 0 //destination
                                && diary[8] == 0 //start_time
                                && diary[9] == 0 //end_time
                                && diary[10] == 0) //duration
                                ) {

                            continue;
                        }

                        Integer[] diaryAsObj = ArrayUtils.toObject(diary);
                        Collection<String> stringValues = CollectionUtils.collect(Arrays.asList(diaryAsObj), StringValueTransformer.getInstance());
                        csvWriter.writeNext(stringValues.toArray(new String[stringValues.size()]));
                    }
				}
			}

		} catch (IOException e) {
			logger.error("Failed to create Travel Diaries file" + fileName, e);
		} finally {
			try {
				if (csvWriter != null) {
                    csvWriter.close();
                }
			} catch (IOException e) {
				logger.error("Failed to close file: " + fileName, e);
			}
		}
	}

	// =================================================================================
	/**
	 * 
	 * @param spTD
	 * @param fileName
	 */
	public static void writeSpTD2CSV(List<List<int[][]>> spTD, String fileName) {
        final String[] header = {"hholdID", "indivID", "tripID", "departure_time", "trip_time", "mode", "purpose", "origin", "destination"};

        CSVWriter writer = null;

		try {
			writer = new CSVWriter(new BufferedWriter(new FileWriter(fileName)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			writer.writeNext(header);

            for (List<int[][]> spTDwOD : spTD) {
                for (int[][] tdHhold : spTDwOD) {
                    for (int[] aTdHhold : tdHhold) {
                        //NOT write to csv file for travel diary does NOT travel (stay at home)
                        if ((aTdHhold[3] == 0 //departure_time
                                && aTdHhold[4] == 0 //trip_time
                                && aTdHhold[7] == 0 //origin
                                && aTdHhold[8] == 0)//destination
                                || (aTdHhold[3] == -1 //departure_time
                                && aTdHhold[4] == -1 //trip_time
                                && aTdHhold[7] == -1 //origin
                                && aTdHhold[8] == -1)//destination
                                ) {

                            continue;
                        }

                        Collection<String> stringValues = CollectionUtils.collect(Arrays.asList(ArrayUtils.toObject(aTdHhold)), StringValueTransformer.getInstance());
                        writer.writeNext(stringValues.toArray(new String[stringValues.size()]));
                        writer.flush();
                    }
                }
            }
		} catch (IOException e) {
			logger.error("Failed to write to file: " + e, e);
		}  finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error("Failed to close file: " + fileName, e);
            }
        }
	}

	// ======================================================================================

	public static void writeToText(String filename, List<String> dataout, boolean append) {
        BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename, append));

            for (String row : dataout) {
                writer.append(row);
                writer.newLine();
                writer.flush();
            }
			
		} catch (IOException e) {
			logger.error("Failed to write text file: " + filename, e);
		}  finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + filename, e);
                }
            }
        }
		
	}
	
	// ======================================================================================
		public static void writeToCSV(String filename, List<String[]> dataout, boolean append) {
	        BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(filename, append));

				 for (String[] row : dataout) {
		            	for (String col : row) {
		            		writer.append(col);
		            		writer.append(',');
						}
		            	writer.newLine();
		                writer.flush();		                
		            }
				
			} catch (IOException e) {
				logger.error("Failed to write text file: " + filename, e);
			}  finally {
	            if (writer != null) {
	                try {
	                    writer.close();
	                } catch (IOException e) {
	                    logger.error("Failed to close file after writing: " + filename, e);
	                }
	            }
	        }
		}
	

    /**
     *
     */
    public static void writeToText(String filename, String text, boolean append) {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(filename, append));
            writer.append(text);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            logger.error("Failed to write file: " + filename, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + filename, e);
                }
            }
        }
    }


	// ========================================================================================================
	/**
	 * Write the HashMap into CSV file.
	 * 
	 * @param displayHashMap
	 * @param numOfObject
	 *            : number of objects that will be written. When it equals -1,
	 *            all objects will be display
	 * @param fileName
	 *            : the name of the file will be written
	 * 
	 * @author vlcao
	 */
	@SuppressWarnings("rawtypes")
	public static void writeHashMaptoCSV(Map<?, ?> displayHashMap, int numOfObject,
			String fileName) {
		int count = 0;

        CSVWriter writer = null;

		try {
			writer = new CSVWriter(new BufferedWriter(new FileWriter(fileName)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

            for (Map.Entry entry : displayHashMap.entrySet()) {

                // Only write as many as we are supposed to. -1 indicates to write all
                if (numOfObject != -1 && count >= numOfObject) {
                    break;
                }

                String[] stringValues = {String.valueOf(entry.getKey()), String.valueOf(entry.getValue())};
                writer.writeNext(stringValues);

                count++;
            }


            writer.flush();

		} catch (IOException e) {
			logger.error("Failed to write to CSV file: " + fileName, e);
		} finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + fileName, e);
                }
            }
        }

	}

	// ========================================================================================================
	/**
	 * Write the HashMap into CSV file.
	 * 
	 * @param displayHashMap
	 * @param numOfObject
	 *            : number of objects that will be written. When it equals -1,
	 *            all objects will be display
	 * @param fileName
	 *            : the name of the file will be written
	 * 
	 * @author vlcao
	 */
	@SuppressWarnings("rawtypes")
	public static void writeHashMapStringDoubleArrToCSV(
			Map<String, double[]> displayHashMap, int numOfObject,
			String fileName) {
		int count = 0;

        CSVWriter writer = null;

		try {
			writer = new CSVWriter(new BufferedWriter(new FileWriter(fileName)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

            for (Map.Entry<String, double[]> entry : displayHashMap.entrySet()) {
                // Only write as many as we are supposed to. -1 indicates to write all
                if (numOfObject != -1 && count >= numOfObject) {
                    break;
                }

                StringBuilder line = new StringBuilder();

                line.append(entry.getKey()).append('#');

                // write the hash map ultTravelMode into csv file
                for (int l = 0; l < entry.getValue().length; l++) {
                    line.append(String.valueOf(entry.getValue()[l]));

                    if (l < entry.getValue().length - 1) {
                        line.append('#');
                    }
                }

                writer.writeNext(StringUtils.split(line.toString(), '#'));

                count++;

            }

			writer.flush();

		} catch (IOException e) {
			logger.error("Failed to write CSV file" + fileName, e);
		}  finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to close file after writing: " + fileName, e);
                }
            }
        }

	}

    /**
     * reads a csv file.
     *
     * @param fileName
     *            the source file
     * @return an arraylist contains an arraylist which stores all the
     *         informaion
     */
    // ========================================================================================================
    public static List<List<String>> readCSV(String fileName) {
        logger.debug("Start to read: " + fileName);
        List<List<String>> sheetData = new ArrayList<>();
        BufferedReader br = null;
        try {
            // create BufferedReader to read csv file containing data
            br = new BufferedReader(new FileReader(fileName));
            String strLine = "";

            StringTokenizer st = null;

            // read comma separated file line by line
            while ((strLine = br.readLine()) != null) {

                // break comma separated line using ","
                st = new StringTokenizer(strLine, ",");

                ArrayList<String> data = new ArrayList<String>();

                while (st.hasMoreTokens()) {
                    // display csv file
                    String editedValue = String.valueOf(st.nextToken())
                            .replace("\"", "").trim();

                    data.add(editedValue);
                }

                sheetData.add(data);
            }

        } catch (Exception e) {
            logger.error("Exception while reading csv file: ", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                logger.error("Exception while closing csv file: ", e);
            }
        }

        logger.debug("Finished read: " + fileName);
        return sheetData;
    }

}
