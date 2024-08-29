package com.taein.travelmap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taein.travelmap.detailHotPlace.DetailHotPlaceScreen
import com.taein.travelmap.detailPhotoMarker.DetailPhotoMarkerScreen
import com.taein.travelmap.map.MapScreen


enum class Destination(val route: String) {
    Home("Home"),

    // BASIC
    Map("Map"),
    DetailHotPlace("DetailHotPlace"),
    DetailPhotoMarker("DetailPhotoMarker"),
    MapInColumn("MapInColumn"),
    MapClustering("MapClustering"),

    // OVERLAY
    Marker("Marker"),
    PolygonOverlay("PolygonOverlay"),
    PolylineOverlay("PolylineOverlay"),
    CircleOverlay("CircleOverlay"),
    LocationOverlay("LocationOverlay"),
    GroundOverlay("GroundOverlay"),
    PathOverlay("PathOverlay"),
    MultipartPathOverlay("MultipartPathOverlay"),
    ArrowheadPathOverlay("ArrowheadPathOverlay"),
    OverlayMinMaxZoom("OverlayMinMaxZoom"),
    GlobalZIndex("GlobalZIndex"),
    OverlayCollision("OverlayCollision"),

    // CAMERA
    CameraMove("CameraMove"),
    CameraAnimation("CameraAnimation"),
    CameraUpdateParams("CameraUpdateParams"),
    FitBounds("FitBounds"),
    Pivot("Pivot"),
    CameraEvent("CameraEvent"),

    // MAP
    MapTypesAndLayerGroups("MapTypesAndLayerGroups"),
    DisplayOptions("DisplayOptions"),
    IndoorMap("IndoorMap"),
    LiteMode("LiteMode"),
    NightMode("NightMode"),
    Locale("Locale"),

    // MAP OPTIONS
    MinMaxZoom("MinMaxZoom"),
    MaxTilt("MaxTilt"),
    Extent("Extent"),
    ContentPadding("ContentPadding"),
    ControlSettings("ControlSettings"),
    GestureSettings("GestureSettings"),

    // MAP EVENT
    MapClickEvent("MapClickEvent"),
    OverlayClickEvent("OverlayClickEvent"),
    SymbolClickEvent("SymbolClickEvent"),
    ZoomGesturesEvent("ZoomGesturesEvent"),

    // LOCATION
    LocationTracking("LocationTracking"),
    CustomLocationSource("CustomLocationSource"),
}

@Composable
fun NavGraph(
    startDestination: String = Destination.DetailPhotoMarker.route,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val upPress: () -> Unit = {
        navController.navigateUp()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Destination.Map.route) {
            MapScreen()
        }
        composable(Destination.DetailPhotoMarker.route) {
            DetailPhotoMarkerScreen()
        }
        composable(Destination.DetailHotPlace.route) {
            DetailHotPlaceScreen()
        }
    }
}
