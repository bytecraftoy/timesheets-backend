package models

case class BigDecimalWithPrecision private (number: BigDecimal)

object BigDecimalWithPrecision {
  private val setPrecision: Int = 8
  private val defaultContext    = new java.math.MathContext(setPrecision)

  def apply(number: BigDecimal): BigDecimalWithPrecision =
    new BigDecimalWithPrecision(
      scala.math.BigDecimal.apply(number.toString(), defaultContext)
    )
}
