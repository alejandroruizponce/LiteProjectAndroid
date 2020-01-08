package com.chekinlite.app.CurrentVersion.NavigationMenu

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.chekinlite.app.*
import com.chekinlite.app.CurrentVersion.Helpers.UserProfile
import com.chekinlite.app.OldVersionFiles.Fragment_Documents
import com.chekinlite.app.OldVersionFiles.fragment_Properties


class NavigationAcitivity : AppCompatActivity() {

    var currentFragment: Fragment =
        fragment_Booking()
    var propertiesFragment: Fragment =
        fragment_Properties()
    var newChekinFragment: Fragment =
        Fragment_NewChekin()
    var documentsFragment: Fragment =
        Fragment_Documents()
    var settingsFragment: Fragment =
        Fragment_Settings()

    lateinit var ft: FragmentTransaction


    var selectedTab = 0

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            ft = supportFragmentManager.beginTransaction()
            when (item.itemId) {

                R.id.navigation_bookings -> {
                    if (!currentFragment.isAdded) {
                        ft.add(R.id.fragmentNavigation, currentFragment, "Bookings Fragment")
                    } else {
                        println("Se esconde ${getVisibleFragment().tag}")
                        println("Se muestra Bookings Fragment")
                        ft.hide(getVisibleFragment())
                        ft.hide(documentsFragment)
                        ft.hide(propertiesFragment)
                        ft.hide(newChekinFragment)
                        ft.hide(settingsFragment)
                        ft.show(currentFragment)
                    }

                    ft.commit()
                    return@OnNavigationItemSelectedListener true
                }
                /*
                R.id.navigation_properties -> {
                    if (!propertiesFragment.isAdded) {
                        ft.add(R.id.fragmentNavigation, propertiesFragment, "Properties Fragment")
                    } else {
                        println("Se esconde ${getVisibleFragment().tag}")
                        println("Se muestra Properties Fragment")
                        ft.hide(getVisibleFragment())
                        ft.hide(currentFragment)
                        ft.hide(documentsFragment)
                        ft.hide(newChekinFragment)
                        ft.hide(settingsFragment)
                        ft.show(propertiesFragment)
                    }
                    ft.commit()
                    return@OnNavigationItemSelectedListener true
                }*/
                R.id.navigation_chekin -> {
                    if (!newChekinFragment.isAdded) {
                        ft.add(R.id.fragmentNavigation, newChekinFragment, "Chekin Fragment")
                    } else {
                        println("Se esconde ${getVisibleFragment().tag}")
                        println("Se muestra Chekin Fragment")
                        ft.hide(getVisibleFragment())
                        ft.hide(currentFragment)
                        ft.hide(propertiesFragment)
                        ft.hide(documentsFragment)
                        ft.hide(settingsFragment)
                        ft.show(newChekinFragment)
                    }

                    ft.commit()
                    return@OnNavigationItemSelectedListener true
                }
                /*
                R.id.navigation_documents -> {
                    if (!documentsFragment.isAdded) {
                        ft.add(R.id.fragmentNavigation, documentsFragment, "Documents Fragment")
                    } else {
                        println("Se esconde ${getVisibleFragment().tag}")
                        println("Se muestra Documents Fragment")
                        ft.hide(currentFragment)
                        ft.hide(propertiesFragment)
                        ft.hide(newChekinFragment)
                        ft.hide(settingsFragment)
                        ft.show(documentsFragment)
                    }
                    ft.commit()
                    return@OnNavigationItemSelectedListener true
                }*/

                R.id.navigation_settings -> {
                    if (!settingsFragment.isAdded) {
                        ft.add(R.id.fragmentNavigation, settingsFragment, "Settings Fragment")
                    } else {
                        println("Se esconde ${getVisibleFragment().tag}")
                        println("Se muestra Settings Fragment")
                        ft.hide(currentFragment)
                        ft.hide(propertiesFragment)
                        ft.hide(newChekinFragment)
                        ft.hide(documentsFragment)
                        ft.show(settingsFragment)
                    }
                    ft.commit()
                    return@OnNavigationItemSelectedListener true
                }

            }

            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        println("HEMOS VUELTO A NAVIGATION ACTIVITY ONCREATE")

        selectedTab = intent.getIntExtra("selectedTab", 0)





        val navigation =
            findViewById(R.id.navigationView) as BottomNavigationView
        UserProfile.navigationBar = navigation

        ft = supportFragmentManager.beginTransaction()


        if(selectedTab == 0) {
            navigation.setSelectedItemId(R.id.navigation_bookings);
            ft.add(R.id.fragmentNavigation, currentFragment)
            ft.commit()
        } /*else if(selectedTab == 1) {
            ft.add(R.id.fragmentNavigation, propertiesFragment)
            ft.commit()
            navigation.setSelectedItemId(R.id.navigation_properties);
        }*/

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)



    }

    override fun onResume() {
        super.onResume()

        println("HEMOS VUELTO A NAVIGATION ACTIVITY ON RESUME")

        if(UserProfile.needBookingReload) {
            val ft = supportFragmentManager.beginTransaction()
            ft.detach(currentFragment)
            ft.attach(currentFragment)
            ft.commit()
        } else {
            println("No hace falta recargar el fragment de Reservas")
        }
    }

    fun getVisibleFragment(): Fragment {
        var currentFrag = Fragment()

        currentFrag = supportFragmentManager.findFragmentById(R.id.fragmentNavigation)!!

        return currentFrag

    }

}
