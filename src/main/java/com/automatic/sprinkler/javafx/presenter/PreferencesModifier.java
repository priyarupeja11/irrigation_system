package com.automatic.sprinkler.javafx.presenter;

import java.io.IOException;

import com.automatic.sprinkler.SprinklerI18n;
import com.bitplan.appconfig.Preferences;
import com.bitplan.appconfig.Preferences.LangChoice;
import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.App;
import com.bitplan.i18n.I18n;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.BaseModifier;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.GenericPanel;

import javafx.stage.Stage;


/**
 * present the preferences
 * @author priya
 *
 */
@SuppressWarnings("restriction")
public class PreferencesModifier extends BaseModifier<Preferences>{
  
  /**
   * construct me
   * @param preferencesPanel 
   * @param app 
   * @param stage 
   * @param preferences
   */
  public PreferencesModifier(Stage stage, App app, ExceptionHandler exceptionHandler,GenericPanel preferencesPanel, Preferences preferences) {
    super.init(stage, app, exceptionHandler);
    this.setModel(preferences);
    this.setView(preferencesPanel);
    updateView();
  }

  @Override
  public void updateView() {
    this.getView().setValues(this.getModel().asMap());
  }

  @Override
  public Preferences updateModel() {
    Preferences preferences=this.getModel();
    LangChoice lang = preferences.getLanguage();
    preferences.fromMap(this.getView().getValueMap());
    try {
      preferences.save();
    } catch (IOException e) {
      this.getExceptionHandler().handleException(e);
    }
    if (!lang.equals(preferences.getLanguage())) {
      Translator.initialize(Translator.APPLICATION_PREFIX, preferences.getLanguage().name());
      GenericDialog.showAlert(getStage(), I18n.get(SprinklerI18n.LANGUAGE_CHANGED_TITLE),
          I18n.get(SprinklerI18n.LANGUAGE_CHANGED), I18n.get(SprinklerI18n.NEWLANGUAGE_RESTART));
    }
    return preferences;
  }

}
