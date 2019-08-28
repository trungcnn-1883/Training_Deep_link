package gooner.demo.training_deep_link

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class ViewDetailActivity : AppCompatActivity() {

    var mDesTxt: TextView? = null
    var mLinkTxt: TextView? = null
    var mGenBtn: Button? = null
    var mShareBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_detail)
        mDesTxt = findViewById(R.id.view_detail_des_txt)
        mLinkTxt = findViewById(R.id.view_link_txt)

        mGenBtn = findViewById(R.id.view_gen_btn)
        mShareBtn = findViewById(R.id.view_share_btn)

        if (intent != null) {
            Log.d("ViewDetailActivity", "intent != null")

            FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                        mDesTxt?.text = deepLink?.getQueryParameter("title")
                        Log.d("ViewDetailActivity1", "intent != null " + deepLink?.getQueryParameter("title"))


                    }
                }.addOnFailureListener(this) { exception ->
                    Log.d("ViewDetailActivity", "Fail with error {}")
                }
        } else {
            Log.d("ViewDetailActivity", "intent == null")
        }

        mGenBtn?.setOnClickListener {
            mLinkTxt?.text = generateContentLink().toString()
        }

        mShareBtn?.setOnClickListener {
            onShareClicked()
        }
    }

    fun generateContentLink(): Uri {
        val baseUrl = Uri.parse("https://gooner.demo/test/?title=Happy-to-meet-you")
        val domain = "https://gooner.page.link"

        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(domain)
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("gooner.demo.training_deep_link").build())
            .setIosParameters(DynamicLink.IosParameters.Builder("gooner.demo.iOS").build())
            .buildDynamicLink()

        return link.uri
    }

    private fun onShareClicked() {
        val link = generateContentLink()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link.toString())

        startActivity(Intent.createChooser(intent, "Share Link"))
    }
}