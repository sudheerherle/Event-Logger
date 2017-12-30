/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import java.util.logging.Logger;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import eventlogger.common.SharedData;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

/**
 *
 * @author I14746
 */
public class HeaderTable extends PdfPageEventHelper{
    public static final String DEST = "results/events/table_header.pdf";
 
//    public class HeaderTable  {
        protected PdfPTable table;
        private boolean isDebug = false;
        protected float tableHeight;
        public HeaderTable() {
            isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
        getInputArguments().toString().indexOf("jdwp") >= 0;
            table = new PdfPTable(3);
             int[] t = new int[table.getNumberOfColumns()];
             t[0] = 45;
             t[1] = 380;
             t[2] = 100;
        try {
            table.setWidths(t);
        } catch (DocumentException ex) {
            Logger.getLogger(HeaderTable.class.getName()).log(Level.SEVERE, null, ex);
        }
            table.setTotalWidth(530);
            table.setLockedWidth(true);
            Font headerfont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Color.BLACK); 
            Font datetimefont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Color.BLACK);  
//            table.addCell("Header row 1");
//            table.addCell("Header row 2");
//            table.addCell("Header row 3");
            
            
            
            Image logo = null;
            try {
                
                final URL url = EventLoggerView.class.getProtectionDomain().getCodeSource().getLocation();
                String jarPath=null;
              try {
                    jarPath = new File(url.toURI()).getAbsolutePath();
                } catch (URISyntaxException ex) {
//                    Exceptions.printStackTrace(ex);
                }
                String pt=jarPath;
                pt = pt.replace("EventLogger.jar", "insys_logo_w200.png");
                System.out.println("jar path: "+pt);
                if(isDebug){
                    pt = "D:\\GitHub\\Event-Logger\\Event Logger\\src\\eventlogger\\resources\\insys_logo_w200.png";
                }
                logo = Image.getInstance(pt);// E:\GitHub\Event-Logger\Event Logger\src\eventlogger\resources
            } catch (BadElementException ex) {
                Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EventLoggerView.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            logo.setAlignment(Image.MIDDLE);
            logo.scaleAbsoluteHeight(20);
            logo.scaleAbsoluteWidth(20);
            logo.scalePercent(20);
            Chunk chunk = new Chunk(logo, 0, -15);
            
            PdfPCell cell = new PdfPCell(new Phrase(chunk)); 
            cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            
            float cellheight = 45;
            cell.setFixedHeight(cellheight);
            table.addCell(cell);
            //<html><body  width=180 leftmargin=5 topmargin=20 marginwidth=5 marginheight=20>SSDAC Setup</body></html>
//            Paragraph col = new Paragraph("<html><body  width=180 leftmargin=5 topmargin=20 marginwidth=5 marginheight=20>SSDAC Setup</body></html>");
            Paragraph col = new Paragraph("                                        Insys Digital Systems Private Limited, Bangalore \n                                                   Single Section Digital Axle Counter\n                                                               Event logger Report", headerfont);
            
            cell = new PdfPCell(new Phrase(col));
//            cell.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(cell); 
            String date = SharedData.getDate();
            String time = SharedData.getDateTime();
            String stringcell = String.format("\nDate: %s\nTime: %s",date,time);
            Paragraph coltime = new Paragraph(stringcell, datetimefont);
            table.addCell(coltime); 
            tableHeight = table.getTotalHeight();
            
        }
 
        public float getTableHeight() {
            return tableHeight;
        }
 
        public void onEndPage(PdfWriter writer, Document document) {
            table.writeSelectedRows(0, -1,
            32,
            document.top() + (tableHeight) + 10,
            writer.getDirectContent());
        }
    }
//}