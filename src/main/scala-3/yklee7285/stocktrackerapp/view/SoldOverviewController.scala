package yklee7285.stocktrackerapp.view

import scalafx.beans.property.ObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import yklee7285.stocktrackerapp.MainApp
import yklee7285.stocktrackerapp.model.Stock
import scalafx.Includes.*
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scala.util.{Failure, Success}

import java.time.LocalDate

@FXML
class SoldOverviewController:
  @FXML
  private var soldTable: TableView[Stock] = null
  @FXML
  private var nameColumn: TableColumn[Stock, String] = null
  @FXML
  private var quantityColumn: TableColumn[Stock, Integer] = null
  @FXML
  private var buyPriceColumn: TableColumn[Stock, Float] = null
  @FXML
  private var sellPriceColumn: TableColumn[Stock, Float] = null
  @FXML
  private var profitColumn: TableColumn[Stock, Float] = null
  @FXML
  private var soldDateColumn: TableColumn[Stock, LocalDate] = null
  @FXML
  private var noteColumn: TableColumn[Stock, String] = null
  @FXML
  private var totalProductsLabel: Label = null
  @FXML
  private var totalExpenseLabel: Label = null
  @FXML
  private var totalIncomeLabel: Label = null
  @FXML
  private var totalProfitLabel: Label = null

  // Initialize table to display contents
  def initialize(): Unit =
    soldTable.items = MainApp.soldList
    // initialize columns's cell values
    nameColumn.cellValueFactory = {_.value.name}
    quantityColumn.cellValueFactory = {_.value.quantity}
    soldDateColumn.cellValueFactory = {_.value.date}
    buyPriceColumn.cellValueFactory = {_.value.buyPrice}
    sellPriceColumn.cellValueFactory = {_.value.sellPrice}
    profitColumn.cellValueFactory = { data =>
      val stock = data.value
      ObjectProperty[Float](
        (stock.sellPrice.value - stock.buyPrice.value) * stock.quantity.value
      )
    }
    noteColumn.cellValueFactory = {_.value.note}

    // Refresh profit column when values change
    MainApp.soldList.foreach { stock =>
      stock.buyPrice.onChange { (_, _, _) =>
        soldTable.refresh()
        updateSummary()
      }
      stock.sellPrice.onChange { (_, _, _) =>
        soldTable.refresh()
        updateSummary()
      }
      stock.quantity.onChange { (_, _, _) =>
        soldTable.refresh()
        updateSummary()
      }
    }
    // Compute totals whenever list changes
    MainApp.soldList.onChange { (_, _) =>
      updateSummary()
    }
    updateSummary()

  private def updateSummary(): Unit =
    val totalProducts = MainApp.soldList.map(_.quantity.value.toInt).sum
    val totalExpenses = MainApp.soldList.map(s => s.buyPrice.value * s.quantity.value).sum
    val totalIncome = MainApp.soldList.map(s => s.sellPrice.value * s.quantity.value).sum
    val totalProfit = totalIncome - totalExpenses

    totalProductsLabel.setText(totalProducts.toString)
    totalExpenseLabel.setText(f"RM$totalExpenses%.2f")
    totalIncomeLabel.setText(f"RM$totalIncome%.2f")
    totalProfitLabel.setText(f"RM$totalProfit%.2f")

  @FXML
  def handleReturn(): Unit =
    MainApp.showStockOverview()

  @FXML
  // Handle delete button
  def handleDelete(action: ActionEvent): Unit =
    val selectedIndex = soldTable.selectionModel().selectedIndex.value
    val selectedStock = soldTable.selectionModel().selectedItem.value

    if (selectedIndex >= 0) then
      selectedStock.deleteFromSold() match
        case Success(x) =>
          soldTable.items().remove(selectedIndex)
        case Failure(e) =>
          val alert = new Alert(Alert.AlertType.Warning):
            initOwner(MainApp.stage)
            title = "Failed to Delete"
            headerText = "Database Error"
            contentText = "Database problem failed to delete sold item"
          .showAndWait()
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
    val selectedStock = soldTable.selectionModel().selectedItem.value
    if (selectedStock != null) then
      val confirmClicked = MainApp.showStockEditDialog(selectedStock)
      if (confirmClicked) then
        // Update the sold item in database 
        selectedStock.save() match
          case Success(x) =>
            soldTable.refresh()
            updateSummary()
          case Failure(e) =>
            val alert = new Alert(Alert.AlertType.Warning):
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database problem failed to save changes"
            .showAndWait()
    else
      // Show alert popup if no item selected
      val alert = new Alert(Alert.AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection Made"
        headerText = "No Item Selected"
        contentText = "Please select an item in the table."
      .showAndWait()