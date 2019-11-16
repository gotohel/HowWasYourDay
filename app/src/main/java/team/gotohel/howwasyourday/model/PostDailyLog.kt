package team.gotohel.howwasyourday.model

data class PostDailyLog(
    val user_id: Int,
    val text_log: String,
    val is_sharable: Boolean
)