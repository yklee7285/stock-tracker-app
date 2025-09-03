package yklee7285.stocktrackerapp.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import scalafx.stage.Stage

class AboutController():
  var dialogStage: Stage = null
  var closeClicked: Boolean = false

  @FXML
  def handleClose(action: ActionEvent) : Unit = {
    closeClicked = true
    dialogStage.close()
  }