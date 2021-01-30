package com.lasy.dwbk.gui.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.lasy.dwbk.util.Check;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class ButtonTableCell<TModelType> extends TableCell<TModelType, Button>
{

  public static <TModelType> Callback<TableColumn<TModelType, Button>, TableCell<TModelType, Button>> create(Supplier<Button> button,
    Consumer<TModelType> modelConsumer)
  {
    return create(button, modelConsumer, null);
  }

  // additional predicate to set button visibility
  public static <TModelType> Callback<TableColumn<TModelType, Button>, TableCell<TModelType, Button>> create(Supplier<Button> button,
    Consumer<TModelType> modelConsumer, Predicate<TModelType> btnVisibilityPredicate)
  {
    return param -> new ButtonTableCell<>(button.get(), modelConsumer, btnVisibilityPredicate);
  }

  private Button actionButton;
  private final Optional<Predicate<TModelType>> btnVisibilityPredicate;

  private ButtonTableCell(Button button, Consumer<TModelType> modelConsumer, Predicate<TModelType> btnVisibilityPredicate)
  {
    this.btnVisibilityPredicate = Optional.ofNullable(btnVisibilityPredicate);

    this.actionButton = Check.notNull(button, "button");
    this.actionButton.setOnAction(e -> {
      showLoadingWhileUsingModelConsumer(modelConsumer);
    });

    setAlignment(Pos.CENTER);
  }

  private void showLoadingWhileUsingModelConsumer(Consumer<TModelType> modelConsumer)
  {
    final Node graphic = this.actionButton.getGraphic();
    this.actionButton.setGraphic(new ImageView(GuiIcon.LOADING));
    
    try 
    {
      TModelType item = getCurrentItem().orElse(null);
      modelConsumer.accept(item);
    }
    finally
    {
      this.actionButton.setGraphic(graphic);
      getTableView().refresh();
    }
  }

  public Optional<TModelType> getCurrentItem()
  {
    TableRow<TModelType> tableRow = getTableRow();
    if (tableRow != null)
    {
      return Optional.ofNullable(tableRow.getItem());
    }
    return Optional.empty();
  }

  @Override
  public void updateItem(Button item, boolean empty)
  {
    // super.updateItem(item, empty);

    if (empty)
    {
      setGraphic(null);
    }
    else
    {
      if (this.btnVisibilityPredicate.isPresent() && getCurrentItem().isPresent())
      {
        boolean isVisible = this.btnVisibilityPredicate.get().test(getCurrentItem().get());
        this.actionButton.setVisible(isVisible);
      }

      setGraphic(this.actionButton);
    }
  }
}
