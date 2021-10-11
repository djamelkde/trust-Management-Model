/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author djamelKDE
 */
public class Transaction implements Serializable{

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setIdTrustor(int idTrustor) {
        this.idTrustor = idTrustor;
    }

    public void setIdTrustee(int idTrustee) {
        this.idTrustee = idTrustee;
    }

    @Override
    public String toString() {
        return "Transaction{" + "timestamp=" + timestamp + ", idTrustor=" + idTrustor + ", idTrustee=" + idTrustee + ", trsutvalue=" + trsutvalue + ", creteria=" + creteria + '}';
    }

    public void setTrsutvalue(double trsutvalue) {
        this.trsutvalue = trsutvalue;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getIdTrustor() {
        return idTrustor;
    }

    public int getIdTrustee() {
        return idTrustee;
    }

    public double getTrsutvalue() {
        return trsutvalue;
    }

    public Transaction(Date timestamp, int idTrustor, int idTrustee, double trsutvalue) {
        this.timestamp = timestamp;
        this.idTrustor = idTrustor;
        this.idTrustee = idTrustee;
        this.trsutvalue = trsutvalue;
    }
    public Date timestamp;
    public int idTrustor;
    public int idTrustee;
    public double trsutvalue;
    public String creteria;

    public String getCreteria() {
        return creteria;
    }
    
    public boolean containsCreterias(String cr){
        return creteria.contains(cr);
    }
    
    public int intersectionCreterias(String cr){
        String[] tab = cr.split(" ");
        int nb=0;
        for(int i =0; i<tab.length;i++){
            if(creteria.contains(tab[i])) nb++;
        }
        return nb;
    }
    
    public double weightCreteria(String cr){
        return ((double)cr.split(" ").length)/nbCreterias();
    }
    
    public int nbCreterias(){
        return creteria.split(" ").length;
    }
}
