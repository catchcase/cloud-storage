package org.catchcase.cloudcomputing.webserver.robert.HashFS;

/* *******************************************************************
 * class HashFS
 * -------------------------------------------------------------------
 * Implements a distributes file storage system based on hash values
 * -------------------------------------------------------------------
 * Author: Cloud Computing WS 2017/18 Group 2
 * Version: 1.0
 * Created on: Jan 2018
 * Last modified: 6.12.2018
 * -------------------------------------------------------------------
 * This class is designed to be used in conjunction with a webserver.
 * All methods are thread safe. Multiple threads can access one 
 * instance of this class contemorarily. This class doesn't exploit
 * parallelism. Instead the webserver (Apache,...) should spawn one
 * thread per request.
 ******************************************************************** */

                                       // Import packages

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;


public final class HashFS {

  /* *****************************************************************
   * inner class HashBucket
   * -----------------------------------------------------------------
   * Implements one bucket. A bucket can store multiple key/value 
   * pairs
   ****************************************************************** */
  
  private class HashBucket{
  
    private HashMap<String,String> hm = null;
              // a hashmap that stores pairs of Strings: the first
              // String is the key, the second the filename of the
              // file in which the value is stored
              // We use a hashmap because a hashmap gives access to
              // its elements in constant time. (RW)
              
    private Semaphore hmSemaphore = null;
              // A semaphore that locks the access to hm. The 
              // semaphore is necesarry as hm is read/write. A
              // concurrent hash map is not sufficient as complex
              // operations are carried out.
              
    private int bucketnum = -1;   // number of the bucket
    
    
    /* ***************************************************************
     * boolean insert( String key, String val )
     * ---------------------------------------------------------------
     * Insert a key/value pair in the hashmap.
     * ---------------------------------------------------------------
     * Parameters:
     *   key ... key
     *   val ... value to be stored
     * Returns:
     *   boolean ... True if value has been stored, false if 
     *     key already exists of storage operation not possible
     * ---------------------------------------------------------------
     * Thread-safe: fully (semaphores)
     * Blocks: waits on disk-io
     * Throws: no exception thrown
     * Exploits parallelism: no
     **************************************************************** */
    
    boolean insert( String key, String val ){
      boolean ret = false;               // initialize return value
    
      hmSemaphore.acquireUninterruptibly();
                       // aquire semaphore
      
      if (!hm.containsKey( key )){      // key already exists ?
        ret = true;                     // no -> store
        
        String filename = fspath+key;   // Create filename
        
        try{
          byte[] buf = val.getBytes();   // Value to byte-array
          Files.write(Paths.get(filename), buf);
                                          // write file
          hm.put( key, filename );     // add key/filename 
        }
        catch( Exception e ){
          ret = false;              // something went wrong -> return
                                       // false
        }
        
        hfslog.info( "(" + System.currentTimeMillis() + ") Bucket " + bucketnum + ": <" 
                  + key + "," + filename +"> has been inserted" );
                              // output log information
      }
      else {
        hfslog.info( "(" + System.currentTimeMillis() + ") Bucket " + bucketnum  + ": <" 
                  + key + "> already exists, value cannot be stored" );
                      // if key already exists output log information
      }
      
      hmSemaphore.release();      // release semaphore
      
      return( ret );
    }  // insert
    
    
    
    /* ***************************************************************
     * boolean delete( String key )
     * ---------------------------------------------------------------
     * Deletes a key/value pair in the hashmap.
     * ---------------------------------------------------------------
     * Parameters:
     *   key ... key
     * Returns:
     *   boolean ... True if value has been deleted, false if 
     *     key doesn't exists
     * ---------------------------------------------------------------
     * Thread-safe: fully (semaphores)
     * Blocks: waits on disk-io
     * Throws: no exception thrown
     * Exploits parallelism: no
     **************************************************************** */
    
    boolean delete( String key ){
      boolean ret = false;     // initialize return value
    
      hmSemaphore.acquireUninterruptibly();
                                 // Acquire semaphore
      
      if (hm.containsKey( key )){      // key exists ?
        ret = true;                  // yes -> return true
        
        String filename = hm.get( key );    // get filename
        
        hm.remove( key );               // remove the key
        
        try{
                                // Try to delete filename
          File f = new File( filename );
          f.delete();
        }
        catch( Exception e ){
        }    
        
        hfslog.info( "(" + System.currentTimeMillis() + ") Bucket " + bucketnum + ": <" 
                          + key + "," + filename +"> has been deleted" );
                                 // log info
      }
      else {
        hfslog.info( "(" + System.currentTimeMillis() + ") Bucket " + bucketnum + ": <" 
                          + key + "> doesn' exist (delete)" );
                                 // log info if key doesn't exist
      }
      
      hmSemaphore.release();          // release semaphore
      
      return( ret );
   
    } // delete
    
    
    
     /* ***************************************************************
     * String search( String key )
     * ---------------------------------------------------------------
     * Searches a key and returns its associated value or null if key
     * doesn't exist.
     * ---------------------------------------------------------------
     * Parameters:
     *   key ... key
     * Returns:
     *   String ... Value associated, null if key doesn't exist of
     *     file-io reports an error.
     * ---------------------------------------------------------------
     * Thread-safe: fully (semaphores)
     * Blocks: waits on disk-io
     * Throws: no exception thrown
     * Exploits parallelism: no
     **************************************************************** */
       
    String search( String key ){
      String ret = null;             // Initialize return value
    
      hmSemaphore.acquireUninterruptibly();    // Acquire semaphore
      
      if (hm.containsKey( key )){     // Check if key exists
      
        String filename = hm.get( key );   // get filename
                
                           // Try to read value from file
        try{
          byte[] encoded = Files.readAllBytes(Paths.get(filename));
          ret = new String(encoded);
        }
        catch( Exception e ){
          ret = null;     // file-io went wrong
        }        
        
                                 // log info
        if (ret != null){
          hfslog.info( "(" + System.currentTimeMillis() + ") Bucket " + bucketnum + ": <" 
                  + key + "> found, value = '" + ret + "'" );
        }
        else {
          hfslog.info( "(" + System.currentTimeMillis() + ") Bucket " + bucketnum + ": <" 
                  + key + "> found, but file-io not possible" );
        }
                       

      }
      else {
        hfslog.info( "(" + System.currentTimeMillis() + ") Bucket " + bucketnum + ": <" 
                  + key + "> not found!" );
                           // log info if key not found
      }
      
      hmSemaphore.release();             // release semaphore
      
      return( ret );
    } // search
    
    
    
     /* ***************************************************************
     * LinkedList<String> Range( String key1, String key2 )
     * ---------------------------------------------------------------
     * Performs an alphanumerical range query. Returns all keys that
     * lexicographically lie between key1 and key2.
     * ---------------------------------------------------------------
     * Parameters:
     *   key1 ... first key
     *   key2 ... second key
     * Returns:
     *   LinkedList<String> ... List of keys that lie 
     *     lexicographically between key1 and key2.
     * ---------------------------------------------------------------
     * Thread-safe: fully (semaphores)
     * Blocks: never
     * Throws: no exception thrown
     * Exploits parallelism: no
     **************************************************************** */
        
    LinkedList<String> Range( String key1, String key2 ){
    
      LinkedList<String> ret = new LinkedList<String>();
                  // initialize return value
      
      hmSemaphore.acquireUninterruptibly(); 
                     // acquire semaphore
      
      Set<String> keys = hm.keySet();
                     // get a set of keys
      
      Iterator<String> itr = keys.iterator();
                        // create an iterator
      
                             // iterate over keys
      while (itr.hasNext()){
        String k = itr.next();   // get next key

                          // compare keys
        if ((k.compareTo( key1 ) >= 0) && (k.compareTo( key2 ) <= 0)){
                      // add key to linked list
          ret.add( new String( k ) );
        }
      }
      
      hmSemaphore.release();    // release semaphore
      
      
      return( ret );
    } // Range
    
    
    /* ***************************************************************
     * constructor HashBucket( int num )
     * ---------------------------------------------------------------
     * Initializes a bucket.
     * ---------------------------------------------------------------
     * Parameters:
     *   num ... unique number of the bucket
     * ---------------------------------------------------------------
     * Thread-safe: fully
     * Blocks: never
     * Throws: no exception thrown
     * Exploits parallelism: no
     **************************************************************** */
   
    HashBucket( int num ){
                               // initialize attributes
      hm = new HashMap<String,String>();
      hmSemaphore = new Semaphore( 1 );
      bucketnum = num;
    } // HashBucket
  }  // HashBucket
  
  
                                      // Attributes of class HashFS
  
  private final Logger hfslog = Logger.getLogger( "HashFSLogger" );
                                   // a logger for all infos (RO)
    
  private String fspath = "";      // The path where the files for
                  // all entries should be stored (RO)

  private int anzbuckets = -1;    // number of buckets (RO)
  
  private static final String rndchars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
                  // chars that may be used for key generation (RO)
                  
  private final SecureRandom rnd = new SecureRandom();
                       // a random generator (RO)
  
  private HashBucket[] HashBucketArray = null;
                         // An array of HashBuckets (RO)            
  
  private static final int KEYLENGTH = 64;
                     // Length of a created key (RO)

  
  
  /* *****************************************************************
   * private String createKey()
   * -----------------------------------------------------------------
   * Creates a new random key if the client doesn't provide a key.
   * -----------------------------------------------------------------
   * Parameters: none
   * Returns:
   *   String ... a key with exacly KEYLENGTH chars.
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */
  
  private String createKey(){

   StringBuilder sb = new StringBuilder( KEYLENGTH );
                 // Create a string builder
   
   for( int i = 0; i < KEYLENGTH; i++ ) {
      sb.append( rndchars.charAt( rnd.nextInt( rndchars.length() ) ) );
               // Append random chars
   }                
   
   return sb.toString();  // return key

  }  // createKey
  
  
  
  /* *****************************************************************
   * private int getBucket( String key )
   * -----------------------------------------------------------------
   * Returns a bucket associated with the key. The bucket number
   * is calculated from a modulo operation of the absolute of a
   * 32-bit hash value of key.
   * -----------------------------------------------------------------
   * Parameters: 
   *   String key ... key
   * Returns:
   *   int ... bucket number (0 <= int <= anzbuckets)
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */
  
  private int getBucket( String key ) {
    return (Math.abs(key.hashCode()))%anzbuckets;
            // Calculate and return bucket number
  } // getBucket
  
  
  /* *****************************************************************
   * public String insert( String val )
   * -----------------------------------------------------------------
   * Inserts a value. Random key will be created. 
   * -----------------------------------------------------------------
   * Parameters: 
   *   String val ... value
   * Returns:
   *   String ... key associated with value
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */
  
  public String insert( String val ){
  
    String ret = "";                  // initialize return value
    boolean weiter = false;           // initialize loop variable
    
    while (weiter == false){       // repeat until value has been
                                     // inserted
      ret = createKey();            // create a key
      int buc = getBucket( ret );     // get bucket
      weiter = HashBucketArray[buc].insert( ret, val );
                      // try to store, if key already exists repeat
                      // procedure
    }
    
    return( ret );         // return key
  }  // insert
  

  /* *****************************************************************
   * public boolean insert( String key, String val )
   * -----------------------------------------------------------------
   * Inserts a key/value pair.
   * -----------------------------------------------------------------
   * Parameters: 
   *   String key ... key
   *   String val ... value
   * Returns:
   *   Boolean ... false if key already exists, true else
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */

  public boolean insert( String key, String val ){
  
    boolean ret = false;          // initialize return value
    
    int buc = getBucket( key );   // get bucket
    ret = HashBucketArray[buc].insert( key, val );
                          // Try to insert
    return( ret );
  } // insert
  
  
  /* *****************************************************************
   * public boolean delete( String key )
   * -----------------------------------------------------------------
   * Deletes a key/value pair.
   * -----------------------------------------------------------------
   * Parameters: 
   *   String key ... key
   * Returns:
   *   Boolean ... true if key exists, false else
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */
  
  public boolean delete( String key ){
    return HashBucketArray[ getBucket( key ) ].delete( key );
  } // delete
  
  
  /* *****************************************************************
   * public String search( String key )
   * -----------------------------------------------------------------
   * Returns a value for a key. Null if key doesn't exist.
   * -----------------------------------------------------------------
   * Parameters: 
   *   String key ... key
   * Returns:
   *   String ... Value associated with key, null if key doesn't 
   *     exist.
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */
  
  public String search( String key ){
    return HashBucketArray[ getBucket( key ) ].search( key );
  } // search
  

  /* *****************************************************************
   * public String[] Range( String key1, String key2 )
   * -----------------------------------------------------------------
   * Returns a String-Array for a lexicographical range query.
   * -----------------------------------------------------------------
   * Parameters: 
   *   String key1 ... first key
   *   String key2 ... second key
   * Returns:
   *   String[] ... An array with all keys in all buckets that lie
   *     lexicographically between key1 and key2.
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */

  public String[] Range( String key1, String key2 ){
    LinkedList<String> ll = new LinkedList<String>();
                // This list will collect all keys
    
    for( int i = 0; i<anzbuckets; i++ ){
      ll.addAll( HashBucketArray[ i ].Range( key1, key2 ) );
                    // Check in all buckets for relevant keys
    }
    
    return( ll.toArray( new String[0] ) );
           // Convert linked list to array and return it.
  } // Range

  
  /* *****************************************************************
   * constructor HashFS( int num, String fs )
   * -----------------------------------------------------------------
   * Initializes a hash based storage.
   * -----------------------------------------------------------------
   * Parameters:
   *   int num ... unique number of buckets to be used
   *   String fs ... Path where to store files
   * -----------------------------------------------------------------
   * Thread-safe: fully
   * Blocks: never
   * Throws: no exception thrown
   * Exploits parallelism: no
   ****************************************************************** */
  
  public HashFS( int num, String fs ) {
  
    hfslog.info( "(" + System.currentTimeMillis() + ") HashFS is beeing initialized (" + num + " buckets)" );
                      // log info
                      
    HashBucketArray = new HashBucket[num];  
                                    // initialize hash bucket array
    
    anzbuckets = num;                  // save number of buckets
    
    fspath = new String( fs );             // save copy(!) of path
    
    for( int i = 0; i<anzbuckets; i++ ){
      HashBucketArray[i] = new HashBucket( i );
                                         // initialize every bucket
    }
    
    hfslog.info( "(" +System.currentTimeMillis() + ") HashFS has been initialized" );    
                                        // log info
  } // HashFS
  

} // HashFS
