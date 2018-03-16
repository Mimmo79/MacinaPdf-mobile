package macinapdf;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.util.Arrays;

/**
 *
 * @author massi
 */


public class Scansionatore {
    
    static String line = null;
    static String bim,anno,nFatt;

    static String id0="Linea";
    static String id1="Tipo";
    static String id2="GB";
    static String id3="Importo GB";
    static String id4="QtaRicariche";
    static String id5="Importo Ricariche";
    static String id6="TCG";
    static String id7="Totale traffico";
    static String id8="Totale";
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
    static String id102="M2M";
    static String id103="abb";
    static String id104="ric";
    static String id105="Intercent";
    static String id106="Ricariche";
    static String id107="SERVIZI OPZIONALI";
    static String id108="Tassa";
    static String id109="Totale traffico";
    static String id110="Totale";
    static String id111="Fax e Dati"; 


    
    static int n_row=1;  // nella prima riga ci sono le intestazioni
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
              
                // contiene "linea"
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
                    if (tipo.equals(id102)){        //M2M
                        data[n_row][1]=id102;                        
                        System.out.println(Num+" "+tipo);                        
                    } else if (tipo.equals(id105)){ //Intercent
                        riga.next();
                        data[n_row][1]=riga.next(); //ric/abb
                        System.out.println(Num+" "+data[n_row][1]);
                    } 
                    
                    
                    
                    
                // dati fattura
                data[n_row][9] = bim;
                data[n_row][10] = anno;
                data[n_row][11] = nFatt;               

                    
                n_row++;                //contatore linee array
                line = in.nextLine();   // passo alla riga successiva

                // entro nel dettaglio dopo la parola "importo"
                } else if (line.contains(id101)){
                    if (data[n_row-1][0].equals(data[n_row-2][0])) //nei cambi pagina a volte il numero sim si ripete
                        n_row--;
                    
                    // M2M
                    if (data[n_row-1][1].equals(id102)){    
                        line = in.nextLine();
                        outputStream.println("id101 M2M "+line);
                        while ( line.contains(id105) ||
                                line.contains(id111)){

                            if (line.contains(id105)){ //intercent
                                Scanner riga = new Scanner(line);
                                riga.next(); riga.next();
                                String GB = riga.next();
                                riga.next(); riga.next(); riga.next();
                                String Importo = riga.next();
                                data[n_row-1][2]=GB;
                                data[n_row-1][3]=Importo.replace(".","").replace(",",".");  //il primo elimina i punti, il secondo converte le virgole in punti
                                line = in.nextLine();
                            } else if (line.contains(id111)){//Fax e Dati
                            line = in.nextLine();
                            }
                        }
                        
                        Scanner riga = new Scanner(line);   // Totale
                        riga.next();
                        data[n_row-1][8]=riga.next().replace(".","").replace(",",".");
                    
                        System.out.println( " GB -> "+data[n_row-1][2]+
                                            " Importo x bundle-> "+data[n_row-1][3]+
                                            " Totale -> "+data[n_row-1][8]);
                        System.out.println( " ***************");
                        
                    // Abb    
                    } else if (data[n_row-1][1].equals(id103)){
                        line = in.nextLine();
                        outputStream.println("id101 Abb "+line);
                        
                        while (line.contains(id105) || //Intercet-Tassa-Totale traffico
                               line.contains(id108) ||
                               line.contains(id109)){
                            
                            Scanner riga = new Scanner(line);
                            
                            if (line.contains(id105)){           // "intercent"
                                riga.next();riga.next();
                                data[n_row-1][2]=riga.next();   // GB
                                riga.next();riga.next();riga.next();
                                data[n_row-1][3]=riga.next().replace(".","").replace(",",".");   //importo
 
                            } else if (line.contains(id108)){    // "tassa"
                                riga.next();riga.next();riga.next();
                                riga.next();riga.next();riga.next();
                                riga.next();riga.next();riga.next();
                                data[n_row-1][6]=riga.next().replace(".","").replace(",",".");                                  
                                
                            } else if (line.contains(id109)){    // "Totale traffico"
                                riga.next();riga.next();riga.next();
                                riga.next();riga.next();
                                data[n_row-1][7]=riga.next().replace(".","").replace(",",".");                               
                                
                            } else {
                                System.out.println("Errore in sottosezione Abb - Nessun match");
                                break;
                            }
                            
                        line = in.nextLine();
                        
                        }
                    
                        if (line.contains(id110)){ //Totale
                            Scanner riga = new Scanner(line);
                            riga.next();
                            data[n_row-1][8]=riga.next().replace(".","").replace(",",".");
                        }
                            
                            
                        System.out.println( " GB -> "+data[n_row-1][2]+
                                            " Importo x bundle -> "+data[n_row-1][3]+
                                            " TCG -> "+data[n_row-1][6]+
                                            " Totale importo traffico -> "+data[n_row-1][7]+
                                            " Totale -> "+data[n_row-1][8]);
                        System.out.println( " 0000000000000000000000000000");                    
                            

                        
                    // ric    
                    } else if (data[n_row-1][1].equals(id104)){ //ric
                        line = in.nextLine();
                        outputStream.println("id101 Ric "+line);                     
                        while (line.contains(id105)){  //intercent
                            Scanner riga = new Scanner(line);
                            riga.next();riga.next();
                            String check=riga.next();
                            if (check.equals(id104)){ //ric -> Intercent 2014 ric, contributo ricaricabile sempre a 0
                                riga.next(); riga.next(); riga.next(); riga.next();
                                String Importo = riga.next();
                                //data[n_row-1][3]=Importo;
                                //System.out.println("ric  GB - Importo "+Importo);
                            } else {
                                String GB = check;
                                riga.next(); riga.next(); riga.next(); riga.next(); riga.next();
                                String Importo = riga.next();
                                data[n_row-1][2]=GB;
                                data[n_row-1][3]=Importo.replace(".","").replace(",",".");  
                            }
                            
                        line = in.nextLine();
                        
                        }
                        if (line.contains(id106)){ //ricariche
                            Scanner riga = new Scanner(line);
                            riga.next(); 
                            data[n_row-1][4]=riga.next().replace(".","").replace(",",".");   //qta 
                            data[n_row-1][5]=riga.next().replace(".","").replace(",",".");   //importo
                            line = in.nextLine();
                        }
                        
                        if (line.contains(id109)){ //Totale
                            Scanner riga = new Scanner(line);
                            riga.next();
                            data[n_row-1][7]=riga.next().replace(".","").replace(",",".");
                        }
                        
                        System.out.println( " GB -> "+data[n_row-1][2]+
                                            " Importo x bundle -> "+data[n_row-1][3]+
                                            " ricariche -> "+data[n_row-1][4]+
                                            " Importo ricariche -> "+data[n_row-1][5]+
                                            " Totale -> "+data[n_row-1][7]);
                        System.out.println( " -----------------------");
                        
                                              
                    } else {
                        
                        System.out.println("Errore Scansionatore - tipo non M2M/ric/abb");
                      
                    }
                
                // "SERVIZI OPZIONALI.."  salto questa parte perchè contiene solo i consumi dati solo per M2M e Abb  
                } else if (line.contains(id107)){
                    
                    break;
                   
                }            

            line = in.nextLine();
                
            }
        //outputStream.println(Arrays.deepToString(data)); esplode l'array
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
  



