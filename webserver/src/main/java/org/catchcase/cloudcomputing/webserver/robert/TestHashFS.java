package org.catchcase.cloudcomputing.webserver.robert;

import org.catchcase.cloudcomputing.webserver.robert.HashFS.*;

public class TestHashFS {

    public static void main(String[] args) {
      
      HashFS hfs = new HashFS( 16, "/home/robert/Informatik/CloudComputing/SaaS/fs/" );
      

      System.out.println( hfs.insert( "Temporal" ) );
      hfs.insert( "Robert", "Fritze" );
      
      String[] s = hfs.Range("A","Z" );
      
      System.out.println( "Result of range query: ");
      
      for(int i=0; i<s.length; i++ ){
        System.out.println( s[i] + ": " + hfs.search( s[i] ) );
      }
    }
    
    

}
