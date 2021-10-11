/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author djamelKDE
 */
public class ExportTrustworthiness extends TimerTask{
    Hashtable<Integer, Hashtable<Integer,Transaction>> blockchain;
    public static int cpt = 1;
    File file,file2,file3;
    public String[] statDevices;
    public int cible=0;
    public ExportTrustworthiness(Hashtable<Integer, Hashtable<Integer, Transaction>> blockchain) {
        System.out.println("trustworthiness");
        this.blockchain = blockchain;
        file = new File("statistic-trust.csv");
        file2 = new File("statistic-trust-all.csv");
        file3 = new File("statistic-target.csv");
        try {
            file.createNewFile();
            //FileOutputStream f=new FileOutputStream(file,true);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            //DataOutputStream dos = new DataOutputStream(f);
            String format = "#timeStamp, IdProvider, state, TrustAverage\n";
            writer.append(format);
            writer.close();
            
            file2.createNewFile();
            //FileOutputStream f=new FileOutputStream(file,true);
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2));
            //DataOutputStream dos = new DataOutputStream(f);
            String format2 = "#timeStamp, IdProvider, state, TrustAverage\n";
            writer2.append(format2);
            writer2.close();
            
            BufferedWriter writer3;
            String format3;
            file3.createNewFile();
            System.err.println("create target file");
            writer3 = new BufferedWriter(new FileWriter(file3));
            //DataOutputStream dos = new DataOutputStream(f);
            format3 = "#timeStamp, IdProvider, state, TrustAverage\n";
            writer3.append(format3);
            writer3.close();
            
            
            File file1 = new File("devices-states.csv");
            FileInputStream fin1 = new FileInputStream(file1);
            byte[] b = new byte[fin1.available()];
            fin1.read(b);
            String s = new String(b);
            statDevices=s.split(" ");
            fin1.close();
            while((cible <PublicParameters.nbDevices)&&(Integer.parseInt(statDevices[cible])==0)) cible++;
        } catch (FileNotFoundException ex) {
            System.out.println("exception file does not exist...");
        } catch (IOException ex) {
            System.out.println("exception read...");
        
        }
    }
    @Override
    public void run() {     
        try {
            System.out.println("bc.trust.ExportTrustworthiness.run()");
            File file1 = new File("devices-states.csv");
            FileInputStream fin1 = new FileInputStream(file1);
            byte[] b = new byte[fin1.available()];
            fin1.read(b);
            String s = new String(b);
            statDevices=s.split(" ");
            fin1.close();
            while((cible <PublicParameters.nbDevices)&&(Integer.parseInt(statDevices[cible])==0)) cible++;
            System.out.println("run ..."+cpt);
            
            //FileOutputStream f=new FileOutputStream(file,true);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2,true));
           
            System.out.println("target file exists");
            BufferedWriter    writer3 = new BufferedWriter(new FileWriter(file3,true));
            //DataOutputStream dos = new DataOutputStream(f);
            
            cpt++;
            String format;
            boolean dec=true;
            for(int i=0;i<blockchain.size();i++){
                double trustmean = computeMeanHonests(i);
                double trustmean2 = computeMean(i);
                if(trustmean !=-1.0){
                    dec=false;
                    //int state = BCTrust.listIoTDevices.get(i).state?1:0;
                    format = cpt+","+i+","+trustmean+"\n";
                    System.out.println("Fog node, export BC: "+format);
                    writer.append(format);
                    
                    format = cpt+","+i+","+trustmean2+"\n";
                    System.out.println("Fog node, export BC: "+format);
                    writer2.append(format);
                    
                    if(i==cible){
                        System.err.println("Fog node, export BC honest target: "+format);
                        format = cpt+","+i+","+trustmean+"\n";
                        writer3.append(format);
                        writer3.close();
                    }

                }
            }
            if(dec)cpt--;
            writer.close();
            writer2.close();
         } catch (Exception ex) {
            System.out.println("bc.trust.ExportTrustworthiness.run() ... exception!");
            System.exit(0);
            Logger.getLogger(ExportTrustworthiness.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public double computeMeanHonests(int trustee){
        double mean = 0.0;
        Enumeration<Integer> keys =  blockchain.get(trustee).keys();
        int n=0;
        while(keys.hasMoreElements()){
            int idrecommender = keys.nextElement();
            if(Integer.parseInt(statDevices[idrecommender])==1){// if the recommender is honest
                mean+=blockchain.get(trustee).get(idrecommender).trsutvalue;
                n++;
            }
        }
        if(n==0)
            return !blockchain.get(trustee).isEmpty()? 0.5:-1.0;
        else
            return mean/n;
    }
    
    public double computeMean(int trustee){
        double mean = 0.0;
        Enumeration<Integer> keys =  blockchain.get(trustee).keys();
        while(keys.hasMoreElements()){
            mean+=blockchain.get(trustee).get(keys.nextElement()).trsutvalue;
        }
        return !blockchain.get(trustee).isEmpty()? mean/blockchain.get(trustee).size():-1.0;
    }
}

