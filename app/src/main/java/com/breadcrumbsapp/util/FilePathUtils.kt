package com.breadcrumbsapp.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import com.breadcrumbsapp.R
import java.io.*

object FilePathUtils {

    fun  passUri(data: Intent?, mContext : Context) : String?{

        var filename = ""
        try {
            val uri: Uri = data?.getData()!!
            if (false) {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.file_too_large),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val mimeType: String = mContext.getContentResolver().getType(uri)!!
                if (mimeType == null) {
                    val path = getPath(mContext, uri)
                    if (path == null) {
                        //filename = FilenameUtils.getName(uri.toString())
                    } else {
                        val file = File(path)
                        filename = file.name
                        return path
                    }
                } else {
                    val returnUri: Uri = data?.getData()!!
                    val returnCursor: Cursor? =
                        mContext.getContentResolver().query(returnUri, null, null, null, null)
                    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = returnCursor!!.getColumnIndex(OpenableColumns.SIZE)
                    returnCursor!!.moveToFirst()
                    filename = returnCursor.getString(nameIndex)
                    val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
                }
                val fileSave: File? = mContext.getExternalFilesDir(null)
                val sourcePath: String = mContext.getExternalFilesDir(null).toString()
                try {
                    copyFileStream(File("$sourcePath/$filename"), uri, mContext)
                    return "$sourcePath/$filename"
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    private fun copyFileStream(
        dest: File,
        uri: Uri,
        context: Context
    ) {
        var inputStream: InputStream? = null
        var os: OutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            os = FileOutputStream(dest)
            val buffer = ByteArray(1024)
            var length: Int = 0
            while (inputStream!!.read(buffer).also({ length = it }) > 0) {
                os.write(buffer, 0, length)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            inputStream!!.close()
            os!!.close()
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs =
                    arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }
        return null
    }

    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}