package yklee7285.stocktrackerapp.view

import javafx.fxml.FXML
import yklee7285.stocktrackerapp.MainApp

@FXML
class RootLayoutController:
  @FXML
  // Handle Exit
  def handleExit(): Unit =
    MainApp.stage.close()

  @FXML
  // Handle Stock
  def handleStock(): Unit =
    MainApp.showStockOverview()

  @FXML
  // Handle History
  def handleHistory(): Unit =
    MainApp.showSoldOverview()

  @FXML
  // Handle About
  def handleAbout(): Unit =
    MainApp.showAbout()

