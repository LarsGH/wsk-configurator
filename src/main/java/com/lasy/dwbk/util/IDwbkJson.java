package com.lasy.dwbk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface IDwbkJson
{
  /**
   * Creates a framework object from a JSON.
   * @param <TJson> the framework object
   * @param clazz the framework object class
   * @param json the JSON
   * @return framework object
   */
  public static <TJson extends IDwbkJson> TJson createFromJson(Class<TJson> clazz, String json)
  {
    TJson dwbkJson = new Gson().fromJson(json, clazz);
    return dwbkJson;
  }

  /**
   * Returns the JSON (pretty printed).
   * @return JSON
   */
  default String asJson()
  {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(this);
  }
}
