/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

/**
 *
 * @author djamelKDE
 */
public class Trustworthiness {
    public double trust;
    public int serviceID;
    public int IDdevice;

    public Trustworthiness(double trust, int serviceID, int IDdevice) {
        this.trust = trust;
        this.serviceID = serviceID;
        this.IDdevice = IDdevice;
    }

    public double getTrust() {
        return trust;
    }

    public int getServiceID() {
        return serviceID;
    }

    public int getIDdevice() {
        return IDdevice;
    }

    public void setTrust(double trust) {
        if(trust<=1.0 && trust >=0.0){
            this.trust = trust;
        }
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public void setIDdevice(int IDdevice) {
        this.IDdevice = IDdevice;
    }
}
