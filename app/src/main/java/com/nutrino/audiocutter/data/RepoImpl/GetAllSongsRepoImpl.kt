package com.nutrino.audiocutter.data.RepoImpl

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.nutrino.audiocutter.data.DataClass.Song
import com.nutrino.audiocutter.domain.Repository.GetAllSongRepository
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject


@UnstableApi
class GetAllSongsRepoImpl @Inject constructor(private val context: Context): GetAllSongRepository {
    override suspend fun getAllSongs(): Flow<ResultState<List<Song>>> = flow{
        val songs = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ID

        )
        val contentResolver = context.contentResolver
        val selection = null
        emit(ResultState.Loading)
        try {
            val cursor = contentResolver.query(uri,projection,selection,null,null)
            cursor?.use {cursorelement->
                while (cursorelement.moveToNext()){
                    val id = cursorelement.getString(0)
                    val path = cursorelement.getString(1)
                    val size = cursorelement.getString(2)
                    val album = cursorelement.getString(3)
                    val title = cursorelement.getString(4)
                    val artist = cursorelement.getString(5)
                    val duration = cursorelement.getString(6)
                    val year = cursorelement.getString(7)
                    val composer = cursorelement.getString(8)
                    val albumID = cursorelement.getString(9)
                    val song = Song(
                        id=id,
                        path = path,
                        size = size,
                        album = album,
                        title = title,
                        artist = artist,
                        duration = duration,
                        year = year,
                        composer = composer,
                        albumId = albumID
                    )
                    songs.add(song)
                }
            }
            emit(ResultState.Success(data = songs))

        }catch (e: Exception){
            emit(ResultState.Error(message = e.message.toString()))

        }

    }

    override suspend fun getAllSongsForMerge(): Flow<ResultState<List<Song>>> = flow{
        val songs = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ID

        )
        val contentResolver = context.contentResolver
        val selection = null
        emit(ResultState.Loading)
        try {
            val cursor = contentResolver.query(uri,projection,selection,null,null)
            cursor?.use {cursorelement->
                while (cursorelement.moveToNext()){
                    val id = cursorelement.getString(0)
                    val path = cursorelement.getString(1)
                    val size = cursorelement.getString(2)
                    val album = cursorelement.getString(3)
                    val title = cursorelement.getString(4)
                    val artist = cursorelement.getString(5)
                    val duration = cursorelement.getString(6)
                    val year = cursorelement.getString(7)
                    val composer = cursorelement.getString(8)
                    val albumID = cursorelement.getString(9)
                    val song = Song(
                        id=id,
                        path = path,
                        size = size,
                        album = album,
                        title = title,
                        artist = artist,
                        duration = duration,
                        year = year,
                        composer = composer,
                        albumId = albumID
                    )
                    songs.add(song)
                }
            }
            emit(ResultState.Success(data = songs))

        }catch (e: Exception){
            emit(ResultState.Error(message = e.message.toString()))

        }

    }

    override suspend fun mergeSongs(
        uriList: List<Uri>,
        filename: String
    ): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        val resultChannel = Channel<ResultState<String>>()

        try {
            // Create EditedMediaItem for each song
            val editedMediaItems = uriList.map { uri ->
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()
                EditedMediaItem.Builder(mediaItem).build()
            }

            // Create sequence of edited media items
            val sequence = EditedMediaItemSequence(editedMediaItems)

            // Create composition from sequence
            val composition = Composition.Builder(listOf(sequence)).build()

            val outputFile = File(context.cacheDir, "$filename.m4a")

            val transformer = Transformer.Builder(context)
                .setAudioMimeType(MimeTypes.AUDIO_AAC)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        val savedUri = saveAudioFile(outputFile, "${filename}_${System.currentTimeMillis()}.m4a")
                        resultChannel.trySend(ResultState.Success(savedUri.toString()))
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        resultChannel.trySend(ResultState.Error(exportException.message ?: "Merge failed"))
                    }
                })
                .build()

            transformer.start(composition, outputFile.absolutePath)

            emit(resultChannel.receive())

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        }
    }

    private fun saveAudioFile(sourceFile: File, displayName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToDownloads(sourceFile, displayName)
        } else {
            saveToLegacyDownloads(sourceFile, displayName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloads(sourceFile: File, displayName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/AudioCutter")
        }

        val resolver = context.contentResolver
        val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(audioCollection, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            sourceFile.delete()
        }
        return uri
    }

    private fun saveToLegacyDownloads(sourceFile: File, displayName: String): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val audioCutterDir = File(downloadsDir, "AudioCutter")

        if (!audioCutterDir.exists()) {
            audioCutterDir.mkdirs()
        }

        val destFile = File(audioCutterDir, displayName)
        sourceFile.copyTo(destFile, overwrite = true)
        sourceFile.delete()

        return destFile.toUri()
    }
}