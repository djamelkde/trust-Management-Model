/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author djamelKDE
 */
public class FogNode extends Thread{
    Hashtable<Integer, Hashtable<Integer,Transaction>> blockchain;
    Hashtable<Integer, Hashtable<Integer,Transaction>> previousBlockchain;
    Hashtable<Integer, Hashtable<Integer,Transaction>> previousRecommendations;
    public FogNode() {
        initialiseBlockchain();
    }
    
    public void initialiseBlockchain(){
        blockchain = new Hashtable<>();
        previousBlockchain = new Hashtable<>();
        previousRecommendations= new Hashtable<>();
        for(int i=0; i<PublicParameters.nbDevices; i++){
            blockchain.put(i, new Hashtable<Integer,Transaction>());
            previousBlockchain.put(i, new Hashtable<Integer,Transaction>());
            previousRecommendations.put(i, new Hashtable<Integer,Transaction>());
        }
    }
    
    public void addTransaction(int trustee, Transaction tx){
        if(blockchain.get(trustee).containsKey(tx.getIdTrustor()))
        previousBlockchain.get(trustee).put(tx.getIdTrustor(), blockchain.get(trustee).get(tx.getIdTrustor()));
     /*   else{
            Transaction tx2 = new Transaction(tx.timestamp, tx.idTrustee,tx.idTrustor, 0.5);
            previousBlockchain.get(trustee).put(tx.getIdTrustor(), tx2);
        }*/
        blockchain.get(trustee).put(tx.getIdTrustor(),tx);
    }
    
    public double computeRecomendation(int trustee, int trustor, String creterias){
        double recomendation1 = 0.0;
        double recomendation2 = 0.0;
        boolean anomaly=false;
        int i=0;
        if(PublicParameters.TreatAnomaly){
            Enumeration<Integer> keys2 =  blockchain.get(trustee).keys();
            double min=1.0, max = 0.0;
            
            while(keys2.hasMoreElements()){
                int index = keys2.nextElement();
                i++;
                Transaction tx =blockchain.get(trustee).get(index);
                long deltat = System.currentTimeMillis()- tx.timestamp.getTime();
                if(deltat>PublicParameters.deltaRead){
                    continue;
                }
                if(max<tx.trsutvalue) max=tx.trsutvalue;
                if(min>tx.trsutvalue) min=tx.trsutvalue;
            }
            anomaly = (max-min>PublicParameters.anomalyThreshold);
        }
        
        if(anomaly){
            System.err.println("There is anomaly !!");
            System.err.println("keys size:"+i);
        }
        
        Enumeration<Integer> keys =  blockchain.get(trustee).keys();
        int nbRecomenders = blockchain.get(trustee).size();
        if(nbRecomenders ==0){
            return 0.5;
        }
        double rTotal = 0.0;
        double creTotal = 0.0;
        int nbsp=0;
     /*  if(trustee==0){
           System.out.println("BCprevi="+previousBlockchain.get(trustee).size());
           System.out.println("BCcurre="+blockchain.get(trustee).size());
       }*/
        while(keys.hasMoreElements()){
            int index = keys.nextElement();
            Transaction tx =blockchain.get(trustee).get(index);
            long deltat = System.currentTimeMillis()- tx.timestamp.getTime();
            if(deltat>PublicParameters.deltaRead){
                nbRecomenders--;
                continue;
            }
            
            if(anomaly && previousBlockchain.get(trustee).containsKey(index)){
                nbRecomenders--;
        //        System.err.println("previous blockchain:"+
          //              previousBlockchain.get(trustee).get(index)+"\n current blockchain:"+tx);
                continue;
            }
            if(blockchain.containsKey(index) && !blockchain.get(index).isEmpty()){ // the device which has the identifier index is a service provider.
                double t=0.5;
                if(blockchain.get(index).containsKey(trustor)){
                    t=blockchain.get(index).get(trustor).trsutvalue;
                }
                rTotal+=t;
                recomendation1+=t*blockchain.get(trustee).get(index).trsutvalue;
                nbsp++;
            }
            else{ // not a service provider
               creTotal +=tx.intersectionCreterias(creterias);
                //System.out.println("creTotal pour "+index+" ="+creTotal);
               recomendation2+=tx.intersectionCreterias(creterias)*blockchain.get(trustee).get(index).trsutvalue;
               //System.out.println("recommendation2 pour "+index+"="+recomendation2);
            }
        }
        if(creTotal==0) creTotal=1;
        if(rTotal==0) rTotal=1;
        if(nbRecomenders==0){
            System.err.println("nb recomenders=0");
            /*if(previousBlockchain.get(trustee).containsKey(trustor)){ // if there is a previous recommendation
                double previowRecomendation = previousBlockchain.get(trustee).get(trustor).trsutvalue;
                double newRecomendation = previowRecomendation*(1-PublicParameters.penality);
                previousRecommendations.get(trustee).put(trustor, new Transaction(new Date(System.currentTimeMillis()), trustor, trustee, newRecomendation));
                return newRecomendation;
            }*/
            return 0.5;
        }
      //  if(trustee==0)
      //  System.out.println("nbsp="+nbsp+", nbRecomenders="+nbRecomenders+", recomendation1="+recomendation1+", recomendation2="+recomendation2);
        double recomendation = (nbsp*(recomendation1/rTotal)+(nbRecomenders-nbsp)*(recomendation2/creTotal))/nbRecomenders;
        previousRecommendations.get(trustee).put(trustor, new Transaction(new Date(System.currentTimeMillis()), trustor, trustee, recomendation));
        return recomendation;
    }
        
    public ArrayList<Transaction> selectTxByCreteria(int creteria){
        return null;
    }
   
    @Override
    public void run() {
        System.out.println("fog node start.........");
        Timer timer;
        timer = new Timer();
        timer.schedule(new ExportTrustworthiness(blockchain), 1000, 20000);
        try {
            //new ServerSocket
            ServerSocket server= new ServerSocket(PublicParameters.portFogNode, PublicParameters.nbDevices);
            while(true){
                Socket s = server.accept();
                ObjectInputStream obsin = new ObjectInputStream(s.getInputStream());
                Message msg = (Message)obsin.readObject();
                //System.out.println("Fog Node : received message: "+msg);
                if(msg.type.compareTo("ADD")==0){
                    if(msg.transaction.idTrustor!=msg.transaction.idTrustee)
                        addTransaction(msg.transaction.getIdTrustee(), msg.transaction);
                }
                else{ // INFO
                    double value = computeRecomendation(msg.transaction.getIdTrustee(), msg.transaction.getIdTrustor(), msg.transaction.creteria);
                    if(msg.transaction.getIdTrustee()==0)
                    System.out.println("Fog node send the recommendation: "+value+" for the node 0");
                    ObjectOutputStream obsout = new ObjectOutputStream(s.getOutputStream());
                    obsout.writeDouble(value);
                    obsout.flush();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FogNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FogNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public static void main(String[] args){
        // TODO code application logic here
        FogNode fogNode = new FogNode();
        fogNode.start();
    }
    
}
