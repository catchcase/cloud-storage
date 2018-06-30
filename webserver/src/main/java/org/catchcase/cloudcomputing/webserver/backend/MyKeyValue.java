package org.catchcase.cloudcomputing.webserver.backend;

import java.io.*;

/**
 * class MyKeyValue
 *
 * This class instantiates the key/value pairs as an object.
 */
public class MyKeyValue implements Serializable{

    private static final long serialVersionUID = 1L;

    private int key;
    private String value;

    /**
     * String getValue ()
     *
     * @return the value as String
     */
    public String getValue() {
        return value;
    }

    /**
     * void setValue (String value)
     *
     * @param value - value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * int getKey ()
     *
     * @return the key as int
     */
    public int getKey() {

        return key;
    }

    /**
     * void setKey (int key)
     *
     * @param key - key
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * String toString ()
     *
     * @return MyKeyValue object as String
     */
    @Override
    public String toString(){
        return new StringBuffer()
                .append(Integer.toString(this.key))
                .append(" : ")
                .append(this.value)
                .toString();
    }
}
