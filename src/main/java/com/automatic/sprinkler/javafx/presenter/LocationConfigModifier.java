package com.automatic.sprinkler.javafx.presenter;

import java.io.IOException;
import java.util.Map;

import com.automatic.sprinkler.Configuration;
import com.automatic.sprinkler.LocationConfig;
import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.App;
import com.bitplan.javafx.BaseModifier;
import com.bitplan.javafx.GenericPanel;

import javafx.stage.Stage;


/**
 * present the preferences
 * @author priya
 *
 */
@SuppressWarnings("restriction")
public class LocationConfigModifier extends BaseModifier<LocationConfig>{
  
  private Configuration configuration;
  private Map<String, Object> prevValues;

  /**
   * construct me
   * @param preferencesPanel 
   * @param app 
   * @param stage 
   * @param preferences
   */
  public LocationConfigModifier(Stage stage, App app, ExceptionHandler exceptionHandler,GenericPanel configPanel, LocationConfig locationConfig, Configuration configuration) {
    super.init(stage, app, exceptionHandler);
    this.setModel(locationConfig);
    this.setView(configPanel);
    this.configuration=configuration;
    updateView();
  }

  @Override
  public void updateView() {
    prevValues=this.getModel().asMap();
    this.getView().setValues(prevValues);
  }

  @Override
  public LocationConfig updateModel() {
    LocationConfig locationConfig=this.getModel();
    locationConfig.fromMap(this.getView().getValueMap());
    try {
      // set the configurations location
      // check whether a different location was specified
      LocationConfig prevLocation = new LocationConfig();
      prevLocation.fromMap(prevValues);
      if (!prevLocation.getFullName().equals(locationConfig.getFullName())) {
        locationConfig.setId(0L);
        locationConfig.dwdid="";
        locationConfig.dwdStation="";
      }
      configuration.setLocation(locationConfig.getLocation());
      configuration.save();
      // set back the locationConfig
      // FIXME - we need the DWD Station stored somewhere ...
      locationConfig.fromLocation(configuration.getLocation());
      // and show it
      updateView();
    } catch (Exception e) {
      this.getExceptionHandler().handleException(e);
    }
    return locationConfig;
  }

}
