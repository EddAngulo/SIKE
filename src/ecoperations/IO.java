/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecoperations;

import java.util.List;

/**
 *
 * @author user
 */
public class IO {
    
    List<ECPoint> list;
    EC EC ;

    public IO( EC EC, List<ECPoint> list) {
        this.list = list;
        this.EC = EC;
    }

    public List<ECPoint> getList() {
        return list;
    }

    public void setList(List<ECPoint> list) {
        this.list = list;
    }

    public EC getEC() {
        return EC;
    }

    public void setEC(EC EC) {
        this.EC = EC;
    }
    
    
    
    
}
