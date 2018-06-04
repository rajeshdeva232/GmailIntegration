package tinderz.com.cog.gmailintegration

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.plus.Plus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class ProgressDialog : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var displayName: String
    lateinit var imageUrl: String
    lateinit var gmaiId: String
    lateinit var googleId: String
    internal var mGoogleSignInClient: GoogleSignInClient? = null
    private val tag = "gmail Using Firebase"
    internal var mAuth: FirebaseAuth? = null
    var sharedprf: SharedPreferences? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_dialog)
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        sharedprf = PreferenceManager.getDefaultSharedPreferences(this)
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()
    }

    companion object {
        internal var rcSignIn = 234
    }

    override fun onStart() {
        super.onStart()
        if (mAuth?.currentUser != null) {
            finish()
            println("last user method called")
            val user = mAuth?.currentUser
            displayName = user?.displayName.toString()
            gmaiId = user?.email.toString()
            imageUrl = user?.photoUrl.toString()
            googleId = user?.uid.toString()
            firstName = displayName.split(" ").get(0)
            lastName = displayName.split(" ").get(1)
            sharedprf?.edit()?.putString("googleId",googleId)?.apply()
            println("user details in already login " + displayName + " " + gmaiId + " " + imageUrl + " " + googleId + " " + firstName + " " + lastName)

        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                startActivity(Intent(this, MainActivity::class.java))

            }

        }
    }

    public fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth?.currentUser
                        displayName = user?.displayName.toString().trim()
                        gmaiId = user?.email.toString().trim()
                        imageUrl = user?.photoUrl.toString().trim()
                        googleId = user?.uid.toString().trim()
                        val signInStatus:Boolean = true
                        sharedprf?.edit()?.putString("googleId",googleId)?.apply()
                        sharedprf?.edit()?.putString("signInStatus", signInStatus.toString())?.apply()
                        firstName = displayName.split(" ").get(0).trim()
                        lastName = displayName.split(" ").get(1).trim()
                        println("user details in main activity " + displayName + " " + gmaiId + " " + imageUrl + " " + googleId + " " + firstName + " " + lastName)

                        intent = Intent(this, SuccessActivity::class.java)
                        startActivity(intent)
                        Log.d(tag, "signInWithCredential:success")


                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        // If sign in fails, display a message to the user.
                        Log.w(tag, "signInWithCredential:failure", task.exception)

                    }


                }
    }

    public fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }


}
