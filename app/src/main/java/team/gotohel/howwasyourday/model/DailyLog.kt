package team.gotohel.howwasyourday.model

data class DailyLog(
    val user_id: Int,
    val text_log: String,
    val created_at: String
)