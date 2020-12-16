package com.lasy.dwbk;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.db.DwbkGeoPackage;

public class Main
{

  public static void main(String[] args)
  {
    try (DwbkFramework framework = DwbkFramework.getInstance())
    {
      DwbkGeoPackage tmGpkg = framework.getGeoPackage();
      
      System.out.println("Available tables: " + tmGpkg.getAvailableTableNames());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
