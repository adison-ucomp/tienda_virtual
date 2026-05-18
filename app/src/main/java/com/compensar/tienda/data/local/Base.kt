package com.compensar.tienda.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.compensar.tienda.data.local.dao.AddressDao
import com.compensar.tienda.data.local.dao.CategoryDao
import com.compensar.tienda.data.local.dao.GatewayDao
import com.compensar.tienda.data.local.dao.ImageDao
import com.compensar.tienda.data.local.dao.PaymentDao
import com.compensar.tienda.data.local.dao.ProductDao
import com.compensar.tienda.data.local.dao.PurchaseDao
import com.compensar.tienda.data.local.dao.RoleDao
import com.compensar.tienda.data.local.dao.SellerDao
import com.compensar.tienda.data.local.dao.ShopDao
import com.compensar.tienda.data.local.dao.SpecifyDao
import com.compensar.tienda.data.local.dao.UserDao
import com.compensar.tienda.data.local.entity.AddressEntity
import com.compensar.tienda.data.local.entity.CategoryEntity
import com.compensar.tienda.data.local.entity.GatewayEntity
import com.compensar.tienda.data.local.entity.ImageEntity
import com.compensar.tienda.data.local.entity.PaymentEntity
import com.compensar.tienda.data.local.entity.ProductEntity
import com.compensar.tienda.data.local.entity.PurchaseEntity
import com.compensar.tienda.data.local.entity.RoleEntity
import com.compensar.tienda.data.local.entity.SellerEntity
import com.compensar.tienda.data.local.entity.ShopEntity
import com.compensar.tienda.data.local.entity.SpecifyEntity
import com.compensar.tienda.data.local.entity.UserEntity

@Database(
    entities = [
        AddressEntity::class,
        CategoryEntity::class,
        GatewayEntity::class,
        ImageEntity::class,
        PaymentEntity::class,
        ProductEntity::class,
        PurchaseEntity::class,
        RoleEntity::class,
        SellerEntity::class,
        ShopEntity::class,
        SpecifyEntity::class,
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