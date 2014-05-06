package core.csv;

import java.io.Reader;
import java.util.List;

/**
 * Bean for JourneyToWork (JTW).
 */
public class JourneyToWork {

    private int originTz06;
    private int originSla06;
    private int originLga06;
    private int originSsd06;
    private int originSd06;
    private int originState;
    private int destinationTz06;
    private int destinationSla06;
    private int destinationLga06;
    private int destinationSsd06;
    private int destinationSd06;
    private int destinationArea;
    private long originZone06_abs;
    private long destinationZone06_abs;
    private int mode9;
    private int freq;
    private String sourceNotInTz;
    private String destinationInTz;

    public static List<JourneyToWork> readFromCsv(Reader file) {
        String[] columns = new String[]{"originTz06", "originSla06", "originLga06", "originSsd06", "originSd06",
                "originState", "destinationTz06", "destinationSla06", "destinationLga06", "destinationSsd06",
                "destinationSd06", "destinationArea", "originZone06_abs", "destinationZone06_abs",
                "mode9", "freq", "sourceNotInTz", "destinationInTz"}; // the fields to bind to in your JavaBean

        return CSVHelper.readFromCsv(file, JourneyToWork.class, columns, 1);
    }

    public int getOriginTz06() {
        return originTz06;
    }

    public void setOriginTz06(int originTz06) {
        this.originTz06 = originTz06;
    }

    public int getOriginSla06() {
        return originSla06;
    }

    public void setOriginSla06(int originSla06) {
        this.originSla06 = originSla06;
    }

    public int getOriginLga06() {
        return originLga06;
    }

    public void setOriginLga06(int originLga06) {
        this.originLga06 = originLga06;
    }

    public int getOriginSsd06() {
        return originSsd06;
    }

    public void setOriginSsd06(int originSsd06) {
        this.originSsd06 = originSsd06;
    }

    public int getOriginSd06() {
        return originSd06;
    }

    public void setOriginSd06(int originSd06) {
        this.originSd06 = originSd06;
    }

    public int getOriginState() {
        return originState;
    }

    public void setOriginState(int originState) {
        this.originState = originState;
    }

    public int getDestinationTz06() {
        return destinationTz06;
    }

    public void setDestinationTz06(int destinationTz06) {
        this.destinationTz06 = destinationTz06;
    }

    public int getDestinationSla06() {
        return destinationSla06;
    }

    public void setDestinationSla06(int destinationSla06) {
        this.destinationSla06 = destinationSla06;
    }

    public int getDestinationLga06() {
        return destinationLga06;
    }

    public void setDestinationLga06(int destinationLga06) {
        this.destinationLga06 = destinationLga06;
    }

    public int getDestinationSsd06() {
        return destinationSsd06;
    }

    public void setDestinationSsd06(int destinationSsd06) {
        this.destinationSsd06 = destinationSsd06;
    }

    public int getDestinationSd06() {
        return destinationSd06;
    }

    public void setDestinationSd06(int destinationSd06) {
        this.destinationSd06 = destinationSd06;
    }

    public int getDestinationArea() {
        return destinationArea;
    }

    public void setDestinationArea(int destinationArea) {
        this.destinationArea = destinationArea;
    }

    public long getOriginZone06_abs() {
        return originZone06_abs;
    }

    public void setOriginZone06_abs(long originZone06_abs) {
        this.originZone06_abs = originZone06_abs;
    }

    public long getDestinationZone06_abs() {
        return destinationZone06_abs;
    }

    public void setDestinationZone06_abs(long destinationZone06_abs) {
        this.destinationZone06_abs = destinationZone06_abs;
    }

    public int getMode9() {
        return mode9;
    }

    public void setMode9(int mode9) {
        this.mode9 = mode9;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getSourceNotInTz() {
        return sourceNotInTz;
    }

    public void setSourceNotInTz(String sourceNotInTz) {
        this.sourceNotInTz = sourceNotInTz;
    }

    public String getDestinationInTz() {
        return destinationInTz;
    }

    public void setDestinationInTz(String destinationInTz) {
        this.destinationInTz = destinationInTz;
    }
}
