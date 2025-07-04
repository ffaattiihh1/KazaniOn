// Generated by view binder compiler. Do not edit!
package com.kazanion.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kazanion.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentEditProfileBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final TextInputLayout addressLayout;

  @NonNull
  public final MaterialButton buttonChangePhoto;

  @NonNull
  public final MaterialButton buttonSave;

  @NonNull
  public final MaterialButton buttonSelectEndTime;

  @NonNull
  public final MaterialButton buttonSelectStartTime;

  @NonNull
  public final CheckBox checkboxFriday;

  @NonNull
  public final CheckBox checkboxMonday;

  @NonNull
  public final CheckBox checkboxSaturday;

  @NonNull
  public final CheckBox checkboxSunday;

  @NonNull
  public final CheckBox checkboxThursday;

  @NonNull
  public final CheckBox checkboxTuesday;

  @NonNull
  public final CheckBox checkboxWednesday;

  @NonNull
  public final TextInputEditText editTextAddress;

  @NonNull
  public final TextInputEditText editTextEmail;

  @NonNull
  public final TextInputEditText editTextName;

  @NonNull
  public final TextInputEditText editTextPhone;

  @NonNull
  public final TextInputLayout emailLayout;

  @NonNull
  public final TextInputLayout nameLayout;

  @NonNull
  public final TextInputLayout phoneLayout;

  @NonNull
  public final ShapeableImageView profileImage;

  @NonNull
  public final MaterialToolbar toolbar;

  private FragmentEditProfileBinding(@NonNull CoordinatorLayout rootView,
      @NonNull TextInputLayout addressLayout, @NonNull MaterialButton buttonChangePhoto,
      @NonNull MaterialButton buttonSave, @NonNull MaterialButton buttonSelectEndTime,
      @NonNull MaterialButton buttonSelectStartTime, @NonNull CheckBox checkboxFriday,
      @NonNull CheckBox checkboxMonday, @NonNull CheckBox checkboxSaturday,
      @NonNull CheckBox checkboxSunday, @NonNull CheckBox checkboxThursday,
      @NonNull CheckBox checkboxTuesday, @NonNull CheckBox checkboxWednesday,
      @NonNull TextInputEditText editTextAddress, @NonNull TextInputEditText editTextEmail,
      @NonNull TextInputEditText editTextName, @NonNull TextInputEditText editTextPhone,
      @NonNull TextInputLayout emailLayout, @NonNull TextInputLayout nameLayout,
      @NonNull TextInputLayout phoneLayout, @NonNull ShapeableImageView profileImage,
      @NonNull MaterialToolbar toolbar) {
    this.rootView = rootView;
    this.addressLayout = addressLayout;
    this.buttonChangePhoto = buttonChangePhoto;
    this.buttonSave = buttonSave;
    this.buttonSelectEndTime = buttonSelectEndTime;
    this.buttonSelectStartTime = buttonSelectStartTime;
    this.checkboxFriday = checkboxFriday;
    this.checkboxMonday = checkboxMonday;
    this.checkboxSaturday = checkboxSaturday;
    this.checkboxSunday = checkboxSunday;
    this.checkboxThursday = checkboxThursday;
    this.checkboxTuesday = checkboxTuesday;
    this.checkboxWednesday = checkboxWednesday;
    this.editTextAddress = editTextAddress;
    this.editTextEmail = editTextEmail;
    this.editTextName = editTextName;
    this.editTextPhone = editTextPhone;
    this.emailLayout = emailLayout;
    this.nameLayout = nameLayout;
    this.phoneLayout = phoneLayout;
    this.profileImage = profileImage;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentEditProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentEditProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_edit_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentEditProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.addressLayout;
      TextInputLayout addressLayout = ViewBindings.findChildViewById(rootView, id);
      if (addressLayout == null) {
        break missingId;
      }

      id = R.id.buttonChangePhoto;
      MaterialButton buttonChangePhoto = ViewBindings.findChildViewById(rootView, id);
      if (buttonChangePhoto == null) {
        break missingId;
      }

      id = R.id.buttonSave;
      MaterialButton buttonSave = ViewBindings.findChildViewById(rootView, id);
      if (buttonSave == null) {
        break missingId;
      }

      id = R.id.buttonSelectEndTime;
      MaterialButton buttonSelectEndTime = ViewBindings.findChildViewById(rootView, id);
      if (buttonSelectEndTime == null) {
        break missingId;
      }

      id = R.id.buttonSelectStartTime;
      MaterialButton buttonSelectStartTime = ViewBindings.findChildViewById(rootView, id);
      if (buttonSelectStartTime == null) {
        break missingId;
      }

      id = R.id.checkboxFriday;
      CheckBox checkboxFriday = ViewBindings.findChildViewById(rootView, id);
      if (checkboxFriday == null) {
        break missingId;
      }

      id = R.id.checkboxMonday;
      CheckBox checkboxMonday = ViewBindings.findChildViewById(rootView, id);
      if (checkboxMonday == null) {
        break missingId;
      }

      id = R.id.checkboxSaturday;
      CheckBox checkboxSaturday = ViewBindings.findChildViewById(rootView, id);
      if (checkboxSaturday == null) {
        break missingId;
      }

      id = R.id.checkboxSunday;
      CheckBox checkboxSunday = ViewBindings.findChildViewById(rootView, id);
      if (checkboxSunday == null) {
        break missingId;
      }

      id = R.id.checkboxThursday;
      CheckBox checkboxThursday = ViewBindings.findChildViewById(rootView, id);
      if (checkboxThursday == null) {
        break missingId;
      }

      id = R.id.checkboxTuesday;
      CheckBox checkboxTuesday = ViewBindings.findChildViewById(rootView, id);
      if (checkboxTuesday == null) {
        break missingId;
      }

      id = R.id.checkboxWednesday;
      CheckBox checkboxWednesday = ViewBindings.findChildViewById(rootView, id);
      if (checkboxWednesday == null) {
        break missingId;
      }

      id = R.id.editTextAddress;
      TextInputEditText editTextAddress = ViewBindings.findChildViewById(rootView, id);
      if (editTextAddress == null) {
        break missingId;
      }

      id = R.id.editTextEmail;
      TextInputEditText editTextEmail = ViewBindings.findChildViewById(rootView, id);
      if (editTextEmail == null) {
        break missingId;
      }

      id = R.id.editTextName;
      TextInputEditText editTextName = ViewBindings.findChildViewById(rootView, id);
      if (editTextName == null) {
        break missingId;
      }

      id = R.id.editTextPhone;
      TextInputEditText editTextPhone = ViewBindings.findChildViewById(rootView, id);
      if (editTextPhone == null) {
        break missingId;
      }

      id = R.id.emailLayout;
      TextInputLayout emailLayout = ViewBindings.findChildViewById(rootView, id);
      if (emailLayout == null) {
        break missingId;
      }

      id = R.id.nameLayout;
      TextInputLayout nameLayout = ViewBindings.findChildViewById(rootView, id);
      if (nameLayout == null) {
        break missingId;
      }

      id = R.id.phoneLayout;
      TextInputLayout phoneLayout = ViewBindings.findChildViewById(rootView, id);
      if (phoneLayout == null) {
        break missingId;
      }

      id = R.id.profileImage;
      ShapeableImageView profileImage = ViewBindings.findChildViewById(rootView, id);
      if (profileImage == null) {
        break missingId;
      }

      id = R.id.toolbar;
      MaterialToolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new FragmentEditProfileBinding((CoordinatorLayout) rootView, addressLayout,
          buttonChangePhoto, buttonSave, buttonSelectEndTime, buttonSelectStartTime, checkboxFriday,
          checkboxMonday, checkboxSaturday, checkboxSunday, checkboxThursday, checkboxTuesday,
          checkboxWednesday, editTextAddress, editTextEmail, editTextName, editTextPhone,
          emailLayout, nameLayout, phoneLayout, profileImage, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
