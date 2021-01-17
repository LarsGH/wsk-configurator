package com.lasy.dwbk;

import java.util.logging.Level;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.db.DwbkGeoPackage;

public class Main
{

  public static void main(String[] args)
  {
    try (DwbkFramework framework = DwbkFramework.getInstance())
    {
      DwbkGeoPackage tmGpkg = framework.getDwbkGeoPackage();
      
      System.out.println("Available tables: " + tmGpkg.getAvailableTableNames());
    }
    catch (DwbkFrameworkException e)
    {
      if(e.hasLevel(Level.SEVERE))
      {
        System.exit(-1);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
