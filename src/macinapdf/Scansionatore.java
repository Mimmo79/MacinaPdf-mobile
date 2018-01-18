package macinapdf;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author massi
 */


public class Scansionatore {
    
    static String line = null;
    static String bim,anno,nFatt;

    static String id0="Linea";
    static String id1="tipo";
    static String id2="TOTALE TRAFFICO";
    static String id3="TOTALE ALTRI ADDEBITI E ACCREDITI";
    static String id4="F.C.IVA";
    static String id5="TOTALE";
    static String id6="Imponibile (TOTALE - F.C.IVA)";
    static String id7="IVA";
    static String id8="TOTALE IVATO";
    static String id9="Bimestre";
    static String id10="Anno";
    static String id11="n_fattura";
    static String id12="CAP SPESA";
    static String id13="CdR";
    static String id14="CdG";
    static String id15="RIL. IVA";
    static String id16="IMPEGNO";
    static String id17="Sede";
    static String id18="Note";
  
    public static String id20="Fattura periodo:";
    static String id100="RIEPILOGO PER UTENZA";
    static String id101="Importo";


    
    static int n_row=1;  // nella prima riga ci sono le intestazioni
    static boolean n_FCIVA=true;
    static float val_FCIVA=0;
    static String[][] data = new String[Main.nRigheArrayData][Main.nColonneArrayData];
    
    
    /**
    * Questo metodo compila un array "data" estrapolando i dati dalla fattura multipla.
    * 
    * La prima riga contiene l'intestazione
    * [id0][id1][id2][id3][id4][id5][id6]...
    * 
    * @author Massi
    * @param nomeFile nome del file(senza estensione)da elaborare
    * @return array contenente i dati estrapolati.
    */
    
    public static String[][] scansiona(String nomeFile) {

        try {   
            // apro il file .txt in lettura
            BufferedReader inputStream = new BufferedReader( new FileReader(nomeFile+".txt"));
            Scanner in = new Scanner(inputStream);
            // apro il file -elab1.txt in cui riverso i dati elaborati
            PrintWriter outputStream = new PrintWriter(new FileWriter(nomeFile+"-elab1.txt"));
            
            //cerco i dati di intestazione
            while (in.hasNextLine()) {
                line = in.nextLine();
                if (line.contains(id20)){
                    line = in.nextLine();
                    Scanner riga = new Scanner(line);
                    bim = riga.next().substring(0, 1);      //bimestre
                    anno = riga.next().replace(":","");     //anno
                    line = in.nextLine();
                    riga = new Scanner(line);
                    riga.next();riga.next();
                    nFatt = riga.next();                    //n. fattura
                break;
                }
            }

            //salto la prima parte del documento
            while (in.hasNextLine()) {
                line = in.nextLine();
                if (line.contains(id100))
                    break;
            }
       
        
            // scansiono la parte in cui compaiono i report fattura
            while (in.hasNextLine()) {
              
                if (line.contains(id0)){
                    outputStream.println(" ");
                    outputStream.println("id0 "+line);

                    //inizializzo le celle per evitare i valori null che danno errore con la conversione in numeri
                    int i;
                    for (i=0; i<20; i++){
                        data[n_row][i]="0";
                    }                    
                    // numero linea
                    Scanner riga = new Scanner(line);
                    riga.next(); 
                    String Num = riga.next();
                    data[n_row][0]=Num;
                    
                    //M2M o ric
                    String tipo = riga.next();
                    if (tipo.equals("M2M")){
                        tipo="M2M";
                        data[n_row][1]=tipo;
                        System.out.println(Num+" "+tipo);                        
                    } else if (tipo.equals("Intercent")){
                        riga.next();
                        tipo=riga.next();
                        data[n_row][1]=tipo;
                        System.out.println(Num+" "+tipo);
                    }
                    
                    // dati fattura
                    data[n_row][9] = bim;
                    data[n_row][10] = anno;
                    data[n_row][11] = nFatt;               

                    
                    n_row++;        //contatore linee array
                    val_FCIVA=0;    //valore somma di tutti i FCIVA relativi ad un numero
                    n_FCIVA=true;   //controllo di FCIVA multipli

                // entro nel dettaglio dopo la parola "importo"
                } else if (line.contains(id101)){
                    line = in.nextLine();
                    
                    outputStream.println("id101 "+line);
                    
                    Scanner riga = new Scanner(line);
                    riga.next(); riga.next(); 
                    String Tot_ConAbb = riga.next();
                    System.out.println(Tot_ConAbb);
                    //data[n_row-1][1]=Tot_ConAbb.replace(".","").replace(",",".");  //il primo elimina i punti, il secondo converte le virgole in punti

                    
                } else if (line.contains(id2)){
                    outputStream.println("id2 "+line);
                    
                    Scanner riga = new Scanner(line);
                    riga.next(); riga.next();
                    String Tot_Traff = riga.next();
                    //System.out.println(Tot_Traff);
                    data[n_row-1][2]=Tot_Traff.replace(".","").replace(",",".");
 
                } else if (line.contains(id3)){
                    outputStream.println("id3 "+line);
                    
                    Scanner riga = new Scanner(line);
                    riga.next(); riga.next(); riga.next(); riga.next(); riga.next();
                    String Tot_Altri = riga.next();
                    //System.out.println(Tot_Altri);
                    data[n_row-1][3]=Tot_Altri.replace(".","").replace(",",".");
                                     
                } else if (line.contains(id4)){
                    outputStream.println("id4 "+line);
                    
                    Scanner riga = new Scanner(line);
                    //rilevo il valore del fuori campo iva
                    String a = riga.next();
                    String b = riga.next();
                    while (!b.equals(id4)){
                    a=b;
                    b = riga.next();                    
                    }

                    data[n_row-1][4]=a.replace(".","").replace(",",".");   // il fuori F.C.IVA occupa i campi dal 12 in su
                    
                    // sommo i F.C.IVA multipli
                    if (n_FCIVA){
                        val_FCIVA = Float.parseFloat(data[n_row-1][4]);
                    } else {
                        val_FCIVA = val_FCIVA + Float.parseFloat(data[n_row-1][4]);
                        data[n_row-1][4]=Float.toString(val_FCIVA);
                        //System.out.println("FCIVA : "+val_FCIVA+" "+importiFatt[n_row-1][5]);
                    }
                    n_FCIVA=false;
                    
                }   else if (line.contains(id5)){
                    outputStream.println("id5"+line);
                    
                    Scanner riga = new Scanner(line);
                    riga.next();
                    String Tot = riga.next();
                    //System.out.println(Tot);
                    data[n_row-1][5]=Tot.replace(".","").replace(",","."); 
                    
                }
            //SERVIZI OPZIONALI A BUNDLE
            line = in.nextLine();
                
            }
            
        outputStream.close();        
        inputStream.close();
        
        } catch(IOException e){
            JOptionPane.showMessageDialog(null,"Scansionatore.scansiona ** "+e);
        }
                
    //intestazione
    data[0][0]=id0;    
    data[0][1]=id1;
    data[0][2]=id2;    
    data[0][3]=id3;
    data[0][4]=id4;
    data[0][5]=id5;
    data[0][6]=id6;
    data[0][7]=id7;
    data[0][8]=id8;
    data[0][9]=id9;
    data[0][10]=id10;
    data[0][11]=id11;
    data[0][12]=id12;
    data[0][13]=id13;
    data[0][14]=id14;
    data[0][15]=id15;
    data[0][16]=id16;
    data[0][17]=id17;
    data[0][18]=id18;



    return data;


    }
    
}
  



