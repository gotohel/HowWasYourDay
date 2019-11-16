package team.gotohel.howwasyourday.util

import android.util.Log
import team.gotohel.howwasyourday.MyApplication
import team.gotohel.howwasyourday.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateTimeHelper {
    internal var TAG = "DateTimeHelper"

    fun parseDateTime(dateString: String?): Date? {
        if (dateString == null) return null

        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA)

        try {
            val result = fmt.parse(dateString)
            Log.d(TAG, "parseDateTime: " + dateString + " -> " + result.toString())
            return result
        } catch (e: ParseException) {
            Log.e(TAG, "Could not parse datetime: $dateString")
            return null
        }
    }

    private fun getCurrentZoneTime(timestamp: Long): Date {
        val cal = Calendar.getInstance(TimeZone.getTimeZone(SimpleDateFormat("z").format(Date())))
        cal.timeInMillis = timestamp
        return cal.time
    }

    private fun getFormatDate(targetDate: Date, formatter: SimpleDateFormat): String {
        return formatter.format(targetDate)
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     * 2015-02-17 16:34:49
     */
    fun getFormattedTextAsDateAndTime(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
    }


    /**
     * a hh:mm
     * 오후 04:34
     */
    fun getHourMinute(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("a hh:mm"))
    }

    /**
     * E, a hh:mm
     * 화, 오후 04:34
     */
    private fun getDayOfWeekTime(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("E, a hh:mm"))
    }

    /**
     * M월 d일 a hh:mm
     * 2월 17일 오후 04:34
     */
    private fun getMonthDayTime(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("M월 d일 a hh:mm"))
    }

    /**
     * yyyy년 M월 d일
     * 2015년 2월 17일
     */
    fun getYearMonthDay(timestamp: Long?): String {
        return if (timestamp == null || timestamp == 0L) {
            "-"
        } else {
            return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("yyyy년 M월 d일"))
        }
    }

    /**
     * yyyy-MM
     * 2015-02
     */
    fun getYearMonth(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("yyyy-MM"))
    }

    /**
     * yyyy년 M월
     * 2015년 2월
     */
    fun getYearMonth2(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("yyyy년 M월"))
    }

    /**
     * yyyy-MM-dd
     * 2015-02-17
     */
    fun getYearMonthDaySimple(timestamp: Long?): String {
        return if (timestamp == null || timestamp == 0L) {
            "-"
        } else {
            getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("yyyy-MM-dd"))
        }
    }

    /**
     * yyyy. MM. dd
     * 2015. 02. 17
     */
    fun getYearMonthDaySimple2(timestamp: Long?): String {
        return if (timestamp == null || timestamp == 0L) {
            "-"
        } else {
            getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("yyyy. MM. dd"))
        }
    }

    /**
     * M월 d일
     * 2월 17일
     */
    fun getMonthDay(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("M월 d일"))
    }

    /**
     * MM/dd
     * 02/17
     */
    fun getMonthDayShort(timestamp: Long): String {
        return getFormatDate(getCurrentZoneTime(timestamp), SimpleDateFormat("MM/dd"))
    }

    /**
     * yyyy년 MM월 dd일 a hh:mm
     * 2015년 2월 17일 오후 4:34
     */
    fun getDefaultDatetime(timestamp: Long): String {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(getCurrentZoneTime(timestamp))
    }

    /**
     * 방금 전 , 1분 전, 2시간 전, 2015년 2월 17일 오후 4:34
     */
    fun getEditingTime(editedTimeMillis: Long): String {

        val context = MyApplication.context

        if (editedTimeMillis == 0L) {
            return MyApplication.context.getString(R.string.unknown)
        }

        val currentTimeMillis = System.currentTimeMillis()

        val editedTimeSec = editedTimeMillis.toDouble() / 1000
        val currentTimeSec = currentTimeMillis.toDouble() / 1000

        val passedTimeSec = currentTimeSec - editedTimeSec

        val minInSec = 60
        val hourInSec = minInSec * 60
        val dayInSec = hourInSec * 24

        var result = ""

        if (passedTimeSec < 1) {
            result = context!!.getString(R.string.just_before)
        } else if (passedTimeSec < minInSec) {
            result = (passedTimeSec / 1).toInt().toString() + context!!.getString(R.string.seconds_ago)
        } else if (passedTimeSec < hourInSec) {
            result = (passedTimeSec / minInSec).toInt().toString() + context!!.getString(R.string.minutes_ago)
        } else if (passedTimeSec < dayInSec) {
            result = (passedTimeSec / hourInSec).toInt().toString() + context!!.getString(R.string.hours_ago)
        } else {
            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = currentTimeMillis

            val editedTimeCalendar = Calendar.getInstance()
            editedTimeCalendar.timeInMillis = editedTimeMillis


            if (currentCalendar.get(Calendar.YEAR) == editedTimeCalendar.get(Calendar.YEAR)) { // 년도가 같은 경우.
                val nDiffDays = currentCalendar.get(Calendar.DAY_OF_YEAR) - editedTimeCalendar.get(Calendar.DAY_OF_YEAR)

                if (nDiffDays <= 1) { // 딱 하루 차이가 나는 경우.
                    result = context!!.getString(R.string.yesterday) + ", " + getHourMinute(editedTimeMillis)
                } else if (nDiffDays < 4) { // 4 일 보다 작게 차이나는 경우.
                    result = getDayOfWeekTime(editedTimeMillis)
                } else {
                    result = getMonthDayTime(editedTimeMillis)
                }
            } else {
                result = getDefaultDatetime(editedTimeMillis)
            }
        }

        return result
    }

    /**
     * 방금 전 , 1분 전, 2시간 전, 2015년 2월 17일
     */
    fun getEditingDay(editedTimeMillis: Long?): String {

//        Log.d("날짜", "getFormattedTextAsDateAndTime : "+getFormattedTextAsDateAndTime(editedTimeMillis));
//        Log.d("날짜", "getDefaultDatetime : "+getDefaultDatetime(editedTimeMillis));
//        Log.d("날짜", "getHourMinute : "+getHourMinute(editedTimeMillis));
//        Log.d("날짜", "getDayOfWeekTime : "+getDayOfWeekTime(editedTimeMillis));
//        Log.d("날짜", "getMonthDayTime : "+getMonthDayTime(editedTimeMillis));
//        Log.d("날짜", "getPassedTimeGapOfPasswordChanged : "+getPassedTimeGapOfPasswordChanged(editedTimeMillis));

        val context = MyApplication.context

        if (editedTimeMillis == null || editedTimeMillis == 0L) {
            return context!!.getString(R.string.unknown)
        }

        val currentTimeMillis = System.currentTimeMillis()

        val editedTimeSec = editedTimeMillis!!.toDouble() / 1000
        val currentTimeSec = currentTimeMillis.toDouble() / 1000

        val passedTimeSec = currentTimeSec - editedTimeSec

        val minInSec = 60
        val hourInSec = minInSec * 60
        val dayInSec = hourInSec * 24

        var result = ""

        if (passedTimeSec < 1) {
            result = context!!.getString(R.string.just_before)
        } else if (passedTimeSec < minInSec) {
            result = (passedTimeSec / 1).toInt().toString() + context!!.getString(R.string.seconds_ago)
        } else if (passedTimeSec < hourInSec) {
            result = (passedTimeSec / minInSec).toInt().toString() + context!!.getString(R.string.minutes_ago)
        } else if (passedTimeSec < dayInSec) {
            result = (passedTimeSec / hourInSec).toInt().toString() + context!!.getString(R.string.hours_ago)
        } else {
            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = currentTimeMillis

            val editedTimeCalendar = Calendar.getInstance()
            editedTimeCalendar.timeInMillis = editedTimeMillis

            if (currentCalendar.get(Calendar.YEAR) == editedTimeCalendar.get(Calendar.YEAR)) { // 년도가 같은 경우.
                val nDiffDays = currentCalendar.get(Calendar.DAY_OF_YEAR) - editedTimeCalendar.get(Calendar.DAY_OF_YEAR)

                if (nDiffDays <= 1) { // 딱 하루 차이가 나는 경우.
                    result = context!!.getString(R.string.yesterday) + ", " + getHourMinute(editedTimeMillis)
                } else if (nDiffDays < 4) { // 4 일 보다 작게 차이나는 경우.
                    result = getDayOfWeekTime(editedTimeMillis)
                } else {
                    result = getMonthDay(editedTimeMillis)
                }
            } else {
                result = getYearMonthDay(editedTimeMillis)
            }
        }

        return result
    }
}
