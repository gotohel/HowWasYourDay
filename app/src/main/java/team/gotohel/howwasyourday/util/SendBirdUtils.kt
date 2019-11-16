package team.gotohel.howwasyourday.util

import android.content.Context
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import team.gotohel.howwasyourday.MyPreference


object SendBirdUtils {

    fun registerPushTokenForCurrentUser(context: Context, handler: SendBird.RegisterPushTokenWithStatusHandler?) {
        // FIXME 파이어베이스 설정하고 아래 주석 풀기..
//        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), handler)
    }

    fun unregisterPushTokenForCurrentUser(context: Context, handler: SendBird.UnregisterPushTokenHandler?) {
        // FIXME 파이어베이스 설정하고 아래 주석 풀기..
//        SendBird.unregisterPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), handler)
    }

    interface ConnectionManagementHandler {
        /**
         * A callback for when connected or reconnected to refresh.
         *
         * @param reconnect Set false if connected, true if reconnected.
         */
        fun onConnected(reconnect: Boolean)
    }

    fun addConnectionManagementHandler(handlerId: String, handler: ConnectionManagementHandler?) {
        SendBird.addConnectionHandler(handlerId, object : SendBird.ConnectionHandler {
            override fun onReconnectStarted() {}

            override fun onReconnectSucceeded() {
                if (handler != null) {
                    handler!!.onConnected(true)
                }
            }

            override fun onReconnectFailed() {}
        })

        if (SendBird.getConnectionState() == SendBird.ConnectionState.OPEN) {
            if (handler != null) {
                handler!!.onConnected(false)
            }
        } else if (SendBird.getConnectionState() == SendBird.ConnectionState.CLOSED) { // push notification or system kill
            val userId = MyPreference.savedUserId
            SendBird.connect(userId, SendBird.ConnectHandler { user, e ->
                if (e != null) {
                    return@ConnectHandler
                }

                if (handler != null) {
                    handler!!.onConnected(false)
                }
            })
        }
    }

    fun removeConnectionManagementHandler(handlerId: String) {
        SendBird.removeConnectionHandler(handlerId)
    }
}