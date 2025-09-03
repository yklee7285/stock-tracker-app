package yklee7285.stocktrackerapp.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.Includes.*
import scalikejdbc.*
import yklee7285.stocktrackerapp.util.Database
import yklee7285.stocktrackerapp.util.DateUtil.*

import java.time.LocalDate
import scala.util.Try

class Stock(val _name: String, val _quantity: Integer) extends Database:
  def this() = this(null, null)

  var id = ObjectProperty[Int](-1)
  var name : StringProperty = StringProperty(_name)
  var quantity : ObjectProperty[Integer] = ObjectProperty[Integer](1)
  var buyPrice : ObjectProperty[Float] = ObjectProperty[Float](1)
  var sellPrice: ObjectProperty[Float] = ObjectProperty[Float](1)
  var date: ObjectProperty[LocalDate] = ObjectProperty[LocalDate](LocalDate.now())
  var note : StringProperty = StringProperty("No Note Added")

  def calculateExpectedProfit: Float = {
    (sellPrice.value - buyPrice.value) * quantity.value
  }

  // Save stock to stockItems table
  def save(): Try[Int] =
    if (!isExist) then
      Try(DB autoCommit { implicit session =>
        val newId = sql"""
          insert into stockItems (name, quantity, buyPrice, sellPrice, date, note)
          values (${name.value}, ${quantity.value}, ${buyPrice.value},
                  ${sellPrice.value}, ${date.value.asString}, ${note.value})
        """.updateAndReturnGeneratedKey.apply().toInt
        id.value = newId
        newId
      })
    else
      Try(DB autoCommit { implicit session =>
        sql"""
          update stockItems
          set
            name = ${name.value},
            quantity = ${quantity.value},
            buyPrice = ${buyPrice.value},
            sellPrice = ${sellPrice.value},
            date = ${date.value.asString},
            note = ${note.value}
          where id = ${id.value}
        """.update.apply()
      })

  // Move stock from stockItems to soldItems table
  def sell(): Try[Int] =
    if (isExist) then
      Try(DB autoCommit { implicit session =>
        // Insert into soldItems
        val newId = sql"""
          insert into soldItems (name, quantity, buyPrice, sellPrice, date, note)
          values (${name.value}, ${quantity.value}, ${buyPrice.value},
                  ${sellPrice.value}, ${date.value.asString}, ${note.value})
        """.updateAndReturnGeneratedKey.apply().toInt

        // Remove from stockItems
        sql"""
          delete from stockItems where id = ${id.value}
        """.update.apply()

        // Update the object's ID to the new one from soldItems table
        id.value = newId
        newId
      })
    else
      throw new Exception("Stock item does not exist in stockItems table")

  // Delete stock from stockItems table
  def delete(): Try[Int] =
    if (isExist) then
      Try(DB autoCommit { implicit session =>
        sql"""
          delete from stockItems where id = ${id.value}
        """.update.apply()
      })
    else
      throw new Exception("Stock not found in database")

  // Delete from soldItems table
  def deleteFromSold(): Try[Int] =
    if (isExistInSold) then
      Try(DB autoCommit { implicit session =>
        sql"""
          delete from soldItems where id = ${id.value}
        """.update.apply()
      })
    else
      throw new Exception("Stock not found in sold items")

  // Save sold item to soldItems table
  def saveSoldItem(): Try[Int] =
    if (isExistInSold) then
      Try(DB autoCommit { implicit session =>
        sql"""
          update soldItems
          set
            name = ${name.value},
            quantity = ${quantity.value},
            buyPrice = ${buyPrice.value},
            sellPrice = ${sellPrice.value},
            date = ${date.value.asString},
            note = ${note.value}
          where id = ${id.value}
        """.update.apply()
      })
    else
      throw new Exception("Stock not found in sold items")

  // Check if stock exists in stockItems table
  def isExist: Boolean =
    if (id.value == -1) then false
    else
      DB readOnly { implicit session =>
        sql"""
          select * from stockItems where id = ${id.value}
        """.map(rs => rs.string("name")).single.apply()
      } match
        case Some(x) => true
        case None => false

  // Check if stock exists in soldItems table
  def isExistInSold: Boolean =
    if (id.value == -1) then false
    else
      DB readOnly { implicit session =>
        sql"""
          select * from soldItems where id = ${id.value}
        """.map(rs => rs.string("name")).single.apply()
      } match
        case Some(x) => true
        case None => false

object Stock extends Database:
  def apply(
             nameS: String,
             quantityI: Int,
             buyPriceF: Float,
             sellPriceF: Float,
             dateS: String,
             noteS: String,
             idI: Int = -1
           ): Stock =
    new Stock(nameS, quantityI):
      buyPrice.value = buyPriceF
      sellPrice.value = sellPriceF
      date.value = dateS.parseLocalDate.getOrElse(LocalDate.now())
      note.value = noteS
      id.value = idI

  // Initialize both tables
  def initializeTables(): Unit =
    DB autoCommit { implicit session =>
      // Create stockItems table
      sql"""
        create table stockItems (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          name varchar(200),
          quantity int,
          buyPrice float,
          sellPrice float,
          date varchar(64),
          note varchar(500)
        )
      """.execute.apply()

      // Create soldItems table
      sql"""
        create table soldItems (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          name varchar(200),
          quantity int,
          buyPrice float,
          sellPrice float,
          date varchar(64),
          note varchar(500)
        )
      """.execute.apply()
    }

  // Get all stocks from stockItems table
  def getAllStocks: List[Stock] =
    DB readOnly { implicit session =>
      sql"select * from stockItems".map(rs =>
        Stock(
          rs.string("name"),
          rs.int("quantity"),
          rs.float("buyPrice"),
          rs.float("sellPrice"),
          rs.string("date"),
          rs.string("note"),
          rs.int("id")
        )
      ).list.apply()
    }

  // Get all sold items from soldItems table
  def getAllSoldItems: List[Stock] =
    DB readOnly { implicit session =>
      sql"select * from soldItems".map(rs =>
        Stock(
          rs.string("name"),
          rs.int("quantity"),
          rs.float("buyPrice"),
          rs.float("sellPrice"),
          rs.string("date"),
          rs.string("note"),
          rs.int("id")
        )
      ).list.apply()
    }