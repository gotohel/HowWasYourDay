package team.gotohel.howwasyourday.model

data class DailyLog(
    val id: Int,
    val user_id: Int,
    val text_log: String,
    val created_at: String
)