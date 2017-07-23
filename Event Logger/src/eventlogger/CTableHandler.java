// CTableHandler.java - All User Interface functionalities for JTable

/*$Id: CTableHandler.java,v 1.9 2013/05/07 05:30:53 herles Exp $*/
/*
 ******************************************************************************
 *                                                                            *
 *              (c) Copyright 2009 Microchip Technologies Pvt. Ltd            *
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
 * <dt>Purpose: All User Interface functionalities for JTable
 * <dd>
 *
 * <dt>Description:
 * <dd> All User Interface functionalities for JTable
 *
 * </dl>
 *
 * @version $Date: 2013/05/07 05:30:53 $
 * @author  Sudheer
 * @since   JDK 1.6.21
 */

package eventlogger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


public class CTableHandler
{
    private JTable jTable;
    private DefaultTableModel tabModel;
//    private ColorRenderer colorRender=null;
//    private TableCellListener tableCellListener ;
    /**
     * CTableHandler  -  Table Handler Constructor
     * @param JTable pjTable
     * @return none
    */
    public CTableHandler(JTable pjTable)
    {
        jTable=pjTable;
        tabModel = new DefaultTableModel();
        jTable.setModel(tabModel);

    }
     
    public void setwidth(){

    }

    /**
     * Add rows to the table
     * @param Stirng[] rowvalue
     * @return
    */
    public void addRows(String[] rowValue)
    {
        if(rowValue.length==jTable.getColumnCount()){
           // try{
            tabModel.addRow(rowValue);

//            }
//            catch(Exception ex){
//                System.out.println("Error in tabulating...");
//            }
            
            //tabModel.setRowCount(jTable.getRowCount());
        }
         
    }

    public void setCellEditor(){
//        String [] ss = {"Dfaf","Dfaf"};
//        JTextField textField = new JTextField();
//        TableColumn dc = jTable.getColumnModel().getColumn(jTable.getSelectedColumn());
//        dc.setCellEditor(new DefaultCellEditor(textField));

    }

    /**
     * Removes all row from the table
     *
     * @return
     */
    public void removeAllRows()//Table Rows
    {
        try{
            tabModel.getDataVector().clear(); 
        }catch(Exception e){
            System.out.println("Removing Rows caused an Error!!.. ");

        }
    }


    /**
     * Add Columns specified in the String array
     *
     * @param  String[] colName
     * @return
     */
    public void addColumns(String[] colName)//Table Columns
    {
      
    //    for(int ip=0;ip<colName.length;ip++)

            tabModel.setColumnIdentifiers(colName);
    }

    /**
     * Removes all columns from the table
     *
     * @return
    */
    public void removeAllColumns()//Table Columns
    {
        if(jTable.getColumnCount()>0)
        {
            for(int c = jTable.getColumnCount()-1; c >= 0; c--)
            {
                   TableColumn tcol = jTable.getColumnModel().getColumn(c);
                   jTable.removeColumn(tcol);
            }
            tabModel.getDataVector().removeAllElements();
            tabModel.setColumnCount(0);
            jTable.removeAll();
            jTable.revalidate();
        }
    }

    /**
     * Refresh Color of the Table
     *
     * @return
     */
//    public void refreshColor()
//    {
//        if(colorRender!=null)
//        {
//            for (int i=0;i<jTable.getColumnCount();i++)
//            {
//                jTable.getColumn(jTable.getColumnName(i)).setCellRenderer(colorRender);
//            }
//        }
//    }
//}
//
///** Added for alternative row coloring for JTable -i00182 **/
//class ColorRenderer extends JLabel implements TableCellRenderer
//{
//
//    public ColorRenderer()
//    {
//        setOpaque(true);
//    }
//
//    /**
//     * prepareRenderer, Invokes getCellRender for all rows and columns
//     *
//     * @param  JTable table
//     * @param  int row
//     * @param  int column
//     * @return Component
//     */
//    public Component prepareRenderer(JTable table, int row, int column)
//    {
//        final TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
//        for (int i = 0; i < table.getColumnCount(); ++i)
//            table.getColumnModel().getColumn(i).setPreferredWidth(
//                renderer.getTableCellRendererComponent(table,
//                table.getModel().getColumnName(i), false, false, 0, i)
//                .getPreferredSize().width);
//        return this;
//    }
//
//    /**
//     * getTableCellRendererComponent Invoke by event & colors the table items
//     *
//     * @param  JTable table
//     * @param  Object Value
//     * @param  boolean isSelected
//     * @param  boolean hasFocus
//     * @param  int row
//     * @param  int column
//     * @return Component
//     */
//    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column)
//    {
//        final TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
//        for (int i = 0; i < table.getColumnCount(); ++i)
//            table.getColumnModel().getColumn(i).setPreferredWidth(
//                renderer.getTableCellRendererComponent(table,
//                table.getModel().getColumnName(i), false, false, 0, i)
//                .getPreferredSize().width);
//
//        if (value != null) setText(value.toString());
//        if(isSelected)
//        {
//            setBackground(table.getSelectionBackground());
//            setForeground(table.getSelectionForeground());
//        }
//        else
//        {
//            setBackground(table.getBackground());
//            setForeground(table.getForeground());
//            if((row%2)==0)
//                setBackground(java.awt.Color.white);
//            else
//                setBackground(java.awt.Color.lightGray);
//        }
//        return this;
//    }

//        private JComponent createAlternating(DefaultTableModel model)
//	{
//		JTable table = new JTable( model )
//		{
//			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
//			{
//				Component c = super.prepareRenderer(renderer, row, column);
//
//				//  Alternate row color
//
//				if (!isRowSelected(row))
//					c.setBackground(row % 2 == 0 ? getBackground() : Color.LIGHT_GRAY);
//
//				return c;
//			}
//		};
//
//		table.setPreferredScrollableViewportSize(table.getPreferredSize());
//		table.changeSelection(0, 0, false, false);
//		return new JScrollPane( table );
//	}
//
//	private JComponent createBorder(DefaultTableModel model)
//	{
//
//		JTable table = new JTable( model )
//		{
//			private Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
//			private Border inside = new EmptyBorder(0, 1, 0, 1);
//			private Border highlight = new CompoundBorder(outside, inside);
//
//			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
//			{
//				Component c = super.prepareRenderer(renderer, row, column);
//				JComponent jc = (JComponent)c;
//
//				// Add a border to the selected row
//
//				if (isRowSelected(row))
//					jc.setBorder( highlight );
//
//				return c;
//			}
//		};
//
//		table.setPreferredScrollableViewportSize(table.getPreferredSize());
//		table.changeSelection(0, 0, false, false);
//		return new JScrollPane( table );
//	}
//
//	public JComponent createData()
//	{
//		JTable table = new JTable( tabModel )
//		{
//			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
//			{
//				Component c = super.prepareRenderer(renderer, row, column);
//
//				//  Color row based on a cell value
//
//				if (!isRowSelected(row))
//				{
//					c.setBackground(getBackground());
//					int modelRow = convertRowIndexToModel(row);
//					String type = (String)getModel().getValueAt(modelRow, 0);
//					if ("NORMAL".equals(type)) c.setBackground(Color.GREEN);
//					if ("FAILED".equals(type)) c.setBackground(Color.RED);
//				}
//
//				return c;
//			}
//		};
//
//		table.setPreferredScrollableViewportSize(table.getPreferredSize());
//		table.changeSelection(0, 0, false, false);
//                table.setAutoCreateRowSorter(true);
//		return new JScrollPane( table );
//	}

}
