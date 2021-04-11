package com.lasy.wsk.gui.panes;

import com.lasy.wsk.gui.util.GuiUtil;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class AWskMarginPane extends BorderPane
{
  /**
   * Constructor.
   */
  protected AWskMarginPane()
  {
    super();
  }
  
  /**
   * Sets the top content with standard margin.
   * @param node node
   */
  protected void setTopWithMargin(Node node)
  {
    setTopWithMargin(node, createDefaultInsets());
  }
  
  /**
   * Sets the top content with standard margin.
   * @param node node
   * @param insets insets
   */
  protected void setTopWithMargin(Node node, Insets insets)
  {
    if(node != null)
    {
      setMargin(node, insets);
      setTop(node);
    }
  }
  
  /**
   * Sets the center content with standard margin.
   * @param node node
   */
  protected void setCenterWithMargin(Node node)
  {
    setCenterWithMargin(node, createDefaultInsets());
  }
  
  /**
   * Sets the center content with standard margin.
   * @param node node
   * @param insets insets
   */
  protected void setCenterWithMargin(Node node, Insets insets)
  {
    if(node != null)
    {
      setMargin(node, insets);
      setCenter(node);
    }
  }
  
  /**
   * Sets the bottom content with standard margin.
   * @param node node
   */
  protected void setBottomWithMargin(Node node)
  {
    setBottomWithMargin(node, createDefaultInsets());
  }
  
  /**
   * Sets the bottom content with standard margin.
   * @param node node
   * @param insets insets
   */
  protected void setBottomWithMargin(Node node, Insets insets)
  {
    if(node != null)
    {
      setMargin(node, insets);
      setBottom(node);
    }
  }

  private Insets createDefaultInsets()
  {
    return new Insets(GuiUtil.DEFAULT_SPACING);
  }
}
