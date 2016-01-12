/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;

/**
 *
 * @author MHL
 */
public class NetworkConnection extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1593910932474472258L;
	//Model
    private boolean value1 = true;
    private boolean value2 = false;
    private boolean value3 = false;
    private boolean value4 = false;
    private boolean value5 = false;
    private boolean value6 = false;
    
    //
    
    /*
gsm 	GSM/CSD	(Up: 14.4, down: 14.4)
edge 	EDGE/EGPRS 	(Up: 118.4, down: 236.8)
umts 	UMTS/3G	(Up: 128.0, down: 1920.0)
hsdpa 	HSDPA	(Up: 348.0, down: 14400.0)
full
    */
    
    
    //TODO Rechtsbuendig
    public NetworkConnection(){
        super(Constants.NETWORK);
    }

    
    private String getNetworkSpeed(){
        if(value1){
            return "0 0";
        }else if(value2){
            return "GSM";
        }else if(value3){
            return "EDGE";
        }else if(value4){
            return "UMTS";
        }else if(value5){
            return "HSDPA";
        }else{
            return "FULL";
        }
    }
    
}
