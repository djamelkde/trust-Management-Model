/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author djamelKDE
 */
public class MaliciousTrustComputation extends TimerTask  {
    // execute the algorithm of trust assessement.
    MaliciousNode iotDevice;
    LinkedList<IoTDevice> listofServiceProviders;
    int honestTargetProvider=0;
    int dishonestTargetProvider=0;

    public MaliciousTrustComputation(MaliciousNode iotDevice) {
        this.iotDevice = iotDevice;
        initiliaseListOfProviders();
        selectHonestTarget();
        //selectDishonestTarget();
        System.err.println("select the malicious node: "+dishonestTargetProvider);
    }
    
    public void selectHonestTarget(){
        while(!listofServiceProviders.get(honestTargetProvider).state){
            honestTargetProvider++;
        }
    }
    
    public void selectDishonestTarget(){
        while(listofServiceProviders.get(dishonestTargetProvider).state){
            dishonestTargetProvider++;
        }
    }
    
    public void initiliaseListOfProviders(){
        listofServiceProviders = new LinkedList<>();
        for(IoTDevice a :iotDevice.getListIoTDevices()){
            if(a.getType()==TypeDevice.bothSPandSR ||a.getType()==TypeDevice.serviceProvider){
                listofServiceProviders.add(a);
            }
        }
    }
    
    public LinkedList<IoTDevice> selectServiceProviderByService(int idService){
        if(listofServiceProviders==null){
            return null;
        }
        else{
            LinkedList<IoTDevice> serviceProviders = new LinkedList<>();
            for(IoTDevice a : listofServiceProviders){
                if(a.serviceID==idService){
                    serviceProviders.add(a);
                }
            }
            return serviceProviders;
        }
    }
    
    public double evaluateSatisfaction(IoTDevice device){
        double satisfaction;
        Random rand = new Random();
        if(device.state){ // honest service provider
            satisfaction = PublicParameters.minsatisfactionvalue+(1.0-PublicParameters.minsatisfactionvalue)*rand.nextDouble();            
        }
        else{ // malicious service provider
            satisfaction = (1.0-PublicParameters.minsatisfactionvalue)*rand.nextDouble();
        }
        System.out.println(iotDevice+" satisfaction="+satisfaction);
        return satisfaction;
    }
  
    public Trustworthiness requestRecomendation(IoTDevice device, int serviceID){
        Trustworthiness trust = new Trustworthiness(1.0, serviceID, device.getIdentifier());
        return trust;
    }
    
    public Trustworthiness evaluteTrustOf(IoTDevice device, int serviceID, double recommendation){
        double newTrustValue=0.5;
        Random rand = new Random();
        if(iotDevice.identifier==device.identifier){
            if(iotDevice.attacks.contains(TypeAttack.selfPromoting)){
                System.out.println(iotDevice+" >>> self promoting attack");
                newTrustValue = PublicParameters.minsatisfactionvalue+(1.0-PublicParameters.minsatisfactionvalue)*rand.nextDouble();
            }
        }
        else if(iotDevice.attacks.contains(TypeAttack.badMouthing) && device.state)
        {
            System.out.println(iotDevice+" >>> Bad Mouthing attack");
            newTrustValue = (1.0-PublicParameters.minsatisfactionvalue)*rand.nextDouble();
        }
        else if(iotDevice.attacks.contains(TypeAttack.ballotStuffing) && !device.state){
            System.out.println(iotDevice+" >>> Ballot Stuffing attack");
            newTrustValue = PublicParameters.minsatisfactionvalue+(1.0-PublicParameters.minsatisfactionvalue)*rand.nextDouble();
        }
        else if(iotDevice.attacks.contains(TypeAttack.OnOff)){ // change its state from malicious to honest and vice versa.
            System.out.println(iotDevice+" >>> On-Off attack");
        }
        else{// no attaque ;p
            System.out.println(iotDevice+" >>> There is no attack against this device !!");
            double directObs = (iotDevice.satisfactionCumul.get(device.identifier))/
                         (iotDevice.satisfactionCumul.get(device.identifier)+iotDevice.dissatisfactionCumul.get(device.identifier));
            System.out.println(iotDevice+": DirectObs = "+directObs);
            System.out.println(iotDevice+": old-trust = "+iotDevice.listOfTrustworthiness.get(device.getIdentifier()).trust);
            newTrustValue = PublicParameters.alpha*iotDevice.listOfTrustworthiness.get(device.getIdentifier()).trust
                                    +PublicParameters.gamma*recommendation+
                                    PublicParameters.beta*directObs;
            Trustworthiness trust = new Trustworthiness(newTrustValue, serviceID, device.getIdentifier());
            iotDevice.listOfTrustworthiness.set(device.getIdentifier(), trust);
        }
        
        Trustworthiness trust = new Trustworthiness(newTrustValue, serviceID, device.getIdentifier());
        return trust;
    }
    
    public IoTDevice choiceBestSP(int idServiceReq){
      LinkedList<IoTDevice> sp = selectServiceProviderByService(idServiceReq);
      if(sp == null || sp.isEmpty()){
          System.err.println(new Date()+ " "+iotDevice+": The is no available service provider for the service "+sp);
          return null;
      }
      IoTDevice bestSP = sp.get(0);
      double maxTrust = evaluteTrustOf(bestSP, idServiceReq,0.5).trust;
      for(IoTDevice a : sp){
          double t = evaluteTrustOf(a, idServiceReq, 0.5).trust;
          if(t > maxTrust){
              maxTrust = t;
              bestSP = a;
          }
      }
      return bestSP;
    }
    
    @Override 
    public void run() {
      System.out.println(new Date()+ " "+iotDevice+": Execution of Malicious Trust computation");
      Random rand = new Random();   
      int idServiceReq = Math.abs(rand.nextInt()) % PublicParameters.nbServices;
      System.out.println(new Date()+ " "+iotDevice+": Request the service "+idServiceReq);
      /*LinkedList<IoTDevice> sp = selectServiceProviderByService(idServiceReq);
      if(sp == null || sp.isEmpty()){
          System.err.println(new Date()+ " "+iotDevice+": The is no available service provider for the service "+sp);
          return;
      }*/
      
      LinkedList<IoTDevice> sp = iotDevice.getListIoTDevices();
      //int serviceProvider = Math.abs(rand.nextInt())%(int)(sp.size()*PublicParameters.rateServiceProviders);
      int serviceProvider = honestTargetProvider;
      //int serviceProvider = dishonestTargetProvider;
      System.out.println(new Date()+ " "+iotDevice+": Service done with the service provider "+serviceProvider);
        try {
            Socket client = new Socket("localhost",PublicParameters.portFogNode);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            Transaction tx = new Transaction(null,iotDevice.identifier , serviceProvider, 0.0);
            tx.creteria="creteria1 creteria2 creteria3";
            out.writeObject(new Message("INFO", tx));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            double recommendation = in.readDouble();
            client.close();
            System.out.println(iotDevice+" fog recommendation "+recommendation);
            System.out.println(iotDevice+" done the service "+idServiceReq+" from the service provider "+serviceProvider);
            double satisfaction = evaluateSatisfaction(iotDevice.listIoTDevices.get(serviceProvider));
            iotDevice.updateSatisfactionCummul(satisfaction, serviceProvider);
            Trustworthiness tr = evaluteTrustOf(listofServiceProviders.get(serviceProvider), idServiceReq, recommendation);
            Transaction tx2 = new Transaction(new Date(System.currentTimeMillis()), iotDevice.identifier,listofServiceProviders.get(serviceProvider).getIdentifier(), tr.trust);
            tx2.creteria=tx.creteria;
            
            Socket client2 = new Socket("localhost",PublicParameters.portFogNode);
            out = new ObjectOutputStream(client2.getOutputStream());
            System.out.println(iotDevice+" send trust value "+tr.trust);
            out.writeObject(new Message("ADD", tx2));
            out.flush();
            client2.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(MaliciousTrustComputation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MaliciousTrustComputation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
