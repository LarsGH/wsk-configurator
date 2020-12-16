package com.lasy.dwbk.gui.util;

import java.util.function.Function;

import com.google.common.base.Preconditions;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * Display value factory for models.
 * @author larss
 *
 * @param <TModelType>
 */
public class ModelValueFactory<TModelType> implements Callback<CellDataFeatures<TModelType,String>, ObservableValue<String>>
{
  private Function<TModelType, String> displayValueFunc;
  
  public ModelValueFactory(Function<TModelType, String> displayValueFunc)
  {
    this.displayValueFunc = Preconditions.checkNotNull(displayValueFunc);
  }

  @Override
  public ObservableValue<String> call(CellDataFeatures<TModelType, String> param)
  {
    String displayValue = this.displayValueFunc.apply(param.getValue());
    return new ReadOnlyObjectWrapper<>(displayValue);
  }

}
