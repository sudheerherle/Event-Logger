/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

/**
 *
 * @author I14746
 */
public class FooterTable extends PdfPageEventHelper{
    public static final String DEST = "results/events/table_footer.pdf";
 
//    public class FooterTable extends PdfPageEventHelper {
        protected PdfPTable table;
        public FooterTable() {
        table = new PdfPTable(2);
        table.setTotalWidth(550);
        PdfPCell cell = new PdfPCell(new Phrase("Verified by:\nSignature:"));
        cell.setBackgroundColor(Color.ORANGE);        
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Approved by:\nSignature:"));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
            
        }
        public void onEndPage(PdfWriter writer, Document document) {
            table.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());
        }
    }
//}
 