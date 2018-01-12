/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macinapdf;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
/*
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFPalette;
*/
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import java.awt.Color;
import org.apache.poi.ss.usermodel.FillPatternType;




/**
 *
 * @author Massi
 */

public class Excel {

    static int rownum;
    static double imp=0;
    static double iva=0;
    static double tot=0;

    
    /**
     * Crea un file .xlsx completo di tutti i dati
     * @param data array dei dati
     */
    public static void compilaExcel(String [][] data) {
        
        try {

            try ( // apro il file excel
                FileOutputStream fileXLSOut = new FileOutputStream(Main.nomeFile+".xlsx")) {
                //HSSFWorkbook wb = new HSSFWorkbook();
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet1 = wb.createSheet("Foglio 1");
                
                // stile titolo
                XSSFCellStyle csTitolo = wb.createCellStyle();      // creo un CellStyle                        
                XSSFFont f = wb.createFont();                       // creo fun Font
                f.setFontHeightInPoints((short) 12);                // setto la dimensione del font
                f.setColor( (short)0x0 );                           // imposto il colore del font
                f.setBold(true);                                    // grassetto
                csTitolo.setFont(f);                                // assegno al CellStyle il font
                csTitolo.setFillForegroundColor(new XSSFColor(Color.green));
                csTitolo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                //Stile riga errore
                XSSFCellStyle csErrore = wb.createCellStyle();
                csErrore.setFillForegroundColor(new XSSFColor(Color.red));
                csErrore.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                
                
                for (rownum = (short) 0; rownum < (Scansionatore.n_row) ; rownum++) { 

                    XSSFRow r = sheet1.createRow(rownum);               // creo una riga
                    
                    for (short cellnum = (short) 0; cellnum < 19; cellnum += 1) {  
                        
                        XSSFCell cell = r.createCell(cellnum);             // creo la cella
                        
                        if (rownum==0){

                            cell.setCellStyle(csTitolo);                     // assegno alla cella lo stile
                            cell.setCellValue(data[rownum][cellnum]);        // inserisco i valori

                        } else {
                            if (cellnum==1 | cellnum==2 |
                                cellnum==3 | cellnum==4 | cellnum==5){
                                cell.setCellValue(Double.parseDouble(data[rownum][cellnum]));  // salvo il campo come double
                            } else if (cellnum==6){
                                imp = Double.parseDouble(data[rownum][5])-Double.parseDouble(data[rownum][4]);
                                cell.setCellValue(imp);
                            } else if (cellnum==7){
                                iva = (Double.parseDouble(data[rownum][5])-Double.parseDouble(data[rownum][4]))*0.22;
                                cell.setCellValue(iva);
                            } else if (cellnum==8){
                                tot = iva+imp-Double.parseDouble(data[rownum][4]);
                                cell.setCellValue(tot);
                                
                            } else {
                                if ((cellnum==13 || cellnum==14 || cellnum==16) && data[rownum][cellnum].equals("")){
                                    cell.setCellStyle(csErrore);
                                    cell.setCellValue("Dato mancante");
                                }
                                cell.setCellValue(data[rownum][cellnum]);  // salvo il campo come stringa
                            }        
                        } 
                    }
                }
                
                wb.write(fileXLSOut);
            }

        } catch(IOException e){
            JOptionPane.showMessageDialog(null,"Errore nella classe Excel ** "+e);
        }

    }
}
