/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macinapdf;


import java.io.File;

/**
 *
 * @author SenMa
 */
public class ManipolaFile {
    
    
    private static String tipoFile;

    

    /**
    * Cancella il file passato come paramentro stringa
    */
    public static void eliminaFile (String fileDaEliminare){
        File daEliminare = new File(fileDaEliminare); //Referenzia oggetto file da percorso
        if(daEliminare.exists()) { //se esiste...
                if(daEliminare.delete()) //prova a eliminarlo...
                        System.out.println(daEliminare + " eliminato!"); //e conferma...
        }
        else {
            System.out.println("Il file non esiste!");//altrimenti avverte l'utente
        }
    }
  
    
}
