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
    static String id1="Tipo";
    static String id2="Importo";
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
    static String id102="SERVIZI OPZIONALI A BUNDLE";

    
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
              
                // contiene id0
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
                    
                    //M2M - ric - abb
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

                    
                n_row++;                //contatore linee array
                line = in.nextLine();   // passo alla riga successiva

                // entro nel dettaglio dopo la parola "importo"
                } else if (line.contains(id101)){
                    line = in.nextLine();
                    outputStream.println("id101 "+line);
                    
                    // M2M
                    if (data[n_row-1][1].equals("M2M")){
                        Scanner riga = new Scanner(line);
                        riga.next(); riga.next();
                        String GB = riga.next();
                        riga.next(); riga.next(); riga.next();
                        String Importo = riga.next();
                        data[n_row-1][2]=GB;
                        data[n_row-1][3]=Importo;        
                    
                        System.out.println("M2M "+GB+" "+Importo);
                        
                    // Abb    
                    } else if (data[n_row-1][1].equals("abb")){
                        Scanner riga = new Scanner(line);

                        
                        System.out.println("abb");
                    // ric    
                    } else if (data[n_row-1][1].equals("ric")){
                        Scanner riga = new Scanner(line);
                        if (riga.next().equals("Intercent")){
                            riga.next();
                            String check=riga.next();
                            if (check.equals("ric")){
                                riga.next(); riga.next(); riga.next(); riga.next();
                                String Importo = riga.next();
                                data[n_row-1][3]=Importo;
                                System.out.println("Importo "+Importo);
                            } else {
                                String GB = check;
                                riga.next(); riga.next(); riga.next(); riga.next(); riga.next();
                                String Importo = riga.next();
                                data[n_row-1][2]=GB;
                                data[n_row-1][3]=Importo;
                                System.out.println(GB+" GB "+Importo+" Importo");
                                
                            }
                            
                        } else if (data[n_row-1][1].equals("Ricariche")){
                            System.out.println("Ricariche");
                        }                        
                    
                        System.out.println("ric");
                    // tipo non riconosciuto    
                    } else {
                        
                        System.out.println("Errore Scansionatore - tipo non M2M/ric/abb");
                        
                    }
                } else if (line.contains(id102)){
                    
                }
                        
                        
                        
                    

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
  



