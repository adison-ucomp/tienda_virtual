package com.compensar.tienda.default

import com.compensar.tienda.model.ModuleModel

object ModuleDefault {

    fun getAll(): List<ModuleModel> {
        return listOf(
            ModuleModel(register = 1L, name = "Modulos", model = "module", detail = "Administra los modulos del sistema", state = false),
            ModuleModel(register = 2L, name = "Direcciones", model = "address", detail = "Administra las direcciones de los usuarios", state = false),
            ModuleModel(register = 3L, name = "Categorias", model = "category", detail = "Administra las categorias de los productos", state = false),
            ModuleModel(register = 4L, name = "Pasarelas", model = "gateway", detail = "Administra las pasarelas del sistema", state = false),
            ModuleModel(register = 5L, name = "Imágenes", model = "image", detail = "Administra las imágenes de los productos", state = false),
            ModuleModel(register = 6L, name = "Pedidos", model = "order", detail = "Administra los pedidos de las compras", state = false),
            ModuleModel(register = 7L, name = "Metodos", model = "payment", detail = "Administra los metodos de pagos de las ordenes", state = false),
            ModuleModel(register = 8L, name = "Productos", model = "product", detail = "Administra los productos de las tiendas", state = false),
            ModuleModel(register = 9L, name = "Compras", model = "purchase", detail = "Administra las compras de los usuarios", state = false),
            ModuleModel(register = 10L, name = "Roles", model = "role", detail = "Administra los roles del sistema", state = false),
            ModuleModel(register = 11L, name = "Vendedores", model = "seller", detail = "Administra los vendedores de los usuarios", state = false),
            ModuleModel(register = 12L, name = "Envios", model = "shipment", detail = "Administra los envios de las ordenes", state = false),
            ModuleModel(register = 13L, name = "Tiendas", model = "shop", detail = "Administra las tiendas de los vendedores", state = false),
            ModuleModel(register = 14L, name = "Especifaciones", model = "specify", detail = "Administra las especificaciones de los productos", state = false),
            ModuleModel(register = 15L, name = "Ubicaciones", model = "ubication", detail = "Administra las ubicaciones de las direcciones", state = false),
            ModuleModel(register = 16L, name = "Usuarios", model = "user", detail = "Administra los usuarios del sistema", state = false),
            ModuleModel(register = 17L, name = "Transacciones", model = "trade", detail = "Administra los trasnacciones de las ordenes", state = false),
            ModuleModel(register = 18L, name = "Recuperados", model = "password", detail = "Administra las recuperaciones de contraseñas de los usuarios", state = false)
        )
    }
}
