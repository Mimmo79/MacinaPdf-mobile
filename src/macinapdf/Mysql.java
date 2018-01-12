/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macinapdf;

   

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mysql {

    
    public static boolean esisteRecord (String db, String tabella, String campo, String record){

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        boolean result = false;

        try {
            
            con = DriverManager.getConnection(Main.dbUrl, Main.dbUser, Main.dbPwd);
            st = con.createStatement();
            rs = st.executeQuery("select * from "+db+"."+tabella+" where "+campo+"="+record+"");
            result = rs.next();

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            
            try {
                
                if (rs != null)
                    rs.close();
                
                if (st != null)
                    st.close();
                            
                if (con != null) 
                    con.close();
                              
            } catch (SQLException ex) {
                
                Logger lgr = Logger.getLogger(Mysql.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    
        return result;
    }
        
    public static void inserisciRecord (String db, String tabella, String campo, String record){
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            
            con = DriverManager.getConnection(Main.dbUrl, Main.dbUser, Main.dbPwd);
            st = con.createStatement();
            st.executeUpdate("INSERT INTO " + db + "." + tabella + " ( "+campo+" ) VALUES ("+record+")");
            

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            
            try {
                
                if (rs != null)
                    rs.close();
                
                if (st != null)
                    st.close();
                            
                if (con != null) 
                    con.close();
                              
            } catch (SQLException ex) {
                
                Logger lgr = Logger.getLogger(Mysql.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        
    }
    
    public static String recuperaRecord (String db, String tabella, String campo, String record_ricercato, String campo_recupero){
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String result = null;

        try {
            
            con = DriverManager.getConnection(Main.dbUrl, Main.dbUser, Main.dbPwd);
            st = con.createStatement();
            rs = st.executeQuery("select * from "+db+"."+tabella+" where "+campo+"="+record_ricercato+"");
            if (rs.next()){
                result=rs.getString(campo_recupero);
            }
            
            

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            
            try {
                
                if (rs != null)
                    rs.close();
                
                if (st != null)
                    st.close();
                            
                if (con != null) 
                    con.close();
                              
            } catch (SQLException ex) {
                
                Logger lgr = Logger.getLogger(Mysql.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        
        return result;
        
    }

    /**
     * esegue una query per avere tutti i parametri di pagamento delle fatture
     * @param data
     * @return l'array completo dei dati mancanti
     */    
    public static String[][] completaArrayConQuery (String[][] data){
        int riga,i;
        String Num;
        Mysql oggettoMysql = new Mysql(Main.dbUrl, Main.dbUser, Main.dbPwd);
        
        String queryTemp=null;
        queryTemp=  " select    A.NLinea,A.CapSpesa, A.Cdr, A.Cdg, A.Ril_iva, A.Impegno, " +
                    "		B.Cessazione, B.Nota, " +
                    "		C.Sede " +
                    " from      "+Main.dbName+".fisso_linee A " +
                    " left join "+Main.dbName+".fisso_linee_indirizzi_sedi B " +
                    "       on A.IDLinea=B.`FK-IDlinea` " +
                    " left join "+Main.dbName+".fisso_sedi C " +
                    "	on B.`FK-IDsede`=C.IDsede" +
                    " ORDER BY NLinea, B.DataInserimento DESC"; // in presenza di più record legati alla linea
                                                                // viene preso il più recente

        oggettoMysql.executeQueryRecuperaMulti(queryTemp, 9);           
        
        for (riga=1; riga < (Scansionatore.n_row) ; riga++){
            //System.out.println(Num);
            Num=data[riga][0];
            for (i=0; i<Main.nRigheArrayData; i++){
                if (Num.equals(arrayResult[i][0])){
                    data[riga][12] = arrayResult[i][1]; // "CapSpesa"
                    if (data[riga][12]==null || data[riga][12].equals(""))
                        data[riga][12]="CapSpesa non presente";
                    data[riga][13] = arrayResult[i][2]; // Cdr
                    data[riga][14] = arrayResult[i][3]; // Cdg
                    data[riga][15] = arrayResult[i][4]; // Ril_iva
                    data[riga][16] = arrayResult[i][5]; // Impegno
                    data[riga][17] = arrayResult[i][8]; // Sede
                    if (!(arrayResult[i][6]==null || arrayResult[i][6].equals("0")))    // se diverso da null e 0
                        data[riga][18] = "Cessata. Nota:"+arrayResult[i][7]; // Nota
                break;    
                }                   
            }       
        }
        oggettoMysql.closeCon();
        return data;      
    }
    
    /**
     * carica i dati su DBMS
     * 
     * @param data
     */
    public static void caricaFattureSuDMBS (String[][] data){
        
        int riga;
        String query=null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            //apro la connessione
            con = DriverManager.getConnection(Main.dbUrl, Main.dbUser, Main.dbPwd);
            //verifico che le fatture non siano già state caricate
            query="select * from "+Main.dbName+"."+Main.tab_fatture+" where Bim="+data[1][9]+" and  Anno="+data[1][10]+" ;";               
            pst = con.prepareStatement(query);
            rs=pst.executeQuery();
            if (rs.next()){
                System.out.println("Almeno un record di questo bimestre è già stato caricato!!!");
                return;
            }
            
            
            
            query=null;
            rs=null;
            query="INSERT INTO " + Main.dbName + "." + Main.tab_fatture + 
                " (             NLinea, Bim,    Anno,   Nfattura,   TotaleContributiEAbbonamenti,   TotaleTraffico, FCIva,  TotaleAltriAddebitiEAccrediti,  Totale)" +
                " VALUES (      ?,      ?,      ?,      ?,          ?,                              ?,              ?,      ?,                              ?)";         
            pst = con.prepareStatement(query);
            
            for (riga=1; riga < (Scansionatore.n_row) ; riga++){
                pst.setString(1, data[riga][0]);
                pst.setString(2, data[riga][9]);
                pst.setString(3, data[riga][10]);
                pst.setString(4, data[riga][11]);
                pst.setString(5, data[riga][1]);
                pst.setString(6, data[riga][2]);
                pst.setString(7, data[riga][4]);
                pst.setString(8, data[riga][3]);
                pst.setString(9, data[riga][5]);
     
                pst.executeUpdate();
                
            }
            
        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            
            try {
                
                if (rs != null)
                    rs.close();
                
                if (pst != null)
                    pst.close();
                            
                if (con != null) 
                    con.close();
                              
            } catch (SQLException ex) {
                
                Logger lgr = Logger.getLogger(Mysql.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        
    }
    
    
    
    public Connection con = null;
    public Statement st = null;
    public PreparedStatement pst = null;
    public ResultSet rs = null;
    public String result = null;
    static public String [][] arrayResult = new String[Main.nRigheArrayData][Main.nColonneArrayData];
    
    public              Mysql(String dbUrl, String dbUser, String dbPwd){
        
        try {
        con = DriverManager.getConnection(Main.dbUrl, Main.dbUser, Main.dbPwd);      
        st = con.createStatement();
        
        
        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } 
        
        
    }
    public Boolean      executeQueryBoolean(String query){
        Boolean a=false;
        try {
            
            this.rs = this.st.executeQuery(query);
            a = this.rs.next();
        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        }
        return a;
    }
    public void         executeQueryRecupera(String query, String campo_recupero){
        try {
            
            
            this.rs = this.st.executeQuery(query);
            if (this.rs.next()){
                result=rs.getString(campo_recupero);
            }
 
        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        }
    }
    public void         executeQueryRecuperaMulti(String query, int nElementi){
        try {
            
            pst=this.con.prepareStatement(query);
            rs = pst.executeQuery();
            int i,n=0;
            while (rs.next()) {
                // l'oggetto "rs" risultato della query è un array con l'indice della prima colonna a 1
                for (i=1; i<nElementi+1; i++){
                        arrayResult[n][i-1]=rs.getString(i);
                }                 
                n++;
            }    

 
        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(Mysql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        }
    }  
    public void         closeCon(){
        try {       
                if (this.rs != null)
                    this.rs.close();            
            
                if (this.st != null)
                    this.st.close();
                
                if (this.con != null) 
                    this.con.close();
                              
            } catch (SQLException ex) {
                
                Logger lgr = Logger.getLogger(Mysql.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        
    }
    
}
    


    

