package org.kelseymountain.dragon

import Time.InvalidTimeException

class Time(val hours: Int, val minutes: Int = 0, val seconds: Int = 0):

  // TODO: What about leap seconds?

  if hours < 0 || hours >= 24 then
    throw new InvalidTimeException("Invalid hour")
  if minutes < 0 || minutes >= 60 then
    throw new InvalidTimeException("Invalid minute")
  if seconds < 0 || seconds >= 60 then
    throw new InvalidTimeException("Invalid second")
end Time

object Time:
  class InvalidTimeException(message: String) extends Exception(message)
end Time
