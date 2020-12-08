package by.profs.rowgame.data.items
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RowerExtraInfo(val achievements: String?, val otherInfo: String?, val cost: Int?)