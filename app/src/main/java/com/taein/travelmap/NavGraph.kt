package com.taein.travelmap

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taein.travelmap.detailHotPlace.DetailHotPlaceScreen
import com.taein.travelmap.detailPhotoMarker.DetailPhotoMarkerScreen
import com.taein.travelmap.gallery.GalleryScreen
import com.taein.travelmap.map.MapScreen
import com.taein.travelmap.timeline.TimelineScreen


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
    startDestination: String = Destination.Map.route,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val upPress: () -> Unit = {
        navController.navigateUp()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom Bar를 표시할 화면들
    val bottomBarRoutes = listOf(
        Destination.Map.route,
        "Timeline",
        "Gallery"
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // 동일한 화면으로 다시 이동하지 않도록
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(
                route = Destination.Map.route
            ) {
                MapScreen(
                    onNavigateToDetailPhotoMarker = { markerId ->
                        Log.d(AppArgs.TAG, "markerId: $markerId")
                        navController.navigate(route = "${Destination.DetailPhotoMarker.route}/$markerId")
                    }
                )
            }
            composable(
                route = "Timeline"
            ) {
                TimelineScreen()
            }
            composable(
                route = "Gallery"
            ) {
                GalleryScreen()
            }
            composable(
                route = "${Destination.DetailPhotoMarker.route}/{$markerIdArg}",
                arguments = listOf(
                    navArgument(markerIdArg) { type = NavType.StringType }
                )
            ) {
                DetailPhotoMarkerScreen()
            }
            composable(
                route = Destination.DetailHotPlace.route
            ) {
                DetailHotPlaceScreen()
            }
        }
    }
}

internal const val markerIdArg = "markerId"

