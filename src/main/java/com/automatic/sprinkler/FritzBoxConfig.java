package com.automatic.sprinkler;

import com.bitplan.fritzbox.Fritzbox;
import com.bitplan.fritzbox.FritzboxImpl;
import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

/**
 * FritzBox Configuration
 * @author priya
 *
 */
public class FritzBoxConfig implements JsonAble {
  String url;
  String username;
  String password;
  String password2;
  private String deviceName;
  transient Fritzbox fritzbox;
  
  // makes gson happy
  public FritzBoxConfig() {};
  
  static FritzBoxConfig instance;
  /**
   * get my instance
   * @return
   * @throws Exception
   */
  public static FritzBoxConfig getInstance() throws Exception {
    JsonManager<FritzBoxConfig> jmConfig = new JsonManagerImpl<FritzBoxConfig>(FritzBoxConfig.class);
    instance= jmConfig.getInstance();
    if (instance!=null && instance.fritzbox == null) {
      instance.fritzbox = FritzboxImpl.readFromProperties();
      if (instance.fritzbox!=null) {
        instance.url=instance.fritzbox.getUrl();
        instance.username=instance.fritzbox.getUsername();
        instance.password=instance.fritzbox.getPassword();
        instance.password2=instance.fritzbox.getPassword();
      }
    }
    return instance;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

}
