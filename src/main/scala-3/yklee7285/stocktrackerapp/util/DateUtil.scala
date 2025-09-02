package yklee7285.stocktrackerapp.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

object DateUtil :
  private val datePattern = "dd.MM.yyyy"
  private val dateFormatter =  DateTimeFormatter.ofPattern(datePattern)

  extension (date: LocalDate)

    def asString: String =
      if (date == null)
        return null;
      return dateFormatter.format(date);

  extension (data : String)

    def parseLocalDate : Option[LocalDate] =
      try
        Option(LocalDate.parse(data, dateFormatter))
      catch
        case  e: DateTimeParseException => None

    def isValid : Boolean =
      data.parseLocalDate != None
