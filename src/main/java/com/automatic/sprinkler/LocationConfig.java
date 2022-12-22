package com.automatic.sprinkler;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openweathermap.weather.Coord;
import org.openweathermap.weather.Location;

import com.bitplan.json.JsonAble;

import de.dwd.geoserver.Station;
import de.dwd.geoserver.WFS;
import de.dwd.geoserver.WFS.WFSResponse;

/**
 * Location Configuration
 * 
 * @author priya
 *
 */
public class LocationConfig implements JsonAble {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.sprinkler");
  
  String name;
  String country;
  String lat;
  String lon;
  private Long id;
  public String dwdid;
  public String dwdStation;
  Station theDwdStation;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * get the location for this configuration
   * 
   * @return - the location
   */
  public Location getLocation() {
    Location location = null;
    try {
      if (getId() != null && !(getId()==0)) {
        location = Location.byId(getId());
      } else if (name != null && country!=null) {
        location = Location.byName(getFullName());
      }
    } catch (Throwable th) {
      // TODO handle exception
      String msg=String.format("could not find location: %s name: %s",getId()==null?"?":""+getId(),name==null?"?":name);
      LOGGER.log(Level.WARNING, msg, th);
    }
    if (location == null) {
      location = new Location();
      location.setId(getId());
      location.setName(name);
      location.setCountry(country);
    }
    //// @TODO set coordinates from DMS / search by DMS
    // Coord coord = new Coord();
    // location.setCoord(coord);
    return location;
  }
  
  /**
   * get the full name of the location
   * @return - the fullname
   */
  public String getFullName() {
    return name+"/"+country;
  }

  /**
   * configure me from the given location
   * 
   * @param location
   * @throws Exception 
   */
  public void fromLocation(Location location) throws Exception {
    if (location == null)
      return;
    this.setId(location.getId());
    this.name = location.getName();
    this.country = location.getCountry();
    Coord coord = location.getCoord();
    if (coord == null)
      return;
    this.lat = coord.getLatDMS();
    this.lon = coord.getLonDMS();
    WFSResponse wfsresponse = WFS.getResponseAt(WFS.WFSType.RR,location.getCoord(), 0.5);
    theDwdStation = wfsresponse
        .getClosestStation(location.getCoord());
    if (theDwdStation!=null) {
      this.dwdid=theDwdStation.id;
      this.dwdStation=theDwdStation.toString();
    }
  }

}
