/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cripto;

import fieldoperations.Fp2Element;
import java.math.BigInteger;

import org.bouncycastle.crypto.digests.SHAKEDigest;

/**
 *
 * @author user
 */
public class PublicKeyScheme {
    
    Parameters parameters;
    SHAKEDigest shake256;
    
    public PublicKeyScheme (Parameters parameters){
        this.parameters = parameters;
        shake256 = new SHAKEDigest(256);
    }
    
    public KeyPair KeyGeneration() {
        SIDH sidhb = new SIDH(parameters);
        Fp2Element[] pk = sidhb.getPublicKey3();
        BigInteger sk = sidhb.sk;
        return new KeyPair(sk, pk);
    }
   
   
  
}
