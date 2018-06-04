package tinderz.com.cog.gmailintegration

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.plus.Plus
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_success.*

class SuccessActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal var mAuth: FirebaseAuth? = null
     var displayName:String?=null
     var gmailId:String?=null
     var imageUrl:String?=null
    private var mGoogleApiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build()

        mAuth = FirebaseAuth.getInstance()
        profile()
    }

    private fun profile() {
        if (mAuth?.currentUser != null) {

            println("last user method called")
            val user = mAuth?.currentUser
            displayName = user?.displayName.toString()
            gmailId = user?.email.toString()
            imageUrl = user?.photoUrl.toString()
            println("user details in already login " + displayName + " " + gmailId + " " + imageUrl)
            Glide.with(this).load(imageUrl).into(ivProfile)
            tvDispalyName.text=displayName
            tvEmail.text=gmailId
            btnLogOut.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        object : ResultCallback<Status> {
                            override fun onResult(status: Status) {
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                            }
                        })
            }

        }
    }
}
