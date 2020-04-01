/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cripto;

import ecoperations.EC;
import ecoperations.ECPoint;
import fieldoperations.Fp2Element;
import fieldoperations.Fp2Field;
import java.math.BigInteger;

/**
 *
 * @author user
 */
public abstract class Parameters {

    BigInteger p;
    int e2, e3;

    Fp2Element A ,B;
    ECPoint Q2, P2, Q3, P3;
    Fp2Element xR2, xR3;
    Fp2Field field;
    EC curve;
    
    
    protected void init()
    {A = new Fp2Element(new BigInteger("6"), BigInteger.ZERO);
     B = new Fp2Element(new BigInteger("1"), BigInteger.ZERO);
     field = new Fp2Field(p);
       curve = new EC(A, B, field);
    }
    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public int getE2() {
        return e2;
    }

    public void setE2(int e2) {
        this.e2 = e2;
    }

    public int getE3() {
        return e3;
    }

    public void setE3(int e3) {
        this.e3 = e3;
    }

    public Fp2Element getA() {
        return A;
    }

    public void setA(Fp2Element A) {
        this.A = A;
    }

    public Fp2Element getB() {
        return B;
    }

    public void setB(Fp2Element B) {
        this.B = B;
    }

    public ECPoint getQ2() {
        return Q2;
    }

    public void setQ2(ECPoint Q2) {
        this.Q2 = Q2;
    }

    public ECPoint getP2() {
        return P2;
    }

    public void setP2(ECPoint P2) {
        this.P2 = P2;
    }

    public ECPoint getQ3() {
        return Q3;
    }

    public void setQ3(ECPoint Q3) {
        this.Q3 = Q3;
    }

    public ECPoint getP3() {
        return P3;
    }

    public void setP3(ECPoint P3) {
        this.P3 = P3;
    }

    public Fp2Element getxR2() {
        return xR2;
    }

    public void setxR2(Fp2Element xR2) {
        this.xR2 = xR2;
    }

    public Fp2Element getxR3() {
        return xR3;
    }

    public void setxR3(Fp2Element xR3) {
        this.xR3 = xR3;
    }

    public Fp2Field getField() {
        return field;
    }

    public void setField(Fp2Field field) {
        this.field = field;
    }

    public EC getCurve() {
        return curve;
    }

    public void setCurve(EC curve) {
        this.curve = curve;
    }
    public abstract int getN3();
    
    public abstract int getN2();
    
    

}
