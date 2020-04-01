/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecoperations;

import fieldoperations.Fp2Element;
import fieldoperations.Fp2Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class EC {

    final Fp2Element a;
    final Fp2Element b;
    final Fp2Field field;
  /*
  This class represents the Montgomery curve Ea,b/Fp^2 : by^2 = x^3 + ax^2 + x.
  */
    public EC(Fp2Element a, Fp2Element b, Fp2Field field) {
        this.a = a;
        this.b = b;
        this.field = field;
    }

    public Fp2Element getA() {
        return a;
    }

    public Fp2Element getB() {
        return b;
    }
   /*
    Input:   P=(Xp,Yp)∈ Ea,b
    output: 2P=(X2p,Y2p)∈ Ea,b
    */
    public ECPoint xDBL(ECPoint P) {
        if (P.isIdentity()) {
            return new ECPoint();
        }
         if (this.areEqual(P, negate(P))) {

            return new ECPoint();
        }
        
        Fp2Element t0 = this.field.square(P.x);
        Fp2Element t1 = this.field.add(t0, t0);
        Fp2Element t2 = this.field.getOne();
        t0 = field.add(t0, t1);
        t1 = field.multiply(a, P.x);
        t1 = field.add(t1, t1);
        t0 = field.add(t0, t1);
        t0 = field.add(t0, t2);
        t1 = field.multiply(b, P.y);
        t1 = field.add(t1, t1);
        t1 = field.mInverse(t1);
        t0 = field.multiply(t0, t1);
        t1 = field.square(t0);
        t2 = field.multiply(b, t1);
        t2 = field.substract(t2, a);
        t2 = field.substract(t2, P.x);
        t2 = field.substract(t2, P.x);
        t1 = field.multiply(t0, t1);
        t1 = field.multiply(b, t1);
        t1 = field.add(t1, P.y);
        Fp2Element y = field.add(P.x, P.x);
        y = field.add(y, P.x);
        y = field.add(y, a);
        y = field.multiply(y, t0);
        y = field.substract(y, t1);
        Fp2Element x = t2;

        return new ECPoint(x, y);
    }
   /*
    Input:   P=(Xp,Yp)∈ Ea,b
    output:  2^eP=P+P+P+...+P ∈ Ea,b (2^e additions of P)
    */
    public ECPoint xDBLe(ECPoint P, int e) {

        ECPoint Q = P.clone();
        for (int i = 1; i <= e; i++) {
            Q = this.xDBL(Q);

        }

        return Q;
    }
/*
    Input:   P=(Xp,Yp)∈ Ea,b
    output:  -P=(Xp,-Yp)∈ Ea,b
    */
    public ECPoint negate(ECPoint P) {

        return new ECPoint(P.x, field.aInverse(P.y));
    }

    public boolean areEqual(ECPoint P, ECPoint Q) {
        if (!P.isIdentity() && Q.isIdentity()) {
            return false;
        }

        if (P.isIdentity() && !Q.isIdentity()) {
            return false;
        }

        if (!P.isIdentity() && !Q.isIdentity()) {
            if (!field.areEqual(P.x, Q.x)) {
                return false;
            }

            if (!field.areEqual(P.y, Q.y)) {
                return false;
            }

        }

        return true;
    }
/*
    Input:   P=(Xp,Yp)∈ Ea,b and Q=(Xq,Yq)∈ Ea,b
    output:  P+Q=(X,Y)∈ Ea,b
    */
    public ECPoint xADD(ECPoint P, ECPoint Q) {
        if (P.isIdentity()) {
            return Q.clone();
        }
        if (Q.isIdentity()) {

            return P.clone();
        }

        if (this.areEqual(P, Q)) {

            return this.xDBL(P);
        }

        if (this.areEqual(P, negate(Q))) {

            return new ECPoint();
        }

        Fp2Element t0 = field.substract(Q.y, P.y);//t0 ← yQ − yP
        Fp2Element t1 = field.substract(Q.x, P.x);//t1 ← xQ − xP
        t1 = field.mInverse(t1);//t1 ← t1^−1
        t0 = field.multiply(t0, t1);//t0 ← t0 · t1
        t1 = field.square(t0);//t1 ← t0^2
        Fp2Element t2 = field.add(P.x, P.x);//t2 ← xP + xP
        t2 = field.add(t2, Q.x);//t2 ← t2 + xQ
        t2 = field.add(t2, a);//t2 ← t2 + a
        t2 = field.multiply(t2, t0);//t2 ← t2 · t0
        t0 = field.multiply(t0, t1);// t0 ← t0 · t1
        t0 = field.multiply(b, t0);//t0 ← b · t0

        t0 = field.add(t0, P.y);//t0 ← t0 + yP
        t0 = field.substract(t2, t0);//t0 ← t2 − t0
        t1 = field.multiply(b, t1);//t1 ← b · t1
        t1 = field.substract(t1, a);//t1 ← t1 − a
        t1 = field.substract(t1, P.x);//t1 ← t1 − xP
        Fp2Element x = field.substract(t1, Q.x);//x[P+Q] ← t1 − xQ
        Fp2Element y = t0;//y[P+Q] ← t0

        return new ECPoint(x, y);
    }
   
     /*
    Input:   P=(Xp,Yp)∈ Ea,b
    output: 3P=P+P+P=(X,Y)∈ Ea,b
    */
    public ECPoint xTPL(ECPoint P) {

        ECPoint C = this.xDBL(P);
        C = this.xADD(C, P);
        return C;
    }
    
    /*
    Input:   P=(Xp,Yp)∈ Ea,b
    output:  3^eP=P+P+P+...+P ∈ Ea,b (3^e additions of P)
    */
    public ECPoint xTPLe(ECPoint P, int e) {

        ECPoint Q = P.clone();
        for (int i = 1; i <= e; i++) {
            Q = this.xTPL(Q);

        }

        return Q;
    }
   /*
    Input: m = (ml−1, . . . , m0) ∈ Z, P = (x, y)∈ Ea,b 
     Output: mP=P+P+P+...+P ∈ Ea,b (m additions of P)
    */
    public ECPoint double_and_add(BigInteger m, ECPoint P) {

        ECPoint Q = new ECPoint(field.getZero(), field.getZero());

        for (int i = m.bitLength() - 1; i >= 0; i--) {
            Q = this.xDBL(Q);
            if (m.testBit(i)) {
                Q = this.xADD(Q, P);
            }

        }

        return Q;
    }
    
    /*
    Input:   P = (Xp,Yp), Q = (Xq, Yq) ∈ Ea,b ; s = (sl−1, . . . , s0) ∈ Z
    Output:  P + [s]Q ∈ Ea,b
    */
    public ECPoint addPointMult(BigInteger s, ECPoint P, ECPoint Q) {
        ECPoint R1 = P;
        ECPoint R0 = Q;
        ECPoint R2 = xADD(Q, negate(P));
        for (int i = 0; i < s.bitLength(); i++) {
            R0 = xDBL(R0);
            if (s.testBit(i)) {
                R1 = xADD(R0, negate(R2));
            }else {
                R2 = xADD(R0, negate(R1));
            }
        }
        return R1;
    }
    

    /*
    Computes the j-invariant of the elliptic curve Ea,b given by J(Ea,b)=2^8(a^2-3)^3/(A^2-4)
   
    */
    public Fp2Element j_inv() {
        Fp2Element t0 = field.square(a);//t0 ← a^2

        Fp2Element j = new Fp2Element(new BigInteger("3"), BigInteger.ZERO);//j ← 3
        j = field.substract(t0, j);//j ← t0 − j
        Fp2Element t1 = field.square(j);//t1 ← j^2
        j = field.multiply(j, t1);//j ← j · t1
        j = field.add(j, j);//j ← j + j
        j = field.add(j, j);//j ← j + j
        j = field.add(j, j);//j ← j + j
        j = field.add(j, j);//j ← j + j
        j = field.add(j, j);//j ← j + j
        j = field.add(j, j);//j ← j + j
        j = field.add(j, j);//j ← j + j
        j = field.add(j, j);//j ← j + j
        t1 = new Fp2Element(new BigInteger("4"), BigInteger.ZERO);//t1 ← 4
        t0 = field.substract(t0, t1);//t0 ← t0 − t1
        t0 = field.mInverse(t0);//t0 ← t0^{−1}
        j = field.multiply(j, t0);//j ← j · t0
        
        return j;

    }
    

    /*
     input:  P2 such that it has exact order 2 on Ea,b. 
     output: outputs Ea',b'= Ea,b/<P2>
     **/
    public EC curve_2_iso(ECPoint P2) {
        Fp2Element t1 = field.square(P2.x);//t1 ← xP2^2
        t1 = field.add(t1, t1);//t1 ← t1 + t1
        t1 = field.substract(field.getOne(), t1);//t1 ← 1 − t1
        Fp2Element an = field.add(t1, t1);//a' ← t1 + t1
        Fp2Element bn = field.multiply(P2.x, b);//b' ← xP2· b

        return new EC(an, bn, field);
    }
    /*
    input: Q and P2, where Q ∈ Ea,b, and P2 has exact order 2 on Ea,b
    output: Q' ∈ Ea',b', where Ea',b' is the curve 2-isogenous to Ea,b output from
    curve_2_iso
    */
    public ECPoint eval_2_iso(ECPoint Q, ECPoint P2) {
        Fp2Element t1 = field.multiply(Q.x, P2.x);//t1 ← xQ · xP2
        Fp2Element t2 = field.multiply(Q.x, t1);//t2 ← xQ · t1
        Fp2Element t3 = field.multiply(t1, P2.x);//t3 ← t1 · xP2
        t3 = field.add(t3, t3);//t3 ← t3 + t3
        t3 = field.substract(t2, t3);//t3 ← t2 − t3
        t3 = field.add(t3, P2.x);//t3 ← t3 + xP2
        t3 = field.multiply(Q.y, t3);//t3 ← yQ · t3
        t2 = field.substract(t2, Q.x);//t2 ← t2 − xQ
        t1 = field.substract(Q.x, P2.x);//t1 ← xQ − xP2
        t1 = field.mInverse(t1);//t1 ← t1^{−1}
        Fp2Element x = field.multiply(t2, t1);//xQ' ← t2 · t1
        t1 = field.square(t1);//t1 ← t1^2
        Fp2Element y = field.multiply(t3, t1);//yQ' ← t3 · t1

        return new ECPoint(x, y);

    }

    /*Input: P4 s.t it has exact order 4 on Ea,b.
    Output: Ea',b' = Ea,b/<P4>
    */ 
    public EC curve_4_iso(ECPoint P4) {
        Fp2Element t1 = field.square(P4.x);//t1 ← xP4^2
        Fp2Element an = field.square(t1);//a' ← t1^2
        an = field.add(an, an);//a' ← a' + a'
        an = field.add(an, an);//a' ← a' + a'
        Fp2Element t2 = new Fp2Element(new BigInteger("2"), BigInteger.ZERO);//t2 ← 2
        an = field.substract(an, t2);//a' ← a' − t2
        t1 = field.multiply(P4.x, t1);//t1 ← xP4· t1
        t1 = field.add(t1, P4.x);//t1 ← t1 + xP4
        t1 = field.multiply(t1, b);//t1 ← t1 · b
        t2 = field.mInverse(t2);//t2 ← t2^(−1)
        t2 = field.aInverse(t2);//t2 ← −t2
        Fp2Element bn = field.multiply(t2, t1);// b' ← t2 · t1
        return new EC(an, bn, field);
    }
    
    /*
    input: Q and P4, where P ∈ Ea,b, and P4 has exact order 4 on Ea,b
     Output: Q' ∈ Ea',b', where Ea',b' is the curve 4-isogenous to Ea,b output from
curve_4_iso
    
    */
    public ECPoint eval_4_iso(ECPoint Q, ECPoint P4) {
        Fp2Element t1 = field.square(Q.x);//t1 ← xQ^2
        Fp2Element t2 = field.square(t1);//t2 ← t1^2
        Fp2Element t3 = field.square(P4.x);//t3 ← xP4^2
        Fp2Element t4 = field.multiply(t2, t3);//t4 ← t2 · t3
        t2 = field.add(t2, t4);//t2 ← t2 + t4
        t4 = field.multiply(t1, t3);//t4 ← t1 · t3
        t4 = field.add(t4, t4);//t4 ← t4 + t4
        Fp2Element t5 = field.add(t4, t4);//t5 ← t4 + t4
        t5 = field.add(t5, t5);//t5 ← t5 + t5
        t4 = field.add(t4, t5);//t4 ← t4 + t5
        t2 = field.add(t2, t4);//t2 ← t2 + t4
        t4 = field.square(t3);//t4 ← t3^2
        t5 = field.multiply(t1, t4);//t5 ← t1 · t4
        t5 = field.add(t5, t5);//t5 ← t5 + t5
        t2 = field.add(t2, t5);//t2 ← t2 + t5
        t1 = field.multiply(t1, Q.x);//t1 ← t1 · xQ
        t4 = field.multiply(P4.x, t3);//t4 ← xP4· t3
        t5 = field.multiply(t1, t4);//t5 ← t1 · t4
        t5 = field.add(t5, t5);//t5 ← t5 + t5
        t5 = field.add(t5, t5);//t5 ← t5 + t5
        t2 = field.substract(t2, t5);//t2 ← t2 − t5
        t1 = field.multiply(t1, P4.x);//t1 ← t1 · xP4
        t1 = field.add(t1, t1);//t1 ← t1 + t1
        t1 = field.add(t1, t1);//t1 ← t1 + t1
        t1 = field.substract(t2, t1);//t1 ← t2 − t1
        t2 = field.multiply(Q.x, t4);//t2 ← xQ · t4
        t2 = field.add(t2, t2);//t2 ← t2 + t2
        t2 = field.add(t2, t2);//t2 ← t2 + t2
        t1 = field.substract(t1, t2);//t1 ← t1 − t2
        t1 = field.add(t1, t3);// t1 ← t1 + t3
        t1 = field.add(t1, field.getOne());//t1 ← t1 + 1
        t2 = field.multiply(Q.x, P4.x);//t2 ← xQ · xP4
        t4 = field.substract(t2, field.getOne());//t4 ← t2 − 1
        t2 = field.add(t2, t2);//t2 ← t2 + t2
        t5 = field.add(t2, t2);//t5 ← t2 + t2
        t1 = field.substract(t1, t5);//t1 ← t1 − t5
        t1 = field.multiply(t4, t1);//t1 ← t4 · t1
        t1 = field.multiply(t3, t1);//t1 ← t3 · t1
        t1 = field.multiply(Q.y, t1);//t1 ← yQ · t1
        t1 = field.add(t1, t1);//t1 ← t1 + t1
        Fp2Element y = field.aInverse(t1);//yQ' ← −t1
        t2 = field.substract(t2, t3);//t2 ← t2 − t3
        t1 = field.substract(t2, field.getOne());//t1 ← t2 − 1
        t2 = field.substract(Q.x, P4.x);//t2 ← xQ − xP4
        t1 = field.multiply(t2, t1);//t1 ← t2 · t1
        t5 = field.square(t1);//t5 ← t1^2
        t5 = field.multiply(t5, t2);//t5 ← t5 · t2
        t5 = field.mInverse(t5);//t5 ← t5^{−1}

        y = field.multiply(y, t5);//yQ' ← yQ' · t5
        t1 = field.multiply(t1, t2);//t1 ← t1 · t2
        t1 = field.mInverse(t1);//t1 ← t1^{−1}
        t4 = field.square(t4);//t4 ← t4^2
        t1 = field.multiply(t1, t4);//t1 ← t1 · t4
        t1 = field.multiply(Q.x, t1);//t1 ← xQ · t1
        t2 = field.multiply(Q.x, t3);//t2 ← xQ · t3
        t2 = field.add(t2, Q.x);//t2 ← t2 + xQ
        t3 = field.add(P4.x, P4.x);//t3 ← xP4 + xP4
        t2 = field.substract(t2, t3);//t2 ← t2 − t3
        t2 = field.aInverse(t2);//t2 ← −t2
        Fp2Element x = field.multiply(t1, t2);//xQ' ← t1 · t2

        return new ECPoint(x, y);
    }

    /*
    Input: P3 where P3 has exact order 3 on Ea,b
    Output: Ea',b' = Ea,b/<P3>
    */ 
    public EC curve_3_iso(ECPoint P3) {
        Fp2Element t1 = field.square(P3.x);//t1 ← xP3^2
        Fp2Element bn = field.multiply(b, t1);//b' ← b · t1
        t1 = field.add(t1, t1);//t1 ← t1 + t1
        Fp2Element t2 = field.add(t1, t1);//t2 ← t1 + t1
        t1 = field.add(t1, t2);//t1 ← t1 + t2
        t2 = new Fp2Element(new BigInteger("6"), BigInteger.ZERO);//t2 ← 6
        t1 = field.substract(t1, t2);//t1 ← t1 − t2
        t2 = field.multiply(a, P3.x);//t2 ← a · xP3
        t1 = field.substract(t2, t1);//t1 ← t2 − t1
        Fp2Element an = field.multiply(t1, P3.x);//a' ← t1 · xP3
        return new EC(an, bn, field);
    }
    
    /*
    Input: Q and P3, where Q ∈ Ea,b, and P3 has exact order 3 on Ea,b
    Output: Q' ∈ Ea',b', where Ea',b' is the curve 3-isogenous to Ea,b output from
    curve_3_iso
    */

    public ECPoint eval_3_iso(ECPoint Q, ECPoint P3) {
        Fp2Element t1 = field.square(Q.x);//t1 ← xQ^2
        t1 = field.multiply(t1, P3.x);//t1 ← t1 · xP3
        Fp2Element t2 = field.square(P3.x);//t2 ← xP3^2
        t2 = field.multiply(Q.x, t2);//t2 ← xQ · t2
        Fp2Element t3 = field.add(t2, t2);//t3 ← t2 + t2
        t2 = field.add(t2, t3);//t2 ← t2 + t3
        t1 = field.substract(t1, t2);//t1 ← t1 − t2
        t1 = field.add(t1, Q.x);//t1 ← t1 + xQ
        t1 = field.add(t1, P3.x);//t1 ← t1 + xP3
        t2 = field.substract(Q.x, P3.x);//t2 ← xQ − xP3
        t2 = field.mInverse(t2);//t2 ← t2^(−1)
        t3 = field.square(t2);//t3 ← t2^2
        t2 = field.multiply(t2, t3);// t2 ← t2 · t3
        Fp2Element t4 = field.multiply(Q.x, P3.x);//t4 ← xQ · xP3
        t4 = field.substract(t4, field.getOne());// t4 ← t4 − 1
        t1 = field.multiply(t4, t1);//t1 ← t4 · t1
        t1 = field.multiply(t1, t2);//t1 ← t1 · t2
        t2 = field.square(t4);//t2 ← t4^2

        t2 = field.multiply(t2, t3);//t2 ← t2 · t3
        Fp2Element x = field.multiply(Q.x, t2);//xQ' ← xQ · t2
        Fp2Element y = field.multiply(Q.y, t1);//yQ' ← yQ · t1

        return new ECPoint(x, y);
    }

    /*
    Input: Integer e2 and S where S has exact order 2e2 on Ea,b.
    Optional input: {(x1, y1), ...,(xn, yn)} on Ea,b
    Output:  Ea',b' = E/<S>
    Optional output: {(x1', y1'), ...,(xn', yn')} on Ea',b'
    */
    public IO iso_2_e(ECPoint S, int e2, List<ECPoint> list) {
        EC ec = new EC(this.a, this.b, this.field);
        List<ECPoint> list2 = null;
        if (list != null) {
            list2 = new ArrayList(list);

        }

        int ep2 = e2;
        ECPoint T;
       // System.out.println(ep2+":"+ec.xDBLe(S, ep2 ));
        if (ep2 % 2 == 1) {
            T = ec.xDBLe(S, ep2 - 1);
           
            S = ec.eval_2_iso(S, T);

            if (list2 != null) {
                for (int i = 0; i < list2.size(); i++) {
                    list2.set(i, ec.eval_2_iso(list2.get(i), T));
                }

            }
            ec = ec.curve_2_iso(T);
            ep2 = ep2 - 1;
            //System.out.println(ep2+":"+ec.xDBLe(S, ep2 ));

        }

        for (int e = ep2 - 2; e >= 0; e = e - 2) {
            T = ec.xDBLe(S, e);
           
            if(!S.x.equals(T.x))
           {   
               S = ec.eval_4_iso(S, T);
           
           }
          

            if (list2 != null) {
                for (int i = 0; i < list2.size(); i++) {
                    list2.set(i, ec.eval_4_iso(list2.get(i), T));
                }

            }
            ec = ec.curve_4_iso(T);
           // System.out.println(e+":"+ec.xDBLe(S, e ));
        }

        return new IO(ec, list2);
    }
   /*
    Input: Integer e3 and S where S has exact order 3e3 on Ea,b.
    Optional input: {(x1, y1), ...,(xn, yn)} on Ea,b
    Output:  Ea',b' = E/<S>
    Optional output: {(x1', y1'), ...,(xn', yn')} on Ea',b'
    */
    public IO iso_3_e(ECPoint S, int e3, List<ECPoint> list) {
        EC ec = new EC(this.a, this.b, this.field);
        List<ECPoint> list3 = null;
        if (list != null) {
            list3 = new ArrayList(list);

        }

        ECPoint T;
       
        for (int e = e3 - 1; e >= 0; e = e - 1) {
         
            T = ec.xTPLe(S, e);
           if(!T.getX().equals(S.x))
           { S = ec.eval_3_iso(S, T);
           }
            if (list3 != null) {
                for (int i = 0; i < list3.size(); i++) {
                    list3.set(i, ec.eval_3_iso(list3.get(i), T));
                }

            }
            ec = ec.curve_3_iso(T);
            // System.out.println(S);
            

        }

        return new IO(ec, list3);
    }
    
    /*
    Input: Parameters of Ea,b with generator points:  P = (xP, yP), Q = (xQ, yQ)
    Output:  R = P − Q
    */

    public static Fp2Element get_xR(EC ec, ECPoint P, ECPoint Q) {

        ECPoint R = ec.xADD(P, ec.negate(Q));
        return R.x;
    }

    public static Fp2Element getA(Fp2Field field, Fp2Element Px, Fp2Element Qx, Fp2Element Rx) {

        Fp2Element t1 = field.add(Px, Qx);//t1 ← xP + xQ
        Fp2Element t0 = field.multiply(Px, Qx);//t0 ← xP · xQ
        Fp2Element A = field.multiply(Rx, t1);//A ← xR · t1
        A = field.add(A, t0);//A ← A + t0
        t0 = field.multiply(t0, Rx);//t0 ← t0 · xR
        A = field.substract(A, field.getOne());//A ← A − 1
        t0 = field.add(t0, t0);//t0 ← t0 + t0
        t1 = field.add(t1, Rx);//t1 ← t1 + xR
        t0 = field.add(t0, t0);//t0 ← t0 + t0
        A = field.multiply(A, A);//A ← A^2
        t0 = field.mInverse(t0);//t0 ← 1/t0
        A = field.multiply(A, t0);//A ← A · t0
        A = field.substract(A, t1);//A ← A − t1

        return A;
    }

    public static  Fp2Element[] get_yP_yQ_A_B(Fp2Field field, Fp2Element Px, Fp2Element Qx, Fp2Element Rx) {
        Fp2Element[] output = new Fp2Element[4];
        output[2] = getA(field, Px, Qx, Rx);//a← get_A(xP, xQ, xR) 
        output[3] = field.getOne();//b←1 
        Fp2Element t1 = field.square(Px);//t1 ← xP^2
        Fp2Element t2 = field.multiply(Px, t1);//t2 ← xP · t1
        t1 = field.multiply(output[2], t1);//t1 ← a · t1
        t1 = field.add(t2, t1);//t1 ← t2 + t1
        t1 = field.add(t1, Px);//t1 ← t1 + xP
        Fp2Element Py = field.sqroot(t1);// yP ←√t1
        t1 = field.square(Qx);// t1 ← xQ^2
        t2 = field.multiply(Qx, t1);//t2 ← xQ · t1
        t1 = field.multiply(output[2], t1);//t1 ← a · t1
        t1 = field.add(t2, t1);//t1 ← t2 + t1
        t1 = field.add(t1, Qx);//t1 ← t1 + xQ
        Fp2Element Qy = field.sqroot(t1);//yQ ←√t1
       //System.out.println("Qy:="+Qy);
        ECPoint P = new ECPoint(Px, Py);
        ECPoint mQ = new ECPoint(Qx, field.aInverse(Qy));
        EC ec = new EC(output[2], output[3], field);
        ECPoint T = ec.xADD(P, mQ);

        if (!T.x.equals(Rx)) {
            Qy = field.aInverse(Qy);
        }
        output[0] = Py;
        output[1] = Qy;
        return output;
    }
    
    
    

}
