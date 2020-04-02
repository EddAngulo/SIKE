/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecoperations;

import fieldoperations.Fp2Element;
import java.util.Objects;

/**
 *
 * @author user
 */
public class ECPoint {

    Fp2Element x;
    Fp2Element y;
    boolean identity;

    public ECPoint() {
        identity = true;
        x = null;
        y = null;
    }

    public ECPoint(Fp2Element x, Fp2Element y) {
        this.identity = false;
        this.x = x;
        this.y = y;
    }

    public boolean isIdentity() {
        return identity;
    }
    
    @Override
    public ECPoint clone() {
        if(identity) {
            return new ECPoint();
        }
        return new ECPoint(x, y);
    }

    public Fp2Element getX() {
        return x;
    }

    public void setX(Fp2Element x) {
        this.x = x;
    }

    public Fp2Element getY() {
        return y;
    }

    public void setY(Fp2Element y) {
        this.y = y;
    }   
    
    @Override
    public String toString() {   
        String s;
        if(x == null && y == null) {
            s = "Inf";
        }else {   
            s = "(" + x.toString() + ", " + y.toString() + ")";
        }
        return s;
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
        final ECPoint other = (ECPoint) obj;
        if(this.identity && other.identity) {
            return true;
        }else if(this.identity && !other.identity) {
            return false;
        }else if(!this.identity && other.identity) {
            return false;
        }else {
            if (!Objects.equals(this.x, other.x)) {
                return false;
            }
            if (!Objects.equals(this.y, other.y)) {
                return false;
            }
            return true;
        }
    }
    
}
