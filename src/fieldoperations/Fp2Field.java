/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fieldoperations;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 *
 * @author user
 */
public class Fp2Field {

    BigInteger p;
    Fp2Element[] secuenceforY;
    Fp2Element[] secuenceforA;
    BigInteger m;
    int potencia;

    public Fp2Field(BigInteger p) {
        this.p = p;
        BigInteger two = new BigInteger("2");
        BigInteger n = p.multiply(p);
                n=n.subtract(BigInteger.ONE).divide(two);
        factorize(n);

       computeNonResidueQuadratic();
    }

    private void factorize(BigInteger n) {
        BigInteger two = new BigInteger("2");
        potencia = 0;
       // System.out.println(n);
        while (!n.testBit(0)) {
            n = n.divide(two);
            potencia++;

        }

        m = n;
        
       // System.out.println(two.pow(potencia).multiply(m));
      
    }

    private void computeNonResidueQuadratic() {

        secuenceforY = new Fp2Element[potencia + 1];
        Fp2Element m1 = aInverse(getOne());

       // System.out.println(m1);
        do {
            Fp2Element randomelement = getRandomElement();
          //  System.out.println(randomelement);
            secuenceforY[0] = this.pow(randomelement, m);
            for (int i = 1; i <= potencia; i++) {
                secuenceforY[i] = square(secuenceforY[i - 1]);
            }
            
       // System.out.println(secuenceforY[potencia]);
        } while (secuenceforY[potencia].x0.compareTo(m1.x0) != 0 || secuenceforY[potencia].x1.compareTo(m1.x1) != 0);
/*System.out.println("residuo no cuadratico");
        System.out.println(secuenceforY[potencia]);*/
    }

    public Fp2Element getRandomElement() {
        byte[] randombits = new byte[p.bitLength() / 8 + 1];
        Random random = new SecureRandom();

        random.nextBytes(randombits);

        BigInteger x = new BigInteger(randombits);
        
        random.nextBytes(randombits);
        
        BigInteger y = new BigInteger(randombits);

        x = x.mod(p);
        y = y.mod(p);
        
        
        
        return new Fp2Element(x, y);

    }

    public Fp2Element pow(Fp2Element x, BigInteger n) {
        if (n.compareTo(BigInteger.ZERO) == 0) {
            return this.getOne();
        }
        if (n.compareTo(BigInteger.ZERO) < 0) {
            n = n.negate();
            x = this.mInverse(x);
        }
        int k = n.bitLength();
        Fp2Element r =x;
        for (int i = k - 2; i >= 0; i--) {
            r = this.square(r);
            if (n.testBit(i)) {
                r = this.multiply(r, x);
            }
        }
        return r;
    }

    public Fp2Element sqroot(Fp2Element a) {
        Fp2Element one = getOne();
        Fp2Element minusOne = aInverse(one);
        

        BigInteger two = new BigInteger("2");

        BigInteger m1 = m.subtract(BigInteger.ONE).divide(two);
//System.out.println("++++++");
        Fp2Element t1 = this.pow(a, m1);
        Fp2Element t2 = this.multiply(a, t1);
        secuenceforA = new Fp2Element[potencia+1];
        secuenceforA[0] = this.multiply(t1, t2);
        if (secuenceforA[0].x0.compareTo(one.x0) == 0 && secuenceforA[0].x1.compareTo(one.x1) == 0) {
            return t2;
        }
        int i = 0;
       // System.out.println("----"+secuenceforA[0] );
        while (secuenceforA[i].x0.compareTo(minusOne.x0) != 0 || secuenceforA[i].x1.compareTo(minusOne.x1)!= 0) {
            i = i + 1;
            secuenceforA[i] = this.square(secuenceforA[i - 1]);
        }
        int t = i;
       // System.out.println("t="+i+";potencia="+potencia);
        if(i==potencia)
        {
         return null;
        }
        

        int[] I = new int[t+1];
        I[0] = potencia - 1;
        int miu = 1;
        Fp2Element rho;
        for (int k = 1; k <= t; k++) {
            rho = secuenceforA[t - k];
            for (int j = 0; j < miu ; j++) {
                rho = this.multiply(rho, secuenceforY[I[j]]);
                I[j] = I[j] - 1;
            }
            if (rho.x0.compareTo(minusOne.x0) == 0 && rho.x1.compareTo(minusOne.x1) == 0) {
                I[miu] = potencia - 1;
                miu = miu + 1;
            }

        }

        for (int j = 0; j <miu; j++) {
            t2 = this.multiply(t2, secuenceforY[I[j]]);

        }

        return t2;
    }

    public Fp2Element add(Fp2Element x, Fp2Element y) {
        BigInteger z0 = x.x0.add(y.x0).mod(p);
        BigInteger z1 = x.x1.add(y.x1).mod(p);

        return new Fp2Element(z0, z1);

    }

    public Fp2Element substract(Fp2Element x, Fp2Element y) {
        BigInteger z0 = x.x0.subtract(y.x0).mod(p);
        BigInteger z1 = x.x1.subtract(y.x1).mod(p);

        return new Fp2Element(z0, z1);

    }

    public Fp2Element multiply(Fp2Element x, Fp2Element y) {

        BigInteger m1 = x.x0.multiply(y.x0);
        BigInteger m2 = x.x1.multiply(y.x1);
        BigInteger m3 = x.x0.multiply(y.x1);
        BigInteger m4 = x.x1.multiply(y.x0);

        return new Fp2Element(m1.subtract(m2).mod(p), m3.add(m4).mod(p));

    }

    public Fp2Element div(Fp2Element x, Fp2Element y) {

        return multiply(x, mInverse(y));
    }

    public Fp2Element square(Fp2Element x) {
        BigInteger s0 = x.x0.add(x.x1);
        BigInteger s1 = x.x0.subtract(x.x1);

        BigInteger z0 = s0.multiply(s1).mod(p);
        BigInteger z1 = x.x0.multiply(x.x1);
        z1 = z1.add(z1).mod(p);

        return new Fp2Element(z0, z1);

    }

    public Fp2Element mInverse(Fp2Element x) {
        BigInteger x02 = x.x0.multiply(x.x0);
        BigInteger x12 = x.x1.multiply(x.x1);
        BigInteger ssq = x02.add(x12);
        BigInteger si = ssq.modInverse(p);

        BigInteger z0 = x.x0.multiply(si).mod(p);
        BigInteger z1 = x.x1.multiply(si).negate().mod(p);

        return new Fp2Element(z0, z1);

    }

    public Fp2Element getOne() {
        Fp2Element one = new Fp2Element(BigInteger.ONE, BigInteger.ZERO);

        return one;
    }

    public Fp2Element getZero() {
        Fp2Element one = new Fp2Element(BigInteger.ZERO, BigInteger.ZERO);

        return one;
    }

    public Fp2Element aInverse(Fp2Element x) {
        return new Fp2Element(x.x0.negate().mod(p), x.x1.negate().mod(p));
    }

    public boolean areEqual(Fp2Element x, Fp2Element y) {
        if (x.x0.mod(p).compareTo(y.x0.mod(p)) != 0) {
            return false;
        }

        if (x.x1.mod(p).compareTo(y.x1.mod(p)) != 0) {
            return false;
        }

        return true;
    }
    
  public static void main(String[] args)
  {
     BigInteger p=BigInteger.probablePrime(100, new SecureRandom());
     BigInteger four=new BigInteger("4");
     BigInteger out=p.mod(four);
     System.out.println(out);
     if (out.compareTo(BigInteger.ONE)!=0)
     {  System.out.println(p);
        Fp2Field field=new Fp2Field(p);
        Fp2Element x,y, z;
        
        x=field.getRandomElement();
        y=null;
        
         int c=0;
         int f=0;
       for(int i=0;i<100;i++)
       { x=field.getRandomElement();
          y=field.sqroot(x);
        
         if(y!=null)
         {   if(field.areEqual(x, field.square(y)))
              {
              f++;
              }
             
           c=c+1;
         }
         else
         {
         System.out.println("no residuo cuadratico");
         }
          
       }
       
       System.out.println(c);
       System.out.println(f);
// System.out.println(field.multiply(z,field.mInverse(z)));
        
     
     }
  
      
 
    
  }
}
