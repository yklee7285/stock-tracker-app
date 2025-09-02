package yklee7285.stocktrackerapp.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.Includes.*

import java.time.LocalDate

class Stock(val _name: String, val _quantity: Integer):
  def this() =this(null, null)

  var id = ObjectProperty[Int](-1)
  var name : StringProperty =  StringProperty(_name)
  var quantity : ObjectProperty[Integer] = ObjectProperty[Integer](1)
  var buyPrice : ObjectProperty[Float] = ObjectProperty[Float](1)
  var sellPrice: ObjectProperty[Float] = ObjectProperty[Float](1)
  var date: ObjectProperty[LocalDate] = ObjectProperty[LocalDate](LocalDate.now())
  var note : StringProperty =  StringProperty("No Note Added")

  def calculateExpectedProfit: Float = {
    (sellPrice.value - buyPrice.value) * quantity.value
  }


