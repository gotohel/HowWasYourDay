package team.gotohel.howwasyourday.util

import android.content.Context
import com.sendbird.android.SendBird


object SendBirdUtils {

    fun registerPushTokenForCurrentUser(context: Context, handler: SendBird.RegisterPushTokenWithStatusHandler?) {
        // FIXME 파이어베이스 설정하고 아래 주석 풀기..
//        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), handler)
    }

    fun unregisterPushTokenForCurrentUser(context: Context, handler: SendBird.UnregisterPushTokenHandler?) {
        // FIXME 파이어베이스 설정하고 아래 주석 풀기..
//        SendBird.unregisterPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), handler)
    }
}