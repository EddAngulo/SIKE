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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

/**
 *
 * @author user
 */
public class SIKE {
    
    Parameters parameters;
    SHAKEDigest shake256; //F(m)
    SHA256Digest sha256; //G(m)
    SHA3Digest sha3_256; //H(m)
    BigInteger sk;
    BigInteger s;
    BigInteger K;

    public SIKE(Parameters parameters) {
        this.parameters = parameters;
        this.shake256 = new SHAKEDigest(256);
        this.sha256 = new SHA256Digest();
        this.sha3_256 = new SHA3Digest(256);
    }
    
    public Fp2Element[] gen() {
        SecureRandom random = new SecureRandom();
        int N3 = parameters.getN3();
        byte sk3[] = new byte[N3];
        BigInteger b3 = new BigInteger("3");
        BigInteger p3 = b3.pow(parameters.getE3());
        random.nextBytes(sk3);
        
        sk = (new BigInteger(sk3)).mod(p3);
        while (sk.compareTo(BigInteger.ZERO) == 0) {
            random.nextBytes(sk3);
            sk = (new BigInteger(sk3)).mod(p3);
        }
        
        return isogen3(sk);
    }
    
    public Pair enc(Fp2Element[] pk3, BigInteger m) {
        SecureRandom random = new SecureRandom();
        int N2 = parameters.getN2();
        BigInteger b2 = new BigInteger("2");
        BigInteger p2 = b2.pow(parameters.getE2());
        byte sk2[] = new byte[N2];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(sk2);
        sk = (new BigInteger(sk2)).mod(p2);
        while (sk.compareTo(BigInteger.ZERO) == 0) {
            random.nextBytes(sk2);
            sk = (new BigInteger(sk2)).mod(p2);
        }
        
        Fp2Element[] c0 = isogen2(sk);
        Fp2Element j = isoex2(sk, pk3[0], pk3[1], pk3[2]);
        
        byte[] h_byte = new byte[32];
        byte[] toHash = concat(j.getX0().toByteArray(), j.getX1().toByteArray());
        shake256.update(toHash, 0, toHash.length);
        shake256.doFinal(h_byte, 0);
        BigInteger h = new BigInteger(h_byte);
        BigInteger c1 = h.xor(m);
        
        return new Pair(c0, c1);
    }
    
    public Pair enc(Fp2Element[] pk3, BigInteger m, BigInteger sk) {
        BigInteger b2 = new BigInteger("2");
        BigInteger p2 = b2.pow(parameters.getE2());
        
        BigInteger sk2 = sk.mod(p2); //Preguntar
        
        Fp2Element[] c0 = isogen2(sk2);
        Fp2Element j = isoex2(sk2, pk3[0], pk3[1], pk3[2]);
        
        byte[] h_byte = new byte[32];
        byte[] toHash = concat(j.getX0().toByteArray(), j.getX1().toByteArray());
        shake256.update(toHash, 0, toHash.length);
        shake256.doFinal(h_byte, 0);
        BigInteger h = new BigInteger(h_byte);
        BigInteger c1 = h.xor(m);
        
        return new Pair(c0, c1);
    }
    
    public BigInteger dec(Pair p) {
        Fp2Element j = isoex3(sk, p.c0[0], p.c0[1], p.c0[2]);
        
        byte[] hbyte = new byte[32];
        byte[] toHash = concat(j.getX0().toByteArray(), j.getX1().toByteArray());
        shake256.update(toHash, 0, toHash.length);
        shake256.doFinal(hbyte, 0);
        BigInteger h = new BigInteger(hbyte);
        
        BigInteger m = h.xor(p.c1);
        
        return m;
    }
    
    public Fp2Element[] keyGen() {
        SecureRandom random = new SecureRandom();
        int N3 = parameters.getN3();
        byte sk3[] = new byte[N3];
        BigInteger b3 = new BigInteger("3");
        BigInteger p3 = b3.pow(parameters.getE3());
        random.nextBytes(sk3);
        
        sk = (new BigInteger(sk3)).mod(p3);
        while (sk.compareTo(BigInteger.ZERO) == 0) {
            random.nextBytes(sk3);
            sk = (new BigInteger(sk3)).mod(p3);
        }
        
        SecureRandom rnd = new SecureRandom();
        byte s_byte[] = new byte[32];
        rnd.nextBytes(s_byte);
        s = new BigInteger(s_byte);
        
        return isogen3(sk);
    }
    
    public Pair encaps(Fp2Element[] pk3) {
        SecureRandom rnd = new SecureRandom();
        byte m_byte[] = new byte[32];
        rnd.nextBytes(m_byte);
        BigInteger m = new BigInteger(m_byte);
        
        byte[] one = concat(pk3[0].getX0().toByteArray(), pk3[0].getX1().toByteArray());
        byte[] two = concat(pk3[1].getX0().toByteArray(), pk3[1].getX1().toByteArray());
        byte[] three = concat(pk3[2].getX0().toByteArray(), pk3[2].getX1().toByteArray());
        
        byte[] toHash1 = concat(concat(m_byte, one), concat(two, three));
        byte[] r_byte = new byte[32];
        sha256.update(toHash1, 0, toHash1.length);
        sha256.doFinal(r_byte, 0);
        BigInteger r = new BigInteger(r_byte);
        
        Pair p = enc(pk3, m, r);
        byte[] toHash2 = concat(m_byte, p.toBytes());
        byte[] k_byte = new byte[32];
        sha3_256.update(toHash2, 0, toHash2.length);
        sha3_256.doFinal(k_byte, 0);
        K = new BigInteger(k_byte);
        
        return p;
    }
    
    public BigInteger decaps(Fp2Element[] pk3, Pair p) {
        BigInteger m_prime = dec(p);
        
        byte[] one = concat(pk3[0].getX0().toByteArray(), pk3[0].getX1().toByteArray());
        byte[] two = concat(pk3[1].getX0().toByteArray(), pk3[1].getX1().toByteArray());
        byte[] three = concat(pk3[2].getX0().toByteArray(), pk3[2].getX1().toByteArray());
        
        byte[] toHash1 = concat(concat(m_prime.toByteArray(), one), concat(two, three));
        byte[] r_prime_byte = new byte[32];
        sha256.update(toHash1, 0, toHash1.length);
        sha256.doFinal(r_prime_byte, 0);
        BigInteger r_prime = new BigInteger(r_prime_byte);
        
        Fp2Element[] c0_prime = isogen2(r_prime);
        BigInteger K;
        byte[] toHash2;
        
        if(fp2ArrayEquals(c0_prime, p.c0)) {
            toHash2 = concat(m_prime.toByteArray(), p.toBytes());
        }else {
            toHash2 = concat(s.toByteArray(), p.toBytes());
        }
        byte[] k_byte = new byte[32];
        sha3_256.update(toHash2, 0, toHash2.length);
        sha3_256.doFinal(k_byte, 0);
        K = new BigInteger(k_byte);
        
        return K;
    }
    
    public boolean fp2ArrayEquals(Fp2Element[] e1, Fp2Element[] e2) {
        if(e1.length != e2.length) {
            return false;
        }
        for (int i = 0; i < e1.length; i++) {
            if(!e1[i].equals(e2[i])) {
                return false;
            }
        }
        return true;
    }
    
    public Fp2Element[] isogen2(BigInteger sk2) {
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
    
    public byte[] concat(byte[] vec1, byte[] vec2) {
        byte[] result = new byte[vec1.length + vec2.length];
        System.arraycopy(vec1, 0, result, 0, vec1.length);
        System.arraycopy(vec2, 0, result, vec1.length, vec2.length);  
        return result;
    }
    
    public static void main(String[] args) {
        SIKEp434 param = new SIKEp434();
        SIKE sike_A = new SIKE(param);
        Fp2Element[] pk_A = sike_A.keyGen();
        SIKE sike_B = new SIKE(param);
        Pair p = sike_B.encaps(pk_A);
        System.out.println("K_B = " + sike_B.K);
        BigInteger K_A = sike_A.decaps(pk_A, p);
        System.out.println("K_A = " + K_A);
    }
    
    private class Pair {
        
        Fp2Element[] c0;
        BigInteger c1;
        
        public Pair(Fp2Element[] c0, BigInteger c1) {
            this.c0 = c0;
            this.c1 = c1;
        }
        
        public byte[] toBytes() {
            byte[] one = concat(c0[0].getX0().toByteArray(), c0[0].getX1().toByteArray());
            byte[] two = concat(c0[1].getX0().toByteArray(), c0[1].getX1().toByteArray());
            byte[] three = concat(c0[2].getX0().toByteArray(), c0[2].getX1().toByteArray());
            return concat(concat(one, two), concat(three, c1.toByteArray()));
        }
        
    }
    
}
