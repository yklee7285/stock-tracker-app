package yklee7285.stocktrackerapp.view

import javafx.fxml.FXML
import javafx.scene.control.TextField
import scalafx.event.ActionEvent
import scalafx.stage.Stage
import scalafx.Includes.*
import scalafx.scene.control.Alert
import yklee7285.stocktrackerapp.model.Stock
import yklee7285.stocktrackerapp.util.DateUtil.*

@FXML
class StockEditDialogController:
    @FXML
    private var nameField: TextField = null
    @FXML
    private var quantityField: TextField = null
    @FXML
    private var buyPriceField: TextField = null
    @FXML
    private var sellPriceField: TextField = null
    @FXML
    private var receivedDateField: TextField = null
    @FXML
    private var noteField: TextField = null

    var dialogStage: Stage = null
    private var __stock: Stock = null
    var confirmClicked = false

    def stock = __stock

    def stock_=(x: Stock): Unit =
      __stock = x

      nameField.text = __stock.name.value
      quantityField.text = __stock.quantity.value.toString
      buyPriceField.text = __stock.buyPrice.value.toString
      sellPriceField.text = __stock.sellPrice.value.toString
      receivedDateField.text = __stock.date.value.asString
      receivedDateField.setPromptText("dd.mm.yyyy")
      noteField.text = __stock.note.value

    // Handle confirm button
    def handleConfirm(action: ActionEvent): Unit =
      if (isInputValid())
        __stock.name <== nameField.text
        __stock.quantity.value = quantityField.getText().toInt
        __stock.buyPrice.value = buyPriceField.getText().toFloat
        __stock.sellPrice.value = sellPriceField.getText().toFloat
        __stock.date.value = receivedDateField.text.value.parseLocalDate.getOrElse(null)
        __stock.note <== noteField.text

        confirmClicked = true
        dialogStage.close()

    // Handle cancel button
    def handleCancel(): Unit =
      dialogStage.close()

    // Helper function for error handling
    def nullChecking(x: String): Boolean = x == null || x.trim.isEmpty

    def isInputValid(): Boolean =
      var errorMessage = ""

      if nullChecking(nameField.text.value) then
        errorMessage += "Name Field Cannot Be Empty.\n"

      if (nullChecking(quantityField.text.value)) then
        errorMessage += "Quantity Field Cannot Be Empty.\n"
      else
        try
          quantityField.getText().toInt
        catch
          case e: NumberFormatException =>
            errorMessage += "Quantity must be an integer.\n"

      if (nullChecking(buyPriceField.text.value)) then
        errorMessage += "Buy Price Field Cannot Be Empty.\n"
      else
        try
          buyPriceField.getText().toFloat
        catch
          case e: NumberFormatException =>
            errorMessage += "Quantity must be a float.\n"
      if (nullChecking(sellPriceField.text.value)) then
        errorMessage += "Sell Price Field Cannot Be Empty.\n"
      else
        try
          sellPriceField.getText().toFloat
        catch
          case e: NumberFormatException =>
            errorMessage += "Sell Price must be a float.\n"
      if nullChecking(receivedDateField.text.value) then
        errorMessage += "Received Date Field Cannot Be Empty.\n"
      else if (!receivedDateField.text.value.isValid) then
        errorMessage += "Invalid Received Date Field. Use the format dd.mm.yyyy!\n";
      if nullChecking(noteField.text.value) then
        noteField.text = "No Note Added"

      if errorMessage.isEmpty then
        true
      else
        // Show the error message.
        val alert = new Alert(Alert.AlertType.Error):
          initOwner(dialogStage)
          title = "Invalid Fields"
          headerText = "Please correct invalid fields"
          contentText = errorMessage
        .showAndWait()
        false
      end if