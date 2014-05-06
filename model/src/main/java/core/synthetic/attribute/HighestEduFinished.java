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
