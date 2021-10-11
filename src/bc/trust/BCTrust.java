/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

    
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author djamelKDE
 */
public class BCTrust {
    public static LinkedList<IoTDevice> listIoTDevices = new LinkedList<>();
    public FogNode fogNode;
    int nb =0;
    
    public void initialiseListDevice() throws IOException{
        String format;
        Random rand = new Random();
        IoTDevice iotd;
        File file = new File("devices-states.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for(int i=0; i<PublicParameters.nbDevices; i++){
            int idService = Math.abs(rand.nextInt()) % PublicParameters.nbServices;
            if(i<PublicParameters.nbDevices*PublicParameters.rateServiceProviders){
                double r = rand.nextDouble();  
                if(r<PublicParameters.rateMaliciousNodes){
                    iotd = new MaliciousNode(i, false, 0, TypeDevice.serviceProvider, idService);
                    LinkedList<TypeAttack> attacks = new LinkedList();
                    attacks.add(TypeAttack.badMouthing);
                    attacks.add(TypeAttack.ballotStuffing);
                    ((MaliciousNode)iotd).setAttacks(attacks);
                    nb++;
                    listIoTDevices.add(iotd);
                    System.out.println("Malicious");
                    format = 0+" ";
                    writer.append(format);
                }
                else{
                    listIoTDevices.add(new HonestDevice(i, true, 0, TypeDevice.serviceProvider, idService));
                    format = format = 1+" ";
                    writer.append(format);
                }
             }
            else{
                if(nb < PublicParameters.nbDevices*PublicParameters.rateMaliciousNodes){
                    iotd = new MaliciousNode(i, false, 0, TypeDevice.serviceRequester, idService);
                    LinkedList<TypeAttack> attacks = new LinkedList();
                    attacks.add(TypeAttack.badMouthing);
                    attacks.add(TypeAttack.ballotStuffing);
                    ((MaliciousNode)iotd).setAttacks(attacks);
                    nb++;
                    listIoTDevices.add(iotd);
                    format = format = 0+" ";
                    writer.append(format);
                }
                else{
                    listIoTDevices.add(new HonestDevice(i, true, 0, TypeDevice.serviceRequester, idService));
                    format = format = 1+" ";
                    writer.append(format);
                }
            }    
        }
        writer.close();
        System.out.println("nb="+nb);
        for(int i=0; i<PublicParameters.nbDevices; i++){
            listIoTDevices.get(i).setListIoTDevices(listIoTDevices);
            listIoTDevices.get(i).initialiseSatisfactions();
        }
    }
    
    public void startSimulation() throws InterruptedException{
        //FogNode.main(null);
        Thread.sleep(1000);
        for(IoTDevice iotD : listIoTDevices){
            iotD.initialiseTrustworthiness();
            iotD.trustAssessementBehavior();
            //Thread.sleep(1000);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        // TODO code application logic here
        BCTrust bcTrust = new BCTrust();
        bcTrust.initialiseListDevice();
        bcTrust.startSimulation();
    }
}
