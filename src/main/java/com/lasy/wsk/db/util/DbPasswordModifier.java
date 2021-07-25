package com.lasy.wsk.db.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.lasy.wsk.app.error.WskFrameworkException;

/**
 * Modifies the password for storage.
 * @author larss
 *
 */
public class DbPasswordModifier
{
  
  /**
   * Returns the encrypted password.
   * 
   * @param pw password
   * @return encrypted password
   */
  public static String toDbValue(String pw)
  {
    if(pw == null)
    {
      return null;
    }
    
    return encrypt(pw);
  }
  
  /**
   * Returns the decrypted password.
   * 
   * @param dbPw password
   * @return decrypted password
   */
  public static String fromDbValue(String dbPw)
  {
    if(dbPw == null)
    {
      return null;
    }
    
    return decrypt(dbPw);
  }
  
  private static String encrypt(String pw)
  {
    try
    {
      byte[] data = pw.getBytes(StandardCharsets.UTF_8);
      Encoder encoder = Base64.getEncoder();
      return encoder.encodeToString(data);
    }
    catch (Exception e)
    {
      // password could not be encrypted
      throw WskFrameworkException.failForReason(e, "Das Passwort konnte nicht verschlüsselt werden!");
    }
  }
  
  private static String decrypt(String pw)
  {
    try
    {      
      byte[] data = pw.getBytes(StandardCharsets.UTF_8);
      Decoder decoder = Base64.getDecoder();
      byte[] decoded = decoder.decode(data);
      return new String(decoded, StandardCharsets.UTF_8);
    }
    catch (Exception e)
    {
      // password could not be decrypted
      throw WskFrameworkException.failForReason(e, "Das Passwort konnte nicht entschlüsselt werden!");
    }
  }
  
}
