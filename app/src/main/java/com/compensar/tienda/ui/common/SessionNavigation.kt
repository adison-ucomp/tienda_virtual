package com.compensar.tienda.ui.common

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.compensar.tienda.ui.buyer.BuyerCartShopActivity
import com.compensar.tienda.ui.home.HomeCategoryActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.home.HomeLoginActivity
import com.compensar.tienda.ui.home.HomeProductActivity
import com.compensar.tienda.ui.profile.ProfileAdminActivity
import com.compensar.tienda.ui.profile.ProfileBuyerActivity
import com.compensar.tienda.ui.profile.ProfileSellerActivity
import com.compensar.tienda.ui.profile.ProfileShareActivity
import com.compensar.tienda.ui.seller.SellerProductListActivity
import com.compensar.tienda.ui.seller.SellerOrderListActivity
import com.compensar.tienda.ui.seller.SellerDashboardActivity
import com.compensar.tienda.ui.setting.SettingAdminActivity
import com.compensar.tienda.ui.setting.SettingBuyerActivity
import com.compensar.tienda.ui.setting.SettingSellerActivity

/**
 * Objeto singleton [SessionNavigation].
 *
 * Responsable de la logica asociada al utilidad comun de interfaz y sesion.
 */
object SessionNavigation {

    fun bindProfile(activity: AppCompatActivity) {
        applySystemBarSpacing(activity)
        bindProfileByRole(activity)
        bindSharedMenu(activity)
        bindBuyerCart(activity)
        bindBuyerInferior(activity)
        applyBuyerSuperiorVisibility(activity)
    }

    private fun applySystemBarSpacing(activity: AppCompatActivity) {
        activity.enableEdgeToEdge()

        val mainView = activity.findViewById<View?>(R.id.main) ?: return
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun bindProfileByRole(activity: AppCompatActivity) {
        val actionAccount = activity.findViewById<LinearLayout?>(R.id.actionAccount)
        val navProfile = activity.findViewById<LinearLayout?>(R.id.navProfile)

        actionAccount?.setOnClickListener {
            openProfileOrLogin(activity)
        }

        navProfile?.setOnClickListener {
            openProfileOrLogin(activity)
        }

        activity.findViewById<LinearLayout?>(R.id.navHome)?.setOnClickListener {
            if (SessionManager.getRole(activity) == 2L && activity !is SellerDashboardActivity) {
                activity.startActivity(Intent(activity, SellerDashboardActivity::class.java))
            }
        }

        activity.findViewById<LinearLayout?>(R.id.navProducts)?.setOnClickListener {
            if (SessionManager.getRole(activity) == 2L && activity !is SellerProductListActivity) {
                activity.startActivity(Intent(activity, SellerProductListActivity::class.java))
            }
        }

        activity.findViewById<LinearLayout?>(R.id.navOrders)?.setOnClickListener {
            if (SessionManager.getRole(activity) == 2L && activity !is SellerOrderListActivity) {
                activity.startActivity(Intent(activity, SellerOrderListActivity::class.java))
            }
        }
    }


    private fun bindBuyerCart(activity: AppCompatActivity) {
        activity.findViewById<View?>(R.id.btnCart)?.setOnClickListener {
            openCartOrLogin(activity)
        }
    }

    private fun bindBuyerInferior(activity: AppCompatActivity) {
        activity.findViewById<LinearLayout?>(R.id.actionHome)?.setOnClickListener {
            when (SessionManager.getRole(activity)) {
                1L -> {
                    if (activity !is AdminDashboardActivity) {
                        activity.startActivity(Intent(activity, AdminDashboardActivity::class.java))
                    }
                }
                else -> {
                    if (activity !is HomeProductActivity) {
                        activity.startActivity(Intent(activity, HomeProductActivity::class.java))
                    }
                }
            }
        }

        activity.findViewById<LinearLayout?>(R.id.actionCategory)?.setOnClickListener {
            if (activity !is HomeCategoryActivity) {
                activity.startActivity(Intent(activity, HomeCategoryActivity::class.java))
            }
        }

        activity.findViewById<LinearLayout?>(R.id.actionShopping)?.setOnClickListener {
            if (SessionManager.getRole(activity) == 3L) {
                if (activity !is BuyerShoppingActivity) {
                    activity.startActivity(Intent(activity, BuyerShoppingActivity::class.java))
                }
            } else {
                activity.startActivity(Intent(activity, HomeLoginActivity::class.java))
            }
        }

        activity.findViewById<LinearLayout?>(R.id.actionAddress)?.setOnClickListener {
            if (SessionManager.getRole(activity) == 3L) {
                if (activity !is BuyerAddressActivity) {
                    activity.startActivity(Intent(activity, BuyerAddressActivity::class.java))
                }
            } else {
                activity.startActivity(Intent(activity, HomeLoginActivity::class.java))
            }
        }
    }

    fun openProfileOrLogin(activity: AppCompatActivity) {
        val intent = when (SessionManager.getRole(activity)) {
            1L -> Intent(activity, ProfileAdminActivity::class.java)
            2L -> Intent(activity, ProfileSellerActivity::class.java)
            3L -> Intent(activity, ProfileBuyerActivity::class.java)
            else -> Intent(activity, HomeLoginActivity::class.java)
        }

        activity.startActivity(intent)
    }

    private fun bindSharedMenu(activity: AppCompatActivity) {
        val buyerMenu = activity.findViewById<View?>(R.id.btnMenu)
        val sellerMenu = activity.findViewById<View?>(R.id.actionMenu)
        val isLogged = isLoggedIn(activity)

        buyerMenu?.visibility = if (isLogged) View.VISIBLE else View.GONE
        buyerMenu?.isEnabled = isLogged
        buyerMenu?.isClickable = isLogged

        sellerMenu?.visibility = if (isLogged) View.VISIBLE else View.GONE
        sellerMenu?.isEnabled = isLogged
        sellerMenu?.isClickable = isLogged

        val listener = {
            if (isLoggedIn(activity)) {
                val intent = Intent(activity, ProfileShareActivity::class.java)
                activity.startActivity(intent)
            }
        }

        buyerMenu?.setOnClickListener { listener() }
        sellerMenu?.setOnClickListener { listener() }
    }

    fun applyBuyerSuperiorVisibility(activity: AppCompatActivity) {
        val btnMenu = activity.findViewById<View?>(R.id.btnMenu)
        val isLogged = isLoggedIn(activity)

        btnMenu?.visibility = if (isLogged) View.VISIBLE else View.GONE
        btnMenu?.isEnabled = isLogged
        btnMenu?.isClickable = isLogged
    }

    fun applyBuyerInferiorVisibility(activity: AppCompatActivity) {
        val actionShopping = activity.findViewById<LinearLayout?>(R.id.actionShopping)
        val actionAddress = activity.findViewById<LinearLayout?>(R.id.actionAddress)

        val isBuyerLogged = isLoggedIn(activity) && SessionManager.getRole(activity) == 3L
        val visibility = if (isBuyerLogged) View.VISIBLE else View.GONE

        actionShopping?.visibility = visibility
        actionAddress?.visibility = visibility
        actionShopping?.isEnabled = isBuyerLogged
        actionAddress?.isEnabled = isBuyerLogged
        actionShopping?.isClickable = isBuyerLogged
        actionAddress?.isClickable = isBuyerLogged
    }

    fun openCartOrLogin(activity: AppCompatActivity) {
        val intent = if (isLoggedIn(activity)) {
            Intent(activity, BuyerCartShopActivity::class.java)
        } else {
            Intent(activity, HomeLoginActivity::class.java)
        }
        activity.startActivity(intent)
    }

    private fun isLoggedIn(activity: AppCompatActivity): Boolean {
        return SessionManager.getRegister(activity) > 0L && SessionManager.getRole(activity) > 0L
    }

    fun showProfileShareDrawer(activity: AppCompatActivity) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.profile_share)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val actionReturn = dialog.findViewById<TextView?>(R.id.actionReturn)
        val textName = dialog.findViewById<TextView>(R.id.textName)
        val textEmail = dialog.findViewById<TextView>(R.id.textEmail)
        val btnSettings = dialog.findViewById<LinearLayout>(R.id.btnSettings)
        val btnSupport = dialog.findViewById<LinearLayout>(R.id.btnSupport)
        val btnLogout = dialog.findViewById<LinearLayout>(R.id.btnLogout)

        textName.text = SessionManager.getFullName(activity)
        textEmail.text = SessionManager.getEmail(activity)

        actionReturn?.setOnClickListener {
            dialog.dismiss()
        }

        btnSettings.setOnClickListener {
            dialog.dismiss()
            val intent = when (SessionManager.getRole(activity)) {
                1L -> Intent(activity, SettingAdminActivity::class.java)
                2L -> Intent(activity, SettingSellerActivity::class.java)
                3L -> Intent(activity, SettingBuyerActivity::class.java)
                else -> Intent(activity, HomeLoginActivity::class.java)
            }
            activity.startActivity(intent)
        }

        btnSupport.setOnClickListener {
            Toast.makeText(activity, "Soporte", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            dialog.dismiss()
            SessionManager.clear(activity)

            val intent = Intent(activity, HomeProductActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = attributes.apply {
                width = (activity.resources.displayMetrics.widthPixels * 0.82).toInt()
                height = WindowManager.LayoutParams.MATCH_PARENT
                gravity = Gravity.START or Gravity.TOP
                dimAmount = 0.45f
            }
        }
    }
}

