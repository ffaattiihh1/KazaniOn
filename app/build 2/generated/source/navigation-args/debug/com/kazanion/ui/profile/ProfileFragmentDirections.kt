package com.kazanion.ui.profile

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.kazanion.R

public class ProfileFragmentDirections private constructor() {
  public companion object {
    public fun actionProfileFragmentToHomeFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_homeFragment)

    public fun actionProfileFragmentToRankingFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_rankingFragment)
  }
}
