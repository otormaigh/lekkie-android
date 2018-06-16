package ie.pennylabs.lekkie.data.room

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RoomConverterTest {
  @Test
  fun testDateFromJson() {
    assertThat(RoomConverter.fromJson("14/06/2018 14:00"))
      .isEqualTo(1528981200000)
  }
}