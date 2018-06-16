package ie.pennylabs.lekkie.data.model

data class Point(
  private val coordinates: String) {
  val longitude: Double
    get() = coordinates.split(",")[1].toDouble()
  val latitude: Double
    get() = coordinates.split(",")[0].toDouble()
}