// Generated by view binder compiler. Do not edit!
package com.kazanion.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kazanion.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentLoginBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final MaterialButton buttonGoogle;

  @NonNull
  public final MaterialButton buttonLogin;

  @NonNull
  public final MaterialButton buttonRegister;

  @NonNull
  public final TextInputEditText editTextEmail;

  @NonNull
  public final TextInputEditText editTextPassword;

  @NonNull
  public final TextInputLayout emailLayout;

  @NonNull
  public final TextView forgotPasswordText;

  @NonNull
  public final ImageView imageViewLogo;

  @NonNull
  public final TextView orText;

  @NonNull
  public final TextInputLayout passwordLayout;

  @NonNull
  public final TextView textViewSubtitle;

  @NonNull
  public final TextView textViewTitle;

  private FragmentLoginBinding(@NonNull ConstraintLayout rootView,
      @NonNull MaterialButton buttonGoogle, @NonNull MaterialButton buttonLogin,
      @NonNull MaterialButton buttonRegister, @NonNull TextInputEditText editTextEmail,
      @NonNull TextInputEditText editTextPassword, @NonNull TextInputLayout emailLayout,
      @NonNull TextView forgotPasswordText, @NonNull ImageView imageViewLogo,
      @NonNull TextView orText, @NonNull TextInputLayout passwordLayout,
      @NonNull TextView textViewSubtitle, @NonNull TextView textViewTitle) {
    this.rootView = rootView;
    this.buttonGoogle = buttonGoogle;
    this.buttonLogin = buttonLogin;
    this.buttonRegister = buttonRegister;
    this.editTextEmail = editTextEmail;
    this.editTextPassword = editTextPassword;
    this.emailLayout = emailLayout;
    this.forgotPasswordText = forgotPasswordText;
    this.imageViewLogo = imageViewLogo;
    this.orText = orText;
    this.passwordLayout = passwordLayout;
    this.textViewSubtitle = textViewSubtitle;
    this.textViewTitle = textViewTitle;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonGoogle;
      MaterialButton buttonGoogle = ViewBindings.findChildViewById(rootView, id);
      if (buttonGoogle == null) {
        break missingId;
      }

      id = R.id.buttonLogin;
      MaterialButton buttonLogin = ViewBindings.findChildViewById(rootView, id);
      if (buttonLogin == null) {
        break missingId;
      }

      id = R.id.buttonRegister;
      MaterialButton buttonRegister = ViewBindings.findChildViewById(rootView, id);
      if (buttonRegister == null) {
        break missingId;
      }

      id = R.id.editTextEmail;
      TextInputEditText editTextEmail = ViewBindings.findChildViewById(rootView, id);
      if (editTextEmail == null) {
        break missingId;
      }

      id = R.id.editTextPassword;
      TextInputEditText editTextPassword = ViewBindings.findChildViewById(rootView, id);
      if (editTextPassword == null) {
        break missingId;
      }

      id = R.id.emailLayout;
      TextInputLayout emailLayout = ViewBindings.findChildViewById(rootView, id);
      if (emailLayout == null) {
        break missingId;
      }

      id = R.id.forgot_password_text;
      TextView forgotPasswordText = ViewBindings.findChildViewById(rootView, id);
      if (forgotPasswordText == null) {
        break missingId;
      }

      id = R.id.imageViewLogo;
      ImageView imageViewLogo = ViewBindings.findChildViewById(rootView, id);
      if (imageViewLogo == null) {
        break missingId;
      }

      id = R.id.orText;
      TextView orText = ViewBindings.findChildViewById(rootView, id);
      if (orText == null) {
        break missingId;
      }

      id = R.id.passwordLayout;
      TextInputLayout passwordLayout = ViewBindings.findChildViewById(rootView, id);
      if (passwordLayout == null) {
        break missingId;
      }

      id = R.id.textViewSubtitle;
      TextView textViewSubtitle = ViewBindings.findChildViewById(rootView, id);
      if (textViewSubtitle == null) {
        break missingId;
      }

      id = R.id.textViewTitle;
      TextView textViewTitle = ViewBindings.findChildViewById(rootView, id);
      if (textViewTitle == null) {
        break missingId;
      }

      return new FragmentLoginBinding((ConstraintLayout) rootView, buttonGoogle, buttonLogin,
          buttonRegister, editTextEmail, editTextPassword, emailLayout, forgotPasswordText,
          imageViewLogo, orText, passwordLayout, textViewSubtitle, textViewTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
