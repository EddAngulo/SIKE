/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fieldoperations;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author user
 */
public class Main {
    
    public static void main(String[] args)
    {
   // 
      BigInteger p=new BigInteger("7");
      System.out.println(p.isProbablePrime(100));
      int k=p.bitLength()-1;
      
      System.out.println(p.testBit(k+1));
      System.out.println(p.testBit(k-1));
      System.out.println(p.testBit(k-2));
      
    
    }
    
}
