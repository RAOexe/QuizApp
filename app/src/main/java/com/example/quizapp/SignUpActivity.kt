package com.example.quizapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.quizapp.databinding.ActivitySignUpBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import soup.neumorphism.NeumorphButton

class SignUpActivity : BaseActivity()

{

    private lateinit var etName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword: EditText

    private lateinit var mBinding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)


        mBinding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        val goToLoginButton = mBinding.btnGoToLogin //findViewById<TextView>(R.id.btn_go_to_login)

        etName =  mBinding.etName

        etEmail =  mBinding.etEmail

        etPassword = mBinding.etPassword


        goToLoginButton.setOnClickListener {
            finish()
        }

        val signUpButton =  mBinding.nbtnLogin

        signUpButton.setOnClickListener {
            signUpUser()
        }


    }


    private fun validateEntries(name: String, email: String, password: String): Boolean
    {
        if (TextUtils.isEmpty(name)) return false

        if (TextUtils.isEmpty(email)) return false

        if (TextUtils.isEmpty(password)) return false

        return true
    }


    private fun signUpUser()
    {

        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        /** checks if all the entries are filled or not
         */
        if (validateEntries(name, email, password))
        {

            /**
             * show loading dialog to user sign up task is being performed in background
             */
            showProgressDialog()

            /**
             * create user account using firebase
             */
            FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    /** if user account is created successfully
                     */
                    if (it.isSuccessful){

                        val user = FirebaseAuth.getInstance().currentUser!!

                        // add user information to fire-store

                        user.sendEmailVerification()
                            .addOnCompleteListener {

                                /**
                                 * hide loading dialog from screen
                                 */
                                hideProgressDialog()

                                /** if email is sent successfully
                                 */
                                if (it.isSuccessful)
                                {
                                    /**
                                     * if email sent successfully show verification email sent dialog
                                      */

                                    val intent = Intent(this, EmailVerificationActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                    FirebaseAuth.getInstance().signOut()
                                }
                                else {

                                    /**
                                     * if user entered invalid email
                                     */
                                    FirebaseAuth.getInstance().signOut()

                                    Toast.makeText(
                                        this, "Please enter an valid email id",
                                        Toast.LENGTH_LONG).show()
                                }

                            }
                            /** if some error occurs while sending email
                             */
                            .addOnFailureListener {

                                /**
                                 * hide loading dialog from screen
                                 */
                                hideProgressDialog()

                                Toast.makeText(
                                    this, it.localizedMessage,
                                    Toast.LENGTH_LONG).show()
                            }
                    }



                }
                /**
                 * if some error occurs while creating account
                 */
                .addOnFailureListener {

                    /**
                     * hide loading dialog from screen
                     */
                    hideProgressDialog()

                    Toast.makeText(
                        this, it.localizedMessage,
                        Toast.LENGTH_LONG).
                        show()
                }

        }

    }


}