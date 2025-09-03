package yklee7285.stocktrackerapp

import javafx.fxml.FXMLLoader
import scalafx.stage.Stage
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.stage.Modality
import yklee7285.stocktrackerapp.model.Stock
import yklee7285.stocktrackerapp.view.{AboutController, SoldOverviewController, StockEditDialogController, StockOverviewController}

import java.net.URL

object MainApp extends JFXApp3:

  private var rootPane: Option[javafx.scene.layout.BorderPane] = None
  private var stockOverviewController: Option[StockOverviewController] = None
  private var soldOverviewController: Option[SoldOverviewController] = None
  private var stockEditDialogController: Option[StockEditDialogController] = None
  private var aboutController: Option[AboutController] = None

  private val cssResource: URL = getClass.getResource("view/Stylesheet.css") // Get CSS Resource

  val stockList = new ObservableBuffer[Stock]() // Buffer to store stock items
  stockList += new Stock("Karina Photocard", 1)
  val soldList = new ObservableBuffer[Stock]() // Buffer to store sold items


  override def start(): Unit = {
    // Get and store RootLayout FXML resource path
    val rootLayoutResource: URL = getClass.getResource("view/RootLayout.fxml")
    // Initialize loader and load the UI from RootLayout.fxml
    val loader = new FXMLLoader(rootLayoutResource)
    loader.load()
    // Get the root component border pane from the loaded FXML and store it in rootPane
    rootPane = Option(loader.getRoot[javafx.scene.layout.BorderPane])
    stage = new PrimaryStage():
      title = "Stock Inventory App" // Set window title
      icons += new Image(getClass.getResource("/images/logo.png").toExternalForm)
      scene = new Scene():
        stylesheets = Seq(cssResource.toExternalForm)
        root = rootPane.get // Set border pane from RootLayout.fxml as root of the scene
    showStockOverview() // Call function to show welcome page
  }

  // Function to show stock overview page in RootPane
  def showStockOverview(): Unit =
    val stockOverviewResource: URL = getClass.getResource("view/stockOverview.fxml")
    val loader = new FXMLLoader(stockOverviewResource)
    val stockOverview = loader.load[javafx.scene.layout.AnchorPane]()

    val controller = loader.getController[StockOverviewController]()
    stockOverviewController = Option(controller)

    rootPane.foreach(_.setCenter(stockOverview))

  // Function to show sold overview page in RootPane
  def showSoldOverview(): Unit =
    val soldOverviewResource: URL = getClass.getResource("view/soldOverview.fxml")
    val loader = new FXMLLoader(soldOverviewResource)
    val soldOverview = loader.load[javafx.scene.layout.AnchorPane]()

    val controller = loader.getController[SoldOverviewController]()
    soldOverviewController = Option(controller)

    rootPane.foreach(_.setCenter(soldOverview))

  // Function to show stock edit popup dialog
  def showStockEditDialog(stock: Stock): Boolean =
    val stockEditDialogResource = getClass.getResource("view/StockEditDialog.fxml")
    val loader = new FXMLLoader(stockEditDialogResource)
    loader.load();
    val pane = loader.getRoot[javafx.scene.layout.AnchorPane]()
    val controller = loader.getController[StockEditDialogController]()

    val dialog = new Stage():
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene:
        title = "Stock Edit Dialog"
        stylesheets = Seq(cssResource.toExternalForm)
        root = pane

    controller.dialogStage = dialog
    controller.stock = stock
    dialog.showAndWait()

    controller.confirmClicked

  def showAbout(): Boolean =
    val aboutResource = getClass.getResource("view/About.fxml")
    val loader = new FXMLLoader(aboutResource)
    loader.load();
    val pane = loader.getRoot[javafx.scene.layout.AnchorPane]()
    val controller = loader.getController[AboutController]()

    val dialog = new Stage():
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene:
        title = "About"
        stylesheets = Seq(cssResource.toExternalForm)
        root = pane

    controller.dialogStage = dialog
    dialog.showAndWait()
    controller.closeClicked
