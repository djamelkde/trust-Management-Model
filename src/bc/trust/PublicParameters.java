/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

/**
 *
 * @author djamelKDE
 */
enum AttackType{NoATTACK, BadMouthing, BallotStuffing, SelfPromoting};
public class PublicParameters {
    
    static public int delTaTx; // periodic timer to send transactions.
    static public long deltaRead=50000; // period of time to read transactions from the blockchain.
    static public String ipFogNode = "127.0.0.1"; // the ip adresse of the home fog node.
    static public int portFogNode = 7001;
    static public int nbDevices = 100;
    static public double rateMaliciousNodes = 0.25;
    static public double rateServiceProviders = 0.2;
    static public double rateserviceRequesters = 1.0;
    static public int nbServices = 1;
    static public double minsatisfactionvalue = 0.90;
    static public int nbCreteria = 5;
    static public double alpha = 1.0/3;
    static public double beta = 1.0/3;
    static public double gamma = 1.0/3;
    static public int t=-1;
    static public double anomalyThreshold=0.3;
    static public boolean TreatAnomaly=true;
    static public double penality=0.05;
    static public AttackType listAttacks[] = {AttackType.NoATTACK};
}
