package ifmg.juaniwk3.musicfinder

import com.google.gson.JsonParser

class Parser {
    fun parse(data: String): List<Music> {
        val json = JsonParser.parseString(data).asJsonObject.getAsJsonArray("results")

        return json.mapNotNull {
            val trackName = it.asJsonObject.get("trackName")?.asString
            val releaseDate = it.asJsonObject.get("releaseDate")?.asString
            val collectionName =
                it.asJsonObject.get("collectionName")?.asString

            if (trackName == null || releaseDate == null || collectionName == null) {
                null
            } else {
                Music(trackName, releaseDate, collectionName)
            }
        }
    }
}