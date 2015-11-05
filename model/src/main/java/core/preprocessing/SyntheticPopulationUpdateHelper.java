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
package core.preprocessing;

import hibernate.postgres.SyntheticPopulationDAO;
import hibernate.postgres.SyntheticPopulationEntity;

import java.io.File;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import core.ApplicationContextHolder;

public class SyntheticPopulationUpdateHelper {

	private final CdToTzCovention cdToTzCovention;
	private final File sourceCsvFile;

	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		ApplicationContextHolder holder = ctx
				.getBean(ApplicationContextHolder.class);
		
		String fileNameCd2Tz = "CDtoTZ2006RwGs20121127.csv";
		// "C:/Documents and Settings/qun/Desktop/TransportNSW/CDtoTZ2006RwGs20121127.csv";
		String fileNameIndvRecords = "finIndivRecords_20120430.csv";
		// "C:\\Documents and Settings\\qun\\Desktop\\TransportNSW\\synthetic population\\finIndivRecords_20120430.csv";
		SyntheticPopulationUpdateHelper syntheticPopulationUpdateHelper = new SyntheticPopulationUpdateHelper(
				new CdToTzCovention(new File(fileNameCd2Tz)),
				new File(fileNameIndvRecords));
		
		syntheticPopulationUpdateHelper.updateTable();
	}

	public SyntheticPopulationUpdateHelper(CdToTzCovention cdToTzCovention,
			File sourceCsvFile) {
		super();
		this.cdToTzCovention = cdToTzCovention;
		this.sourceCsvFile = sourceCsvFile;
	}

	/**
	 * updates synthetic_population table with csv file
	 */
	public void updateTable() {

		SyntheticPopulationDAO syntheticPopulationDAO = ApplicationContextHolder
				.getBean(SyntheticPopulationDAO.class);

		syntheticPopulationDAO.deleteAll();

		List<SyntheticPopulationEntity> syntheticPopulationEntities = this.cdToTzCovention
				.generateIndivious(this.sourceCsvFile);


		for (SyntheticPopulationEntity syntheticPopulationEntity : syntheticPopulationEntities) {
			syntheticPopulationDAO.save(syntheticPopulationEntity);
		}


	}

}
