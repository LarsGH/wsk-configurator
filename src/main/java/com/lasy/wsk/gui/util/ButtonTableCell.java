package com.lasy.wsk.gui.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.lasy.wsk.util.Check;

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

  /**
   * Creates a button table cell.
   * @param <TModelType> the model type
   * @param button button supplier
   * @param modelConsumer model consumer
   * @param btnVisibilityPredicate predicate to determine if the button should be visible.
   *                                (may be {@code null} if button should always be shown)
   * @return button table cell
   */
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
