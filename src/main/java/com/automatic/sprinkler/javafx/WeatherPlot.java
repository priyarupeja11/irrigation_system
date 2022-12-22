package com.automatic.sprinkler.javafx;

import java.util.Date;
import java.util.logging.Logger;

import org.openweathermap.weather.Forecast;
import org.openweathermap.weather.WeatherForecast;

import com.automatic.sprinkler.SprinkleHistory;
import com.automatic.sprinkler.SprinklePeriod;

import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

@SuppressWarnings("restriction")
public class WeatherPlot<XT,YT> {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.sprinkler.javafx");
  public static boolean debug = false;

  String title;
  String xTitle;
  String yTitle;
  private WeatherForecast forecast;
  private SprinkleHistory plan;

  /**
   * construct me from the given titles
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   * @param forecast2
   * @param seriesName
   */
  public WeatherPlot(String title, String xTitle, String yTitle,
      String seriesName) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.seriesName = seriesName;
  }

  /**
   * construct me with the given title, xTitle and yTitle
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   */
  public WeatherPlot(String title, String xTitle, String yTitle,
      String seriesName, WeatherForecast forecast) {
    this(title, xTitle, yTitle, seriesName);
    this.forecast = forecast;
  }

  /**
   * create a weather Plot for the given plan
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   * @param plan
   */
  public WeatherPlot(String title, String xTitle, String yTitle,
      String seriesName, SprinkleHistory plan) {
    this(title, xTitle, yTitle, seriesName);
    this.plan = plan;
  }

  XYChart.Series<XT, YT> series;
  private NumberAxis yAxis;
  private String seriesName;

  /**
   * get the Line or Bar Chart for the weather forecast or history
   * 
   * @return - the chart
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public XYChart<XT, YT> getChart(String chartType) {
    // defining the axes
    Axis xAxis = null;
    yAxis = new NumberAxis();
    yAxis.setLabel(yTitle);
    // creating the chart
    XYChart<XT, YT> chart = null;
    BarChart<XT, YT> barchart;
    switch (chartType) {
    case "LineChart":
      xAxis = new NumberAxis();
      chart = (XYChart<XT, YT>) new LineChart<String, Number>(xAxis, yAxis);
      break;
    case "BarChart":
      xAxis = new CategoryAxis();
      barchart = (BarChart<XT, YT>) new BarChart<String, Number>(xAxis, yAxis);
      barchart.setCategoryGap(0);
      barchart.setBarGap(1);
      chart = barchart;
      break;
    }
    xAxis.setLabel(xTitle);
    chart.setTitle(title);
    // defining a series
    series = new XYChart.Series<XT, YT>();
    series.setName(seriesName);
    ObservableList<Data<XT, YT>> seriesData = series.getData();
    if (forecast != null) {
      for (Forecast forecast : forecast.list) {
        String time = forecast.dt_txt.substring(8, 13) + "h";
        seriesData.add((Data<XT, YT>) new XYChart.Data<String, Number>(time,
            forecast.getPrecipitation()));
      }
    } else if (plan != null) {
      if (plan.getSprinklePeriods().size() > 2) {
        Date firstStart = plan.getSprinklePeriods().get(0).start;
        for (SprinklePeriod period : plan.getSprinklePeriods()) {
          Double time = (period.start.getTime()-firstStart.getTime())/1000.0/60/60/24;
          seriesData.add((Data<XT, YT>) new XYChart.Data<Number, Number>(time, period.mm));
        }
      }
    }
    chart.getData().add(series);
    return chart;
  }

}
