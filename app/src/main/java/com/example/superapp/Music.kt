package com.example.superapp

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eukaprotech.xmlprocessor.RssXmlProcessor
import com.eukaprotech.xmlprocessor.RssXmlToJSONArrayHandler
import kotlinx.android.synthetic.main.activity_music.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.*
import org.json.JSONArray
import java.io.IOException
import java.lang.String
import java.net.URL
import java.util.concurrent.TimeUnit


class Music : AppCompatActivity() {

    var player: MediaPlayer? = null
    val job: Job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        recyclerView.layoutManager = LinearLayoutManager(this)
        scope.launch(Dispatchers.IO) {
            net()
        }

        startButton.setOnClickListener {
            if (player?.isPlaying == true) {
                player?.stop()
                player?.reset()
                player?.release()
            } else {
                val url = "http://datashat.net/music_for_programming_11-miles_tilmann.mp3"
                player = MediaPlayer()
                try {
                    player?.setDataSource(url)
                    player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    player?.prepare()
                    player?.start()
                } catch (e: Exception) {
                    Log.i("Exception", "Exception in streaming mediaplayer e = $e")
                }


                player?.setOnCompletionListener {

                }
                player?.setOnBufferingUpdateListener { mp, percent ->
                    initializeSeekBar()
                }
            }

        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    player?.seekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })


    }

    @SuppressLint("SetTextI18n")
    private fun initializeSeekBar() {
        seekBar.max = player?.duration!!
        scope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                while (true) {
                    delay(1000)
                    seekBar.progress = player?.currentPosition!!

                    val diff = player?.duration!! - player?.currentPosition!!.toLong()
                    textView2.text = convertMillis(diff)
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun convertMillis(milliseconds2: Long): kotlin.String {
        val sdfsd = String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliseconds2) % 24,
            TimeUnit.MILLISECONDS.toMinutes(milliseconds2) % 60,
            TimeUnit.MILLISECONDS.toSeconds(milliseconds2) % 60
        )
        return sdfsd
    }

    private fun net() {
        try {
            val result = URL("https://www.musicforprogramming.net/rss.php").readText()
            var entryTag = "item"
            val entryKeys = ArrayList<kotlin.String>()
            val dataSet: ArrayList<MusicResponse> = ArrayList()
            entryKeys.add("title")
            entryKeys.add("duration")
            entryKeys.add("comments")
            val xmlProcessor = RssXmlProcessor(entryTag, entryKeys)
            xmlProcessor.execute(result, object : RssXmlToJSONArrayHandler {
                override fun onStart() {}
                override fun onSuccess(items: JSONArray) {
                    //consuming the processed items using the same keys
                    for (i in 0 until items.length()) {
                        try {

                            val item = items.getJSONObject(i)
                            dataSet.add(
                                MusicResponse(
                                    item.getString("title"),
                                    item.getString("duration"),
                                    item.getString("comments")
                                )
                            )
                            recyclerView.adapter = CustomAdapter(dataSet)
                            Log.d(
                                "Tar",
                                dataSet.toString()
                            )
                        } catch (ex: java.lang.Exception) {
                        }
                    }
                }

                override fun onFail(ex: java.lang.Exception) {
                    Log.d("Tar", ex.toString())
                }

                override fun onComplete() {}
            })
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("hello", e.toString())
        }
    }
}

class CustomAdapter(private val dataSet: ArrayList<MusicResponse>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    var player: MediaPlayer? = null
    val job1: Job = SupervisorJob()
    val scope1 = CoroutineScope(Dispatchers.IO + job1)

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameMusic: TextView
        val timeMusic: TextView
        val playMusic: ImageButton
        val seekBarMusic: SeekBar


        init {
            // Define click listener for the ViewHolder's View.
            nameMusic = view.findViewById(R.id.nameMusic)
            timeMusic = view.findViewById(R.id.timeMusic)
            playMusic = view.findViewById(R.id.playMusic)
            seekBarMusic = view.findViewById(R.id.seekBarMusic)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.music_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.nameMusic.text = dataSet[position].title
        viewHolder.timeMusic.text = dataSet[position].duration

        viewHolder.playMusic.setOnClickListener {
            if (player?.isPlaying == true) {
                player?.stop()
                player?.reset()
                player?.release()
                scope1.cancel()
            } else {
                val url = dataSet[position].comments
                player = MediaPlayer()
                try {
                    player?.setDataSource(url)
                    player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    player?.prepare()
                    player?.start()
                    scope1.run {  }
                } catch (e: Exception) {
                    Log.i("Exception", "Exception in streaming mediaplayer e = $e")
                }


                player?.setOnCompletionListener {

                }
                player?.setOnBufferingUpdateListener { mp, percent ->


                    viewHolder.seekBarMusic.max = player?.duration!!
                    scope1.launch(Dispatchers.IO) {

                        withContext(Dispatchers.Main) {
                            while (true) {
                                delay(1000)
                               viewHolder.seekBarMusic.progress = player?.currentPosition!!

                                val diff = player?.duration!! - player?.currentPosition!!.toLong()
                                viewHolder.timeMusic.text = convertMillis1(diff)
                            }
                        }
                    }

                }
            }

        }

        viewHolder.seekBarMusic.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    player?.seekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

    }


    @SuppressLint("DefaultLocale")
    private fun convertMillis1(milliseconds2: Long): kotlin.String {
        val sdfsd = String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliseconds2) % 24,
            TimeUnit.MILLISECONDS.toMinutes(milliseconds2) % 60,
            TimeUnit.MILLISECONDS.toSeconds(milliseconds2) % 60
        )
        return sdfsd
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}