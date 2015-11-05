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
package core.synthetic.attribute;

import core.HardcodedData;

/**
 * The highest education finished distribution for individuals in the individual
 * pool
 * 
 * @author qun
 */
public enum HighestEduFinished {
	/**
	 * name="postgraduateDegree"
	 * 
	 * @uml.associationEnd
	 */
	PostgraduateDegree, /**
	 * 
	 * name="graduateDiplomaAndGraduateCertificate"
	 * 
	 * @uml.associationEnd
	 */
	GraduateDiplomaAndGraduateCertificate, /**
	 * 
	 * name="bachelorDegree"
	 * 
	 * @uml.associationEnd
	 */
	BachelorDegree, /**
	 * name="advancedDiplomaAndDiploma"
	 * 
	 * @uml.associationEnd
	 */
	AdvancedDiplomaAndDiploma,
	/**
	 * name="certificateNfd"
	 * 
	 * @uml.associationEnd
	 */
	CertificateNfd, /**
	 * name="certificateIIIAndIVc"
	 * 
	 * @uml.associationEnd
	 */
	CertificateIIIAndIVc, /**
	 * name="certificateIAndIId"
	 * 
	 * @uml.associationEnd
	 */
	CertificateIAndIId, /**
	 * 
	 * name="levelOfEducationInadequatelyDescribed"
	 * 
	 * @uml.associationEnd
	 */
	LevelOfEducationInadequatelyDescribed,
	/**
	 * name="levelOfEducationNotStated"
	 * 
	 * @uml.associationEnd
	 */
	LevelOfEducationNotStated;
	public static HighestEduFinished random() {
		// SecureRandom random = GlobalRandom.getInstance();
		return HighestEduFinished.values()[HardcodedData.random
				.nextInt(HighestEduFinished.values().length)];
	}
}
