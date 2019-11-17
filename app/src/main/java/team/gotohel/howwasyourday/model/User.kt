package team.gotohel.howwasyourday.model

data class User(
    val id: Int,
    val email: String,
    val nickname: String,
    val user_type: Int,
    val status: Int,
    val created_at: String
)