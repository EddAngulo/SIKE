/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fieldoperations;

import java.math.BigInteger;
import java.util.Objects;

/**
 *
 * @author user
 */
public class Fp2Element {
   BigInteger x0;
   BigInteger x1;

    public Fp2Element(BigInteger x0, BigInteger x1) {
        this.x0 = x0;
        this.x1 = x1;
    }

    public BigInteger getX0() {
        return x0;
    }

    public void setX0(BigInteger x0) {
        this.x0 = x0;
    }

    public BigInteger getX1() {
        return x1;
    }

    public void setX1(BigInteger x1) {
        this.x1 = x1;
    }
   
  
   @Override
   public String toString()
   {
   
   return x0.toString()+"+"+x1.toString()+"*i";
   
   }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.x0);
        hash = 11 * hash + Objects.hashCode(this.x1);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fp2Element other = (Fp2Element) obj;
        if (!Objects.equals(this.x0, other.x0)) {
            return false;
        }
        if (!Objects.equals(this.x1, other.x1)) {
            return false;
        }
        return true;
    }
   
   
   
 
}
