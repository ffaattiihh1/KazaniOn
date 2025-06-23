package com.kazanion.ui.story

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class StoryViewFragmentArgs(
  public val storyTitle: String = "Duyuru",
  public val storyDescription: String = "Liderler Kazanıyor!",
  public val storyImageUrl: String? = null,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("storyTitle", this.storyTitle)
    result.putString("storyDescription", this.storyDescription)
    result.putString("storyImageUrl", this.storyImageUrl)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("storyTitle", this.storyTitle)
    result.set("storyDescription", this.storyDescription)
    result.set("storyImageUrl", this.storyImageUrl)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): StoryViewFragmentArgs {
      bundle.setClassLoader(StoryViewFragmentArgs::class.java.classLoader)
      val __storyTitle : String?
      if (bundle.containsKey("storyTitle")) {
        __storyTitle = bundle.getString("storyTitle")
        if (__storyTitle == null) {
          throw IllegalArgumentException("Argument \"storyTitle\" is marked as non-null but was passed a null value.")
        }
      } else {
        __storyTitle = "Duyuru"
      }
      val __storyDescription : String?
      if (bundle.containsKey("storyDescription")) {
        __storyDescription = bundle.getString("storyDescription")
        if (__storyDescription == null) {
          throw IllegalArgumentException("Argument \"storyDescription\" is marked as non-null but was passed a null value.")
        }
      } else {
        __storyDescription = "Liderler Kazanıyor!"
      }
      val __storyImageUrl : String?
      if (bundle.containsKey("storyImageUrl")) {
        __storyImageUrl = bundle.getString("storyImageUrl")
      } else {
        __storyImageUrl = null
      }
      return StoryViewFragmentArgs(__storyTitle, __storyDescription, __storyImageUrl)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): StoryViewFragmentArgs {
      val __storyTitle : String?
      if (savedStateHandle.contains("storyTitle")) {
        __storyTitle = savedStateHandle["storyTitle"]
        if (__storyTitle == null) {
          throw IllegalArgumentException("Argument \"storyTitle\" is marked as non-null but was passed a null value")
        }
      } else {
        __storyTitle = "Duyuru"
      }
      val __storyDescription : String?
      if (savedStateHandle.contains("storyDescription")) {
        __storyDescription = savedStateHandle["storyDescription"]
        if (__storyDescription == null) {
          throw IllegalArgumentException("Argument \"storyDescription\" is marked as non-null but was passed a null value")
        }
      } else {
        __storyDescription = "Liderler Kazanıyor!"
      }
      val __storyImageUrl : String?
      if (savedStateHandle.contains("storyImageUrl")) {
        __storyImageUrl = savedStateHandle["storyImageUrl"]
      } else {
        __storyImageUrl = null
      }
      return StoryViewFragmentArgs(__storyTitle, __storyDescription, __storyImageUrl)
    }
  }
}
