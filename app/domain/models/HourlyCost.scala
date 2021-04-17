package domain.models

import play.api.libs.json.{Json, OFormat}

case class HourlyCost(value: BigDecimal, currency: String) {
  def +(that: HourlyCost): HourlyCost = {
    if (this.currency != that.currency)
      throw new Exception("Cannot add different currencies together")
    HourlyCost(this.value + that.value, this.currency)
  }
}
object HourlyCost {

  implicit def costFormat: OFormat[HourlyCost] =
    Json.using[Json.WithDefaultValues].format[HourlyCost]
}
