package kg.devcats.processflow.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object PictureUtil {

    fun compressImage(imagePath: String?, quality: Int): File? {
        val file = imagePath?.let { File(it) }
        return compressImage(file, quality)
    }

    fun compressImage(imageFile: File?, quality: Int): File? {
        if (imageFile == null) return null
        val bitmap = decidePictureOrientation(imageFile.absolutePath)
        return compressBitmap(bitmap, imageFile, quality)
    }

    private fun compressBitmap(bitmap: Bitmap, file: File, quality: Int): File? {
        val outputStream = FileOutputStream(file)
        return try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            if (!bitmap.isRecycled) bitmap.recycle()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            outputStream.close()
        }
    }

    fun deleteFile(file: File) {
        if (file.exists()) file.delete()
    }

    private fun decidePictureOrientation(picturePath: String): Bitmap {
        val ei = ExifInterface(picturePath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(BitmapFactory.decodeFile(picturePath), 90.0f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(BitmapFactory.decodeFile(picturePath), 180.0f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(BitmapFactory.decodeFile(picturePath), 270.0f)
            else -> BitmapFactory.decodeFile(picturePath)
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float, preventMirror: Boolean = false): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        if (preventMirror) matrix.preScale(-1.0f, 1.0f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun createTemporaryFiles(context: Context?, prefix: String, suffix: String): File {
        val directory = getExternalStorage(context, "/moy_o_registrar")!!
        when {
            directory.exists() -> directory.listFiles()?.forEach { it.deleteRecursively() }
            else -> directory.mkdirs()
        }
        val file = File.createTempFile(prefix, suffix, directory)
        return file
    }

    private fun getExternalStorage(context: Context?, directory: String): File {
        return when (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
            true -> File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directory)
            else -> File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directory)
        }
    }

}