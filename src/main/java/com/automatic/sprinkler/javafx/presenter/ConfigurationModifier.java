package com.automatic.sprinkler.javafx.presenter;

import java.io.IOException;

import com.automatic.sprinkler.Configuration;
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
public class ConfigurationModifier extends BaseModifier<Configuration>{
  
  /**
   * construct me
   * @param preferencesPanel 
   * @param app 
   * @param stage 
   * @param preferences
   */
  public ConfigurationModifier(Stage stage, App app, ExceptionHandler exceptionHandler,GenericPanel configPanel, Configuration config) {
    super.init(stage, app, exceptionHandler);
    this.setModel(config);
    this.setView(configPanel);
    updateView();
  }

  @Override
  public void updateView() {
    this.getView().setValues(this.getModel().asMap());
  }

  @Override
  public Configuration updateModel() {
    Configuration configuration=this.getModel();
    configuration.fromMap(this.getView().getValueMap());
    try {
      configuration.save();
    } catch (IOException e) {
      this.getExceptionHandler().handleException(e);
    }
    return configuration;
  }

}
