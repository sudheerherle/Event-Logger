/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

import eventlogger.common.SharedData;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author I14746
 */
public class DownTimeToolTipGenerator implements XYToolTipGenerator 
   { 
    SharedData sharedData = SharedData.getSingletonObject();
       public String generateToolTip(XYDataset xyDataset, int series, int item) 
          { 
             SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
             Number x = xyDataset.getX(series, item);
             Number y = xyDataset.getY(series, item);
             String ret = "Date: "+sdf.format(x);
             ret = ret + " | MDT: "+ sharedData.getDuration(y.longValue() * 3600);
             return ret;
          }
   }
