/*
 * EventLoggerView.java
 */

package eventlogger;

import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
//import com.itextpdf.text.BaseColor;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.field.RtfPageNumber;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import eventlogger.FooterTable;
//import eventlogger.FooterTable.FooterTable;
import eventlogger.common.SharedData;
import eventlogger.fileutilities.ExtensionFileFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.print.PrinterException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
//import javax.swing.text.Document;
import org.apache.pdfbox.PDFBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.omg.PortableServer.POAManagerPackage.State;
/**
 * The application's main frame.
 */
public class EventLoggerView extends FrameView {

    SharedData sharedData = SharedData.getSingletonObject();
    private SerialHelper sh = new SerialHelper();
    private static byte[] poll_port = new byte[]{0x1,0x0};
    private  TimerTask Blinker_Task ;
    String fullPath = "";
    UtilDateModel from_model = new UtilDateModel();
    UtilDateModel to_model = new UtilDateModel();
    private TimerTask Timeout_task;
    boolean isDebug = false;
    private TimerTask Write_Timeout_task;
    private boolean time_out = false;
    private SerialHelper serial_helper;
    private DataFrame DF_recieved;
    
    byte GET_LOGGED_EVENTS      =0;		/* Type: Command for getting logged events */
    byte SET_RTC_DATE_AND_TIME	=1;		/* Type: Command for Setting Date and Time */ 
    byte GET_RTC_DATE_TIME	=2;		/* Type: Command for getting system Date and Time */
    byte ERASE_EVENTS_EEPROM	=3;		/* Type: Command for Erasing all logged events */
    byte GET_DAC_STATUS         =4;
    byte REPLY_TO_RECORDS	=5;		/* Type: Reply of Record Reception status from Host/Smc */ 
    byte GET_EVENT_COUNTS	=6;		/* Type: Command for receving number of events, start date and end date*/ 
    
    int GET_LOGGED_EVENTS_Length 	= 6;
    int SET_RTC_DATE_AND_TIME_Length 	= 11;	  
    int GET_RTC_DATE_TIME_Length	= 4;
    int ERASE_EVENTS_EEPROM_Length	= 4;
    int GET_DAC_STATUS_Length		= 4;
    int REPLY_TO_RECORDS_Length         = 5;		
    int GET_EVENT_COUNTS_Length         = 4;
    
    int GET_LOGGED_EVENTS_Resp_Length 	= 19;
    int SET_RTC_DATE_AND_TIME_Resp_Length 	= 10;	  
    int GET_RTC_DATE_TIME_Resp_Length	= 10;
    int ERASE_EVENTS_EEPROM_Resp_Length	= 6;
    int GET_DAC_STATUS_Resp_Length		= 10;
    int REPLY_TO_RECORDS_Resp_Length         = 19;		
    int GET_EVENT_COUNTS_Resp_Length         = 18;
    private byte cpu_Addrs = 0;
    private String port;
    private TimerTask Com_Blinker_Task;
    private int unit_type;
    private String unit_type_txt ="";
    LinkedList<EventDetails> event_list = new LinkedList();
//    private CTableHandler tabHandle;
    private Timer timeout_timer = new Timer();
    private boolean Stop_Updating = false;
    private String Network_ID="NA";
    private int percent = 0;
    private String[] tableColumns = {"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Loc Fwd","Rem Fwd","Loc Rev","Rem Rev","Total Wheels"};
    private  MyTableModel model = new MyTableModel();    
    private SingleFrameApplication sfa = null;
    private long rtctime_diff;
    public EventLoggerView(SingleFrameApplication app) {
        super(app);
        sfa = app;
        initComponents();  
        
//        jTabbedPane1.addChangeListener(new ChangeListener() {
//        public void stateChanged(ChangeEvent e) {
//            int click = jTabbedPane1.getSelectedIndex();
//            System.out.println("Tab: " + click);
//            if(click == 6){
//                new ChartWorker().execute();    
//            }else if(click == 2){
//                Thread thread = new Thread(new UpdateRTCTime(sfa));
//                thread.start();
//            }else if(click == 3){
//                RefreshCounts();
//            }else if(click == 1){
//                refreshDACstatus();
//            }
//            else if(click == 4){
//                if(sharedData.connectedToHardware && (sharedData.event_list == null || sharedData.event_list.size() == 0)){
//                    int i = JOptionPane.showConfirmDialog(EventLoggerApp.getApplication().getMainFrame(), "Do you want to download the events from the event logger?", "Download events", JOptionPane.YES_NO_OPTION);
//                if(i==0){
//                    BtnDownloadEvents.doClick();
//                }
//                }
//            }
//        }
//        });
        isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
        getInputArguments().toString().indexOf("jdwp") >= 0;
//        JFrame frame = new JFrame();
        
          model = new MyTableModel();
          jTable1.setModel(model);
//          JTable table = new JTable(model);
//          frame.setLayout(new BorderLayout());
//                frame.add(new JScrollPane(table));
//                frame.pack();
//                frame.setVisible(true);
//        tabHandle =new CTableHandler(TblData);  
//        final CTableHandler model = new CTableHandler(TblData);
//                JTable table = new JTable(model);
        usb.setSelected(true);
        java.awt.Image mainLogo = Toolkit.getDefaultToolkit().getImage(EventLoggerView.class.getResource("resources/insys_logo_w200.png"));
        getFrame().setIconImage(mainLogo);
        DateFiled.setDate(new Date());
        Icon setting = getResourceMap().getIcon("Setting");
        jTabbedPane1.setIconAt(0, setting);
        setting = getResourceMap().getIcon("ssdac");
        jTabbedPane1.setIconAt(1, setting);
        setting = getResourceMap().getIcon("Datetime");
        jTabbedPane1.setIconAt(2, setting);
        setting = getResourceMap().getIcon("Download");
        jTabbedPane1.setIconAt(3, setting);
        setting = getResourceMap().getIcon("View");
        jTabbedPane1.setIconAt(4, setting);
        setting = getResourceMap().getIcon("Erase");
        jTabbedPane1.setIconAt(5, setting);
        setting = getResourceMap().getIcon("piechart");
        jTabbedPane1.setIconAt(6, setting);
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        
        port = "";
        if(usb.isSelected()){
            port = "USB";
        }   
        
         Blinker_Task = new TimerTask() {
            @Override
            public void run() {
            timelbl.setText(sharedData.getDateTime());
            datelbl.setText(sharedData.getDate());
            if(rtctime_diff!=0){
                Date dt = new Date();
                long timenow = dt.getTime();
                DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                rtctime_diff = rtctime_diff + 1000;
                String dmy = df.format(rtctime_diff);

                DateFormat h = new SimpleDateFormat("HH:mm:ss");   
                String hms = h.format(rtctime_diff);

                RTC_dateLbl.setText(dmy);
                timelbl1.setText(hms);
            }
            }
         };
         
        Timer timer = new Timer();        
        timer.scheduleAtFixedRate(Blinker_Task, 0, 1000);
        
        Timeout_task = new TimerTask() {
            @Override
            public void run() {
                time_out = false;
            }
        };
        
        Write_Timeout_task = new TimerTask() {
            @Override
            public void run() {
                time_out = true;
            }
        };       
        
        Com_Blinker_Task = new TimerTask() {
            @Override
            public void run() {
             //if(sharedData.connected == false){
             //    com_connect();
             //}
             if(sharedData.connected){
                 BtnConnect.setText("Disconnect");
                 if(connection_indicator_panel.getBackground()==Color.GREEN){
                     connection_indicator_panel.setBackground(Color.GRAY);
                 }else{
                     connection_indicator_panel.setBackground(Color.GREEN);
                 }
                 if(sharedData.connectedToHardware){
                 if(connection_indicator_panel1.getBackground()==Color.GREEN){
                     connection_indicator_panel1.setBackground(Color.GRAY);
                 }else{
                     connection_indicator_panel1.setBackground(Color.GREEN);
                 }
                 }else{
                     if(connection_indicator_panel1.getBackground()==Color.RED){
                    connection_indicator_panel1.setBackground(Color.GRAY);
                }else{
                    connection_indicator_panel1.setBackground(Color.RED);
                }
                }
            }else{
                BtnConnect.setText("Connect");
                if(connection_indicator_panel.getBackground()==Color.RED){
                    connection_indicator_panel.setBackground(Color.GRAY);
                }else{
                    connection_indicator_panel.setBackground(Color.RED);
                }
                if(connection_indicator_panel1.getBackground()==Color.RED){
                    connection_indicator_panel1.setBackground(Color.GRAY);
                }else{
                    connection_indicator_panel1.setBackground(Color.RED);
                }
            }
            }
         };
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(Com_Blinker_Task, 0, 300);
        jTabbedPane1.setSelectedIndex(0);
        if(sharedData.connected==false)
        BtnConnect.doClick();
        
//        UtilDateModel model = new UtilDateModel();
//model.setDate(20,04,2014);
// Need this...
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(from_model, p);
        // Don't know about the formatter, but there it is...
        JDatePickerImpl fromDate = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        
//        jPanel18.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100.0;
        jPanel18.add(fromDate,c);
//        UtilDateModel tomodel = new UtilDateModel();
        to_model.setValue(new Date());
        JDatePanelImpl todatePanel = new JDatePanelImpl(to_model, p);
        // Don't know about the formatter, but there it is...
        JDatePickerImpl toDate = new JDatePickerImpl(todatePanel, new DateLabelFormatter());
//        jPanel19.setLayout(new GridBagLayout());
        jPanel19.add(toDate,c);
        ButtonGroup bg = new ButtonGroup();
        bg.add(jRadioButton1);
        bg.add(jRadioButton2);
        jRadioButton1.setSelected(true);
        if(sharedData.event_list == null || sharedData.event_list.size() == 0){
            SwingUtilities.invokeLater(
              new Runnable() {
                public void run() {
                  jTabbedPane1.setSelectedIndex(1);
                }
              }
            );
        }
    }

     public boolean SendPacketRecieveResponse(DataFrame df){
        boolean retval  = false;    
        retval = SendData(df);
        return retval; 
    } 
    
         public class MyTableModel extends AbstractTableModel {

//        private String[] columnNames = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Local Forward","Remote Forward","Local Reverse","Remote Reverse","Total Train Wheels"};
        private List<RowData> data;

        public MyTableModel() {
            data = new ArrayList<RowData>(1);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public String getColumnName(int col) {
            return tableColumns[col];
        }

        @Override
        public int getColumnCount() {
            return tableColumns.length;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            RowData value = data.get(row);
            String d =  value.getData()[col];
//            System.out.println(d);
            return d;//col == 0 ? value.getDate() : value.getRow();
        }

        public void addRow(RowData value) {
            int rowCount = getRowCount();
            data.add(value);
            fireTableRowsInserted(rowCount, rowCount);
        }

        public void addRows(RowData... value) {
            addRows(Arrays.asList(value));
        }

        private void addRows(List<RowData> rows) {
            int rowCount = getRowCount();
            data.addAll(rows);
            fireTableRowsInserted(rowCount, getRowCount() - 1);
        }
    }
         
          public class TimeCellRenderer extends DefaultTableCellRenderer {

        private DateFormat df;

        public TimeCellRenderer() {
            df = new SimpleDateFormat("HH:mm:ss");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof Date) {

                value = df.format(value);

            }

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            return this;

        }
    }

     private static int ModRTU_CRC(byte[] buf)
        {
        int crc = 0xFFFF;
        int len = buf.length;
        for (int pos = 0; pos < len; pos++) {
        int temp = (int)(buf[pos] & 0xFF);
        crc ^= temp; // XOR byte into least sig. byte of crc

        for (int i = 8; i != 0; i--) { // Loop over each bit
        if ((crc & 0x0001) != 0) { // If the LSB is set
        crc >>= 1; // Shift right and XOR 0xA001
        crc ^= 0xA001;
        }
        else // Else LSB is not set
        crc >>= 1; // Just shift right
        }
        }
        // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
        return crc;
        }
     public void GiveResponse(final String string, final Color color) {
        
         javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
           lblStatus(string, color,3000);
        }
    });
//         Thread te = new Thread(new Runnable() {
//
//        public void run()
//        {
//            lblStatus(string, color,3000);
//        }
//        });
//        te.start();
    }
    
        public void lblStatus(String string,Color g,long time){
        Icon ic=null;
        final long timeOut  = time;
        lblStatus.setForeground(g);//setBackground(g);
        lblStatus.setText(string);
        lblStatus.setVisible(true);
        if(g==Color.RED){
          ic=this.getResourceMap().getIcon("wrong.icon");
        }
        else if(g==Color.BLUE){
           ic= this.getResourceMap().getIcon("correct.icon");
        }        
        else{
            ic =null;
        }
        if(string.equals("")){
          ic = null;
        }
        lblStatus.setIcon(ic);
        
        Thread te = new Thread(new Runnable() {

        public void run()
        {
            try{
                Sleep(timeOut);
                if(timeOut!=0){
                lblStatus.setText("");
                lblStatus.setIcon(null);
                ClearStatusLabel();
                }
            }catch(Exception x){
                
            }
        }
        });
        te.start();
        
    }
        
        private void Sleep(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException ex) {
            Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    private void ClearStatusLabel(){
        lblStatus.setText("");
        lblStatus.setIcon(null);
        lblStatus.setForeground(Color.BLACK);
    } 
    public boolean com_connect(String port){
        boolean retval = false;
//        try {
                controlAllButtons(false);
                sh.disconnect();
                sharedData.connected = false;
                sharedData.connectedToHardware = false;
                String[] ports = new String[1];
                if(port=="USB"){
                    ports = sh.getSerialPorts();
                    if(ports.length==0){
                        GiveResponse("Insys Digital SSDAC Event Logger not connected", Color.RED);
                        retval = false;
                    }
                }
                else ports[0] = port;                
                for(int p = 0; p< ports.length;p++){
                GiveResponse("Connecting to the event logger for the first time. Please wait...", Color.blue);  
                try{
                if(sh.connect(ports[p], 9600)){
                    GiveResponse("Connected to "+port, Color.BLUE);
                    sharedData.connected = true;
                    DataFrame df = new DataFrame();
                    df.CMD = GET_DAC_STATUS;
                    df.CPU_address = 0x55;
                    df.data = new byte[0];
                    if(SendPacketRecieveResponse(df)){
                     sharedData.connectedToHardware = true;
                     GiveResponse("Event logger is communicating successfully.", Color.BLUE);
                     retval =  true;
                     break;
                    }else{
                        retval = false;
                    }                    
                } }catch(IOException ex){
                    
                }
                }
        controlAllButtons(true);
        return retval;
    }
   
private void prepareChart(){
        jPanel17.removeAll();
        DefaultPieDataset dataset = new DefaultPieDataset(); 
        int normal=0;
        int error=0;
        int missing=0;
        //Image mainLogo = Toolkit.getDefaultToolkit().getImage(EventLoggerView.class.getResource("resources/insys_logo.png"));        
        String evt_desc = "";
        LinkedList<EventDetails> ed = sharedData.get_logged_events();
        for(int i=0;i<ed.size();i++){
            evt_desc  = get_event_desc(ed.get(i).event_ID);
            if(evt_desc.contains("Missing")){
                missing++;
            }else if(evt_desc.contains("Failure")
                    ||evt_desc.contains("Defective")
                     ||evt_desc.contains("Failed")
                     ||evt_desc.contains("Mismatch")
                     ||evt_desc.contains("Direct")
                     ||evt_desc.contains("Pulsating")
                     ||evt_desc.contains("NOT Detecting")
                     ||evt_desc.contains("Influence")
                     ||evt_desc.contains("Theft")
                     ||evt_desc.contains("Door Open")
                     ||evt_desc.contains("BAD")){
                error++;
            }else if(evt_desc.contains("Normal")||evt_desc.contains("Clear")){
                normal++;
            }
        }
        dataset.setValue("Error", error);//error
        dataset.setValue("Missing", missing);//missing
        dataset.setValue("Normal", normal);//normal
        JFreeChart chart3 = ChartFactory.createPieChart3D("SSDAC System Analysis", dataset, true, true, true);
        PiePlot3D plot3 = (PiePlot3D) chart3.getPlot();
        plot3.setDarkerSides(true);
        plot3.setForegroundAlpha(0.7f);        
        plot3.setCircular(true);
        jPanel17.add(new ChartPanel(chart3));
        jPanel17.repaint(); 
        jPanel17.invalidate();
}
    
    public boolean com_disconnect(){
        boolean retval = false;
        try{
            sh.disconnect();
            GiveResponse("Disconnected from "+port, Color.RED);
            sharedData.connected = false;
            retval = true;
        }
        catch(Exception ex){
            retval = false;
        }
        return retval;
    }
        private boolean serialPortWrite(byte[] data){
        boolean retval = false;
        try {
            if(sh.getSerialOutputStream() == null){
               if(com_connect(port)){
               sh.getSerialOutputStream().write(data);
               retval =  true;
            }
            else retval = false;
            }
            else{
               sh.getSerialOutputStream().write(data);
               retval =  true;
            }
        } catch (IOException ex) {
            sharedData.connected = false;
            sharedData.connectedToHardware = false;
            GiveResponse("Port was not found or in use...", Color.red);
            retval = false;
            //Logger.getLogger(SimpleSerialPort.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }     
        
    void Buttons(boolean b, boolean progress){
        progressBar.setIndeterminate(progress);
        BtnConnect.setEnabled(b);
        jTabbedPane1.setEnabledAt(0,b);
        jTabbedPane1.setEnabledAt(1,b);
        jTabbedPane1.setEnabledAt(2,b);
        jTabbedPane1.setEnabledAt(3,b);
        jTabbedPane1.setEnabledAt(4,b);
        jTabbedPane1.setEnabledAt(5,b);  
        jTabbedPane1.setEnabledAt(6,b);  
        jButton2.setEnabled(b);
        jButton3.setEnabled(b);
        cpuselectCmbBx.setEnabled(b);
        jButton12.setEnabled(b);
        BtnRefresh.setEnabled(b);
        BtnDownloadEvents.setEnabled(b);
        BtnGetDACStatus.setEnabled(b);
        jButton5.setEnabled(b);
        jButton6.setEnabled(b);
        jButton7.setEnabled(b);
        jButton8.setEnabled(b);
//        TblData.setEnabled(b);
        BtnEraseEventsinLogger.setEnabled(b);  
    }  
    private void controlAllButtons(boolean b) {
        if(b==false){
            progressBar.setIndeterminate(true);
        }
        else{
           progressBar.setIndeterminate(false);
        }
       Buttons(b,!b);
     }
    private int get_size_of_packet(int cmd){
        int size =0;
        switch(cmd){
            case 0:
                size = GET_LOGGED_EVENTS_Length;
                break;
            case 1:
                size = SET_RTC_DATE_AND_TIME_Length;
                break;
            case 2:
                size = GET_RTC_DATE_TIME_Length;
                break;
            case 3:
                size = ERASE_EVENTS_EEPROM_Length;
                break;
            case 4:
                size = GET_DAC_STATUS_Length;
                break;
            case 5:
                size = REPLY_TO_RECORDS_Length;
                break;
            case 6:
                size = GET_EVENT_COUNTS_Length;
                break;
                       
        }
        return size;
    }
    private byte[] getArrayFromPacket(DataFrame df){
        int size_of_packet = get_size_of_packet(df.CMD);
        byte[] array_data  = new byte[size_of_packet];
        array_data[0] = df.CPU_address;
        array_data[1] = df.CMD;
        System.arraycopy(df.data, 0, array_data, 2, array_data.length-4);
        byte[] temp = new byte[size_of_packet-2];
        System.arraycopy(array_data, 0, temp, 0, temp.length);
        int crc16 = ModRTU_CRC(temp);
        array_data[size_of_packet-2] = (byte) (crc16 >>8 & 0xFF);
        array_data[size_of_packet-1] = (byte) (crc16 & 0xFF);
        return array_data;
    }
    
    
    private boolean SendData(DataFrame frame){
        byte[] data_to_send = getArrayFromPacket(frame);
        System.out.println("Sent: "+Arrays.toString(data_to_send));
        sharedData.dataRecievedFlag = false;
        if(serialPortWrite(data_to_send)){
           return wait_for_resp();  
        }
        else{             
            return false;
        }
    }
    
        public boolean wait_for_resp(){
        boolean retval = false;
        sharedData.time_out = false;
        //timeout_timer.schedule(new TimerThread(), 3000);
        while (sharedData.dataRecievedFlag==false/* && sharedData.time_out == false*/){
            Thread.yield();
        }
//        if(sharedData.time_out){
//            sharedData.time_out = false;
//            sh.disconnect(); 
//            return false;
//        }
        sharedData.dataRecievedFlag = false;
        DF_recieved = sharedData.DF_recieved;
        int cmd = DF_recieved.CMD;
        switch(cmd){
            
//            case 0:
//            wait_for_resp();
//            break;
            case 1:         // GET RTC DATE TIME
            long mktime =0;
            mktime = (DF_recieved.data[3] & 0xFF) << 24;
            mktime = mktime + ((DF_recieved.data[2] & 0xFF) << 16);
            mktime = mktime + ((DF_recieved.data[1]&0xFF) << 8);
            mktime = mktime + (DF_recieved.data[0]&0xFF);
            mktime = mktime - (330*60);
            mktime = mktime * 1000;
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            Date rtc_date_time = new Date(mktime);
            Date time_now = new Date();
            long timenow = time_now.getTime();
            long rtctime = rtc_date_time.getTime();
            long diff = timenow - rtctime;    
            rtctime_diff = rtctime;
            RTC_dateLbl.setText(df.format(rtc_date_time));
            DateFormat df_time = new SimpleDateFormat("HH:mm:ss");
            timelbl1.setText((df_time.format(rtc_date_time)));      
            this.getFrame().repaint();
            this.getFrame().validate();
            GiveResponse("Updated the date and time from Event logger", Color.BLUE);
            if(diff > 60000 || diff < -60000){
                JOptionPane.showMessageDialog(this.getFrame(), "Time mismatch of more than a minute is observed.\nPlease consider setting correct time.", "Time mismatch", JOptionPane.ERROR_MESSAGE);
            }
            retval = true;
            break;   
            
            case 2:
            lblStatus.setText("Event logger is erasing all the events. Please wait...");
            lblStatus.setForeground(Color.BLUE);
            wait_for_resp();
            retval = true;
            break;
            
            case 3:
            this.cpu_Addrs = DF_recieved.CPU_address;
            this.unit_type = (int)DF_recieved.data[0];
            this.Network_ID = Long.toString(0xff & (long)DF_recieved.data[1]);
            
            UpdateStatusPanel();
            retval = true;
            break;
            case 4:
            GiveResponse("Updated all the events", Color.BLUE);
            retval = true;
            break;
            case 5:
            GiveResponse("Sucessfully erased all events from Event Logger", Color.BLUE);
            sharedData.getSingletonObject().event_list.clear();
            retval = true;
            break;
           
            case 6:
            int total_events = 0;
            total_events = total_events + (DF_recieved.data[0] & 0xFF);
            total_events = total_events  + ((DF_recieved.data[1] & 0xFF) << 8);
            total_events = (total_events ) + ((DF_recieved.data[2] & 0xFF) << 16);
            total_events = (total_events ) + ((DF_recieved.data[3] & 0xFF)<< 24);
            
            long from_time =0;
            String Str_from_date = "";
            from_time = (DF_recieved.data[7] & 0xFF) << 24;
            from_time = from_time + ((DF_recieved.data[6] & 0xFF) << 16);
            from_time = from_time + ((DF_recieved.data[5]&0xFF) << 8);
            from_time = from_time + (DF_recieved.data[4]&0xFF);
            from_time = from_time - (330*60);
            from_time = from_time * 1000;
            if(from_time == 0){
               Str_from_date = "N/A"; 
            }else{
                DateFormat from_date = new SimpleDateFormat("dd-MMM-yyyy  ");
                Date rtc_from_date_time = new Date(from_time);
                Str_from_date = from_date.format(rtc_from_date_time);
                DateFormat df_from_time = new SimpleDateFormat("HH:mm:ss");
                Str_from_date = Str_from_date + df_from_time.format(rtc_from_date_time);
            }
            long to_time =0;
            String Str_to_date = "";
            to_time = (DF_recieved.data[11] & 0xFF) << 24;
            to_time = to_time + ((DF_recieved.data[10] & 0xFF) << 16);
            to_time = to_time + ((DF_recieved.data[9]&0xFF) << 8);
            to_time = to_time + (DF_recieved.data[8]&0xFF);
            to_time = to_time - (330*60);
            to_time = to_time * 1000;
            if(to_time == 0){
               Str_to_date = "N/A"; 
            }else{
                DateFormat to_date = new SimpleDateFormat("dd-MMM-yyyy  ");
                Date rtc_to_date_time = new Date(to_time);
                Str_to_date = to_date.format(rtc_to_date_time);
                DateFormat df_to_time = new SimpleDateFormat("HH:mm:ss");
                Str_to_date = Str_to_date + df_to_time.format(rtc_to_date_time);            
            }
            EventStatus.setValueAt(total_events, 0, 1);
            EventStatus.setValueAt(Str_from_date, 1, 1);
            EventStatus.setValueAt(Str_to_date, 2, 1);
            
            GiveResponse("Event details updated", Color.BLUE);
            retval = true;
            break;           
        }
        return retval;
    }
   
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        BtnConnect = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        usb = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        stnNameTxtField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BtnGetDACStatus = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        networkIDField = new javax.swing.JTextField();
        dpField = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        EnterStnNameHint = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        DateFiled = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        HrSpinner = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        MinSpinner = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        SecSpinner = new javax.swing.JSpinner();
        jPanel11 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        datelbl = new javax.swing.JLabel();
        timelbl = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        timelbl1 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        RTC_dateLbl = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        BtnDownloadEvents = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        EventStatus = new javax.swing.JTable();
        BtnRefresh = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel18 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        cpuselectCmbBx = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        BtnEraseEventsinLogger = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        ErasePwdTxtField = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        connection_indicator_panel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        connection_indicator_panel1 = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(eventlogger.EventLoggerApp.class).getContext().getResourceMap(EventLoggerView.class);
        jTabbedPane1.setFont(resourceMap.getFont("jTabbedPane1.font")); // NOI18N
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e)
            {
                //       if(jTabbedPane1.getSelectedIndex()==6){
                    //          Thread RefreshTable = new Thread(new Runnable() {
                        //
                        //            public void run()
                        //            {
                            //              PieChart();
                            //            }
                        //            });
                //            RefreshTable.start();
                //
                //        }
        }
    });

    jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("jPanel3.border.highlightColor"), resourceMap.getColor("jPanel8.border.shadowColor"))); // NOI18N
    jPanel3.setName("jPanel3"); // NOI18N

    BtnConnect.setFont(resourceMap.getFont("BtnConnect.font")); // NOI18N
    BtnConnect.setText(resourceMap.getString("BtnConnect.text")); // NOI18N
    BtnConnect.setName("BtnConnect"); // NOI18N
    BtnConnect.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            BtnConnectActionPerformed(evt);
        }
    });

    jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    jPanel4.setName("jPanel4"); // NOI18N

    usb.setText(resourceMap.getString("usb.text")); // NOI18N
    usb.setName("usb"); // NOI18N
    usb.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            usbActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(usb, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
            .addContainerGap())
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(usb)
            .addContainerGap(8, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(BtnConnect, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(521, 521, 521))
    );

    jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BtnConnect, jPanel4});

    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(12, 12, 12)
            .addComponent(BtnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(427, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("jPanel3.border.highlightColor"), resourceMap.getColor("jPanel8.border.shadowColor"))); // NOI18N
    jPanel1.setName("jPanel1"); // NOI18N

    jLabel1.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N

    stnNameTxtField.setText(resourceMap.getString("stnNameTxtField.text")); // NOI18N
    stnNameTxtField.setName("stnNameTxtField"); // NOI18N
    stnNameTxtField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            stnNameTxtFieldKeyReleased(evt);
        }
    });

    jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N

    jLabel2.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N

    BtnGetDACStatus.setFont(resourceMap.getFont("BtnGetDACStatus.font")); // NOI18N
    BtnGetDACStatus.setText(resourceMap.getString("BtnGetDACStatus.text")); // NOI18N
    BtnGetDACStatus.setName("BtnGetDACStatus"); // NOI18N
    BtnGetDACStatus.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            BtnGetDACStatusActionPerformed(evt);
        }
    });

    jLabel5.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N

    networkIDField.setEditable(false);
    networkIDField.setName("networkIDField"); // NOI18N

    dpField.setEditable(false);
    dpField.setName("dpField"); // NOI18N

    jTextField4.setEditable(false);
    jTextField4.setName("cpu_Addrs_txtfield"); // NOI18N

    EnterStnNameHint.setForeground(resourceMap.getColor("EnterStnNameHint.foreground")); // NOI18N
    EnterStnNameHint.setText(resourceMap.getString("EnterStnNameHint.text")); // NOI18N
    EnterStnNameHint.setName("EnterStnNameHint"); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(30, 30, 30)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(BtnGetDACStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(27, 27, 27)
                            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(dpField, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                .addComponent(stnNameTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(networkIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(EnterStnNameHint, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(279, 279, 279))
    );

    jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dpField, jTextField4, networkIDField, stnNameTxtField});

    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(34, 34, 34)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(stnNameTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(EnterStnNameHint))
            .addGap(18, 18, 18)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(networkIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(19, 19, 19)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(dpField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(13, 13, 13)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(30, 30, 30)
            .addComponent(BtnGetDACStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(288, Short.MAX_VALUE))
    );

    jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {EnterStnNameHint, dpField, jLabel1, jLabel2, jLabel3, jLabel5, jTextField4, networkIDField, stnNameTxtField});

    jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

    jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("jPanel3.border.highlightColor"), resourceMap.getColor("jPanel8.border.shadowColor"))); // NOI18N
    jPanel6.setName("jPanel6"); // NOI18N
    jPanel6.setLayout(new java.awt.GridLayout(2, 2));

    jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel10.border.title"), 0, 0, resourceMap.getFont("jPanel10.border.titleFont"))); // NOI18N
    jPanel10.setFont(resourceMap.getFont("jPanel11.font")); // NOI18N
    jPanel10.setName("jPanel10"); // NOI18N

    jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
    jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
    jLabel7.setName("jLabel7"); // NOI18N

    DateFiled.setName("DateFiled"); // NOI18N

    jLabel8.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
    jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
    jLabel8.setName("jLabel8"); // NOI18N

    jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
    jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
    jButton2.setName("jButton2"); // NOI18N
    jButton2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
        }
    });

    jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    jPanel16.setName("jPanel16"); // NOI18N

    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
    jLabel10.setName("jLabel10"); // NOI18N

    HrSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
    HrSpinner.setName("HrSpinner"); // NOI18N

    jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
    jLabel12.setName("jLabel12"); // NOI18N

    MinSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
    MinSpinner.setName("MinSpinner"); // NOI18N

    jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
    jLabel11.setName("jLabel11"); // NOI18N

    SecSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
    SecSpinner.setName("SecSpinner"); // NOI18N

    javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
    jPanel16.setLayout(jPanel16Layout);
    jPanel16Layout.setHorizontalGroup(
        jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel16Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(HrSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel11)
            .addGap(1, 1, 1)
            .addComponent(MinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(SecSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel16Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {HrSpinner, MinSpinner, SecSpinner});

    jPanel16Layout.setVerticalGroup(
        jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel16Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(MinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(HrSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(SecSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    jPanel16Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {HrSpinner, MinSpinner, SecSpinner, jLabel10, jLabel11, jLabel12});

    javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
    jPanel10.setLayout(jPanel10Layout);
    jPanel10Layout.setHorizontalGroup(
        jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel10Layout.createSequentialGroup()
            .addContainerGap(32, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DateFiled, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))))
            .addContainerGap(69, Short.MAX_VALUE))
    );
    jPanel10Layout.setVerticalGroup(
        jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel10Layout.createSequentialGroup()
            .addGap(25, 25, 25)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel7)
                .addComponent(DateFiled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel8)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, Short.MAX_VALUE)
            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(74, Short.MAX_VALUE))
    );

    jPanel10Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DateFiled, jLabel7});

    jPanel10Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, jPanel16});

    jPanel6.add(jPanel10);

    jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel11.border.title"), 0, 0, resourceMap.getFont("jPanel10.border.titleFont"))); // NOI18N
    jPanel11.setFont(resourceMap.getFont("jPanel11.font")); // NOI18N
    jPanel11.setName("jPanel11"); // NOI18N

    jButton3.setFont(resourceMap.getFont("jButton3.font")); // NOI18N
    jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
    jButton3.setName("jButton3"); // NOI18N
    jButton3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton3ActionPerformed(evt);
        }
    });

    jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
    jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
    jLabel13.setName("jLabel13"); // NOI18N

    jLabel14.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
    jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
    jLabel14.setName("jLabel14"); // NOI18N

    datelbl.setFont(resourceMap.getFont("timelbl.font")); // NOI18N
    datelbl.setForeground(resourceMap.getColor("timelbl.foreground")); // NOI18N
    datelbl.setText(resourceMap.getString("datelbl.text")); // NOI18N
    datelbl.setName("datelbl"); // NOI18N

    timelbl.setFont(resourceMap.getFont("timelbl.font")); // NOI18N
    timelbl.setForeground(resourceMap.getColor("timelbl.foreground")); // NOI18N
    timelbl.setText(resourceMap.getString("timelbl.text")); // NOI18N
    timelbl.setName("timelbl"); // NOI18N

    javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
    jPanel11.setLayout(jPanel11Layout);
    jPanel11Layout.setHorizontalGroup(
        jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel11Layout.createSequentialGroup()
            .addContainerGap(54, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(datelbl, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timelbl, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))))
            .addContainerGap(66, Short.MAX_VALUE))
    );
    jPanel11Layout.setVerticalGroup(
        jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel11Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel13)
                .addComponent(datelbl))
            .addGap(18, 18, 18)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel14)
                .addComponent(timelbl))
            .addGap(18, 18, Short.MAX_VALUE)
            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(74, Short.MAX_VALUE))
    );

    jPanel11Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {datelbl, jLabel13, jLabel14, timelbl});

    jPanel6.add(jPanel11);

    jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel13.border.title"), 0, 0, resourceMap.getFont("jPanel13.border.titleFont"))); // NOI18N
    jPanel13.setFont(resourceMap.getFont("jPanel13.font")); // NOI18N
    jPanel13.setName("jPanel13"); // NOI18N

    jButton12.setFont(resourceMap.getFont("jButton12.font")); // NOI18N
    jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
    jButton12.setName("jButton12"); // NOI18N
    jButton12.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton12ActionPerformed(evt);
        }
    });

    timelbl1.setFont(resourceMap.getFont("timelbl1.font")); // NOI18N
    timelbl1.setForeground(resourceMap.getColor("timelbl1.foreground")); // NOI18N
    timelbl1.setText(resourceMap.getString("timelbl1.text")); // NOI18N
    timelbl1.setName("timelbl1"); // NOI18N

    jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
    jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
    jLabel15.setName("jLabel15"); // NOI18N

    jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
    jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
    jLabel16.setName("jLabel16"); // NOI18N

    RTC_dateLbl.setFont(resourceMap.getFont("RTC_dateLbl.font")); // NOI18N
    RTC_dateLbl.setForeground(resourceMap.getColor("RTC_dateLbl.foreground")); // NOI18N
    RTC_dateLbl.setText(resourceMap.getString("RTC_dateLbl.text")); // NOI18N
    RTC_dateLbl.setName("RTC_dateLbl"); // NOI18N

    javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
    jPanel13.setLayout(jPanel13Layout);
    jPanel13Layout.setHorizontalGroup(
        jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel13Layout.createSequentialGroup()
            .addGap(51, 51, 51)
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel16)
                        .addComponent(jLabel15))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(RTC_dateLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                        .addComponent(timelbl1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))))
            .addGap(237, 237, 237))
    );
    jPanel13Layout.setVerticalGroup(
        jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel13Layout.createSequentialGroup()
            .addGap(26, 26, 26)
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(RTC_dateLbl)
                .addComponent(jLabel15))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(timelbl1)
                .addComponent(jLabel16))
            .addGap(18, 18, 18)
            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(71, Short.MAX_VALUE))
    );

    jPanel13Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel16, timelbl1});

    jPanel13Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {RTC_dateLbl, jLabel15});

    jPanel6.add(jPanel13);

    jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

    jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("jPanel3.border.highlightColor"), resourceMap.getColor("jPanel8.border.shadowColor"))); // NOI18N
    jPanel12.setName("jPanel12"); // NOI18N

    BtnDownloadEvents.setFont(resourceMap.getFont("BtnDownloadEvents.font")); // NOI18N
    BtnDownloadEvents.setIcon(resourceMap.getIcon("BtnDownloadEvents.icon")); // NOI18N
    BtnDownloadEvents.setText(resourceMap.getString("BtnDownloadEvents.text")); // NOI18N
    BtnDownloadEvents.setName("BtnDownloadEvents"); // NOI18N
    BtnDownloadEvents.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            BtnDownloadEventsActionPerformed(evt);
        }
    });

    jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel15.border.title"), 0, 0, resourceMap.getFont("jPanel15.border.titleFont"))); // NOI18N
    jPanel15.setName("jPanel15"); // NOI18N

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    EventStatus.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {"Total Events", null},
            {"Events Available from", null},
            {"Events Available upto", null}
        },
        new String [] {
            "Property", "Values"
        }
    ));
    EventStatus.setName("EventStatus"); // NOI18N
    jScrollPane2.setViewportView(EventStatus);

    BtnRefresh.setFont(resourceMap.getFont("BtnRefresh.font")); // NOI18N
    BtnRefresh.setIcon(resourceMap.getIcon("BtnRefresh.icon")); // NOI18N
    BtnRefresh.setText(resourceMap.getString("BtnRefresh.text")); // NOI18N
    BtnRefresh.setName("BtnRefresh"); // NOI18N
    BtnRefresh.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            BtnRefreshActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
    jPanel15.setLayout(jPanel15Layout);
    jPanel15Layout.setHorizontalGroup(
        jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(BtnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
            .addContainerGap())
    );
    jPanel15Layout.setVerticalGroup(
        jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel15Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(BtnRefresh))
    );

    jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel5.border.title"))); // NOI18N
    jPanel5.setName("jPanel5"); // NOI18N

    jRadioButton1.setText(resourceMap.getString("jRadioButton1.text")); // NOI18N
    jRadioButton1.setName("jRadioButton1"); // NOI18N
    jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButton1ActionPerformed(evt);
        }
    });

    jRadioButton2.setText(resourceMap.getString("jRadioButton2.text")); // NOI18N
    jRadioButton2.setName("jRadioButton2"); // NOI18N
    jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButton2ActionPerformed(evt);
        }
    });

    jPanel18.setName("jPanel18"); // NOI18N
    jPanel18.setLayout(new java.awt.GridBagLayout());

    jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
    jLabel18.setIconTextGap(0);
    jLabel18.setMinimumSize(null);
    jLabel18.setName("jLabel18"); // NOI18N
    jLabel18.setPreferredSize(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel18.add(jLabel18, gridBagConstraints);

    jPanel19.setName("jPanel19"); // NOI18N
    jPanel19.setLayout(new java.awt.GridBagLayout());

    jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
    jLabel19.setIconTextGap(0);
    jLabel19.setName("jLabel19"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel19.add(jLabel19, gridBagConstraints);

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jRadioButton1)
                .addComponent(jRadioButton2)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                        .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))))
            .addContainerGap())
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jRadioButton1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jRadioButton2)
            .addGap(7, 7, 7)
            .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(37, Short.MAX_VALUE))
    );

    jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel18, jPanel19});

    javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
    jPanel12.setLayout(jPanel12Layout);
    jPanel12Layout.setHorizontalGroup(
        jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel12Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                    .addGap(457, 457, 457))
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addComponent(BtnDownloadEvents, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(457, Short.MAX_VALUE))))
    );

    jPanel12Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BtnDownloadEvents, jPanel15, jPanel5});

    jPanel12Layout.setVerticalGroup(
        jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel12Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(BtnDownloadEvents, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(122, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab(resourceMap.getString("jPanel12.TabConstraints.tabTitle"), jPanel12); // NOI18N

    jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("jPanel3.border.highlightColor"), resourceMap.getColor("jPanel8.border.shadowColor"))); // NOI18N
    jPanel7.setName("jPanel7"); // NOI18N

    jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel14.border.title"), 0, 0, resourceMap.getFont("jPanel14.border.titleFont"))); // NOI18N
    jPanel14.setName("jPanel14"); // NOI18N
    jPanel14.setLayout(new java.awt.GridLayout(1, 0));

    jButton5.setFont(resourceMap.getFont("jButton5.font")); // NOI18N
    jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
    jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
    jButton5.setName("jButton5"); // NOI18N
    jButton5.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton5ActionPerformed(evt);
        }
    });
    jPanel14.add(jButton5);

    jButton6.setFont(resourceMap.getFont("jButton5.font")); // NOI18N
    jButton6.setIcon(resourceMap.getIcon("jButton6.icon")); // NOI18N
    jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
    jButton6.setName("jButton6"); // NOI18N
    jButton6.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton6ActionPerformed(evt);
        }
    });
    jPanel14.add(jButton6);

    jButton7.setFont(resourceMap.getFont("jButton5.font")); // NOI18N
    jButton7.setIcon(resourceMap.getIcon("jButton7.icon")); // NOI18N
    jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
    jButton7.setName("jButton7"); // NOI18N
    jButton7.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton7ActionPerformed(evt);
        }
    });
    jPanel14.add(jButton7);

    jButton8.setFont(resourceMap.getFont("jButton5.font")); // NOI18N
    jButton8.setIcon(resourceMap.getIcon("jButton8.icon")); // NOI18N
    jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
    jButton8.setName("jButton8"); // NOI18N
    jButton8.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton8ActionPerformed(evt);
        }
    });
    jPanel14.add(jButton8);

    jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
    jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
    jButton4.setName("jButton4"); // NOI18N
    jButton4.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton4ActionPerformed(evt);
        }
    });

    jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
    jLabel17.setName("jLabel17"); // NOI18N

    cpuselectCmbBx.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "CPU-1", "CPU-2", "CPU-3", "CPU-4", "CPU-5", "CPU-6", "CPU-7", "CPU-8" }));
    cpuselectCmbBx.setName("cpuselectCmbBx"); // NOI18N
    cpuselectCmbBx.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cpuselectCmbBxActionPerformed(evt);
        }
    });

    jScrollPane3.setName("jScrollPane3"); // NOI18N

    jTable1.setModel(model);
    jTable1.setName("jTable1"); // NOI18N
    jScrollPane3.setViewportView(jTable1);

    javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
    jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(
        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 820, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jLabel17)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cpuselectCmbBx, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );
    jPanel7Layout.setVerticalGroup(
        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel17)
                .addComponent(cpuselectCmbBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(14, 14, 14))
    );

    jTabbedPane1.addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

    jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("jPanel3.border.highlightColor"), resourceMap.getColor("jPanel8.border.shadowColor"))); // NOI18N
    jPanel8.setName("jPanel8"); // NOI18N

    BtnEraseEventsinLogger.setFont(resourceMap.getFont("BtnEraseEventsinLogger.font")); // NOI18N
    BtnEraseEventsinLogger.setText(resourceMap.getString("BtnEraseEventsinLogger.text")); // NOI18N
    BtnEraseEventsinLogger.setName("BtnEraseEventsinLogger"); // NOI18N
    BtnEraseEventsinLogger.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            BtnEraseEventsinLoggerActionPerformed(evt);
        }
    });

    jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
    jLabel9.setName("jLabel9"); // NOI18N

    ErasePwdTxtField.setText(resourceMap.getString("ErasePwdTxtField.text")); // NOI18N
    ErasePwdTxtField.setName("ErasePwdTxtField"); // NOI18N

    javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
    jPanel8.setLayout(jPanel8Layout);
    jPanel8Layout.setHorizontalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel8Layout.createSequentialGroup()
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(jLabel9)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(ErasePwdTxtField))
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(BtnEraseEventsinLogger)))
            .addContainerGap(589, Short.MAX_VALUE))
    );
    jPanel8Layout.setVerticalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel8Layout.createSequentialGroup()
            .addGap(22, 22, 22)
            .addComponent(BtnEraseEventsinLogger, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(ErasePwdTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(435, Short.MAX_VALUE))
    );

    jPanel8Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ErasePwdTxtField, jLabel9});

    jTabbedPane1.addTab(resourceMap.getString("jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("jPanel2.border.highlightColor"), resourceMap.getColor("jPanel2.border.shadowColor"))); // NOI18N
    jPanel2.setName("jPanel2"); // NOI18N

    jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
    jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
    jButton1.setName("jButton1"); // NOI18N
    jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });

    jPanel17.setName("jPanel17"); // NOI18N
    jPanel17.setLayout(new java.awt.GridLayout(1, 0));

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jButton1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
            .addContainerGap())
    );

    jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

    jPanel9.setBorder(new javax.swing.border.SoftBevelBorder(0));
    jPanel9.setName("jPanel9"); // NOI18N

    lblStatus.setName("lblStatus"); // NOI18N

    connection_indicator_panel.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
    connection_indicator_panel.setName("connection_indicator_panel"); // NOI18N

    javax.swing.GroupLayout connection_indicator_panelLayout = new javax.swing.GroupLayout(connection_indicator_panel);
    connection_indicator_panel.setLayout(connection_indicator_panelLayout);
    connection_indicator_panelLayout.setHorizontalGroup(
        connection_indicator_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 22, Short.MAX_VALUE)
    );
    connection_indicator_panelLayout.setVerticalGroup(
        connection_indicator_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 18, Short.MAX_VALUE)
    );

    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N

    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N

    connection_indicator_panel1.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
    connection_indicator_panel1.setName("connection_indicator_panel1"); // NOI18N

    javax.swing.GroupLayout connection_indicator_panel1Layout = new javax.swing.GroupLayout(connection_indicator_panel1);
    connection_indicator_panel1.setLayout(connection_indicator_panel1Layout);
    connection_indicator_panel1Layout.setHorizontalGroup(
        connection_indicator_panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 22, Short.MAX_VALUE)
    );
    connection_indicator_panel1Layout.setVerticalGroup(
        connection_indicator_panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 18, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
    jPanel9.setLayout(jPanel9Layout);
    jPanel9Layout.setHorizontalGroup(
        jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel9Layout.createSequentialGroup()
            .addGap(52, 52, 52)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 977, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addComponent(jLabel4)
                    .addGap(18, 18, 18)
                    .addComponent(connection_indicator_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel6)
                    .addGap(18, 18, 18)
                    .addComponent(connection_indicator_panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    jPanel9Layout.setVerticalGroup(
        jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel9Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connection_indicator_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connection_indicator_panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel9Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {connection_indicator_panel, jLabel4});

    javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(mainPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jTabbedPane1, 0, 1045, Short.MAX_VALUE)
            .addContainerGap())
        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap()))
    );
    mainPanelLayout.setVerticalGroup(
        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
            .addGap(110, 110, 110))
        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(574, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap()))
    );

    menuBar.setName("menuBar"); // NOI18N

    fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
    fileMenu.setName("fileMenu"); // NOI18N

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(eventlogger.EventLoggerApp.class).getContext().getActionMap(EventLoggerView.class, this);
    exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
    exitMenuItem.setName("exitMenuItem"); // NOI18N
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
    jMenu1.setName("jMenu1"); // NOI18N

    jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
    jMenuItem1.setName("jMenuItem1"); // NOI18N
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem1ActionPerformed(evt);
        }
    });
    jMenu1.add(jMenuItem1);

    jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
    jMenuItem2.setName("jMenuItem2"); // NOI18N
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem2ActionPerformed(evt);
        }
    });
    jMenu1.add(jMenuItem2);

    menuBar.add(jMenu1);

    statusPanel.setName("statusPanel"); // NOI18N

    statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

    progressBar.setName("progressBar"); // NOI18N

    javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
    statusPanel.setLayout(statusPanelLayout);
    statusPanelLayout.setHorizontalGroup(
        statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1065, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 1041, Short.MAX_VALUE)
            .addGap(14, 14, 14))
    );
    statusPanelLayout.setVerticalGroup(
        statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(statusPanelLayout.createSequentialGroup()
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    setComponent(mainPanel);
    setMenuBar(menuBar);
    setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void BtnEraseEventsinLoggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEraseEventsinLoggerActionPerformed
        String pwd = ErasePwdTxtField.getText();
        if(pwd.equals("insys123")==false){
           JOptionPane.showMessageDialog(this.getFrame(), "Please enter correct password to erase all the events in Event Logger", "Password", JOptionPane.ERROR_MESSAGE);
           return;
        }        
        Thread erase_events = new Thread(new Runnable() {

            public void run()
            {
                int i = JOptionPane.showConfirmDialog(EventLoggerApp.getApplication().getMainFrame(), "This will Erase events from the Event logger and it is not reversible. \nAre you sure?", "Erase events", JOptionPane.YES_NO_OPTION);
                if(i==0){
                    controlAllButtons(false);
                    DataFrame df = new DataFrame();
                    df.CPU_address = cpu_Addrs;
                    df.CMD = ERASE_EVENTS_EEPROM;
                    df.data = new byte[0];
                    SendPacketRecieveResponse(df); 
                    controlAllButtons(true);
                }else{
                    GiveResponse("Aborted by user", Color.RED);
                }
                      
            }
            });
            erase_events.start(); 
        
    }//GEN-LAST:event_BtnEraseEventsinLoggerActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       
        Thread set_rtc_time = new Thread(new Runnable() {

        public void run()
        {
            controlAllButtons(false);
            DataFrame df = new DataFrame();
            df.CPU_address = EventLoggerApp.getApplication().evlogView.cpu_Addrs;
            df.CMD = SET_RTC_DATE_AND_TIME;
            df.data = new byte[SET_RTC_DATE_AND_TIME_Length-4];
            Calendar cal= Calendar.getInstance();
            cal.getTime().getYear();
            int yr = cal.getTime().getYear();
            if(yr>100){
            yr = 2000 + yr % 100; 
            }
            df.data[0] = (byte) (yr & 0xFF);
            df.data[1] = (byte) ((byte) (yr >> 8) & 0xFF);
            df.data[2] = (byte) ((byte) (cal.getTime().getMonth()+1) & 0xFF);
            df.data[3] = (byte) ((byte) (cal.getTime().getDate())  & 0xFF);
            df.data[4] = (byte) ((byte) (cal.getTime().getHours())  & 0xFF);
            df.data[5] = (byte) ((byte) (cal.getTime().getMinutes())  & 0xFF);
            df.data[6] = (byte) ((byte) (cal.getTime().getSeconds())  & 0xFF);
            SendPacketRecieveResponse(df); 
            controlAllButtons(true);
            }
        });
        set_rtc_time.start();
            
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Thread get_rtc_time = new Thread(new Runnable() {

            public void run()
            {
                if(DateFiled.getDate() == null){
                    GiveResponse("Please set valid date and time...", Color.RED);
                    return;
                }
                controlAllButtons(false);  
                DataFrame df = new DataFrame();
                df.CPU_address = EventLoggerApp.getApplication().evlogView.cpu_Addrs;
                df.CMD = SET_RTC_DATE_AND_TIME;
                df.data = new byte[SET_RTC_DATE_AND_TIME_Length-4];
                int yr = DateFiled.getDate().getYear();
                if(yr>100){
                    yr = 2000 + yr % 100; 
                }
                df.data[0] = (byte) (yr & 0xFF);
                df.data[1] = (byte) ((byte) (yr >> 8) & 0xFF);
                df.data[2] = (byte) (DateFiled.getDate().getMonth()+1 & 0xFF);
                df.data[3] = (byte) (DateFiled.getDate().getDate() & 0xFF);
                df.data[4] = (byte) (Integer.parseInt(HrSpinner.getValue().toString()));
                df.data[5] = (byte) (Integer.parseInt(MinSpinner.getValue().toString()));
                df.data[6] = (byte) (Integer.parseInt(SecSpinner.getValue().toString()));
                SendPacketRecieveResponse(df);
                controlAllButtons(true);
            }
        });
        get_rtc_time.start();
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
       Thread thread = new Thread(new UpdateRTCTime(this.sfa));
        thread.start();       
    }//GEN-LAST:event_jButton12ActionPerformed

    public void UpdateRTC(){
        controlAllButtons(false);
        DataFrame df = new DataFrame();
        df.CPU_address = cpu_Addrs;
        df.CMD = GET_RTC_DATE_TIME;
        df.data = new byte[0];        
        SendPacketRecieveResponse(df);
        controlAllButtons(true);               
                     
    }
    private void ClearAllFields(){
        networkIDField.setText("");
        dpField.setText("");
        jTextField4.setText("");
    }
    private void BtnGetDACStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnGetDACStatusActionPerformed
             refreshDACstatus();             
    }//GEN-LAST:event_BtnGetDACStatusActionPerformed

    private void refreshDACstatus(){
            Thread get_dac_status = new Thread(new Runnable() {

            public void run()
            {
                ClearAllFields();
                DataFrame df = new DataFrame();
                df.CMD = GET_DAC_STATUS;
                df.CPU_address = 0x55;
                df.data = new byte[0];
                SendPacketRecieveResponse(df);               
            }
            });
            get_dac_status.start();   
    }
    private void BtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRefreshActionPerformed
        RefreshCounts();  
    }//GEN-LAST:event_BtnRefreshActionPerformed

    private void RefreshCounts(){
        Thread get_event_counts = new Thread(new Runnable() {

            public void run()
            {
                controlAllButtons(false);
                EventStatus.setValueAt("Updating...", 0, 1);
                EventStatus.setValueAt("Updating...", 1, 1);
                EventStatus.setValueAt("Updating...", 2, 1);
                DataFrame df = new DataFrame();
                df.CPU_address =cpu_Addrs;
                df.CMD = GET_EVENT_COUNTS;
                df.data = new byte[0];
                SendPacketRecieveResponse(df);
                controlAllButtons(true);
            }
            });
            get_event_counts.start();     
    }
    private void BtnDownloadEventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDownloadEventsActionPerformed
        sharedData.event_downloaded = false;
        if(jRadioButton2.isSelected() && from_model.getValue()!=null && from_model.getValue().after(to_model.getValue())){
                JOptionPane.showMessageDialog(this.getFrame(), "'From' date cannot be greater than 'To' date", "Wrong dates", JOptionPane.ERROR_MESSAGE);
                return;
        }
        if(jRadioButton2.isSelected() && from_model.getValue()==null){
            JOptionPane.showMessageDialog(this.getFrame(), "'From' date is required", "Wrong dates", JOptionPane.ERROR_MESSAGE);
                return;
        }
        Thread get_events = new Thread(new Runnable() {

            public void run()
            {
                cpuselectCmbBx.setSelectedIndex(0);
                Buttons(false,true);
//                tabHandle.removeAllRows();
                model.data.clear();
                model.fireTableDataChanged();
                Buttons(false,true);
                progressBar.setValue(0);
//                model.removeTableModelListener(jTable1);
                               
                sharedData.event_list.clear();
                Buttons(false,true);
                DataFrame df = new DataFrame();
                df.CPU_address =cpu_Addrs;
                df.CMD = GET_LOGGED_EVENTS;
                df.data = new byte[2];
                df.data[0] = (byte) 255;
                df.data[1] = 0;
                SendPacketRecieveResponse(df);   
//                UpdateEventList("All");
                jTabbedPane1.setSelectedIndex(4); 
//                Buttons(true,false);
                }
                  });
            get_events.start();     
        
    }//GEN-LAST:event_BtnDownloadEventsActionPerformed
//    private boolean fillColoumns(String[] labelString) {
//         try{
//            tabHandle.addColumns(labelString);
//            return true;
//            }
//            catch(Exception e){
//            return false;
//            }
//    }
    
    private String getString(int i){
        if(i==0) return "--";
        else return Long.toString(i,10);
    }
    public void UpdateEventList(String cpu){
        
        lblStatus.setText("Updating the table. Please wait...");
        lblStatus.setForeground(Color.BLUE);
        percent = 0;
        model = new MyTableModel();
        jTable1.setModel(model);
        Buttons(false,false);
        jTable1.setAutoResizeMode(jTable1.AUTO_RESIZE_OFF);
//        String[] StringToDisplay = new String[tableColumns.length];
        progressBar.setIndeterminate(false);
        event_list = sharedData.get_logged_events();
        if(sharedData.event_downloaded && event_list.size()==0){
            sharedData.event_downloaded = false;
            JOptionPane.showMessageDialog(this.getFrame(), "There are no events logged in the Event Logger", "Events not available", JOptionPane.ERROR_MESSAGE);
            lblStatus.setText("");
            return;
        } else if(!sharedData.event_downloaded && cpu.equals("All")==false) {
            JOptionPane.showMessageDialog(this.getFrame(), "Please go to 'Event Download' tab and download the events first", "Events not available", JOptionPane.ERROR_MESSAGE);
            lblStatus.setText("");
            return;
        }
//        int total_events = event_list.size();
        for(int col =0; col<tableColumns.length;col++){
            jTable1.getColumnModel().getColumn(col).setCellRenderer(new HighlightRenderer());
        }
         TableColumnAdjustment tca = new TableColumnAdjustment( jTable1);
        tca.setDynamicAdjustment(true);  
        tca.setColumnHeaderIncluded(false);
        Stop_Updating = false;
        controlAllButtons(false);
        if(jRadioButton2.isSelected()){
            {
               LinkedList<EventDetails> temp_event_list = new LinkedList<EventDetails>();
               for(int p=0; p< event_list.size(); p++){
                   String d = event_list.get(p).date_time;
                   Date event_date = new Date(d);
                   long from_time = from_model.getValue().getTime() - (1000* 60 * 60 *12);
                   long event_time = event_date.getTime();
                   long to_time = to_model.getValue().getTime() + (1000* 60 * 60 *12);
                   
                   if(event_time > from_time && event_time < to_time){
                       temp_event_list.add(event_list.get(p));
                   }
               } 
               event_list = temp_event_list;
            }//
        }
        lblStatus.setForeground(Color.BLUE);
        TableSwingWorker worker = new TableSwingWorker(cpu);
        worker.execute();
        tca.adjustColumns();
    }
    
    class RowData {
        private String[] data;
        
        public RowData(String[] d){
            this.data = d;
        }
        
        public String[] getData(){
            return this.data;
        }
    }
    
    
       public class TableSwingWorker extends SwingWorker<String,RowData> {

           private String cpu;
        public TableSwingWorker(String cpu) {
            this.cpu = cpu;
        }

        @Override
        protected String doInBackground() throws Exception {

            Buttons(false,false);
            int total_events = event_list.size();
            String[] StringToDisplay = new String[tableColumns.length];
            for(int i =0; i<total_events;i++){
            EventDetails ed = event_list.get(i);
            StringToDisplay = new String[tableColumns.length];
            StringToDisplay[0] = ed.Station_Name;
            StringToDisplay[1] = unit_type_txt;//ed.DP_Point;
            if(ed.CPU_Addrs == -1){
               StringToDisplay[2] = "N/A"; 
            }
            else StringToDisplay[2] = "CPU-"+Long.toString(ed.CPU_Addrs,10);
            StringToDisplay[3] = Long.toString(ed.event_ID);
            String event_desc = get_event_desc(ed.event_ID);
            StringToDisplay[4] = event_desc;
            StringToDisplay[5] = ed.date_time;
            StringToDisplay[6] = getString(ed.Count1);   //9 & 10
            if(unit_type!=0){
            StringToDisplay[7] = getString(ed.Count2); //5 & 6
            StringToDisplay[8] = getString(ed.Count3); //11  & 12
            if(StringToDisplay.length==10){
                StringToDisplay[9] = "--";
            }
            else{ 
            StringToDisplay[9] = getString(ed.Count4);  //7 & 8            
            StringToDisplay[10] = "--";
            }
            }else{
            StringToDisplay[7] = "--";
            }
            
//            System.out.println(Arrays.toString(StringToDisplay));
            if(cpu.equals("All")){
                RowData rowdata = new RowData(StringToDisplay);
                publish(rowdata);
            }
//                tabHandle.addRows(StringToDisplay);
//                new TableSwingWorker(StringToDisplay).execute();
            else if(cpu.equals(StringToDisplay[2])){
                RowData rowdata = new RowData(StringToDisplay);
                publish(rowdata);
            }
//                tabHandle.addRows(StringToDisplay);
//                new TableSwingWorker(StringToDisplay).execute();         
            if(i==0) percent =0;
            else percent = (i*100)/total_events;            
//            new AnswerWorker(percent).execute();
            progressBar.setValue(percent);
//            Thread.sleep(10);
            lblStatus.setText("Updating the table: "+ percent + "% complete... Please wait.");
            lblStatus.setForeground(Color.BLUE);
            Thread.yield();
            if(Stop_Updating){
                break;
            }
            }
            Buttons(true,false);
            progressBar.setValue(0);
            GiveResponse("Logged events have been populated.", Color.BLUE);
            return "return";
        }

        @Override
        protected void process(List<RowData> row) {
            model.addRows(row);
        }
    }
        
    public void resizeColumnWidth(JTable table) {
    final TableColumnModel columnModel = table.getColumnModel();
    for (int column = 0; column < table.getColumnCount(); column++) {
        int width = 50; // Min width
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component comp = table.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width, width);
        }
        columnModel.getColumn(column).setPreferredWidth(width);
    }
}
    
    private String get_event_desc(int event_ID){
        String retval = "";
        for(EventDescription ed : EventDescription.values()){
            if(event_ID == ed.getOrdinal()){
                retval = ed.toString();
                retval = retval.substring(6);
                retval = retval.replace("_", " ");
                retval = retval.replace("__", ">");
                retval = retval.replace("___", "<");
                retval = retval.replace("MODEM BOARD MISSING1", "MODEM BOARD MISSING");
                retval = retval.replace("MODEM BOARD FOUND1", "MODEM BOARD FOUND");
                retval = getCapCorrected(retval);
            }
        }
        return retval;
    }
    
    private String getCapCorrected(String t){
        String retval = "";
        String[] p = t.split(" ");
        for(int i=0;i<p.length;i++){
            if(p[i].length()<2){
                
            }
            else if(p[i].length() > 3){
                p[i] = p[i].substring(0, 1).concat(p[i].substring(1, p[i].length()).toLowerCase());
            }
            retval = retval.concat(" ").concat(p[i]);
        }
        return retval;
    }
    private void BtnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnConnectActionPerformed
        if(sharedData.connected){
            Thread con_thread = new Thread(new Runnable() {
            public void run()
            {
                controlAllButtons(false);
                sh.disconnect();
                sharedData.connected = false;
                BtnConnect.setText("Connect"); 
                controlAllButtons(true);
            }
            });
            con_thread.start();  
           
        }
        else{
            //serial_helper = new SerialHelper();
            Thread con_thread = new Thread(new Runnable() {

            public void run()
            {
                if(com_connect(port)){
                   BtnConnect.setText("Disconnect");
                   jTabbedPane1.setSelectedIndex(1); 
                   UpdateStatusPanel();
                }
                
            }
            });
            con_thread.start();  
        }   
    }//GEN-LAST:event_BtnConnectActionPerformed

    

    private void usbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usbActionPerformed
        port = "USB";
    }//GEN-LAST:event_usbActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        try{
            jTable1.print();
        }catch(PrinterException ex){
            JOptionPane.showMessageDialog(this.getFrame(), ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }               
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        ExportExcel();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        EventLoggerAboutBox ab = new EventLoggerAboutBox(this.getFrame());
        ab.setLocationRelativeTo(this.getFrame());
        ab.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         
        new ChartWorker().execute();       
    }//GEN-LAST:event_jButton1ActionPerformed

    class ChartWorker extends SwingWorker<Integer, Integer>
    {
        protected Integer doInBackground() throws Exception
        {
            // Do a time-consuming task.
            GiveResponse("Updating chart. Please wait...", Color.BLUE);
            Thread.sleep(1000);
            prepareChart();
            return 42;
        }

        protected void done()
        {
            try
            {
                GiveResponse("Chart refreshed.", Color.BLUE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private void stnNameTxtFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stnNameTxtFieldKeyReleased
         if(stnNameTxtField.getText().equals("")){
            EnterStnNameHint.setVisible(true);
        }else{
            EnterStnNameHint.setVisible(false);
        }
    }//GEN-LAST:event_stnNameTxtFieldKeyReleased

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
        ExtensionFileFilter filter1 =  new ExtensionFileFilter("MS Access (.mdb File)", new String[] {"mdb"});
        fileDialog.setFileFilter (filter1);
        fileDialog.setSelectedFile(new File(sharedData.getDate()));
        fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fileDialog.showSaveDialog(null);

        fileDialog.setVisible(true);
        if(status == fileDialog.CANCEL_OPTION)
            return;
        String directory= fileDialog.getSelectedFile().getParent();

        String fileName=fileDialog.getSelectedFile().getName();

        if(directory!=null){

        directory=directory.replace(File.separatorChar, '\\');

        fileName=fileName.replace(File.separatorChar, '\\');

        fullPath = directory + "\\"+fileName;
        controlAllButtons(false);
        GiveResponse("Exporting to Microsoft Access file...", Color.BLACK);
        if(!fullPath.toLowerCase().contains(".mdb")){
            fullPath=fullPath.concat(".mdb");
        }
        fullPath = fullPath.replace("\\", "/");
        Thread te = new Thread(new Runnable() {

            public void run()
            {
                controlAllButtons(false);
                GiveResponse("Exporting to MS Access takes few minutes. Please wait...", Color.BLUE);
                if(ExportAccess(fullPath)==false){
            JOptionPane.showMessageDialog(EventLoggerApp.getApplication().getView().getFrame(), "Failed to create Microsoft Access file.","Error",  JOptionPane.ERROR_MESSAGE);
            }
            else {
                fullPath = fullPath.replace("\\", "/");
                fullPath = fullPath.replace("//", "/");
                if(!fullPath.endsWith(".mdb")) fullPath = fullPath.concat(".mdb");
                GiveResponse("Microsoft Access file successfully created at "+ fullPath, Color.BLUE);
            }
         controlAllButtons(true);
            }
            });
            te.start();
        
        }
        
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
        ExtensionFileFilter filter1 =  new ExtensionFileFilter("PDF (.pdf File)", new String[] {"pdf"});
        fileDialog.setFileFilter (filter1);
        fileDialog.setSelectedFile(new File(sharedData.getDate()));
        fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fileDialog.showSaveDialog(null);

        fileDialog.setVisible(true);
        if(status == fileDialog.CANCEL_OPTION)
            return;
        String directory= fileDialog.getSelectedFile().getParent();

        String fileName=fileDialog.getSelectedFile().getName();

        if(directory!=null){

        directory=directory.replace(File.separatorChar, '\\');

        fileName=fileName.replace(File.separatorChar, '\\');

        fullPath = directory + "\\"+fileName;
        
        GiveResponse("Exporting to PDF file...", Color.BLACK);
        if(!fullPath.toLowerCase().contains(".pdf")){
            fullPath=fullPath.concat(".pdf");
        }
        
        fullPath = fullPath.replace("\\", "/");
        Thread get_dac_status = new Thread(new Runnable() {

            public void run()
            {
                controlAllButtons(false);
                GiveResponse("Please wait...", Color.BLUE);
               
                 if(putToPDF(fullPath)==false){
            JOptionPane.showMessageDialog(EventLoggerApp.getApplication().getView().getFrame(), "Failed to create PDF file.","Error",  JOptionPane.ERROR_MESSAGE);
            }
            else {
                fullPath = fullPath.replace("\\", "/");
                fullPath = fullPath.replace("//", "/");
                if(!fullPath.endsWith(".pdf")) fullPath = fullPath.concat(".pdf");
                GiveResponse("PDF file successfully created at "+ fullPath, Color.BLUE);
            }
         controlAllButtons(true);            
            }
            });
            get_dac_status.start(); 
        
        }
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Stop_Updating = true;
    }//GEN-LAST:event_jButton4ActionPerformed

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    String readme= null;    
    try {
            readme = new File(EventLoggerView.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath();
        } catch (URISyntaxException ex) {
            Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    if (Desktop.isDesktopSupported()) {
    try {
        if (Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) {
            File file = new File(readme+"/Readme.txt");
            if(!file.isFile()){
                JOptionPane.showMessageDialog(EventLoggerApp.getApplication().getView().getFrame(), "Failed to open the readme file.","Error",  JOptionPane.ERROR_MESSAGE);
            }
            else Desktop.getDesktop().edit(file);
        }
//        // or...
//        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
//            Desktop.getDesktop().open(new File(readme+"/Readme.txt"));
//        }
    } catch (IOException exp) {
        exp.printStackTrace();
    }
}

}//GEN-LAST:event_jMenuItem2ActionPerformed

private void cpuselectCmbBxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpuselectCmbBxActionPerformed
    final String cpu = (String) cpuselectCmbBx.getSelectedItem();
    if(sharedData.event_downloaded == false) return;
    Thread te = new Thread(new Runnable() {

    public void run()
    {
        Buttons(false,false);
        model.data.clear();
        model.fireTableDataChanged();
        UpdateEventList(cpu);
        Buttons(true,false);
    }
    });
    te.start();
    
}//GEN-LAST:event_cpuselectCmbBxActionPerformed

private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        jPanel18.setEnabled(!jRadioButton1.isSelected());
        jPanel19.setEnabled(!jRadioButton1.isSelected());
}//GEN-LAST:event_jRadioButton1ActionPerformed

private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        jPanel18.setEnabled(jRadioButton2.isSelected());
        jPanel19.setEnabled(jRadioButton2.isSelected());
}//GEN-LAST:event_jRadioButton2ActionPerformed

    public boolean ExportPDF(String path){
        
        
        boolean retval = false;
        try {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page); 

        // Image to use 
        
        InputStream is = getClass().getResourceAsStream("resources/insys_logo_w200.jpg");
        
        PDXObjectImage img = new PDJpeg(document, is);

        // Create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.HELVETICA_BOLD;

        // Start a new content stream which will "hold" the content to be created
        PDPageContentStream contentStream = new PDPageContentStream(document, page,true,true);

        contentStream.setFont( font, 6 );
        contentStream.drawImage(img, 200, 179);

        drawTable( page,  contentStream );

        // Make sure that the content stream is closed
        contentStream.close();

        // Save the results and ensure that the document is properly closed
        document.save(path);
        document.close();
        retval = true;
        } catch (Exception e) {
            retval = false;
        System.out.println("Exception is: ");
        }
        return retval;

    }
    
//    private boolean exportPDF1(String path){
//        Document document = new Document(PageSize.A4.rotate()) {};
//        PdfWriter writer = null;
//        try {
//            try {
//                writer = PdfWriter.getInstance(document, new FileOutputStream(path));
//            } catch (DocumentException ex) {
//                Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        document.open();
//        PdfContentByte cb = writer.getDirectContent();
//        cb.saveState();
//      Graphics2D g2 = cb.createGraphics(500, 500);
//
//      Shape oldClip = g2.getClip();
//      g2.clipRect(10, 0, 500, 500);
//
//      jTable1.print(g2);
//      g2.setClip(oldClip);
//
//      g2.dispose();
//      cb.restoreState();
//        return true;   
//
//    }
    
       private void drawTable( PDPage page, PDPageContentStream contentStream) {
        try {
            float y = 650;
            float margin = 130;
            String[] column_headers = new String[jTable1.getColumnCount()];//{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Local Forward","Remote Forward","Local Reverse","Remote Reverse"};
//            jTable1.getco
            final int rows = jTable1.getRowCount();
            final int cols = 10;
            final float rowHeight = 22f;
            final float tableWidth =  900.0f;
            final float tableHeight = rowHeight * rows;
            final float cellMargin=1f;

            for(int c =0; c<column_headers.length;c++){
                column_headers[c] = jTable1.getColumnName(c);
            }
            //draw the rows
            float nexty = y ;
            for (int i = 0; i <= rows; i++)
            {
                contentStream.drawLine(margin, nexty, 400, nexty);
                nexty-= rowHeight;
            }

            float colWidthX [] = {100,100,100,100,100,100,100,100,100,100,100};

            //draw the columns
            float nextx = margin;
            for (int i = 0; i <= cols; i++)
            {
                contentStream.drawLine(nextx, y, nextx, y-tableHeight);
                nextx += colWidthX[i] ; //colWidth;
            }

            //now add the text
            float textx = margin+cellMargin;
            float texty = y-15;
            //textx = margin+cellMargin;


                for(int j = 0 ; j < 7; j++) {
                    contentStream.beginText();
                    contentStream.moveTextPositionByAmount(textx,texty);

                    contentStream.drawString(column_headers[j]);
                    contentStream.endText();
                    textx += colWidthX[0]+9;
                    contentStream.beginText();
                    contentStream.moveTextPositionByAmount(textx,texty);
                    if(j==0)
                        contentStream.drawString( "1" );
                    if(j==1)
                        contentStream.drawString( "12345" );
                    if(j==2)
                        contentStream.drawString( "05-December-2003" );
                    if(j==3)
                        contentStream.drawString( "15" );
                    if(j==4)
                        contentStream.drawString( "1" );
                    if(j==5)
                        contentStream.drawString( "1" );
                    if(j==6)
                        contentStream.drawString( "1" );
                    if(j==7)
                        contentStream.drawString( "1" );
                    if(j==8)
                        contentStream.drawString( "1" );

                    contentStream.endText();
                    textx = margin+cellMargin; //colWidth;
                    texty -= rowHeight; //row height

                }
                texty-=rowHeight;
                textx = margin+cellMargin;
        }
        catch ( IOException ioe )
        {
            //Package.log.error( " drawTable :" + ioe);
            final String errormsg = "Could not drawTable ";
            //Package.log.error("In RuleThread drawTable " + errormsg, ioe);
            throw new RuntimeException(errormsg, ioe);
        }
        catch ( Exception ex )
        {
            //Package.log.error( " drawTable :" + ex);
            final String errormsg = "Could not drawTable ";
            //Package.log.error("In RuleThread drawTable " + errormsg, ex);
            throw new RuntimeException(errormsg, ex);
        }
    }

   public boolean putToPDF(String path){
     try {
        Document doc = new Document();
        doc.setMargins(6, 6, 88, 64);
        PdfWriter pdfwriter = PdfWriter.getInstance(doc, new FileOutputStream(path));       

        HeaderTable headerevent = new HeaderTable();
        pdfwriter.setPageEvent(headerevent);
        
        FooterTable footerevent = new FooterTable();
        pdfwriter.setPageEvent(footerevent);
        
            doc.open();
            PdfPTable pdfTable = new PdfPTable(jTable1.getColumnCount());
//            pdfTable.setTotalWidth(550);
            int[] t = new int[jTable1.getColumnCount()];
            for(int h=0;h<t.length;h++){
                t[h] = 60;
            }
            t[1] = 70;
            t[4] = 140;
            t[5] = 120;
            pdfTable.setWidths(t);
            //adding table headers
            Font headerfont = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, Color.BLUE);
            Font rowfont = FontFactory.getFont(FontFactory.TIMES, 8, Color.BLACK);            
            Paragraph col = null;
            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                col = new Paragraph(jTable1.getColumnName(i), headerfont);
                PdfPCell col_headers = new PdfPCell(col);
                col_headers.setColspan(3);
                col_headers.setBorder(PdfPCell.NO_BORDER);
                col_headers.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);
                pdfTable.addCell(col);
            }
            //extracting data from the JTable and inserting it to PdfPTable
            String value = "";
            for (int rows = 0; rows < jTable1.getRowCount() - 1; rows++) {
                for (int cols = 0; cols < jTable1.getColumnCount(); cols++) {
                    if(jTable1.getModel().getValueAt(rows, cols) != null)
                    value = jTable1.getModel().getValueAt(rows, cols).toString();
                    else value = "";
                    if(value.toString().contains("Failure")
                     ||value.toString().contains("Defective")
                     ||value.toString().contains("Failed")
                     ||value.toString().contains("Mismatch")
                     ||value.toString().contains("Direct")
                     ||value.toString().contains("Pulsating")
                     ||value.toString().contains("NOT Detecting")
                     ||value.toString().contains("Influence")
                     ||value.toString().contains("Theft")
                     ||value.toString().contains("Door Open")
                     ||value.toString().contains("BAD")){                       
                         rowfont = FontFactory.getFont(FontFactory.TIMES, 8, Color.RED);           
                    }else if(value.toString().contains("Clear")
                        || value.toString().contains("Restored")
                    ){
                        rowfont = FontFactory.getFont(FontFactory.TIMES, 8, Color.GREEN);           
                    }else if(value.toString().contains("Missing")){
                        rowfont = FontFactory.getFont(FontFactory.TIMES, 8, Color.PINK); 
                    }else if(value.toString().contains("Normal")){
                        rowfont = FontFactory.getFont(FontFactory.TIMES, 8, Color.BLUE);
                    }else if(value.toString().contains("Occupied")){
                        rowfont = FontFactory.getFont(FontFactory.TIMES, 8, Color.ORANGE);
                    }
                    else{
                        rowfont = FontFactory.getFont(FontFactory.TIMES, 8, Color.BLACK);           
                    }
                    String paragraph = "";
                    if(jTable1.getModel().getValueAt(rows, cols) != null){
                        paragraph = jTable1.getModel().getValueAt(rows, cols).toString();
                    }
                    col = new Paragraph(paragraph, rowfont);
                    
                    PdfPCell row_items = new PdfPCell(col);
                    row_items.setColspan(3);
                    row_items.setBorder(PdfPCell.NO_BORDER);
                    row_items.setHorizontalAlignment(Element.ALIGN_LEFT);
                    pdfTable.addCell(col);

                }
            }
            doc.add(pdfTable);
            doc.close();
            System.out.println("done");
        } catch (DocumentException ex) {
            Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
        }
     return true;
}
       
    public boolean ExportAccess(String path){
        boolean retval = false;
        try {
            File file = new File(path);
            Database db = new DatabaseBuilder(file)
            .setFileFormat(Database.FileFormat.V2010)
            .create();
            
            Table newTable = new TableBuilder("SSDAC Event logs")
                .addColumn(new ColumnBuilder("Stn Name").setSQLType(Types.VARCHAR))
                .addColumn(new ColumnBuilder("DP Point").setSQLType(Types.VARCHAR))
                .addColumn(new ColumnBuilder("CPU Addrs").setSQLType(Types.INTEGER))
                .addColumn(new ColumnBuilder("Event ID").setSQLType(Types.INTEGER))
                .addColumn(new ColumnBuilder("Description").setSQLType(Types.VARCHAR))
                .addColumn(new ColumnBuilder("Date and time").setSQLType(Types.VARCHAR))
                .addColumn(new ColumnBuilder("Local Forward").setSQLType(Types.INTEGER))
                .addColumn(new ColumnBuilder("Remote Forward").setSQLType(Types.INTEGER))
                .addColumn(new ColumnBuilder("Local Reverse").setSQLType(Types.INTEGER))
                .addColumn(new ColumnBuilder("Remote Reverse").setSQLType(Types.INTEGER)).toTable(db);
            
            LinkedList<EventDetails> ed = sharedData.get_logged_events();
            for(int i=0;i<ed.size();i++){
                newTable.addRow(ed.get(i).Station_Name,
                                ed.get(i).DP_Point,
                                ed.get(i).CPU_Addrs,
                                ed.get(i).event_ID,
                                get_event_desc(ed.get(i).event_ID),
                                ed.get(i).date_time,
                                ed.get(i).Count1,
                                ed.get(i).Count2,
                                ed.get(i).Count3,
                                ed.get(i).Count4);
            }  
            db.close();
            retval = true;
        } catch (SQLException ex) {
            retval = false;
            Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            retval = false;
            Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }
      public void ExportExcel(){
        
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
        ExtensionFileFilter filter1 =  new ExtensionFileFilter("Excel (XLS File)", new String[] {"Xls"});
        fileDialog.setFileFilter (filter1);
        fileDialog.setSelectedFile(new File(sharedData.getDate()));
        fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fileDialog.showSaveDialog(null);

        fileDialog.setVisible(true);
        if(status == fileDialog.CANCEL_OPTION)
            return;
        String directory= fileDialog.getSelectedFile().getParent();

        String fileName=fileDialog.getSelectedFile().getName();

        if(directory!=null){

        directory=directory.replace(File.separatorChar, '\\');

        fileName=fileName.replace(File.separatorChar, '\\');

        String fullPath = directory + "\\"+fileName;
        controlAllButtons(false);
        GiveResponse("Exporting to excel sheet...", Color.BLACK);
        if(!fullPath.toLowerCase().contains(".xls")){
            fullPath=fullPath.concat(".xls");
        }
        fullPath = fullPath.replace("\\", "/");
        WriteToExcel excel_writer = new WriteToExcel();
        if(excel_writer.exportExcelFile(fullPath)==false){
            JOptionPane.showMessageDialog(EventLoggerApp.getApplication().getView().getFrame(), "Failed to create excel file.","Error",  JOptionPane.ERROR_MESSAGE);
        }
        else {
            fullPath = fullPath.replace("\\", "/");
            fullPath = fullPath.replace("//", "/");
            if(!fullPath.endsWith(".xls")) fullPath = fullPath.concat(".xls");
            GiveResponse("Excelsheet file successfully created at "+ fullPath, Color.BLUE);
        }
         controlAllButtons(true);
        }
    }
      
    private void UpdateStatusPanel(){
       unit_type_txt ="";
       switch(this.unit_type){
            case 0:
            unit_type_txt = "DE";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Local Count","Total Wheels"};
            break;
                
            case 1:
            unit_type_txt = "SF";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Loc Fwd","Rem Fwd","Loc Rev","Rem Rev","Total Wheels"};
            break;
                
            case 2:
            unit_type_txt = "EF";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Loc Fwd","Rem Fwd","Loc Rev","Rem Rev","Total Wheels"};
            break;
                
            case 3:
            unit_type_txt = "CF";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","SF Count","S-CF Count","E-CF Count","EF Count","Total Wheels"};
            break;
                
            case 4:
            unit_type_txt = "3D1S-3A";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Unit A","Unit B","Unit C","NA","Total Wheels"};
            break;
                
            case 5:
            unit_type_txt = "3D1S-3B";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Unit A","Unit B","Unit C","Total Wheels"};
            
            break;
                
            case 6:
            unit_type_txt = "3D1S-3C";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Unit A","Unit B","Unit C","Total Wheels"};
            break;
            
            case 7:
            unit_type_txt = "3D-SF";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","SF Count","S-CF Count","S-EF Count","EF Count","Total Wheels"};
            break;
                
            case 8:
            unit_type_txt = "3D-EF";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","SF Count","S-CF Count","E-SF Count","EF Count","Total Wheels"};
            break;
                
            case 9:
            unit_type_txt = "LCWS";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Sensor A","Sensor B","Sensor C","Sensor D","Total Wheels"};
            break;
                
            case 10:
            unit_type_txt = "LCWS - DL";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Sensor A","Sensor B","Sensor C","Sensor D","Total Wheels"};
            break;
                    
            case 11:
            unit_type_txt = "4D1S - A";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Unit A","Unit B","Unit C","Unit D","Total Wheels"};
            
            break;
                        
            case 12:
            unit_type_txt = "4D1S - B";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Unit A","Unit B","Unit C","Unit D","Total Wheels"};            
            break;
                
            case 13:
            unit_type_txt = "4D1S - C";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Unit A","Unit B","Unit C","Unit D","Total Wheels"};            
            break;
                    
            case 14:
            unit_type_txt = "4D1S - D";
            tableColumns = new String[]{"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Unit A","Unit B","Unit C","Unit D","Total Wheels"};
            break;
       }
       networkIDField.setText(Network_ID);
       dpField.setText(unit_type_txt);
       jTextField4.setText(Long.toString(this.cpu_Addrs,10));     
       GiveResponse("Updated the DAC status", Color.BLUE);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnConnect;
    private javax.swing.JButton BtnDownloadEvents;
    private javax.swing.JButton BtnEraseEventsinLogger;
    private javax.swing.JButton BtnGetDACStatus;
    private javax.swing.JButton BtnRefresh;
    private com.toedter.calendar.JDateChooser DateFiled;
    private javax.swing.JLabel EnterStnNameHint;
    private javax.swing.JPasswordField ErasePwdTxtField;
    private javax.swing.JTable EventStatus;
    private javax.swing.JSpinner HrSpinner;
    private javax.swing.JSpinner MinSpinner;
    private javax.swing.JLabel RTC_dateLbl;
    private javax.swing.JSpinner SecSpinner;
    public javax.swing.JPanel connection_indicator_panel;
    public javax.swing.JPanel connection_indicator_panel1;
    private javax.swing.JComboBox cpuselectCmbBx;
    private javax.swing.JLabel datelbl;
    private javax.swing.JTextField dpField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextField networkIDField;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel statusPanel;
    public javax.swing.JTextField stnNameTxtField;
    private javax.swing.JLabel timelbl;
    private javax.swing.JLabel timelbl1;
    private javax.swing.JRadioButton usb;
    // End of variables declaration//GEN-END:variables
    private JDialog aboutBox;
}
