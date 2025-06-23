package com.kazanion.ui.home

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.kazanion.R

public class HomeFragmentDirections private constructor() {
  public companion object {
    public fun actionHomeFragmentToProfileFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_profileFragment)

    public fun actionHomeFragmentToStoreFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_storeFragment)

    public fun actionHomeFragmentToWithdrawFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_withdrawFragment)

    public fun actionHomeFragmentToMapPollsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_mapPollsFragment)

    public fun actionHomeFragmentToPollsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_pollsFragment)

    public fun actionHomeFragmentToWalletFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_walletFragment)
  }
}
