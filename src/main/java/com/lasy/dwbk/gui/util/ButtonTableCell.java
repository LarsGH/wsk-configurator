package com.lasy.dwbk.gui.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ButtonTableCell<TModelType> extends TableCell<TModelType, Button> {
  
  public static <TModelType> Callback<TableColumn<TModelType, Button>, TableCell<TModelType, Button>> create(
    Supplier<Button> button,
    Consumer<TModelType> modelConsumer)
  {
    return param -> new ButtonTableCell<>(button.get(), modelConsumer);
  }

  private final Button actionButton;

  private ButtonTableCell(Button button, Consumer<TModelType> modelConsumer) {
      this.actionButton = Preconditions.checkNotNull(button);
      this.actionButton.setOnAction(e -> {
          modelConsumer.accept(getCurrentItem());
      });
      
      setAlignment(Pos.CENTER);
  }

  public TModelType getCurrentItem() {

    TModelType item = getTableRow().getItem();
    return item;
  }

  @Override
  public void updateItem(Button item, boolean empty)
  {
    super.updateItem(item, empty);

    if (empty)
    {
      setGraphic(null);
    }
    else
    {
      setGraphic(this.actionButton);
    }
  }
}
