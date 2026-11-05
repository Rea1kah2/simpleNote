package com.simplenote.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SportsBar
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object IconLibrary {
    private val iconMap: Map<String, ImageVector> = mapOf(
        "utensils" to Icons.Outlined.Restaurant,
        "car" to Icons.Outlined.DirectionsCar,
        "shopping-bag" to Icons.Outlined.ShoppingBag,
        "receipt" to Icons.Outlined.Receipt,
        "ticket" to Icons.Outlined.LocalActivity,
        "coffee" to Icons.Outlined.Coffee,
        "pill" to Icons.Outlined.Medication,
        "book" to Icons.Outlined.MenuBook,
        "heart" to Icons.Outlined.Favorite,
        "sparkles" to Icons.Outlined.AutoAwesome,
        "shopping-cart" to Icons.Outlined.ShoppingCart,
        "bike" to Icons.Outlined.DirectionsBike,
        "bus" to Icons.Outlined.DirectionsBus,
        "train" to Icons.Outlined.Train,
        "fuel" to Icons.Outlined.LocalGasStation,
        "plane" to Icons.Outlined.Flight,
        "map-pin" to Icons.Outlined.Place,
        "tag" to Icons.Outlined.LocalOffer,
        "package" to Icons.Outlined.Inventory2,
        "shirt" to Icons.Outlined.Checkroom,
        "credit-card" to Icons.Outlined.CreditCard,
        "wallet" to Icons.Outlined.AccountBalanceWallet,
        "briefcase" to Icons.Outlined.Work,
        "zap" to Icons.Outlined.Bolt,
        "droplet" to Icons.Outlined.WaterDrop,
        "wifi" to Icons.Outlined.Wifi,
        "phone" to Icons.Outlined.PhoneAndroid,
        "gamepad" to Icons.Outlined.SportsEsports,
        "tv" to Icons.Outlined.Tv,
        "music" to Icons.Outlined.Headphones,
        "camera" to Icons.Outlined.PhotoCamera,
        "heart-pulse" to Icons.Outlined.MonitorHeart,
        "dumbbell" to Icons.Outlined.FitnessCenter,
        "smile" to Icons.Outlined.SentimentSatisfied,
        "graduation-cap" to Icons.Outlined.School,
        "users" to Icons.Outlined.Group,
        "paw" to Icons.Outlined.Pets,
        "home" to Icons.Outlined.Home,
        "wrench" to Icons.Outlined.Build,
        "flame" to Icons.Outlined.LocalFireDepartment,
        "gift" to Icons.Outlined.CardGiftcard,
        "pizza" to Icons.Outlined.Fastfood,
        "cake" to Icons.Outlined.Cake,
        "beer" to Icons.Outlined.SportsBar,
        "apple" to Icons.Outlined.Restaurant
    )

    fun getIcon(name: String?): ImageVector {
        if (name == null) return Icons.Outlined.AutoAwesome
        return iconMap[name] ?: Icons.Outlined.AutoAwesome
    }

    val availableIconNames: List<String> = iconMap.keys.toList()
}

@Composable
fun AppIcon(
    name: String?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    Icon(
        imageVector = IconLibrary.getIcon(name),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}
