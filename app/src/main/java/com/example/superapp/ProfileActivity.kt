package com.example.superapp

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.*
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.IOException
import java.net.URL


class ProfileActivity : AppCompatActivity() {
    val job: Job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()

        findViewById<Button>(R.id.buttonLogout).setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })


        scope.launch(Dispatchers.IO) {
            val z = go()
            var countt = 0
            withContext(Dispatchers.Main) {
                while (true) {
                    delay(1000)
                    countt++
                    helloText.text = z + "$countt"
                }
            }
        }


    }

    private fun go(): String? {

        var allInfoCoronavirusDay: String? = null
        try {
            // Соединяемся с адресом и получаем документ
//            var document:Document = Jsoup.connect("https://xn----dtbfdbwspgnceulm.xn--p1ai/chart-online.php#kurs-valut-cb").get()
//            var title1 = document.select("#curs1 > tbody").text()

            //var document: Document = Jsoup.connect("https://yastat.net/s3/milab/2020/covid19-stat/data/data-by-region/11316.json?v=1606327117185").ignoreContentType(true).get()
            // val url = "https://yastat.net/s3/milab/2020/covid19-stat/data/data-by-region/11316.json?v=1606327117185"


            val result =
                URL("https://yastat.net/s3/milab/2020/covid19-stat/data/data-by-region/977.json?v=1606327117185/").readText()
            allInfoCoronavirusDay = "Регион: ${Response(result).nameRegion}" +
                    " Заражения: ${Response(result).cases}" +
                    " За сутки: ${Response(result).casesDelta}" +
                    " Выздоровления: ${Response(result).cured}" +
                    " За сутки: ${Response(result).curedDelta}" +
                    " Смерти: ${Response(result).deaths}" +
                    " За сутки: ${Response(result).deathsDelta}"

            Log.d("hello", allInfoCoronavirusDay + Thread.currentThread().name)

        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("hello", e.toString())
        }
        return allInfoCoronavirusDay
    }

    class Response(json: String) : JSONObject(json) {
        val type = this.optString("info")
        val nameRegion = Foo(type).name
        val cases = Foo(type).cases
        val casesDelta = Foo(type).casesDelta
        val cured = Foo(type).cured
        val curedDelta = Foo(type).curedDelta
        val deaths = Foo(type).deaths
        val deathsDelta = Foo(type).deathsDelta
    }

    class Foo(json: String) : JSONObject(json) {
        val name: String? = this.optString("name")
        val date = this.optInt("date")
        val shortNname: String? = this.optString("short_name")
        val population = this.optInt("population")
        val cases = this.optInt("cases")
        val casesDelta = this.optInt("cases_delta")
        val deaths = this.optInt("deaths")
        val deathsDelta = this.optInt("deaths_delta")
        val cured = this.optInt("cured")
        val curedDelta = this.optInt("cured_delta")
        val rt = this.optInt("rt")
        val fullName: String? = this.optString("full_name")

    }

}
