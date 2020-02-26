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


public class Scansionatore_2019_11_12 {
    
    static String line = null;
    static String bim="0",anno="0",nFatt="0";

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
  
    public static String id20="Fattura periodo"; //1° bim 2018
    public static String id20bis="Fattura bimestrale"; //5° bim 2017
    static String id100="Dettaglio Spesa per Linea";
    static String id101="Importo";
    static String id102="M2M";
    static String id103="abb";
    static String id104="ric";
    static String id105="Intercent 2018";
    static String id106="Ricariche";
    static String id107="SERVIZI OPZIONALI";
    static String id108="Tassa";
    static String id109="Totale traffico";
    static String id110="Totale";
    static String id111="Fax e Dati";
    static String id112="Ricaricabile Business";
    static String id113="Blocco";
    static String id114="Filtro";
    static String id115="Intercent";
    static String id116="Servizio Twin";
    static String id117="Intercent 1 GB";
    
    static String id120="Totale Spesa Fissa";
    static String id121="Linee Mobili";
    static String id122="Linea:";
    static String id123="Linea ";
    static String id124="Totale Spesa Variabile";
    static String id125="Totale Tassa Concessione Governativa";
    static String id126="Prospetto Informativo";
    static String id127="Ricarica credito 48";
    static String id128="2018";
    static String id129="Intercent 2018";

    
    static int n_row=1;  //contatore array "data - nella prima riga ci sono le intestazioni
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
                if (line.contains(id20) || line.contains(id20bis)){
                    line = in.nextLine();
                    Scanner riga = new Scanner(line);
                    bim = riga.next().substring(0, 1);      //bimestre
                    anno = riga.next().replace(":","");     //anno
                    line = in.nextLine();
                    riga = new Scanner(line);
                    riga.next();riga.next();
                    nFatt = riga.next();                    //n. fattura
                    riga.close();
                break;
                }
            }
            System.out.println("Bim ="+bim+" Anno ="+anno+" nFatt ="+nFatt);
            
            //segnalo errore
            if (    bim.equals("0")    || 
                    anno.equals("0")){
                System.out.println("Errore nella ricerca dell'intestazione "+bim+" "+anno+" "+nFatt);
            }
            

            //salto la prima parte del documento -- id100="Dettaglio Spesa per Linea";
            while (in.hasNextLine()) {
                line = in.nextLine();
                if (line.contains(id100)){
                    line = in.nextLine();
                    break;
                }    
            }

        
            // scansiono la parte in cui compaiono i report fattura
            while (in.hasNextLine()) {
                
                if (line.contains(id122)){ // contiene "Linea:"
                    outputStream.println(" ");
                    outputStream.println("id0 "+line);

                    //inizializzo le celle per evitare i valori null che danno errore con la conversione in numeri
                    int i;
                    for (i=0; i<20; i++){
                        data[n_row][i]="0";
                    }                    
                    //numero linea
                    Scanner riga = new Scanner(line);
                    riga.next(); 
                    String Num = riga.next();
                    data[n_row][0]=Num;
                    riga.close();
                    
                    //sulla stessa linea estraggo il tipo 
                    //M2M - ric - abb
                    if (line.contains(id102)){        //M2M                        
                        data[n_row][1]=id102;                        
                        System.out.println(Num+" "+data[n_row][1]);
                    } else if (line.contains(id104)){ //ric
                        data[n_row][1]=id104;
                        System.out.println(Num+" "+data[n_row][1]);                       
                    } else if (line.contains(id105)){ //Intercent 2018
                        data[n_row][1]=id103; //abb
                        System.out.println(Num+" "+data[n_row][1]);
                    } else if (line.contains(id112)){ //Ricaricabile Business
                        data[n_row][1]=id112;
                        System.out.println(Num+" "+data[n_row][1]);
                    } 
                        
                // dati fattura
                data[n_row][9] = bim;
                data[n_row][10] = anno;
                data[n_row][11] = nFatt;

                //contatore linee array
                n_row++;
                
                
                
                // Se stiamo lavorando con M2M
                // *****************************
                } else if (data[n_row-1][1].equals(id102)){ //M2M   
                    outputStream.println("M2M "+line); 
                    Scanner riga = new Scanner(line);

                    if (line.contains(id105)&&line.contains("GB")){ //intercent 2018 x rilevare GB e relativo costo
                        riga.next(); riga.next();
                        String GB = riga.next();
                        data[n_row-1][2]=GB;
                        //funzione per selezionare l'importo corretto
                        String cursore =riga.next();
                        while(!(cursore.contains("€"))){
                            //System.out.println(cursore+" La parola non contiene il carattere €"); 
                            cursore=riga.next();
                        }
                        String Importo = cursore.replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][3]=Importo;
                        System.out.println("GB ="+GB+" "+"Importo ="+Importo);
                    }
                    
                    if (line.contains(id124)){ //Totale spesa Variabile
                        riga.next(); riga.next();
                        riga.next(); 
                        String TotVar = riga.next().replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][7]=TotVar;
                        System.out.println("TotVar ="+TotVar);
                    }
                    
                    
                    if (line.contains(id123)){ //"Linea " x rilevare totale 
                        riga.next(); riga.next();
                        String Totale = riga.next().replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][8]=Totale;
                        System.out.println("Totale ="+Totale);
                        System.out.println("------------------------");
                    }
                    
                    riga.close();
                    
                // Se stiamo lavorando con abb
                // ****************************
                } else if (data[n_row-1][1].equals(id103)){ //abb   
                    outputStream.println("abb "+line); 
                    Scanner riga = new Scanner(line);

                    if (line.contains(id105)&&line.contains("GB")){ //intercent 2018 x rilevare GB e relativo costo
                        riga.next(); riga.next();
                        String GB = riga.next();
                        data[n_row-1][2]=GB;

                        //funzione per selezionare l'importo corretto
                        String cursore =riga.next();
                        while(!(cursore.contains("€"))){
                            //System.out.println(cursore+" La parola non contiene il carattere €"); 
                            cursore=riga.next();
                        }                                             
                        String Importo = cursore.replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][3]=Importo;
                        System.out.println("GB ="+GB+" "+"Importo ="+Importo);
                    }
                    
                    if (line.contains(id124)){ //Totale spesa Variabile
                        riga.next(); riga.next();
                        riga.next(); 
                        String TotVar = riga.next().replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][6]=TotVar;
                        System.out.println("TotVar ="+TotVar);
                    }
                    
                    if (line.contains(id125)){ //Totale Tassa Concessione Governativa
                        riga.next(); riga.next();
                        riga.next(); riga.next();
                        String TCG = riga.next().replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][7]=TCG;
                        System.out.println("TCG ="+TCG);
                    }
                    
                    if (line.contains(id123)){ //"Linea " x rilevare totale 
                        riga.next(); riga.next();
                        String Totale = riga.next().replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][8]=Totale;
                        System.out.println("Totale ="+Totale);
                        System.out.println("------------------------");
                    }
                
                    riga.close();    
                 
                // Se stiamo lavorando con ric
                // ****************************    
                    
                } else if (data[n_row-1][1].equals(id104)){ //ric   
                    outputStream.println("ric "+line); 
                    Scanner riga = new Scanner(line);

                    if (line.contains(id129)&&line.contains("GB")){ //"2018" e "GB" x rilevare GB e relativo costo
                        //System.out.println("ric intercent+GB "+line);
                        riga.next(); riga.next();
                        String GB = riga.next();
                        data[n_row-1][2]=GB;

                        //funzione per selezionare l'importo corretto
                        line = in.nextLine(); line = in.nextLine(); // la riga "INTERCENT 2018.." viene spezzata in più righe
                        riga = new Scanner(line);                   // e l'importo è su una riga successiva
                        String cursore =riga.next();
                        while(!(cursore.contains("€"))){
                            //System.out.println(cursore+" La parola non contiene il carattere €"); 
                            cursore=riga.next();
                        }                                             
                        String Importo = cursore.replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][3]=Importo;
                        System.out.println("GB ="+GB+" "+"Importo ="+Importo);
                    }
                    
                    if (line.contains(id127)){ //"Ricarica credito 48" 
                        riga.next();riga.next();riga.next();
                        String numRic = riga.next();
                        data[n_row-1][4]=numRic;
                        String impRic = riga.next().replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][5]=impRic;
                        
                        System.out.println("numRic ="+numRic+" impRic ="+impRic);
                    }
                            
                    
                    if (line.contains(id123)){ //"Linea " x rilevare totale 
                        //System.out.println(line);
                        riga = new Scanner(line);
                        riga.next(); riga.next(); 
                        String Totale = riga.next().replace("€","").replace(".","").replace(",",".");
                        data[n_row-1][8]=Totale;
                        System.out.println("Totale ="+Totale);
                        System.out.println("------------------------");
                    }
                    
                    riga.close();
                
                } 
                

                line = in.nextLine();   // passo alla riga successiva
                if (line.contains(id126)) { //"Prospetto Informativo" fine scansione
                    System.out.println("fine");
                    break;
                }
                
            }
        outputStream.println(Arrays.deepToString(data)); //esplode l'array
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
  



