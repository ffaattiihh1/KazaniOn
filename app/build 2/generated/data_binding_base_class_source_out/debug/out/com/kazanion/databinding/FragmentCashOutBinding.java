// Generated by view binder compiler. Do not edit!
package com.kazanion.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public final class FragmentCashOutBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final MaterialButton buttonConfirmCashOut;

  @NonNull
  public final TextInputEditText editTextAmount;

  @NonNull
  public final TextInputLayout textInputLayoutAmount;

  @NonNull
  public final TextView textViewAvailablePoints;

  @NonNull
  public final TextView textViewCashOutTitle;

  @NonNull
  public final TextView textViewCurrentBalance;

  private FragmentCashOutBinding(@NonNull ConstraintLayout rootView,
      @NonNull MaterialButton buttonConfirmCashOut, @NonNull TextInputEditText editTextAmount,
      @NonNull TextInputLayout textInputLayoutAmount, @NonNull TextView textViewAvailablePoints,
      @NonNull TextView textViewCashOutTitle, @NonNull TextView textViewCurrentBalance) {
    this.rootView = rootView;
    this.buttonConfirmCashOut = buttonConfirmCashOut;
    this.editTextAmount = editTextAmount;
    this.textInputLayoutAmount = textInputLayoutAmount;
    this.textViewAvailablePoints = textViewAvailablePoints;
    this.textViewCashOutTitle = textViewCashOutTitle;
    this.textViewCurrentBalance = textViewCurrentBalance;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentCashOutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentCashOutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_cash_out, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentCashOutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonConfirmCashOut;
      MaterialButton buttonConfirmCashOut = ViewBindings.findChildViewById(rootView, id);
      if (buttonConfirmCashOut == null) {
        break missingId;
      }

      id = R.id.editTextAmount;
      TextInputEditText editTextAmount = ViewBindings.findChildViewById(rootView, id);
      if (editTextAmount == null) {
        break missingId;
      }

      id = R.id.textInputLayoutAmount;
      TextInputLayout textInputLayoutAmount = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayoutAmount == null) {
        break missingId;
      }

      id = R.id.textViewAvailablePoints;
      TextView textViewAvailablePoints = ViewBindings.findChildViewById(rootView, id);
      if (textViewAvailablePoints == null) {
        break missingId;
      }

      id = R.id.textViewCashOutTitle;
      TextView textViewCashOutTitle = ViewBindings.findChildViewById(rootView, id);
      if (textViewCashOutTitle == null) {
        break missingId;
      }

      id = R.id.textViewCurrentBalance;
      TextView textViewCurrentBalance = ViewBindings.findChildViewById(rootView, id);
      if (textViewCurrentBalance == null) {
        break missingId;
      }

      return new FragmentCashOutBinding((ConstraintLayout) rootView, buttonConfirmCashOut,
          editTextAmount, textInputLayoutAmount, textViewAvailablePoints, textViewCashOutTitle,
          textViewCurrentBalance);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
