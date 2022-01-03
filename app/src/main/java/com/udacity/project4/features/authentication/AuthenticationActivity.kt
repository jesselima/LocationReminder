package com.udacity.project4.features.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes.*
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.BuildConfig
import com.udacity.project4.common.extensions.launchActivity
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.features.RemindersActivity
import com.udacity.project4.features.onboarding.OnBoardingActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { result: FirebaseAuthUIAuthenticationResult? ->
        onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener {
            startSignIn()
        }

        FirebaseAuth.getInstance().run {
            if (this.currentUser != null) {
                launchActivity<RemindersActivity>(shouldFinish = true)
            }
        }
    }

    private fun startSignIn() {
        binding.layoutContentAuthenticationIntro.isVisible = false

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.authentication_methods_background)
            .setGoogleButtonId(R.id.buttonLoginWithGoogle)
            .setEmailButtonId(R.id.buttonLoginWithEmail)
            .setTosAndPrivacyPolicyId(R.id.textTermsOfService)
            .build()

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
            ))
            .setTosAndPrivacyPolicyUrls(
                getString(R.string.url_terms_of_service),
                getString(R.string.url_privacy_policy))
            .setIsSmartLockEnabled(BuildConfig.DEBUG.not(), true)
            .setTheme(R.style.AuthScreenTheme)
            .setAuthMethodPickerLayout(customLayout)
            .build()
        signInLauncher.launch(signInIntent)
    }

    @SuppressLint("RestrictedApi")
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) {
        binding.layoutContentAuthenticationIntro.isVisible = true
        val isSuccess = result?.idpResponse?.isSuccessful ?: false
        val isNewUser = result?.idpResponse?.isNewUser ?: false

        if (isSuccess) {
            if(isNewUser) {
                launchActivity<OnBoardingActivity>(shouldFinish = true)
            } else {
                launchActivity<RemindersActivity>(shouldFinish = true)
            }
        } else {
            handleAuthenticationError(errorCode = result?.idpResponse?.error?.errorCode)
        }
    }

    private fun handleAuthenticationError(errorCode: Int?) {
        val errorMessage = when(errorCode) {
            NO_NETWORK ->
                getString(R.string.auth_error_not_network)
            PLAY_SERVICES_UPDATE_CANCELLED ->
                getString(R.string.auth_error_must_update_google_services)
            ERROR_USER_DISABLED ->
                getString(R.string.auth_error_account_disable)
            PROVIDER_ERROR ->
                getString(R.string.auth_error_email_does_not_match_provided)
            EMAIL_MISMATCH_ERROR ->
                getString(R.string.auth_error_email_mismatch)
            INVALID_EMAIL_LINK_ERROR ->
                getString(R.string.auth_error_invalid_email_link)
            EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR ->
                getString(R.string.auth_error_please_enter_email)
            EMAIL_LINK_WRONG_DEVICE_ERROR ->
                getString(R.string.auth_error_wrong_device)
            EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR ->
                getString(R.string.auth_error_define_linking_or_sign_in)
            EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR ->
                getString(R.string.auth_error_session_expired_or_cleared)
            else ->
                getString(R.string.auth_error_something_went_wrong)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
