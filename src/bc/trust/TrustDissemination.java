/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author djamelKDE
 */
public class TrustDissemination extends TimerTask {
    IoTDevice iotDevice;

    public TrustDissemination(IoTDevice iotDevice) {
        this.iotDevice = iotDevice;
    }
    
    @Override
    public void run() {
      System.out.println(new Date() + " "+iotDevice+ " Execution of trust dessimination");
    }
}
