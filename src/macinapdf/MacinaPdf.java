/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macinapdf;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author SenMa
 */

public class MacinaPdf {

    /**
     * Questo metodo trasforma il documento .pdf in un file pdf contenente 
     * tutto il testo del file originale senza formattazione
     * 
     * @param nomeFile è il nome del file .pdf da elaborare
     */
    public static void macina(String nomeFile){
 
        try {       
        //converto il file in .txt
        File file = new File(nomeFile+".pdf");                       // apro il file .pdf
        PDDocument inputDoc= PDDocument.load(file);   
        PDFTextStripper stripper = new PDFTextStripper();            // lo strippo
        PrintWriter outputStream = new PrintWriter(new FileWriter(Main.nomeFile+".txt"));
        outputStream.println(stripper.getText(inputDoc));            // salvo lo strippo in un .txt
        outputStream.close();
        inputDoc.close();

        } catch(IOException e){
            JOptionPane.showMessageDialog(null,"Scansionatore.scansiona ** "+e);
        }
        
    } 
}