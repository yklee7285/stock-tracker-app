package yklee7285.stocktrackerapp.util

import scalikejdbc.*
import yklee7285.stocktrackerapp.model.Stock

trait Database :
  private val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  private val dbURL = "jdbc:derby:myDB;create=true;";
  // Initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine")
  // Ad-hoc session provider on the REPL
  given AutoSession = AutoSession

object Database extends Database :
  def setupDB(): Unit =
    if (!hasDBInitialize)
      Stock.initializeTables()

  private def hasDBInitialize : Boolean =
    DB getTable "stockItems" match
      case Some(x) => true
      case None => false
