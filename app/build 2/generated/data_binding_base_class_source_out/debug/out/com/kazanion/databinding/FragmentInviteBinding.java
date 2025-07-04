// Generated by view binder compiler. Do not edit!
package com.kazanion.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.kazanion.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentInviteBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonShare;

  @NonNull
  public final TextView textInviteLink;

  @NonNull
  public final Toolbar toolbar;

  private FragmentInviteBinding(@NonNull ConstraintLayout rootView, @NonNull Button buttonShare,
      @NonNull TextView textInviteLink, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.buttonShare = buttonShare;
    this.textInviteLink = textInviteLink;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentInviteBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentInviteBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_invite, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentInviteBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonShare;
      Button buttonShare = ViewBindings.findChildViewById(rootView, id);
      if (buttonShare == null) {
        break missingId;
      }

      id = R.id.textInviteLink;
      TextView textInviteLink = ViewBindings.findChildViewById(rootView, id);
      if (textInviteLink == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new FragmentInviteBinding((ConstraintLayout) rootView, buttonShare, textInviteLink,
          toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
