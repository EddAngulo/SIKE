/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cripto;

import ecoperations.EC;
import ecoperations.ECPoint;
import ecoperations.IO;
import fieldoperations.Fp2Element;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class SIDH {

    Parameters parameters;
    BigInteger sk;

    public SIDH(Parameters parameters) {
        this.parameters = parameters;
    }

    public Fp2Element[] getPublicKey3() {
        SecureRandom random = new SecureRandom();

        int N3 = (int) Math.ceil(217 / 8);
        byte sk3b[] = new byte[N3];
        BigInteger b3 = new BigInteger("3");
        BigInteger p3 = b3.pow(parameters.getE3());
        random.nextBytes(sk3b);
        sk = (new BigInteger(sk3b)).mod(p3);

        while (sk.compareTo(BigInteger.ZERO) == 0) {
            random.nextBytes(sk3b);
            sk = (new BigInteger(sk3b)).mod(p3);
        }

        return isogen3(sk);
    }

    public Fp2Element[] getPublicKey2() {
        SecureRandom random = new SecureRandom();
        int N2 = (int) Math.ceil(parameters.getE2() / 8);
        BigInteger b2 = new BigInteger("2");
        BigInteger p2 = b2.pow(parameters.getE2());

        byte sk2b[] = new byte[N2];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(sk2b);
        sk = (new BigInteger(sk2b)).mod(p2);

        while (sk.compareTo(BigInteger.ZERO) == 0) {
            random.nextBytes(sk2b);
            sk = (new BigInteger(sk2b)).mod(p2);
        }
        return isogen2(sk);

    }

    public Fp2Element commonSecret2(Fp2Element[] pk3) {

        return isoex2(sk, pk3[0], pk3[1], pk3[2]);

    }
    
      public Fp2Element commonSecret3(Fp2Element[] pk2) {

        return isoex3(sk, pk2[0], pk2[1], pk2[2]);

    }

    public Fp2Element[] isogen2(BigInteger sk2) {
        //System.out.println("sk2"+sk2);
        ECPoint S = parameters.curve.double_and_add(sk2, parameters.Q2);
        S = parameters.curve.xADD(parameters.P2, S);
        List<ECPoint> list = new ArrayList();
        list.add(parameters.P3);
        list.add(parameters.Q3);
        IO out = parameters.curve.iso_2_e(S, parameters.e2, list);
        Fp2Element[] output = new Fp2Element[3];
        ECPoint P3o = out.getList().get(0);
        output[0] = P3o.getX();
        ECPoint Q3o = out.getList().get(1);
        output[1] = Q3o.getX();
        Fp2Element xr3 = EC.get_xR(out.getEC(), P3o, Q3o);
        output[2] = xr3;

        return output;
    }

    public Fp2Element[] isogen3(BigInteger sk3) {
        //System.out.println(sk3);
        ECPoint S = parameters.curve.double_and_add(sk3, parameters.Q3);

        S = parameters.curve.xADD(parameters.P3, S);
        List<ECPoint> list = new ArrayList();
        list.add(parameters.P2);
        list.add(parameters.Q2);

        IO out = parameters.curve.iso_3_e(S, parameters.e3, list);
        Fp2Element[] output = new Fp2Element[3];
        ECPoint P2o = out.getList().get(0);
        output[0] = P2o.getX();
        ECPoint Q2o = out.getList().get(1);
        output[1] = Q2o.getX();
        Fp2Element xr2 = EC.get_xR(out.getEC(), P2o, Q2o);
        output[2] = xr2;
        return output;

    }

    public Fp2Element isoex2(BigInteger sk2, Fp2Element P2x, Fp2Element Q2x, Fp2Element R2x) {
        Fp2Element[] out = EC.get_yP_yQ_A_B(parameters.field, P2x, Q2x, R2x);

        ECPoint lP2 = new ECPoint(P2x, out[0]);
        ECPoint lQ2 = new ECPoint(Q2x, out[1]);

        EC ec = new EC(out[2], out[3], parameters.field);

        ECPoint S = ec.double_and_add(sk2, lQ2);

        S = ec.xADD(S, lP2);

        IO o = ec.iso_2_e(S, parameters.e2, null);

        return o.getEC().j_inv();
    }

    public Fp2Element isoex3(BigInteger sk3, Fp2Element P3x, Fp2Element Q3x, Fp2Element R3x) {
        Fp2Element[] out = EC.get_yP_yQ_A_B(parameters.field, P3x, Q3x, R3x);

        ECPoint lP3 = new ECPoint(P3x, out[0]);
        ECPoint lQ3 = new ECPoint(Q3x, out[1]);

        EC ec = new EC(out[2], out[3], parameters.field);

        ECPoint S = ec.double_and_add(sk3, lQ3);

        S = ec.xADD(S, lP3);

        IO o = ec.iso_3_e(S, parameters.e3, null);

        return o.getEC().j_inv();
    }

    public static void main(String[] args) {
        SIKEp434 param = new SIKEp434();

        //Sending pk3 to Alice. 
         SIDH sidhb= new SIDH(param);
         Fp2Element[] Bob_PK=sidhb.getPublicKey3();
         
        
        //ALice
        // Receving pk3 from Bob.
         SIDH sidha = new SIDH(param);
         Fp2Element[] Alice_PK=sidha.getPublicKey2();
         Fp2Element jalice = sidha.commonSecret2(Bob_PK);
         System.out.println("j:=" + jalice);
        //Sending pk2 to Bob.

        //Bob
        // Receving pk2 from Bob.
        Fp2Element jbob = sidhb.commonSecret3(Alice_PK);

        System.out.println("j:=" + jbob);

    }

}
