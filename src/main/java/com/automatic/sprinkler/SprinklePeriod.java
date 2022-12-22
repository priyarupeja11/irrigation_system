package com.automatic.sprinkler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.bitplan.json.JsonAble;

/**
 * a single period of sprinkling (or rain)
 * 
 * @author priya
 *
 */
public class SprinklePeriod implements JsonAble {
  public static SimpleDateFormat isoDateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm");

  public static enum IrrigationEffect {
    Rain, Sprinkler, Evaporation
  };

  // when sprinkling started
  public Date start;
  // when sprinkling stopped
  public Date stop;
  // how many mm of rain equivalent where applied
  public Double mm;
  // pump energy consumed for this sprinkle period
  public Double kWh;
  // the effect of the irrigation
  public IrrigationEffect source;

  /**
   * 
   * @param dateStr
   * @param mins
   * @param mm
   * @param kWh
   * @param source
   * @throws Exception
   */
  public SprinklePeriod(String dateStr, int mins, double mm, double kWh,
      IrrigationEffect source) throws Exception {
    this.mm = mm;
    this.kWh = kWh;
    start = isoDateFormatter.parse(dateStr);
    stop = new Date(start.getTime() + mins * 60000);
    this.source = source;
  }

  public SprinklePeriod() {
  }

  @Override
  public void reinit() {

  }

  @Override
  public void fromMap(Map<String, Object> map) {
    this.kWh = (Double) map.get("kWh");
    this.mm = (Double) map.get("mm");

  }

  public String toString() {
    String periodStr = String.format("%s - %s %4.1f mm %s",
        start == null ? "?" : isoDateFormatter.format(start),
        stop == null ? "?" : isoDateFormatter.format(stop), mm == null ? 0 : mm,
        source.toString());
    return periodStr;
  }
}
