package team.gotohel.howwasyourday.util

import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException

object ErrorHandler {


//    fun getAPIError(throwable: Throwable?): APIError? {
//        return if (throwable != null && throwable is HttpException) {
//            try {
//                val responseBody = throwable.response().errorBody()
//                val converter = APIClient(UserStatus.empty())
//                        .retrofit
//                        .responseBodyConverter<APIError>(APIError::class.java, arrayOfNulls(0))
//                converter.convert(responseBody!!)
//            } catch (e: Exception) {
//                null
//            }
//
//        } else {
//            null
//        }
//    }
}
