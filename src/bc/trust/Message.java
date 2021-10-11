/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.trust;

import java.io.Serializable;

/**
 *
 * @author djamelKDE
 */
public class Message implements Serializable{

    public Message(String type, Transaction transaction) {
        this.type = type;
        this.transaction = transaction;
    }
    public String type;
    public Transaction transaction;

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", transaction=" + transaction + '}';
    }
}
