package yklee7285.stocktrackerapp.view

import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import scalafx.Includes.*
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import yklee7285.stocktrackerapp.MainApp
import yklee7285.stocktrackerapp.model.Stock
import yklee7285.stocktrackerapp.util.DateUtil.*

@FXML
class StockOverviewController:
  @FXML
  private var stockTable: TableView[Stock] = null
  @FXML
  private var nameColumn:TableColumn[Stock, String] = null
  @FXML
  private var quantityColumn: TableColumn[Stock, Integer] = null
  @FXML
  private var nameLabel: Label = null
  @FXML
  private var quantityLabel: Label = null
  @FXML
  private var buyPriceLabel: Label = null
  @FXML
  private var sellPriceLabel: Label = null
  @FXML
  private var receivedDateLabel: Label = null
  @FXML
  private var expectedProfitLabel: Label = null
  @FXML
  private var noteLabel: Label = null

  //Initialize table to load contents
  def initialize(): Unit =
    stockTable.items = MainApp.stockList
    // initialize column cell values
    nameColumn.cellValueFactory = {
      _.value.name
    }
    quantityColumn.cellValueFactory = {
      _.value.quantity
    }

    showStockDetails(None)

    // Add listener to update stock details when stock is selected
    stockTable.selectionModel().selectedItem.onChange(
      (_, _, newValue) => showStockDetails(Option(newValue))
    )

  // Function to show stock details
  private def showStockDetails(stock: Option[Stock]): Unit =
    stock match
      case Some(stock) =>
        // Fill labels with details from stock object
        nameLabel.text <== stock.name
        quantityLabel.text <== stock.quantity.delegate.asString
        buyPriceLabel.text <== stock.buyPrice.delegate.asString
        sellPriceLabel.text <== stock.sellPrice.delegate.asString
        receivedDateLabel.text <== Bindings.createStringBinding(
          () => {
            stock.date.value.asString
          }, stock.date
        )
        expectedProfitLabel.text <== Bindings.createStringBinding(
          () => f"${stock.calculateExpectedProfit}%.2f",
          stock.buyPrice, stock.sellPrice, stock.quantity
        )
        noteLabel.text <== stock.note

      case None =>
        // Unbind text
        nameLabel.text.unbind()
        quantityLabel.text.unbind()
        buyPriceLabel.text.unbind()
        sellPriceLabel.text.unbind()
        receivedDateLabel.text.unbind()
        expectedProfitLabel.text.unbind()
        noteLabel.text.unbind()

        // No text shown when Stock is null
        nameLabel.text = ""
        quantityLabel.text = ""
        buyPriceLabel.text = ""
        sellPriceLabel.text = ""
        receivedDateLabel.text = ""
        expectedProfitLabel.text = ""
        noteLabel.text = ""

  @FXML
  // Handle add button
  def handleAdd(action: ActionEvent): Unit =
    val stock = new Stock("", 1)
    val confirmClicked = MainApp.showStockEditDialog(stock);
    if (confirmClicked) then {
      MainApp.stockList += stock
    }

  @FXML
  // Handle delete button
  def handleDelete(action: ActionEvent): Unit =
    val selectedIndex = stockTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) then
      stockTable.items().remove(selectedIndex)
    else
      // Nothing selected.
      val alert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Item Selected"
        contentText = "Please select an item in the table."
      .showAndWait()

  @FXML
  // Handle edit button
  def handleEdit(action: ActionEvent): Unit =
    val selectedStock = stockTable.selectionModel().selectedItem.value
    if (selectedStock != null) then
      val confirmClicked = MainApp.showStockEditDialog(selectedStock)

      if (confirmClicked) then {
        showStockDetails(Some(selectedStock))
      }
    else
      // Show alert popup if no item selected
      val alert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection Made"
        headerText = "No Item Selected"
        contentText = "Please select an item in the table."
      .showAndWait()

  @FXML
  def handleSale(action: ActionEvent): Unit =
    val selectedIndex = stockTable.selectionModel().selectedIndex.value

    val selectedStock = stockTable.selectionModel().selectedItem.value
    if (selectedIndex >= 0) then
      stockTable.items().remove(selectedIndex)
      MainApp.soldList += selectedStock
      val today = java.time.LocalDate.now()
      selectedStock.date.value = today
      // selectedStock.sell()
      val alert = new Alert(AlertType.Information):
        initOwner(MainApp.stage)
        title = "Sale Confirmed"
        headerText = s"${selectedStock.name.value} Sold"
        contentText = "Good Job on Closing Sale!"
      alert.showAndWait()
    else
      // Show alert popup if no stock selected
      val alert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection Made"
        headerText = "No Item Selected"
        contentText = "Please select an item in the table."
      .showAndWait()


  @FXML
  def handleSellHistory(action: ActionEvent): Unit =
    MainApp.showSoldOverview()

