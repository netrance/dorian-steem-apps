package lee.dorian.dorian_ktx

import lee.dorian.steem_domain.ext.toJsonObject
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtTest {

    @Test
    fun toJsonObject_case1() {
        val jsonString = "{\"id\": 123, \"name\": \"Dorian Lee\"}"
        val jsonObject = jsonString.toJsonObject()

        // Checks if the JSON object has the keys and values.
        assertTrue(jsonObject.has("id"))
        assertTrue(jsonObject.has("name"))
        assertTrue(jsonObject.get("id").asInt == 123)
        assertTrue(jsonObject.get("name").asString == "Dorian Lee")
    }

    @Test
    fun toJsonObject_case2() {
        val jsonString = "{}"
        val jsonObject = jsonString.toJsonObject()

        // Checks if the JSON object has the keys and values.
        assertTrue(jsonObject.isJsonObject)
        assertTrue(jsonObject.keySet().size == 0)
    }

    @Test
    fun toJsonObject_case3() {
        val jsonString = ""
        val jsonObject = jsonString.toJsonObject()

        // Checks if the JSON object has the keys and values.
        assertTrue(jsonObject.isJsonObject)
        assertTrue(jsonObject.keySet().size == 0)
    }

}