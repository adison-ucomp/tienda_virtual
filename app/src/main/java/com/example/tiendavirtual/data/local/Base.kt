package com.example.tiendavirtual.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tiendavirtual.data.local.dao.AddressDao
import com.example.tiendavirtual.data.local.dao.CategoryDao
import com.example.tiendavirtual.data.local.dao.GatewayDao
import com.example.tiendavirtual.data.local.dao.ImageDao
import com.example.tiendavirtual.data.local.dao.PaymentDao
import com.example.tiendavirtual.data.local.dao.ProductDao
import com.example.tiendavirtual.data.local.dao.PurchaseDao
import com.example.tiendavirtual.data.local.dao.RoleDao
import com.example.tiendavirtual.data.local.dao.SellerDao
import com.example.tiendavirtual.data.local.dao.ShopDao
import com.example.tiendavirtual.data.local.dao.SpecifyDao
import com.example.tiendavirtual.data.local.dao.UserDao
import com.example.tiendavirtual.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun addressDao(): AddressDao
    abstract fun categoryDao(): CategoryDao
    abstract fun gatewayDao(): GatewayDao
    abstract fun imageDao(): ImageDao
    abstract fun paymentDao(): PaymentDao
    abstract fun productDao(): ProductDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun roleDao(): RoleDao
    abstract fun sellerDao(): SellerDao
    abstract fun shopDao(): ShopDao
    abstract fun specifyDao(): SpecifyDao
    abstract fun userDao(): UserDao
}