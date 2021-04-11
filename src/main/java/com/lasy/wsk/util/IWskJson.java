package com.lasy.wsk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface IWskJson
{
  /**
   * Creates a framework object from a JSON.
   * @param <TJson> the framework object
   * @param clazz the framework object class
   * @param json the JSON
   * @return framework object
   */
  public static <TJson extends IWskJson> TJson createFromJson(Class<TJson> clazz, String json)
  {
    TJson wskJson = new Gson().fromJson(json, clazz);
    return wskJson;
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
