package lee.dorian.steem_domain.ext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtTest {

    // Test case 1: Trying to convert UTC time of which the format is "yyyy-MM-dd'T'HH:mm:ss"
    @Test
    fun fromUtcTimeToLocalTime_case1() {
        val sampleUtcTime = "2022-10-04T00:00:00"
        val convertedLocalTime = sampleUtcTime.fromUtcTimeToLocalTime()
        assertEquals("2022-10-04 09:00:00", convertedLocalTime)
    }

    // Test case 2: Trying to convert UTC time of which the format is "yyyy-MM-dd HH:mm:ss"
    @Test
    fun fromUtcTimeToLocalTime_case2() {
        val sampleUtcTime = "2022-10-04 10:00:00"
        val convertedLocalTime = sampleUtcTime.fromUtcTimeToLocalTime()
        assertEquals("2022-10-04 19:00:00", convertedLocalTime)
    }

    // Test case 3: Trying to convert the string that is not a time
    @Test
    fun fromUtcTimeToLocalTime_case3() {
        val sampleUtcTime = "abcdefg"
        val convertedLocalTime = sampleUtcTime.fromUtcTimeToLocalTime()
        assertTrue(convertedLocalTime.isEmpty())
    }

}