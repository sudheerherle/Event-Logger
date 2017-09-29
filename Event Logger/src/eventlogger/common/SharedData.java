// SharedData.java - Contains Shared Data between classes in different packages

/*$Id: SharedData.java,v 1.39 2013/11/07 14:30:11 herles Exp $*/
/*
 ******************************************************************************
 *                                                                            *
 *                                                                            *
 *                                                                            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published by   *
 * the Free Software Foundation; either version 2.1 of the License, or        *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public    *
 * License for more details.                                                  *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program; if not, write to the Free Software Foundation,    *
 * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.                              *
 *                                                                            *
 ******************************************************************************
 */
/**
 * <dl>
 * <dt>Purpose: To Share Data between classes
 * <dd>
 *
 * <dt>Description:
 * <dd> This is a Singleton Class that shares data between classes
 *
 * </dl>
 *
 * @version $Date: 2013/11/07 14:30:11 $
 * @author  Sudheer
 * @since   JDK 1.6.21
 */

package eventlogger.common;

import eventlogger.DataFrame;
import eventlogger.EventDetails;
import eventlogger.EventLoggerApp;
import eventlogger.PreviousStateExplorer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SharedData
{
    
    
    public Properties globalProps= null;
    public DataFrame DF_recieved;
    public boolean cleanInstall=false;
    public boolean event_downloaded = false;
    public LinkedList<EventDetails> event_list = new LinkedList();
    int GET_LOGGED_EVENTS_Resp_Length 	= 19;
    int SET_RTC_DATE_AND_TIME_Resp_Length 	= 10;	  
    int GET_RTC_DATE_TIME_Resp_Length	= 10;
    int ERASE_EVENTS_EEPROM_Resp_Length	= 6;
    int GET_DAC_STATUS_Resp_Length		= 12;
    int REPLY_TO_RECORDS_Resp_Length         = 19;		
    int GET_EVENT_COUNTS_Resp_Length         = 18;
    int GET_DAC_STATUS_END_Resp_Length       = 6;
    
    
    /**
    * Constructor  -
    *
    * @return      void
    */
   
    /**
    * Singleton class object get method
    *
    * @return       SharedData Object
    */
    public static SharedData getSingletonObject()
    {
    if (ref == null)
        // it's ok, we can call this constructor
        ref = new SharedData();
    return ref;
    }
    private int index =0;
    private byte[] buffer;
    public boolean dataRecievedFlag;
    public int AutoManual=1;
    public int FwdRev =1;
    public boolean connected = false;
    public boolean connectedToHardware = false;
    public boolean time_out = false;

    
    public String getWD() {
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(EventLoggerApp.class).getContext().getResourceMap(eventlogger.EventLoggerApp.class);
        String version = resourceMap.getString("Application.version");
        String wd = System.getProperty("user.home");
        if(System.getProperty("os.name").contains("Windows")){
            wd = System.getenv("APPDATA").replace("\\", "/")+"/Preceptor/"+version;
        }
        return wd;
    }
    
    public Properties getGlobalProps(){
    return globalProps;
    }
    

    public void setIndex(int ind){
        this.index = ind;
    }
    
    public  final static String getDateTime()
    {
    DateFormat df = new SimpleDateFormat("HH:mm:ss");
   // DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    return df.format(new Date());
    }
    
    public  final static String getDate()
    {
    //DateFormat df = new SimpleDateFormat("HH:mm:ss");
    DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    return df.format(new Date());
    }
    public  final static String getYear()
    {
    //DateFormat df = new SimpleDateFormat("HH:mm:ss");
    DateFormat df = new SimpleDateFormat("yyyy");
    return df.format(new Date());
    }
    
    public  final static String getMonth()
    {
    //DateFormat df = new SimpleDateFormat("HH:mm:ss");
    DateFormat df = new SimpleDateFormat("MM");
    return df.format(new Date());
    }
    public  final static String getDateOnly()
    {
    //DateFormat df = new SimpleDateFormat("HH:mm:ss");
    DateFormat df = new SimpleDateFormat("dd");
    return df.format(new Date());
    }
    
    public void dataRecieved(byte[] buffer) { 
        int count =0;
        if(buffer[1] ==0){
//            dataRecievedFlag = true;
            count  = (buffer.length-5)/19;
            for(int h=0;h<count*19;h=h+19){
            byte[] buf = new byte[19];
            System.arraycopy(buffer, h, buf, 0, 19);
                
            DF_recieved = new DataFrame();
            DF_recieved.CPU_address = buf[0];
            DF_recieved.CMD = buf[1];
            DF_recieved.data = new byte[GET_LOGGED_EVENTS_Resp_Length-6]; 

            System.arraycopy(buf, 2, DF_recieved.data, 0, DF_recieved.data.length);
            DF_recieved.Csum = (byte) ((byte) ((byte)buf[DF_recieved.data.length+2] << 8) 
                                         +((byte)buf[DF_recieved.data.length+3])); 
            
            EventDetails ed = new EventDetails();
            ed.Station_Name = EventLoggerApp.getApplication().getView().stnNameTxtField.getText();
            ed.event_ID = DF_recieved.data[0];            
            long event_time =0;
            String Str_event_date = "";
            event_time = (DF_recieved.data[4] & 0xFF) << 24;
            event_time = event_time + ((DF_recieved.data[3] & 0xFF) << 16);
            event_time = event_time + ((DF_recieved.data[2]&0xFF) << 8);
            event_time = event_time + (DF_recieved.data[1]&0xFF);
            event_time = event_time - (330*60);
            event_time = event_time * 1000;
            DateFormat event_date = new SimpleDateFormat("dd-MMM-yyyy  ");
            Date rtc_event_date_time = new Date(event_time);
            Str_event_date = event_date.format(rtc_event_date_time);
            DateFormat df_event_time = new SimpleDateFormat("HH:mm:ss");
            Str_event_date = Str_event_date + df_event_time.format(rtc_event_date_time);            
            ed.date_time = Str_event_date;            
            ed.US_FWD_Axle_Count = (int)((DF_recieved.data[6] & 0xFF) << 8) + (DF_recieved.data[5] & 0xFF);
            ed.US_REV_Axle_Count = (int)((DF_recieved.data[8] & 0xFF) << 8) + (DF_recieved.data[7] & 0xFF);
            ed.DS_FWD_Axle_Count = (int)((DF_recieved.data[10] & 0xFF) << 8) + (DF_recieved.data[9] & 0xFF);
            ed.DS_REV_Axle_Count = (int)((DF_recieved.data[12] & 0xFF) << 8) + (DF_recieved.data[11] & 0xFF); 
            ed.CPU_Addrs = DF_recieved.CPU_address;
            event_list.add(ed);  
            }
            dataRecievedFlag = true;
            event_downloaded = true;
            EventLoggerApp.getApplication().getView().UpdateEventList("All");
        }
        DF_recieved = new DataFrame();
        DF_recieved.CPU_address = buffer[0];
        DF_recieved.CMD = buffer[1];
        switch(DF_recieved.CMD){
            case 0:
            DF_recieved.data = new byte[GET_LOGGED_EVENTS_Resp_Length-6]; 
            break;
            case 1:
            DF_recieved.data = new byte[SET_RTC_DATE_AND_TIME_Resp_Length-6]; 
            break;
            case 2:
            DF_recieved.data = new byte[ERASE_EVENTS_EEPROM_Resp_Length-6]; 
            break;
            case 3:
            DF_recieved.data = new byte[GET_DAC_STATUS_Resp_Length-6]; 
            break;
            case 4:
            DF_recieved.data = new byte[GET_DAC_STATUS_END_Resp_Length-6]; 
            break;
            case 5:
            DF_recieved.data = new byte[ERASE_EVENTS_EEPROM_Resp_Length-6]; 
            break;
            case 6:
            DF_recieved.data = new byte[GET_EVENT_COUNTS_Resp_Length-6]; 
            break;
        }
        System.arraycopy(buffer, 2, DF_recieved.data, 0, DF_recieved.data.length);
        DF_recieved.Csum = (byte) ((byte) ((byte)buffer[DF_recieved.data.length+2] << 8) 
                                         +((byte)buffer[DF_recieved.data.length+3])); 
        dataRecievedFlag = true;
      //  EventLoggerApp.getApplication().getView().wait_for_resp();
    }
    public byte[] getBuffer(){
        return this.buffer;
    }
    public int getIndex(){
        return this.index;
    }
    public void setGlobalProps(Properties p){
    globalProps = p;
    }

    public LinkedList<EventDetails> get_logged_events(){
        return this.event_list;
    }    
    
    public void SaveAndApply(Properties prop){
            PreviousStateExplorer  pse = new PreviousStateExplorer();
            //advProp.setProperty("audible.tone", Boolean.toString(audibleNotificationChkBx.isSelected()));
            //advProp.setProperty("Generate Reports", String.valueOf(generateReportsCbox.isSelected()));
            if(prop==null){
                prop = getGlobalProps();
            }
            pse.advancedPropsSaver(prop);
            pse.saveToFile(getGlobalProps());
    }
    private static SharedData ref;


    public void sound(int hz, int msecs, double vol) {
        try {
            if (vol > 1.0 || vol < 0.0) {
                throw new IllegalArgumentException("Volume out of range 0.0- 1.0");
            }
            byte[] buf = new byte[msecs * 8];
            for (int i = 0; i < buf.length; i++) {
                double angle = i / (8000.0 / hz) * 2.0 * Math.PI;
                buf[i] = (byte) (Math.sin(angle) * 127.0 * vol);
            }
            // shape the front and back ends of the wave form
            for (int i = 0; i < 20 && i < buf.length / 2; i++) {
                buf[i] = (byte) (buf[i] * i / 20);
                buf[buf.length - 1 - i] = (byte) (buf[buf.length - 1 - i] *
        i / 20);
            }
            AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.close();
        } catch (LineUnavailableException ex) {
            //Exceptions.printStackTrace(ex);
        }
 }
}
