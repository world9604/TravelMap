package com.taein.travelmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import com.taein.travelmap.MainActivity.Companion.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import kotlin.random.Random

sealed interface MapUiState {
    data object Loading : MapUiState
    data object PhotoNotReady : MapUiState
    data class Success(
        val userPhoto: List<UserPhoto> = emptyList()
    ) : MapUiState {
        fun isEmpty(): Boolean = userPhoto.isEmpty()
    }
    data class Error(val message: String) : MapUiState
}

data class UserPhoto(
    val id: String,
    val uri: Uri,
    val gpsLatitude: Double,
    val gpsLongitude: Double
)

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.PhotoNotReady)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    /**
     * todo : TAG_GPS_LATITUDE_REF, TAG_GPS_LONGITUDE_REF 값을 이용해서 동/서 관련 로직 구현
     * https://velog.io/@im-shung/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%A0%88%EB%8C%80%EA%B2%BD%EB%A1%9C%EC%99%80-gps-%EC%A0%95%EB%B3%B4-%EA%B5%AC%ED%95%98%EA%B8%B0
     * imageUri : content://com.android.providers.media.documents/document/image%3A1000000035
     * exif.TAG_GPS_LATITUDE: 37/1,30/1,472/100
     * exif.TAG_GPS_LONGITUDE: 127/1,1/1,3748/100
     */
    fun processImageUri(
        context: Context,
        uri: Uri
    ) {
        Log.d(TAG, "imageUri in processImageUri : $uri")
        _uiState.value = MapUiState.Loading
        try {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                val exif = ExifInterface(inputStream!!)
                //todo : 이미지 마다 각도가 다르다. 각도를 다 맞출수는 없나?
                /*val angle =
                    exif.getAttribute(ExifInterface.TAG_/)?.let {
                        convertToDegree(it).toDouble()
                    } ?: return@forEach*/

                val latitude =
                    exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let {
                        convertToDegree(it).toDouble()
                    } ?: return

                val longitude =
                    exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let {
                        convertToDegree(it).toDouble()
                    } ?: return

                Log.d(TAG, "exif.TAG_GPS_LATITUDE: $latitude")
                Log.d(TAG, "exif.TAG_GPS_LONGITUDE: $longitude")
                _uiState.value = MapUiState.Success(
                    listOf (
                        UserPhoto(
                            id = Random(5).nextLong().toString(),
                            uri = uri,
                            gpsLatitude = latitude,
                            gpsLongitude = longitude
                        )
                    )
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            _uiState.value = MapUiState.Error("Failed to process image : ${e.message}")
        }
    }

    /**
     * todo : TAG_GPS_LATITUDE_REF, TAG_GPS_LONGITUDE_REF 값을 이용해서 동/서 관련 로직 구현
     * https://velog.io/@im-shung/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%A0%88%EB%8C%80%EA%B2%BD%EB%A1%9C%EC%99%80-gps-%EC%A0%95%EB%B3%B4-%EA%B5%AC%ED%95%98%EA%B8%B0
     * imageUri : content://com.android.providers.media.documents/document/image%3A1000000035
     * exif.TAG_GPS_LATITUDE: 37/1,30/1,472/100
     * exif.TAG_GPS_LONGITUDE: 127/1,1/1,3748/100
     */
    fun processImageUri(
        context: Context,
        uriList: List<Uri>
    ) {
        _uiState.value = MapUiState.Loading
        val userPhotoList = mutableListOf<UserPhoto>()
        uriList.forEach { imageUri ->
            imageUri.let { uri ->
                Log.d(TAG, "imageUri : $uri")
                try {
                    context.contentResolver.openInputStream(uri).use { inputStream ->
                        val exif = ExifInterface(inputStream!!)
                        //todo : 이미지마다 각도가 다르다. 각도를 다 맞출수는 없나?
                        /*val angle =
                            exif.getAttribute(ExifInterface.TAG_/)?.let {
                                convertToDegree(it).toDouble()
                            } ?: return@forEach*/
                        val latitude =
                            exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let {
                                convertToDegree(it).toDouble()
                            } ?: return@forEach
                        val longitude =
                            exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let {
                                convertToDegree(it).toDouble()
                            } ?: return@forEach

                        Log.d(TAG, "exif.TAG_GPS_LATITUDE: ${latitude}")
                        Log.d(TAG, "exif.TAG_GPS_LONGITUDE: ${longitude}")
                        userPhotoList.add(
                            UserPhoto(
                                id = Random(5).nextLong().toString(),
                                uri = uri,
                                gpsLatitude = latitude,
                                gpsLongitude = longitude
                            )
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    _uiState.value = MapUiState.Error("Failed to process image : ${e.message}")
                }
            }
        }
        _uiState.value = MapUiState.Success(userPhotoList)
    }

    private fun convertToDegree(stringDMS: String): Float {
        var result: Float? = null
        val DMS = stringDMS.split(",".toRegex(), limit = 3).toTypedArray()
        val stringD = DMS[0].split("/".toRegex(), limit = 2).toTypedArray()
        val D0 = stringD[0].toDouble()
        val D1 = stringD[1].toDouble()
        val FloatD = D0 / D1
        val stringM = DMS[1].split("/".toRegex(), limit = 2).toTypedArray()
        val M0 = stringM[0].toDouble()
        val M1 = stringM[1].toDouble()
        val FloatM = M0 / M1
        val stringS = DMS[2].split("/".toRegex(), limit = 2).toTypedArray()
        val S0 = stringS[0].toDouble()
        val S1 = stringS[1].toDouble()
        val FloatS = S0 / S1
        result = (FloatD + FloatM / 60 + FloatS / 3600).toFloat()
        return result
    }

    fun resizeBitmap(uri: Uri, context: Context, targetWidth: Int, targetHeight: Int): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            inputStream?.close()
            inputStream = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return null
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height: Int = options.outHeight
        val width: Int = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}