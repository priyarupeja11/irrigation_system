package com.automatic.sprinkler.javafx.presenter;

import java.io.IOException;

import com.automatic.sprinkler.FritzBoxConfig;
import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.App;
import com.bitplan.javafx.BaseModifier;
import com.bitplan.javafx.GenericPanel;

import javafx.stage.Stage;


/**
 * present the preferences
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class FritzBoxConfigModifier extends BaseModifier<FritzBoxConfig>{
  
  /**
   * construct me
   * @param preferencesPanel 
   * @param app 
   * @param stage 
   * @param preferences
   */
  public FritzBoxConfigModifier(Stage stage, App app, ExceptionHandler exceptionHandler,GenericPanel configPanel, FritzBoxConfig config) {
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
  public FritzBoxConfig updateModel() {
    FritzBoxConfig config=this.getModel();
    config.fromMap(this.getView().getValueMap());
    try {
      config.save();
    } catch (IOException e) {
      this.getExceptionHandler().handleException(e);
    }
    return config;
  }

}
