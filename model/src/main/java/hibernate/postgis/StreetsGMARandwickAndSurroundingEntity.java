package hibernate.postgis;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Geometry;


/**
 * The persistent class for the streetspro_gma_randwick_and_surrounding database table.
 * 
 */
@Entity
@Table(name="streetspro_gma_randwick_and_surrounding", schema = "public")
public class StreetsGMARandwickAndSurroundingEntity {
	private Integer gid;
	private String aliasname;
	private String carriage;
	private String endNode;
	private String exitalong;
	private String exitend;
	private String exitstart;
	private double fromleft;
	private double fromright;
	private Integer hnumSys;
	private String id;
	private String label;
	private double lengthM;
	private String linkType;
	private double micode;
	private Integer onewayind;
	private String roadClass;
	private String roadtype;
	private Integer sbc;
	private Integer speedKmh;
	private String stName;
	private String stType;
	private String startNode;
	private Integer state;
	private String street;
	private String swUfi;
	private Integer temp;
	private Geometry theGeom;
	private double toleft;
	private Integer toll;
	private double toright;

	public StreetsGMARandwickAndSurroundingEntity() {
	}


	@Id
	@Column(unique=true, nullable=false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    @SequenceGenerator(name="seq-gen", sequenceName="streetspro_gma_randwick_and_surrounding_gid_seq")
    public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}


	@Column(length=50)
	public String getAliasname() {
		return this.aliasname;
	}

	public void setAliasname(String aliasname) {
		this.aliasname = aliasname;
	}


	@Column(length=2)
	public String getCarriage() {
		return this.carriage;
	}

	public void setCarriage(String carriage) {
		this.carriage = carriage;
	}


	@Column(name="end_node", length=10)
	public String getEndNode() {
		return this.endNode;
	}

	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}


	@Column(length=1)
	public String getExitalong() {
		return this.exitalong;
	}

	public void setExitalong(String exitalong) {
		this.exitalong = exitalong;
	}


	@Column(length=1)
	public String getExitend() {
		return this.exitend;
	}

	public void setExitend(String exitend) {
		this.exitend = exitend;
	}


	@Column(length=1)
	public String getExitstart() {
		return this.exitstart;
	}

	public void setExitstart(String exitstart) {
		this.exitstart = exitstart;
	}

    @Column
	public double getFromleft() {
		return this.fromleft;
	}

	public void setFromleft(double fromleft) {
		this.fromleft = fromleft;
	}

    @Column
	public double getFromright() {
		return this.fromright;
	}

	public void setFromright(double fromright) {
		this.fromright = fromright;
	}


	@Column(name="hnum_sys")
	public Integer getHnumSys() {
		return this.hnumSys;
	}

	public void setHnumSys(Integer hnumSys) {
		this.hnumSys = hnumSys;
	}


	@Column(length=12)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}


	@Column(length=70)
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


	@Column(name="length_m")
	public double getLengthM() {
		return this.lengthM;
	}

	public void setLengthM(double lengthM) {
		this.lengthM = lengthM;
	}


	@Column(name="link_type", length=20)
	public String getLinkType() {
		return this.linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

    @Column
	public double getMicode() {
		return this.micode;
	}

	public void setMicode(double micode) {
		this.micode = micode;
	}

    @Column
	public Integer getOnewayind() {
		return this.onewayind;
	}

	public void setOnewayind(Integer onewayind) {
		this.onewayind = onewayind;
	}


	@Column(name="road_class", length=2)
	public String getRoadClass() {
		return this.roadClass;
	}

	public void setRoadClass(String roadClass) {
		this.roadClass = roadClass;
	}


	@Column(length=6)
	public String getRoadtype() {
		return this.roadtype;
	}

	public void setRoadtype(String roadtype) {
		this.roadtype = roadtype;
	}

    @Column
	public Integer getSbc() {
		return this.sbc;
	}

	public void setSbc(Integer sbc) {
		this.sbc = sbc;
	}


	@Column(name="speed_kmh")
	public Integer getSpeedKmh() {
		return this.speedKmh;
	}

	public void setSpeedKmh(Integer speedKmh) {
		this.speedKmh = speedKmh;
	}


	@Column(name="st_name", length=50)
	public String getStName() {
		return this.stName;
	}

	public void setStName(String stName) {
		this.stName = stName;
	}


	@Column(name="st_type", length=50)
	public String getStType() {
		return this.stType;
	}

	public void setStType(String stType) {
		this.stType = stType;
	}


	@Column(name="start_node", length=10)
	public String getStartNode() {
		return this.startNode;
	}

	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

    @Column
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}


	@Column(length=50)
	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}


	@Column(name="sw_ufi", length=12)
	public String getSwUfi() {
		return this.swUfi;
	}

	public void setSwUfi(String swUfi) {
		this.swUfi = swUfi;
	}

    @Column
	public Integer getTemp() {
		return this.temp;
	}

	public void setTemp(Integer temp) {
		this.temp = temp;
	}


	@Column(name="the_geom")
    @Type(type = "org.hibernatespatial.GeometryUserType")
	public Geometry getTheGeom() {
		return this.theGeom;
	}

	public void setTheGeom(Geometry theGeom) {
		this.theGeom = theGeom;
	}

    @Column
	public double getToleft() {
		return this.toleft;
	}

	public void setToleft(double toleft) {
		this.toleft = toleft;
	}

    @Column
	public Integer getToll() {
		return this.toll;
	}

	public void setToll(Integer toll) {
		this.toll = toll;
	}

    @Column
	public double getToright() {
		return this.toright;
	}

	public void setToright(double toright) {
		this.toright = toright;
	}

}