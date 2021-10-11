/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.math.BigInteger;
import java.util.*;
/**
 *
 * @author djamelKDE
 */
public abstract class IoTDevice{
    public int identifier;  // identifiar of the IoT device
    public boolean state;      // honest or malicious
    public int fogHomeNode;    // Identifiar of the home fog node
    public TypeDevice type;
    public int serviceID;

    @Override
    public String toString() {
        return "IoTDevice{" + "identifier=" + identifier + ", state=" + (state ? ("Honest") : ("Malicious")) + ", type=" + type + '}';
    }
    public BigInteger publickey;
    public BigInteger privatekey;
    public LinkedList<Trustworthiness> listOfTrustworthiness= new LinkedList<>();
    public LinkedList<IoTDevice> listIoTDevices;
    public LinkedList<Double> satisfactionCumul = new LinkedList<>();
    public LinkedList<Double> dissatisfactionCumul = new LinkedList<>();
    
    public LinkedList<IoTDevice> getListIoTDevices() {
        return listIoTDevices;
    }

    public void setListIoTDevices(LinkedList<IoTDevice> listIoTDevices) {
        this.listIoTDevices = listIoTDevices;
    }
    
    public IoTDevice(int identifier, boolean state, int fogHomeNode, TypeDevice type, int serviceID){
        this.identifier=identifier;
        this.fogHomeNode=fogHomeNode;
        this.state=state;
        this.type = type;
        this.serviceID=serviceID;
    }

    public void initialiseTrustworthiness(){
        for(IoTDevice d : listIoTDevices){
            listOfTrustworthiness.add(new Trustworthiness(0.5, d.serviceID, d.getIdentifier()));
        }
    }
    
    public void initialiseSatisfactions(){
        for(int i=0;i<listIoTDevices.size();i++){
            satisfactionCumul.add(0.0);
            dissatisfactionCumul.add(0.0);
        }
    }
    
    public void updateSatisfactionCummul(double currentSatisfaction, int deviceID){
        System.out.println(this+" Before: "+satisfactionCumul.get(deviceID));
        satisfactionCumul.set(deviceID,satisfactionCumul.get(deviceID)+currentSatisfaction); // alpha_ij
        dissatisfactionCumul.set(deviceID,dissatisfactionCumul.get(deviceID)+1.0-currentSatisfaction); // beta_ij
        System.out.println(this+" After: "+satisfactionCumul.get(deviceID));
    }
    
    
    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setFogHomeNode(int fogHomeNode) {
        this.fogHomeNode = fogHomeNode;
    }

    public void setType(TypeDevice type) {
        this.type = type;
    }

    public void setListOfTrustworthiness(LinkedList<Trustworthiness> listOfTrustworthiness) {
        this.listOfTrustworthiness = listOfTrustworthiness;
    }

    public boolean isState() {
        return state;
    }

    public int getFogHomeNode() {
        return fogHomeNode;
    }

    public LinkedList<Trustworthiness> getListOfTrustworthiness() {
        return listOfTrustworthiness;
    }

    public TypeDevice getType() {
        return type;
    }
    
    // send a transaction to the blockchain.
    public int sendTransaction(Trustworthiness trustValue){
        return 0;
    }
    
    // computation of the trustworthiness of the IoTDevice in parameter.
    public Trustworthiness trustComputation(IoTDevice device){
        Trustworthiness trustDevice = null;
        return trustDevice;
    }
    
    // the code executed by the IoT device.
    public abstract void trustAssessementBehavior();
    
   
}
