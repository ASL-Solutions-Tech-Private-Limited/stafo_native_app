package com.stafo.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Settings.Secure
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.stafo.app.screens.auth.LoginWithOTPActivity
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.orhanobut.hawk.Hawk
import com.stafo.app.R
import com.stafo.app.base.EndOfDaySyncWorker
import com.stafo.app.screens.ui.SplashActivity
import com.trackier.sdk.TrackierEvent
import com.trackier.sdk.TrackierSDK.trackEvent
import org.json.JSONObject
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import tech.developingdeveloper.toaster.Toaster
import java.io.File
import java.io.IOException
import java.io.StringReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.math.log10
import kotlin.math.pow


var isUserBlock: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
val isNotification: MutableLiveData<Boolean> = MutableLiveData(false)
val FOLDER_NAME = "Tourney11"
val APP_NAME = "Tourney11"
fun isValidEmail(inputEmailString: String): Boolean {
    return if (TextUtils.isEmpty(inputEmailString)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(inputEmailString).matches()
    }
}


fun isValidPhoneNumber(inputPhoneNumberString: CharSequence): Boolean {
    return inputPhoneNumberString.length in 10..14
}

fun isValidMobile(phone: String): Boolean {
    return if (!Pattern.matches("[a-zA-Z]+", phone)) {
        phone.length in 10..13
    } else false
}

fun isTablet(context: Context): Boolean {
    return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}


fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun hideSoftKeyboard(mContext: Activity?) {
    try {
        if (mContext == null) {
            return
        }
        if (mContext.currentFocus != null) {
            val inputMethodManager =
                mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mContext.currentFocus!!.windowToken, 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getAuthJson(
    user_id: String, name: String, country: String, state: String,
    session_key: String, hash: String, client_id: String, timestamp: String
): JSONObject {
    var jsonObject = JSONObject()
    try {
        jsonObject.put("user_id", user_id)
        jsonObject.put("name", name)
        jsonObject.put("country", country)
        jsonObject.put("state", state)
        jsonObject.put("session_key", session_key)
        jsonObject.put("hash", hash)
        jsonObject.put("client_id", client_id)
        jsonObject.put("timestamp", timestamp)
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
    return jsonObject
}

fun getFantasyAuthJson(
    user_id: String,
    name: String,
    country: String,
    state: String,
    token: String,
    merchant_id: String,
    session_key: String,
    hash: String,
    client_id: String,
    timestamp: String,
    username: String
): JSONObject {
    var jsonObject = JSONObject()
    try {
        jsonObject.put("user_id", user_id)
        jsonObject.put("name", name)
        jsonObject.put("country", country)
        jsonObject.put("state", state)
        jsonObject.put("session_key", session_key)
        jsonObject.put("hash", hash)
        jsonObject.put("client_id", client_id)
        jsonObject.put("timestamp", timestamp)
        jsonObject.put("token", token)
        jsonObject.put("username", username)
        jsonObject.put("merchant_id", merchant_id)
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
    /*{
      "token": "4yHyTnIlad5NkE9bio7rtJnlYMYiv7hTv4s",
      "user_id": "tourney11_1",
      "name": "Nikhil",
      "username": "Nikhil",
      "merchant_id": "blocmatrixexchange",
      "client_id": "blocmatrixexchange",
      "country": "India",
      "state": "Haryana",
      "hash": "d7df5573215049028d230bb6281e2499da29a316260a37dd6fec2ab6495da36a",
      "session_key": "4yHyTnIlad5NkE9bio7rtJnlYMYiv7hTv4s",
      "timestamp": 1674641149
    }

    {
        "token": "dDwdDtIved4PLYIcyEa7qPe2LvbVoYOnWqG",
        "user_id": "tourney11_14",
        "name": "9734957872",
        "username": "9734957872",
        "merchant_id": "blocmatrixexchange",
        "client_id": "blocmatrixexchange",
        "country": "India",
        "state": "Haryana",
        "hash": "a8827257dbbaa35a2beb4ab7b8fb3c915057296cac46e18c14a46f60478d8d4b",
        "session_key": "dDwdDtIved4PLYIcyEa7qPe2LvbVoYOnWqG",
        "timestamp": 1676981283
    }
    * */
    return jsonObject
}

fun currencyFormatter(num: String): String {
    return try {
        if (num.isBlank()) {
            "₹ 00.00"
        } else {
            val m = num.toDouble()
            val formatter = DecimalFormat("###,###,##0.00")
            "₹ " + formatter.format(m)
        }
    } catch (e: Exception) {
        "₹ 00.00"
    }
}

fun currencyFormatterOnly(num: String): String? {
    return try {
        val m = num.toDouble()
        //var value = String.format("%.2f", m)
        val formatter = DecimalFormat("###,###,###")
        "₹ " + formatter.format(m)
        // "₹ $value"

    } catch (e: java.lang.Exception) {
        "₹ 00.00"
    }
}

@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String {
    return Secure.getString(
        context.contentResolver,
        Secure.ANDROID_ID
    )
}

fun formatDate(dateToFormat: String?): String? {
    if (dateToFormat != null) {
        try {
            Log.e("DATE", "Input Date Date is $dateToFormat")
            val convertedDate = SimpleDateFormat("dd MMM yyyy,HH:mm a")
                .format(
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse(dateToFormat)
                )
            Log.e("DATE", "Output Date is $convertedDate")

            //Update Date
            return convertedDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return "-"
}

fun getAmountShortcut(): ArrayList<String> {
    var amountList = ArrayList<String>()

    amountList.add("100")
    amountList.add("200")
    amountList.add("500")
    amountList.add("1000")
    amountList.add("2000")

    return amountList

}

fun getFilterShortcut(): ArrayList<String> {
    var filterName = ArrayList<String>()

    filterName.add("Completed")
    filterName.add("Failed")
    filterName.add("Pending")
    filterName.add("Initiated")





    return filterName

}


fun inviteFriend(context: Context, code: String) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tourney11")
        var shareMessage =
            "Hi! I have been using *Tourney11* to play games and earn money\nUse my refer Code to get Bonus amount" +
                    "\n *Refer Code: $code* \n\nDownload the App Now: "
        shareMessage = """ ${shareMessage + "https://www.tourney11.com?ref=" + code}
            
    
            """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: java.lang.Exception) {
        //e.toString();
    }
}

fun getCounDownTimer(currentTime: Long, scheduleTime: Long): Long {
    return scheduleTime - currentTime
}

fun runGameStartTimer(timer: Long, textView: TextView) {
    if (timer > 0) {
        try {
            val timer = object : CountDownTimer(timer * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    var diff = millisUntilFinished
                    val secondsInMilli: Long = 1000
                    val minutesInMilli = secondsInMilli * 60
                    val hoursInMilli = minutesInMilli * 60
                    val daysInMilli = hoursInMilli * 24

                    val elapsedDays = diff / daysInMilli
                    diff %= daysInMilli

                    val elapsedHours = diff / hoursInMilli
                    diff %= hoursInMilli

                    val elapsedMinutes = diff / minutesInMilli
                    diff %= minutesInMilli

                    val elapsedSeconds = diff / secondsInMilli

                    textView.text =
                        "$elapsedHours h $elapsedMinutes m $elapsedSeconds s"
                }

                override fun onFinish() {
                    textView.text = ""
                }
            }
            timer.start()
        } catch (e: Exception) {
            Log.e("TAG", "bindViewItemsError: " + e.localizedMessage)
        }
    } else {
        textView.text = ""
    }
}


fun formatDateForCricSkillGame(dateToFormat: String?): String {
    if (dateToFormat != null) {
        try {
            Log.e("DATE", "Input Date Date is $dateToFormat")
            val convertedDate = SimpleDateFormat("yyyy-MM-dd HH:mm a")
                .format(
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")//
                        .parse(dateToFormat)
                )
            Log.e("DATE", "Output Date is $convertedDate")

            //Update Date
            return convertedDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return "-"
}

fun timer(currentTime: String, scheduleTime: String): String {
    val dtDeparture = currentTime
    val dtArrival = scheduleTime
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    try {
        val dateDeparture: Date = format.parse(dtDeparture)
        val dateArrival: Date = format.parse(dtArrival)
        dateArrival.compareTo(dateDeparture)
        val diff = dateArrival.time - dateDeparture.time
        val day: Long = TimeUnit.DAYS.toDays(diff)
        val hours: Long = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(diff) - hours * 60


        return if (hours > 0 && minutes > 0) {
            Log.d("fff", "$day $hours hours and $minutes minutes")
            "$hours h: $minutes m"
        } else {
            Log.d("fff", "00 h: 00 m")
            "00 h: 00 m"
        }

        //return "$hours h: $minutes m"
    } catch (e: ParseException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
        return "00 h: 00 m"
    }
}

fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val currentDateandTime = sdf.format(Date())
    return currentDateandTime
}

fun formatTimeForCricSkillGame(dateToFormat: String?): String {
    if (dateToFormat != null) {
        try {
            Log.e("DATE", "Input Date Date is $dateToFormat")
            val convertedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .format(
                    SimpleDateFormat("HH:mm a")
                        .parse(dateToFormat)
                )
            Log.e("DATE", "Output Date is $convertedDate")

            //Update Date
            return convertedDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return "-"
}

fun getTimeDifferent(dateToFormat: String?) {
    val toyBornTime = "2014-06-18 12:56:50"
    val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    )

    try {
        val oldDate = dateFormat.parse(dateToFormat)
        System.out.println(oldDate)
        val currentDate = Date()
        val diff = currentDate.time - oldDate.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        if (oldDate.before(currentDate)) {
            Log.e("oldDate", "is previous date")
            Log.e(
                "Difference: ", "$days-$hours-$minutes-$seconds"
            )
        }

        // Log.e("toyBornTime", "" + toyBornTime);
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}

fun eventsTracking(eventId: String, addParam: String) {
    val event = TrackierEvent(eventId)
    event.param1 = addParam
    trackEvent(event)
    //TrackierSDK.trackEvent(event)
    Log.d("TAG", "onClick: event_track ")

}

fun TextView.setGradientTextColor() {
    var text = text.toString()
    val spannableString = SpannableString(text)

    val startColor = 0xFF00B5FF.toInt()
    val endColor = 0xFF00E5FF.toInt()

    val linearGradient = LinearGradient(
        0f, 0f, paint.measureText(text), paint.textSize,
        startColor, endColor, Shader.TileMode.CLAMP
    )

    val textPaint = TextPaint().apply {
        shader = linearGradient
    }

    spannableString.setSpan(
        ForegroundColorSpan(textPaint.color),
        0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    setBackgroundColor(0)

    text = spannableString.toString()
}

fun setVisibility(view: View?, visible: Boolean) {
    view?.visibility = if (visible) View.VISIBLE else View.GONE
}

fun setImage(imageView: ImageView, imageResId: Int) {
    imageView.setImageResource(imageResId)
}

fun setBackgroundDrawable(view: RelativeLayout, drawableResId: Int) {
    val drawable = ContextCompat.getDrawable(view.context, drawableResId)
    view.background = drawable
}

fun getMinimumYearOfTurning18(): Int {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    return currentYear - 17
}

fun readJsonFromAssets(context: Context, fileName: String): String? {
    try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}


fun getImagePathFromUri(uri: Uri?, context: Context): String? {
    var imagePath: String? = null
    uri?.let {
        val cursor = context.contentResolver.query(it, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                imagePath = it.getString(columnIndex)
            }
            it.close()
        }
    }
    return imagePath
}


fun isCameraAppAvailable(context: Context): Boolean {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    return intent.resolveActivity(context.packageManager) != null
}


private fun getTimestamp(): String {
    val timeFormat = "yyyyMMdd_HHmmssSSS"
    return SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())
}

private fun getCameraDirectory(context: Context): File {
    val dir =
        context.getExternalFilesDir(Environment.DIRECTORY_DCIM) // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    return File(dir, "Camera")
}

fun getImageUri(context: Context, dir: File? = null, extension: String? = null): Uri? {
    try {
        // Create an image file name
        val ext = extension ?: ".jpg"
        val imageFileName = "IMG_${getTimestamp()}$ext"

        // Create File Directory Object
        val storageDir = dir ?: getCameraDirectory(context)

        // Create Directory If not exist
        if (!storageDir.exists()) storageDir.mkdirs()

        // Create File Object
        val file = File(storageDir, imageFileName)

        // Create empty file
        file.createNewFile()

        val authority =
            context.packageName + "Tourney11"
        val uriForFile = FileProvider.getUriForFile(
            context,
            authority,
            file
        )
        return uriForFile
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
}

fun getImageFile(context: Context, dir: File? = null, extension: String? = null): File? {
    try {
        // Create an image file name
        val ext = extension ?: ".jpg"
        val imageFileName = "IMG_${getTimestamp()}$ext"

        // Create File Directory Object
        val storageDir = dir ?: getCameraDirectory(context)

        // Create Directory If not exist
        if (!storageDir.exists()) storageDir.mkdirs()

        // Create File Object
        val file = File(storageDir, imageFileName)

        // Create empty file
        file.createNewFile()

        return file
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
}

fun generateUPIQRCode(upiLink: String): Bitmap? {
    val qrCodeContent = upiLink

    try {
        val writer = QRCodeWriter()
        val hints = mapOf<EncodeHintType, Int>(EncodeHintType.MARGIN to 0) // Set margin to 0
        val bitMatrix: BitMatrix =
            writer.encode(qrCodeContent, BarcodeFormat.QR_CODE, 512, 512, hints)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.TRANSPARENT
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
    }

    return null
}

fun getPeekHeight(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenHeight = displayMetrics.heightPixels
    return (screenHeight * 0.5).toInt()
}

fun setGradientTextColor(textView: TextView, startColor: Int, endColor: Int) {
    // Create the gradient shader
    val textShader: Shader = LinearGradient(
        0f, 0f, 0f, textView.paint.textSize,
        startColor, endColor, Shader.TileMode.CLAMP
    )

    // Apply the shader to the TextView's TextPaint
    textView.paint.shader = textShader
}


fun convertNumberToWords(amountStr: String): String {
    val amount = try {
        amountStr.toDouble()
    } catch (e: NumberFormatException) {
        return "Invalid input"
    }

    val units = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve"
    )
    val teens = arrayOf(
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )
    val tens = arrayOf(
        "",
        "",
        "Twenty",
        "Thirty",
        "Forty",
        "Fifty",
        "Sixty",
        "Seventy",
        "Eighty",
        "Ninety"
    )

    if (amount == 0.0) {
        return "Zero"
    }

    val sb = StringBuilder()

    // Split the amount into whole number and decimal parts
    val wholeNumber = amount.toInt()
    val decimal = ((amount - wholeNumber) * 100).toInt()

    // Convert the whole number part
    if (wholeNumber > 0) {
        sb.append(convertNumberToWordsHelper(wholeNumber / 10000000, " Crore ", units))
        sb.append(convertNumberToWordsHelper((wholeNumber / 100000) % 100, " Lakh ", units))
        sb.append(convertNumberToWordsHelper((wholeNumber / 1000) % 100, " Thousand ", units))
        sb.append(convertNumberToWordsHelper((wholeNumber / 100) % 10, " Hundred ", units))

        if (wholeNumber > 100 && wholeNumber % 100 != 0) {
            sb.append("and ")
        }

        if (wholeNumber % 100 in 1..19) {
            sb.append(units[wholeNumber % 100])
        } else {
            sb.append(tens[(wholeNumber % 100) / 10])
            sb.append(units[wholeNumber % 10])
        }

        sb.append(" Rupees ")
    }

    // Convert the decimal part
    if (decimal > 0) {
        sb.append("and ")
        sb.append(convertNumberToWordsHelper(decimal / 10, "", units))
        sb.append(convertNumberToWordsHelper(decimal % 10, "", units))
        sb.append(" Paise")
    }

    return sb.toString().trim()
}

fun convertNumberToWordsHelper(number: Int, suffix: String, units: Array<String>): String {
    return if (number > 0) {
        units[number] + suffix
    } else {
        ""
    }
}

fun parseDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    val date = inputFormat.parse(dateString)
    return outputFormat.format(date)
}

fun getDates(): Pair<String, String> {
    val currentDate = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val currentDateStr = formatter.format(currentDate.time)

    currentDate.add(Calendar.MONTH, -1)
    val oneMonthAgoStr = formatter.format(currentDate.time)

    return Pair(currentDateStr, oneMonthAgoStr)
}

fun getIPAddress(context: Context): String? {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Check if Wi-Fi is connected
    val wifiInfo =
        (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
    if (wifiInfo.networkId != -1) {
        val ipAddress = wifiInfo.ipAddress
        val ipByteArray = byteArrayOf(
            (ipAddress and 0xff).toByte(),
            (ipAddress shr 8 and 0xff).toByte(),
            (ipAddress shr 16 and 0xff).toByte(),
            (ipAddress shr 24 and 0xff).toByte()
        )

        try {
            val inetAddress = InetAddress.getByAddress(ipByteArray)
            return inetAddress.hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Check if mobile data is connected
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
            val inetAddresses = NetworkInterface.getNetworkInterfaces()
            while (inetAddresses.hasMoreElements()) {
                val inetAddress = inetAddresses.nextElement()
                val addresses = inetAddress.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is InetAddress && address.hostAddress.contains(
                            ":"
                        ).not()
                    ) {
                        return address.hostAddress
                    }
                }
            }
        }
    } else {
        val networks = connectivityManager.allNetworks
        for (network in networks) {
            val networkInfo = connectivityManager.getNetworkInfo(network)
            if (networkInfo?.type == ConnectivityManager.TYPE_MOBILE) {
                val inetAddresses = NetworkInterface.getNetworkInterfaces()
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    val addresses = inetAddress.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val address = addresses.nextElement()
                        if (!address.isLoopbackAddress && address is InetAddress && address.hostAddress.contains(
                                ":"
                            ).not()
                        ) {
                            return address.hostAddress
                        }
                    }
                }
            }
        }
    }

    return null
}

fun showCommonAlertDialog(
    mContext: Activity,
    message: String,
    onClickDialogListener: CommonDialogListener,
    btn_name: String
) {
    AlertDialog.Builder(mContext)
        .setMessage("$message")
        .setCancelable(false)
        .setPositiveButton(
            "$btn_name"
        ) { dialog, which ->
            onClickDialogListener.onClickEvent(true)
            dialog.dismiss()

        }
        .setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
            onClickDialogListener.onClickEvent(false)
        }
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show()
}

fun showCommonAlertWithoutDialog(
    mContext: Activity,
    message: String,
    onClickDialogListener: CommonDialogListener
) {
    AlertDialog.Builder(mContext)
        .setMessage("$message")
        .setCancelable(false)
        .setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
            onClickDialogListener.onClickEvent(false)
        }
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show()
}

interface CommonDialogListener {
    fun onClickEvent(isYes: Boolean);
}

fun createLogJSON(log: String): HashMap<String, Any> {
    var hash = kotlin.collections.HashMap<String, Any>()
    hash["logs"] = log
    /* var mainMap = kotlin.collections.HashMap<String, Any>()
     mainMap[""] = hash*/

    return hash
}


fun CustomToast(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toaster.pop(
            context,
            message,
            Toaster.LENGTH_SHORT
        ).show()
    }
}


inline fun <reified T> String.fromJsonToModel(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (e: Exception) {
        null
    }
}

val aadhaarPattern = "^[2-9]{1}[0-9]{11}$".toRegex()

fun isAadhaarValid(aadhaar: String): Boolean {
    return aadhaarPattern.matches(aadhaar)
}

@Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
fun getDocument(xml: String): Document {
    val dbf = DocumentBuilderFactory.newInstance()
    val db = dbf.newDocumentBuilder()
    val doc = InputSource(StringReader(xml))
    return db.parse(doc)
}

fun isValidPidData(v: String): Array<String?> {
    val values = arrayOfNulls<String>(2)
    try {
        val doc = getDocument(v)
        doc.documentElement.normalize()
        val node = doc.getElementsByTagName("PidData")
        val element = node.item(0) as Element
        val Resp = element.getElementsByTagName("Resp")
        val Res = Resp.item(0) as Element
        values[0] = Res.getAttribute("errCode")
        if (values[0] != "0") {
            values[1] = Res.getAttribute("errInfo")
        } else {
            values[1] = null
        }
    } catch (e: Exception) {
        values[0] = "0010101"
        values[1] = null
        Log.e("TAG", "isValidPidData: :::::::" + e.message)
    }
    return values
}

fun searchPackageName(activity: Activity, deviceTypeSelected: String) {
    val context = activity
    val packageNames = mapOf(
        "Startek" to "com.acpl.registersdk",
        "Morpho" to "com.scl.rdservice",
        "Mantra" to "com.mantra.rdservice",
        "Precision" to "com.precision.pb510.rdservice",
        "NEXT Biometrics OneTouch L0" to "com.nextbiometrics.onetouchrdservice",
        "MIS100V2 RDService" to "com.mantra.mis100v2.rdservice",
        "Morpho L1" to "com.idemia.l1rdservice",
        "MFS110 L1" to "com.mantra.mfs110.rdservice",
    )

    val packageName = packageNames[deviceTypeSelected]

    if (packageName != null) {
        val pm = activity.packageManager
        val intent = Intent()
        intent.`package` = packageName
        val listTemp = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED)

        if (listTemp.size <= 0) {
            val message = when (deviceTypeSelected) {
                "Startek" -> "Please install ACPL FM220 Registered Device Service."
                "Morpho" -> "Please install Morpho SCL RDService."
                "Mantra" -> "Please install Mantra RD Service."
                "Precision" -> "Please install Precision RD Service."
                "NEXT Biometrics OneTouch L0" -> "Please install NEXT Biometrics RD Service."
                "MIS100V2 RDService" -> "Please install MIS100V2 RDService."
                "Morpho L1" -> "Please install Morpho L1."
                else -> "Please install the required service."
            }

            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()

            val intentPlay = Intent(Intent.ACTION_VIEW)
            intentPlay.data = Uri.parse("market://details?id=$packageName")
            try {
                activity.startActivity(intentPlay)
            } catch (e: ActivityNotFoundException) {
                // Handle the exception when Play Store is not available
                Toast.makeText(context, "Play Store not found", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        // Handle the case when deviceTypeSelected is not recognized
    }
}

fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun getPackageForDevice(deviceName: String): String? {
    val packageNames = mapOf(
        "Startek" to "com.acpl.registersdk",
        "Morpho" to "com.scl.rdservice",
        "Mantra" to "com.mantra.rdservice",
        "Precision" to "com.precision.pb510.rdservice",
        "NEXT Biometrics OneTouch L0" to "com.nextbiometrics.onetouchrdservice",
        "MIS100V2 RDService" to "com.mantra.mis100v2.rdservice",
        "Morpho L1" to "com.idemia.l1rdservice",
        "NEXT Biometrics OneTouch L0" to "com.nextbiometrics.onetouchrdservice",
        "Morpho L1" to "com.idemia.l1rdservice"
    )

    return packageNames[deviceName]
}

fun getCurrentDateTimeFormatted(): String {
    val dateFormat = SimpleDateFormat("yyyy-MMM-dd HH:mm a", Locale.getDefault())
    val currentDateAndTime: String = dateFormat.format(Date())
    return currentDateAndTime
}

fun maskAndAddSpace(input: String): String {
    // Check if the input string has at least 8 characters
    return if (input.length >= 8) {
        // Use substring to get the part of the string after the first 8 characters
        val remainingString = input.substring(8)
        // Use String.repeat to create a string of asterisks with the same length as the removed characters
        val maskedDigits = "*".repeat(8)
        // Insert a space after every 4 digits in the maskedDigits
        val maskedWithSpace = maskedDigits.chunked(4).joinToString(" ")
        // Concatenate the masked digits with the remaining string
        "$maskedWithSpace $remainingString"
    } else {
        // If the input string is less than 8 characters, return the input string as is
        input
    }
}


fun showDatePickerDialog(context: Context, onDateSetListener: OnDateForSetListener) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            // Format day and month with leading zeros
            val formattedMonth = String.format("%02d", selectedMonth + 1)
            val formattedDay = String.format("%02d", selectedDay)

            // Call the listener with formatted values
            onDateSetListener.onDateSet(selectedYear, formattedMonth.toInt(), formattedDay.toInt())
        },
        year,
        month,
        day
    )

    datePickerDialog.datePicker.maxDate =
        System.currentTimeMillis() // Optional: Set a max date if needed
    datePickerDialog.show()
}


interface OnDateForSetListener {
    fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
}

fun showGenderSelectionDialog(
    context: Context,
    onGenderSelectedListener: OnGenderSelectedListener
) {
    val genderOptions = arrayOf("Male", "Female", "Other")

    val builder = AlertDialog.Builder(context)
    builder.setTitle("Select Gender")
        .setItems(genderOptions) { _: DialogInterface, which: Int ->
            val selectedGender = genderOptions[which]
            onGenderSelectedListener.onGenderSelected(selectedGender)
        }
        .setNegativeButton("Cancel", null)

    builder.show()
}

interface OnGenderSelectedListener {
    fun onGenderSelected(gender: String)
}

fun getFileSize(size: Long): String {
    if (size <= 0)
        return "0"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

fun getCurrentMonthStartingDateAndToday(): Pair<String, String> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val monthStartingDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    val today = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
    return Pair(monthStartingDate, today)
}

fun formatAmount(amount: Double): String {
    val formatter: NumberFormat = DecimalFormat("###,###,###.##")

    return "₹${formatter.format(amount)}"
}


fun showDatePicker(context: Context, onDateSelected: (Calendar) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            onDateSelected(selectedDate)
        },
        year,
        month,
        day
    )

    datePickerDialog.show()
}

fun formatToIndianCurrency(amount: Double): String {
    val indiaLocale = Locale("en", "IN")
    val currencyFormatter = NumberFormat.getCurrencyInstance(indiaLocale)
    return currencyFormatter.format(amount)
}

fun getCurrentDateFormatted(): String {
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val monthYearFormat = SimpleDateFormat("MMMM, yyyy")
    val monthYear = monthYearFormat.format(calendar.time)

    val dayWithSuffix = when (day % 10) {
        1 -> if (day == 11) "${day}th" else "${day}st"
        2 -> if (day == 12) "${day}th" else "${day}nd"
        3 -> if (day == 13) "${day}th" else "${day}rd"
        else -> "${day}th"
    }

    return "$dayWithSuffix $monthYear"
}

fun Number.dpToPx(context: Context? = null): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

fun Int.toDp(resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    )
}

fun generateQRCode(upiString: String, width: Int = 500, height: Int = 500): Bitmap? {
    return try {
        val bitMatrix: BitMatrix =
            MultiFormatWriter().encode(upiString, BarcodeFormat.QR_CODE, width, height)
        val barcodeEncoder = BarcodeEncoder()
        barcodeEncoder.createBitmap(bitMatrix)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

interface DateRangeSelectedListener {
    fun onDateRangeSelected(startDate: String, endDate: String)
}

fun showRangeDatePickerDialog(context: Context, listener: DateRangeSelectedListener) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, startYear, startMonth, startDay ->
            val startCalendar = Calendar.getInstance().apply {
                set(startYear, startMonth, startDay)
            }

            val endDatePickerDialog = DatePickerDialog(
                context,
                { _, endYear, endMonth, endDay ->
                    val endCalendar = Calendar.getInstance().apply {
                        set(endYear, endMonth, endDay)
                    }

                    if (endCalendar.timeInMillis < startCalendar.timeInMillis) {
                        Toast.makeText(
                            context,
                            "End date cannot be before start date",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val startDateString = sdf.format(startCalendar.time)
                        val endDateString = sdf.format(endCalendar.time)
                        listener.onDateRangeSelected(startDateString, endDateString)
                    }
                },
                currentYear,
                currentMonth,
                currentDay
            )
            endDatePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            endDatePickerDialog.show()
        },
        currentYear,
        currentMonth,
        currentDay
    )

    startDatePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    startDatePickerDialog.show()
}

fun maskString(input: String, numVisibleChars: Int): String {
    // Ensure numVisibleChars is not greater than the length of the string
    val visibleChars = if (numVisibleChars > input.length) input.length else numVisibleChars

    // Calculate the number of characters to mask
    val numMaskChars = input.length - visibleChars

    // Create the masked string
    val maskedString = "*".repeat(numMaskChars) + input.takeLast(visibleChars)

    return maskedString
}

fun convertDate(inputDate: String): String {
    // Define the input and output date formats
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("d'st' MMMM ''yy", Locale.getDefault())

    // Parse the input date string to a Date object
    val date = inputFormat.parse(inputDate)

    // Create a Calendar object from the parsed Date
    val calendar = Calendar.getInstance()
    calendar.time = date

    // Get the day of the month to determine the suffix
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val suffix = getDayOfMonthSuffix(dayOfMonth)

    // Format the date to the desired output string
    val formattedDate = outputFormat.format(date)

    // Replace the suffix placeholder with the correct suffix
    return formattedDate.replace("st", suffix)
}

fun getDayOfMonthSuffix(day: Int): String {
    return when (day) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
}

fun convertTime(inputTime: String): String {
    // Define the input and output time formats
    val inputFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Parse the input time string to a Date object
    val date = inputFormat.parse(inputTime)

    // Format the date to the desired output string
    return outputFormat.format(date)
}

fun openWP(contact: String, context: Activity) {
    val contact = "+91$contact" // use country code with your phone number
    val url = "https://api.whatsapp.com/send?phone=$contact"
    try {
        val pm: PackageManager = context.getPackageManager()
        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        context.startActivity(i)
    } catch (e: PackageManager.NameNotFoundException) {
        Toast.makeText(
            context,
            "Whatsapp app not installed in your phone",
            Toast.LENGTH_SHORT
        ).show()
        e.printStackTrace()
    }
}

fun getGreetingBasedOnTime(): String {
    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

    return when {
        currentHour in 5..11 -> "Good Morning"
        currentHour in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

fun getTodayDate(): String {
    val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault()) // Date format pattern
    val today = Calendar.getInstance().time // Get today's date
    return dateFormat.format(today)
}


fun getFormatDate(inputDate: String?): String {
    if (inputDate.isNullOrBlank() || inputDate == "null") return "--"

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        if (date != null) outputFormat.format(date) else "--"
    } catch (e: Exception) {
        "--"
    }
}



fun doLogout(mContext: Context) {
    Hawk.deleteAll()
    val prefs = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
    val intent = Intent(mContext, SplashActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    mContext.startActivity(intent)
}

fun getFormattedDate(date: String, dateFormat: String, returnDateFormat: String): String {
    val inputFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
    val outputFormat = SimpleDateFormat(returnDateFormat, Locale.getDefault())

    val dateObj = inputFormat.parse(date)
    return outputFormat.format(dateObj)
}

fun getFormattedDate2(date: String, possibleFormats: List<String>, returnDateFormat: String): String {
    val outputFormat = SimpleDateFormat(returnDateFormat, Locale.getDefault())

    for (format in possibleFormats) {
        try {
            val inputFormat = SimpleDateFormat(format, Locale.getDefault())
            val dateObj = inputFormat.parse(date)
            return outputFormat.format(dateObj)
        } catch (e: ParseException) {

        }
    }
    return "Invalid Date"
}

@RequiresApi(Build.VERSION_CODES.O)
fun extractDayNameDateAndMonth(inputDate: String): Triple<String, Int, Int> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(inputDate, formatter)
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    val day = date.dayOfMonth
    val month = date.monthValue
    return Triple(dayName, day, month)
}

@RequiresApi(Build.VERSION_CODES.O)
fun extractDayNameDateAndMonth2(dateStr: String?): Pair<String, String> {
    if (dateStr.isNullOrBlank()) {
        return Pair("N/A", "--")
    }

    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateStr, formatter)
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val dayOfMonth = date.dayOfMonth.toString()
        Pair(dayName, dayOfMonth)
    } catch (e: Exception) {
        Pair("Invalid", "--")
    }
}


fun calculateHours(inTime: String, outTime: String): String {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val inDate = timeFormat.parse(inTime)
    val outDate = timeFormat.parse(outTime)
    val differenceInMillis = outDate.time - inDate.time
    val differenceInHours = (differenceInMillis / (1000 * 60 * 60)).toInt()
    val differenceInMinutes = ((differenceInMillis % (1000 * 60 * 60)) / (1000 * 60)).toInt()

    return "$differenceInHours:$differenceInMinutes"
}

fun calculateHours2(punchIn: String?, punchOut: String?): String {
    if (punchIn.isNullOrEmpty() || punchOut.isNullOrEmpty()) return "Invalid Data"

    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val inTime = format.parse(punchIn)
        val outTime = format.parse(punchOut)

        val diff = outTime.time - inTime.time
        val hours = (diff / (1000 * 60 * 60)).toInt()
        val minutes = ((diff / (1000 * 60)) % 60).toInt()

        "$hours hrs $minutes min"
    } catch (e: Exception) {
        ""
    }
}










fun calculateMinutes(inTime: String, outTime: String): Int {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    try {
        val inDate = timeFormat.parse(inTime)
        val outDate = timeFormat.parse(outTime)

        if (inDate != null && outDate != null) {
            var differenceInMillis = outDate.time - inDate.time

            // In case outTime is before inTime (negative time difference)
            if (differenceInMillis < 0) {
                differenceInMillis += 24 * 60 * 60 * 1000 // Adding 24 hours to account for overnight work
            }

            val differenceInMinutes = (differenceInMillis / (1000 * 60)).toInt() // Convert to minutes
            return differenceInMinutes
        }
    } catch (e: Exception) {
        e.printStackTrace() // Handle parsing errors
    }

    return 0 // Return 0 minutes if there's an error
}


fun convertTo12HourFormat(time: String?): String {
    if (time.isNullOrEmpty()) return ""

    return try {
        val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = sdf24.parse(time) ?: return "Invalid Time"
        sdf12.format(date)
    } catch (e: Exception) {
        Log.e("TimeConversion", "Error parsing time: $time", e)
        "Invalid Time"
    }
}



 fun generateTextBitmap(name: String): Bitmap {
    val size = 200 // Bitmap size
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = Color.parseColor("#0ECBF5")
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

    paint.color = Color.WHITE // Text color
    paint.textSize = 80f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textAlign = Paint.Align.CENTER

    val firstLetter = name.take(1).uppercase()
    val xPos = canvas.width / 2f
    val yPos = (canvas.height / 2f - (paint.descent() + paint.ascent()) / 2f)

    canvas.drawText(firstLetter, xPos, yPos, paint)

    return bitmap
}


fun showFullScreenImage(activity:Activity,imageUrl: String) {
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_full_screen_image)

    val fullScreenImageView = dialog.findViewById<ShapeableImageView>(R.id.fullScreenImageView)
    val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)

    // Load image with Glide
    Glide.with(activity)
        .load(imageUrl)
        .into(fullScreenImageView)
    closeButton.setOnClickListener { dialog.dismiss() }

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    dialog.show()
}

fun reportsFormatToMonthYear(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "N/A"

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        if (date != null) outputFormat.format(date) else "N/A"
    } catch (e: Exception) {
        "N/A"
    }
}
fun showCustomMonthYearPicker(
    context: Context,
    onSelected: (formattedDate: String, displayDate: String) -> Unit
) {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.dialog_month_year_picker)
    dialog.setTitle("Select Month and Year")
    dialog.setCancelable(false)

    val window = dialog.window
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(window?.attributes)
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

    // Set dialog margins
    val marginHorizontal = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
    window?.decorView?.setPadding(marginHorizontal, 0, marginHorizontal, 0)
    window?.attributes = layoutParams

    val monthPicker = dialog.findViewById<NumberPicker>(R.id.month_picker)
    val yearPicker = dialog.findViewById<NumberPicker>(R.id.year_picker)
    val btnOk = dialog.findViewById<AppCompatTextView>(R.id.btn_ok)
    val btnCancel = dialog.findViewById<AppCompatTextView>(R.id.btn_cancel)

    // Month values
    val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    monthPicker.minValue = 0
    monthPicker.maxValue = months.size - 1
    monthPicker.displayedValues = months

    // Get current month and year
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)

    // Set pickers to current values
    monthPicker.value = currentMonth
    yearPicker.minValue = 2000
    yearPicker.maxValue = currentYear + 20
    yearPicker.value = currentYear

    btnOk.setOnClickListener {
        val selectedMonth = monthPicker.value
        val selectedYear = yearPicker.value

        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(Calendar.MONTH, selectedMonth)
        selectedCalendar.set(Calendar.YEAR, selectedYear)

        val postFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault()) // e.g. 2025-05
        val displayFormat = SimpleDateFormat("MMM yy", Locale.getDefault()) // e.g. May 25

        val formattedDate = postFormat.format(selectedCalendar.time)
        val displayDate = displayFormat.format(selectedCalendar.time)

        onSelected(formattedDate, displayDate)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}


/*fun showCustomMonthYearPicker(
    context: Context,
    onSelected: (formattedDate: String, displayDate: String) -> Unit
) {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.dialog_month_year_picker)
    dialog.setTitle("Select Month and Year")
    dialog.setCancelable(false)

    val window = dialog.window
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(window?.attributes)
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
    // Set margins
    val marginHorizontal = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
    window?.decorView?.setPadding(marginHorizontal, 0, marginHorizontal, 0)

    window?.attributes = layoutParams

    val monthPicker = dialog.findViewById<NumberPicker>(R.id.month_picker)
    val yearPicker = dialog.findViewById<NumberPicker>(R.id.year_picker)
    val btnOk = dialog.findViewById<AppCompatTextView>(R.id.btn_ok)
    val btnCancel = dialog.findViewById<AppCompatTextView>(R.id.btn_cancel)

    val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    monthPicker.minValue = 0
    monthPicker.maxValue = months.size - 1
    monthPicker.displayedValues = months

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    yearPicker.minValue = 2000
    yearPicker.maxValue = currentYear + 20
    yearPicker.value = currentYear

    btnOk.setOnClickListener {
        val selectedMonth = monthPicker.value
        val selectedYear = yearPicker.value

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, selectedMonth)
        calendar.set(Calendar.YEAR, selectedYear)

        val postFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM yy", Locale.getDefault())

        onSelected(postFormat.format(calendar.time), displayFormat.format(calendar.time))
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}*/



/*fun showPaymentDialog(
    context: Activity,
    successType:String

) {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.dialog_month_year_picker)
    dialog.setTitle("Select Month and Year")
    dialog.setCancelable(false)

    val window = dialog.window
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(window?.attributes)
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
    // Set margins
    val marginHorizontal = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
    window?.decorView?.setPadding(marginHorizontal, 0, marginHorizontal, 0)

    window?.attributes = layoutParams

    val monthPicker = dialog.findViewById<NumberPicker>(R.id.month_picker)
    val yearPicker = dialog.findViewById<NumberPicker>(R.id.year_picker)
    val btnOk = dialog.findViewById<AppCompatTextView>(R.id.btn_ok)
    val btnCancel = dialog.findViewById<AppCompatTextView>(R.id.btn_cancel)

    val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    monthPicker.minValue = 0
    monthPicker.maxValue = months.size - 1
    monthPicker.displayedValues = months

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    yearPicker.minValue = 2000
    yearPicker.maxValue = currentYear + 20
    yearPicker.value = currentYear

    btnOk.setOnClickListener {
        val selectedMonth = monthPicker.value
        val selectedYear = yearPicker.value

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, selectedMonth)
        calendar.set(Calendar.YEAR, selectedYear)

        val postFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM yy", Locale.getDefault())

        onSelected(postFormat.format(calendar.time), displayFormat.format(calendar.time))
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}*/




fun formatUtcTo12HourLocalTimeLegacy(utcTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        val date: Date = inputFormat.parse(utcTime)!!
        outputFormat.format(date)
    } catch (e: Exception) {
        " "
    }
}
fun generateGradientDrawables(count: Int): List<GradientDrawable> {
    val gradientList = mutableListOf<GradientDrawable>()

    // Safe color combinations that work well with white text
    val gradientColorPairs = listOf(
        intArrayOf(Color.parseColor("#FF5F6D"), Color.parseColor("#FFC371")),  // red-orange
        intArrayOf(Color.parseColor("#36D1DC"), Color.parseColor("#5B86E5")),  // cyan-blue
        intArrayOf(Color.parseColor("#FFB75E"), Color.parseColor("#ED8F03")),  // orange
        intArrayOf(Color.parseColor("#11998e"), Color.parseColor("#38ef7d")),  // green-teal
        intArrayOf(Color.parseColor("#7F00FF"), Color.parseColor("#E100FF")),  // purple
        intArrayOf(Color.parseColor("#FC466B"), Color.parseColor("#3F5EFB")),  // red-blue
        intArrayOf(Color.parseColor("#f7971e"), Color.parseColor("#ffd200"))   // amber-yellow
    )

    val orientations = listOf(
        GradientDrawable.Orientation.LEFT_RIGHT,
        GradientDrawable.Orientation.TOP_BOTTOM,
        GradientDrawable.Orientation.BL_TR
    )

    repeat(count) {
        val colors = gradientColorPairs.random()
        val orientation = orientations.random()

        val gradient = GradientDrawable(orientation, colors).apply {
            cornerRadius = 32f
        }

        gradientList.add(gradient)
    }

    return gradientList
}


fun convertTo12HourFormat2(dateTime: String?): String {
    if (dateTime.isNullOrEmpty()) return "--"

    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    return try {
        val date = inputFormat.parse(dateTime)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        "--"
    }
}


fun convertTo12HourFormat3(dateTime: String?): String {
    if (dateTime.isNullOrEmpty()) return "--"

    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    return try {
        val date = inputFormat.parse(dateTime)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        "--"
    }
}

fun convertTo12Hour(time: String?): String {
    if (time.isNullOrEmpty()) return "--"
    return try {
        val sdf24 = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = sdf24.parse(time)
        date?.let { sdf12.format(it) } ?: "--"
    } catch (e: Exception) {
        "--"
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
fun isGpsEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}




fun getBatteryPercentage(context: Context): Int {
    val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
    return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}
 fun getDeviceName(): String {
    return "${Build.MANUFACTURER} ${Build.MODEL}"
}

fun getAndroidVersion(): String {
    return Build.VERSION.RELEASE ?: "Unknown"
}

fun scheduleDailyEndOfDaySync(context: Context) {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance()

    dueDate.set(Calendar.HOUR_OF_DAY, 19)
    dueDate.set(Calendar.MINUTE, 46)
    dueDate.set(Calendar.SECOND, 0)

    if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 24)
    }

    val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

    val dailyWorkRequest = PeriodicWorkRequestBuilder<EndOfDaySyncWorker>(24,TimeUnit.HOURS)
        .setInitialDelay(timeDiff,TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "endOfDaySyncWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        dailyWorkRequest
    )
}

fun checkExactAlarmPermission(context: Context, onResult: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.data = Uri.parse("package:${context.packageName}")
            (context as Activity).startActivityForResult(intent, 1001)
            onResult(false)
        } else {
            onResult(true)
        }
    } else {
        onResult(true)
    }
}

fun requestIgnoreBatteryOptimization(context: Context, onResult: (Boolean) -> Unit) {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:${context.packageName}")
        (context as Activity).startActivityForResult(intent, 1002)
        onResult(false)
    } else {
        onResult(true)
    }
}


