/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;
import java.util.Timer;

/**
 *
 * @author djamelKDE
 */
public class HonestDevice extends IoTDevice {
    public Thread threadTrustComputation;
   // public Thread threadTrustDessimination;
    public HonestDevice(int identifier, boolean state, int fogHomeNode, TypeDevice type, int serviceID) {
        super(identifier, state, fogHomeNode, type, serviceID);
    }
    
    @Override
    public void trustAssessementBehavior() {
        final IoTDevice iotDevice = this;
        threadTrustComputation = new Thread() {
           @Override
           public void run() {
               System.out.println("device "+this+"..... ");
                Timer timer;
                timer = new Timer();
                timer.schedule(new TrustComputation(iotDevice), 1000, 5000);
           }
        };
        
        /*threadTrustDessimination = new Thread() {
           @Override
           public void run() {
                Timer timer;
                timer = new Timer();
                timer.schedule(new TrustDissemination(iotDevice), 1000, 10000);
           }
        };*/
        threadTrustComputation.start();
        //threadTrustDessimination.start();
    }
  
}
