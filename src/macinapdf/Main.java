/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macinapdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author SenMa
 */

public class Main {

    static String fileParam="C:\\Users\\senma\\Documents\\NetBeansProjects\\MacinaPdf-mobile\\src\\macinapdf\\parametri";
    //C:\\Users\\Massi\\XAMPP\\htdocs\\Pdf_to_txt\\src\\macinapdf
    //C:\Users\senma\Documents\NetBeansProjects\Pdf_to_txt-mobile\src\macinapdf
    //C:\\Users\\massi\\Desktop\\Pdf_to_txt-mobile\\src\\macinapdf\\parametri
    
    
    public static String nomeFile;
    public static String dbUrl;
    public static String dbUser;
    public static String dbPwd;
    public static String dbName;
    public static String tab_linee;
    public static String tab_fatture;
    public static String nome_campo_linea;
    public static int nRigheArrayData;
    public static int nColonneArrayData;
   
    /**
     * Carica i parametri da file 
     */
    public static void getParameters(){
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(fileParam); 
            props.load(in);

        } catch (FileNotFoundException ex) {

            Logger lgr = Logger.getLogger(Pdf_to_txt.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } catch (IOException ex) {

            Logger lgr = Logger.getLogger(Pdf_to_txt.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {

            try {
                 if (in != null) {
                     in.close();
                 }
            } catch (IOException ex) {
                Logger lgr = Logger.getLogger(Pdf_to_txt.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        dbUrl = props.getProperty("dbUrl");
        dbName = props.getProperty("dbName");
        dbUser = props.getProperty("dbUser");
        dbPwd = props.getProperty("dbPwd");
        nomeFile = props.getProperty("nomeFile");
        tab_linee = props.getProperty("tab_linee");
        tab_fatture = props.getProperty("tab_fatture");
        nome_campo_linea = props.getProperty("nome_campo_linea");
        nRigheArrayData = Integer.parseInt(props.getProperty("nRigheArrayData"));
        nColonneArrayData = Integer.parseInt(props.getProperty("nColonneArrayData"));
        
    }
    
    
    
    public static void main(String[] args) throws Exception {
        getParameters();                                                    //carico i parametri     
        Pdf_to_txt.macina(nomeFile);                                         //converto il file in .txt
        String data[][] = Scansionatore_2019_11_12.scansiona(nomeFile);                //elaboro il .txt ed estraggo i dati in un array
        //String data[][] = Scansionatore_2014.scansiona(nomeFile);
        Mysql.caricaFattureSuDMBS(data);
        //Mysql.completaArrayConQuery(data);
        //Excel.compilaExcel(data);                                         //passo l'array ad un metodo per la scrittura su un file excel           
        ManipolaFile.eliminaFile(nomeFile+".txt");                          //elimino i file di appoggio
        ManipolaFile.eliminaFile(nomeFile+"-elab1.txt");
        
        
        
    } 
}