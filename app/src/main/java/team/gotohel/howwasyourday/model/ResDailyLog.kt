package team.gotohel.howwasyourday.model

data class ResDailyLog(
    val error: String?,
    val user: User?,
    val daily_log: DailyLog?
)