/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author djamelKDE
 */
public class Statistics {
    public static void main(String[] args) throws InterruptedException, IOException {
        File file1 = new File("devices-states.csv");
        File fileResult = new File("stats.csv");
        FileInputStream fin1 = new FileInputStream(file1);
        byte[] b = new byte[fin1.available()];
        fin1.read(b);
        String s = new String(b);
        System.out.println(s);
        String str;
        BufferedReader bufferreader = new BufferedReader(new FileReader("statistic-trust.csv"));
        double meanHonest=0.0;
        double meanMalicious=0.0;
        int cpt1=0; int cpt2=0;
        String format = "#timestamp meanTrustHonest meanTrustMalicious\n";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileResult));
        writer.append(format);
        writer.append("1 0.5 0.5\n");
        int prectsmp=2;
        String []tab=s.split(" ");
        while ((str = bufferreader.readLine())!= null) {
            System.out.println(str);
            if(str.split(",")[0].compareTo("#timeStamp")==0){
                continue;
            }
            int tstmp = Integer.parseInt(str.split(",")[0]);
            int provider = Integer.parseInt(str.split(",")[1]);
            double trust = Double.parseDouble(str.split(",")[2]);
            System.out.println("prestamp="+prectsmp+", timestamp="+tstmp);
            if(tstmp==prectsmp){
                if(Integer.parseInt(tab[provider])==0){
                    
                    meanMalicious+=trust;
                    cpt2++;
                    System.out.println("meanMalicious="+meanMalicious+", cpt="+cpt2);
                    
                }
                else{
                    meanHonest+=trust;
                    cpt1++;
                    System.out.println("meanHonest="+meanHonest+", cpt="+cpt1);
                }
            }
            else{
                    if(cpt1==0) {cpt1=1;meanHonest=-1.0;}
                if(cpt2==0) {cpt2=1;meanMalicious=-1.0;}
                writer.append(prectsmp+" "+meanHonest/cpt1+" "+meanMalicious/cpt2+"\n");
                prectsmp++;
                cpt1=0; cpt2=0;meanHonest=0.0;meanMalicious=0.0;
                // save the first value related to the new timestamp.
                if(Integer.parseInt(tab[provider])==0){
                    
                        meanMalicious+=trust;
                        cpt2++;
                        System.out.println("meanMalicious="+meanMalicious+", cpt="+cpt2);
                    
                }
                else{
                    meanHonest+=trust;
                    cpt1++;
                    System.out.println("meanHonest="+meanHonest+", cpt="+cpt1);
                }
            }
        }
        if(cpt1==0) {cpt1=1;meanHonest=-1.0;}
        if(cpt2==0) {cpt2=1;meanMalicious=-1.0;}
        writer.append(prectsmp+" "+meanHonest/cpt1+" "+meanMalicious/cpt2+"\n");
        writer.close();
    }
}

