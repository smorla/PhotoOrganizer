package com.smo.photoorganizer.error;

/**
 *
 * @author Salvador
 */
public class WrongArgumentException extends Exception {
      
      private final String fixedMessage = "Wrong program argument specification";

      //Parameterless Constructor
      public WrongArgumentException() {}

      //Constructor that accepts a message
      public WrongArgumentException(String error)
      {
         super(error);
      }
}
