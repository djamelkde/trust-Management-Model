/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.util.LinkedList;
import java.util.Timer;

/**
 *
 * @author djamelKDE
 */
public class MaliciousNode extends IoTDevice{
    public Thread threadTrustComputation;
    public LinkedList<TypeAttack> attacks = new LinkedList(); // the attacks to simulate.
    public MaliciousNode(int identifier, boolean state, int fogHomeNode, TypeDevice type, int serviceID) {
        super(identifier, state, fogHomeNode, type, serviceID);
    }

    @Override
    public void trustAssessementBehavior() {
        final MaliciousNode iotDevice = this;
        threadTrustComputation = new Thread() {
           @Override
           public void run() {
               System.out.println("device "+this+"..... ");
                Timer timer;
                timer = new Timer();
                timer.schedule(new MaliciousTrustComputation(iotDevice), 1000, 5000);
           }
        };
        threadTrustComputation.start();
    }

    public void setAttacks(LinkedList<TypeAttack> attacks) {
        this.attacks = attacks;
    }
    
    // simulate a trust-related malicious attack
    public void simulateAttack(){
        
    }
   
}
