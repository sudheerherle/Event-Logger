/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;


import eventlogger.common.SharedData;
import java.io.*;
import java.util.LinkedList;
import java.util.Vector;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFPicture;




class WriteToExcel {
    String Path = System.getProperty("user.home");
    FileOutputStream fileOut;
    HSSFWorkbook workbook;
    HSSFSheet worksheet;
    HSSFRow row;
    short RowNo = 0;
    
    public boolean CreateExcelFile(String FileName){
        boolean retval = true;
        try {
            fileOut = new FileOutputStream(FileName);
            workbook = new HSSFWorkbook();
            worksheet = workbook.createSheet("Worksheet1");
	   } catch (FileNotFoundException e) {
			e.printStackTrace();
                        retval = false;
	   } catch (IOException e) {
			e.printStackTrace();
                        retval = false;
	   }
        return retval;
	}
        public void WriteHeadersToFile(){
            row = worksheet.createRow(1);
            HSSFCell cell = row.createCell(0);
            cell = row.createCell(0);
            cell.setCellValue("Insys Digital Systems");
            row = worksheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("SSDAC Event logger report");
            row = worksheet.createRow(3);
            
            row = worksheet.createRow(4);
            
            HSSFCellStyle cellstyle = workbook.createCellStyle();
            cellstyle.setBorderBottom(HSSFCellStyle.BORDER_THICK);
            cellstyle.setBorderRight(HSSFCellStyle.BORDER_THICK);
            cellstyle.setBorderLeft(HSSFCellStyle.BORDER_THICK);
            cellstyle.setBorderTop(HSSFCellStyle.BORDER_THICK);
            String[] tableColumns = {"Stn Name","DP Point","CPU Addrs", "Event ID","Description" , "Date and time","Local Forward","Remote Forward","Local Reverse","Remote Reverse"};
            for(int p=0;p<tableColumns.length;p++){
                cell = row.createCell(p);
                cell.setCellValue(tableColumns[p]);
                cell.setCellStyle(cellstyle);
            }   
       }
        
       private void WriteContentsToFile(){
            row = worksheet.createRow(5);  
            String dummy = "";
            LinkedList<EventDetails> ed = SharedData.getSingletonObject().event_list;
            int total_events = ed.size();
            for(int i =0; i<total_events;i++){
                row = worksheet.createRow(5+i);  
                row.createCell(0).setCellValue(ed.get(i).Station_Name);
                row.createCell(1).setCellValue(ed.get(i).DP_Point);
                if(ed.get(i).CPU_Addrs == -1){
                    dummy = "N/A"; 
                }else 
                    dummy = "CPU-"+Long.toString(ed.get(i).CPU_Addrs,10);
                row.createCell(2).setCellValue(dummy);
                row.createCell(3).setCellValue(ed.get(i).event_ID);
                row.createCell(4).setCellValue(get_event_desc(ed.get(i).event_ID));
                row.createCell(5).setCellValue(ed.get(i).date_time);
                row.createCell(6).setCellValue(Long.toString(ed.get(i).Count1,10));
                row.createCell(7).setCellValue(Long.toString(ed.get(i).Count2,10));
                row.createCell(8).setCellValue(Long.toString(ed.get(i).Count3,10));
                row.createCell(9).setCellValue(Long.toString(ed.get(i).Count4,10));                
            }
       }
       public void writeTestDetailsToExcel(String Name,String Value){

                RowNo++;
                HSSFRow row = worksheet.createRow(RowNo);
                HSSFCell cell0 = row.createCell((short)0);
                HSSFCell cell1 = row.createCell((short)1);
                cell0.setCellValue(Name);
                cell1.setCellValue(Value);
                
           
       }
       private String get_event_desc(int event_ID){
        String retval = "";
        for(EventDescription ed : EventDescription.values()){
            if(event_ID == ed.ordinal()){
                retval = ed.toString();
                retval = retval.replace("_", " ");
                retval = retval.replace("__", ">");
                retval = retval.replace("___", "<");
                retval = retval.replace("MODEM BOARD MISSING1", "MODEM BOARD MISSING");
                retval = retval.replace("MODEM BOARD FOUND1", "MODEM BOARD FOUND");
            }
        }
        return retval;
    }
       public void CloseExcel(){
           try{
               for(int i=0;i<8;i++){
               worksheet.autoSizeColumn(i);
               }
               workbook.write(fileOut);
               fileOut.flush();
               fileOut.close();
               }
           catch (IOException e) {
			e.printStackTrace();
            }

       }

       public void WriteTestResultsToExcel(){
            //CreateRow Row;
            //Enumeration Tbl = Table.elements();
		        RowNo++;
                        HSSFRow row0 = worksheet.createRow((short) RowNo);
                        HSSFCell cellA1 = row0.createCell((short) 2);
                        cellA1.setCellValue("TestCaseNo");
                        HSSFFont font = workbook.createFont();
                        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);


                        HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                        cellStyle.setFont(font);
			cellA1.setCellStyle(cellStyle);


                        HSSFCell cellB1 = row0.createCell((short) 3);
                        cellB1.setCellValue("TestCaseName");
                        cellB1.setCellStyle(cellStyle);

                        HSSFCell cellC1 = row0.createCell((short) 4);
                        cellC1.setCellValue("Result");
                        cellC1.setCellStyle(cellStyle);
//                        while(Tbl.hasMoreElements()){
//                            RowNo++;
//                            Row = Tbl.nextElement();
//                            HSSFRow row = worksheet.createRow((short) RowNo);
//                            HSSFCell cellA = row.createCell((short) 2);
//                            cellA.setCellValue(Row.SerialNo);
//                            HSSFCell cellB = row.createCell((short) 3);
//                            cellB.setCellValue(Row.TestName);
//                            HSSFCell cellC = row.createCell((short) 4);
//                            cellC.setCellValue(Row.Result);
//                            
//                        }
                        
		

	}

    boolean exportExcelFile(String fullPath) {
        boolean retval = false;
        retval = CreateExcelFile(fullPath);
        try{
        WriteHeadersToFile();
        WriteContentsToFile();
        CloseExcel();
        }catch(Exception m){
            retval = false;
        }
        return retval;
    }
       }