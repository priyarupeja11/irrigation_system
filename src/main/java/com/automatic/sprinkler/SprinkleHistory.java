package com.automatic.sprinkler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.automatic.sprinkler.SprinklePeriod.IrrigationEffect;
import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

import de.dwd.geoserver.Station;
import de.dwd.geoserver.WFS;
import de.dwd.geoserver.WFS.Feature;
import de.dwd.geoserver.WFS.WFSResponse;

/**
 * keep track of the sprinkle periods
 * 
 * @author priya
 *
 */
public class SprinkleHistory implements JsonAble {

  String name = null;
  private List<SprinklePeriod> sprinklePeriods = new ArrayList<SprinklePeriod>();
  private Map<Date, SprinklePeriod> sprinklePeriodByDate = new TreeMap<Date, SprinklePeriod>();

  public List<SprinklePeriod> getSprinklePeriods() {
    return sprinklePeriods;
  }

  public void setSprinklePeriods(List<SprinklePeriod> sprinklePeriods) {
    this.sprinklePeriods = sprinklePeriods;
  }

  // make gson happy
  public SprinkleHistory() {
  }

  /**
   * construct me from a name and an array of periods
   * 
   * @param name
   * @param periods
   */
  public SprinkleHistory(String pName, SprinklePeriod... periods) {
    this.name = pName;
    setSprinklePeriods(Arrays.asList(periods));
    sortByStart();
  }

  /**
   * sort the sprinklePeriods by start date
   */
  public void sortByStart() {
    Collections.sort(getSprinklePeriods(), new Comparator<SprinklePeriod>() {
      @Override
      public int compare(SprinklePeriod lhs, SprinklePeriod rhs) {
        long diffmsecs = (lhs.start == null ? 0 : lhs.start.getTime())
            - (rhs.start == null ? 0 : rhs.start.getTime());
        return (int) diffmsecs;
      }
    });
  }

  /**
   * return the total precipitation
   * 
   * @param hours
   * @return - the sum
   */
  public Double totalPrecipitation(int hours) {
    double total = 0.0;
    for (SprinklePeriod period : sprinklePeriods) {
      if (period.mm != null)
        total += period.mm;
    }
    return total;
  }

  public void add(SprinklePeriod period) {
    this.getSprinklePeriods().add(period);
    this.sprinklePeriodByDate.put(period.start, period);
    sortByStart();
  }

  @Override
  public void reinit() {
    sortByStart();
    this.sprinklePeriodByDate.clear();
    for (SprinklePeriod period : sprinklePeriods) {
      sprinklePeriodByDate.put(period.start, period);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fromMap(Map<String, Object> map) {
    this.name = (String) map.get("name");
    this.setSprinklePeriods((List<SprinklePeriod>) map.get("sprinklePeriods"));
  }

  @Override
  public File getJsonFile() {
    String appName = this.getClass().getSimpleName()
        + (name == null ? "" : "_" + name);
    return JsonAble.getJsonFile(appName);
  }

  static SprinkleHistory instance = null;

  /**
   * singleton access
   * 
   * @return the singleton
   */
  public static SprinkleHistory getInstance() {
    if (instance == null) {
      JsonManager<SprinkleHistory> jmConfig = new JsonManagerImpl<SprinkleHistory>(
          SprinkleHistory.class);
      instance = jmConfig.getInstance();
    }
    return instance;
  }

  /**
   * add a SprinklePeriod based on the given parameters
   * 
   * @param source
   * @param start
   * @param mm
   * @param mins
   */
  public void addPeriod(SprinklePeriod.IrrigationEffect source, Date start,
      Double mm, int mins) {
    SprinklePeriod period = new SprinklePeriod();
    period.source = source;
    period.mm = mm;
    period.start = start;
    period.stop = new Date(period.start.getTime() + 60000 * mins);
    if (!this.sprinklePeriodByDate.containsKey(period.start))
      add(period);
  }

  /**
   * get a response for the given station
   * 
   * @param dwdStation
   * @throws Exception
   */
  public void addFromDWDStation(Station dwdStation) throws Exception {
    reinit();
    WFSResponse wfsresponse = WFS.getRainHistory(dwdStation);
    for (Feature feature : wfsresponse.features) {
      addPeriod(IrrigationEffect.Rain, feature.properties.getDate(),
          feature.properties.PRECIPITATION, 24 * 60);
    }
    wfsresponse = WFS.getEvaporationHistory(dwdStation);
    for (Feature feature : wfsresponse.features) {
      addPeriod(IrrigationEffect.Evaporation, feature.properties.getDate(),
          feature.properties.EVAPORATION, 24 * 60);
    }
  }

}
