/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cripto;

import fieldoperations.Fp2Element;
import java.math.BigInteger;

/**
 *
 * @author user
 */
public class KeyPair {
   BigInteger sk;
   Fp2Element[] pk;

    public KeyPair(BigInteger sk, Fp2Element[] pk) {
        this.sk = sk;
        this.pk = pk;
    }

    public BigInteger getSk() {
        return sk;
    }

    public Fp2Element[] getPk() {
        return pk;
    }
   
   
   
}
