package com.example.diagearandroid.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.diagearandroid.R
import com.example.diagearandroid.model.Pharmacy
import com.example.diagearandroid.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val context = LocalContext.current
    val pharmacies by viewModel.pharmacies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedPharmacy by viewModel.selectedPharmacy.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            try {
                fusedClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    viewModel.loadPharmaciesNear(
                        lat = location?.latitude ?: 45.1,
                        lon = location?.longitude ?: 15.2
                    )
                }
            } catch (_: SecurityException) {
                viewModel.loadPharmaciesNear(45.1, 15.2)
            }
        }
    }

    if (!hasPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.location_permission_required),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(32.dp)
            )
        }
        return
    }

    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(7.0)
            controller.setCenter(GeoPoint(45.1, 15.2))
            onResume()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    // Zoom to user location once available
    LaunchedEffect(userLocation) {
        userLocation?.let { (lat, lon) ->
            mapView.controller.animateTo(GeoPoint(lat, lon))
            mapView.controller.setZoom(15.0)
        }
    }

    val pharmacyMarkers = remember { mutableListOf<Marker>() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            update = { view ->
                view.overlays.removeIf { it in pharmacyMarkers }
                pharmacyMarkers.clear()
                val markerIcon = BitmapDrawable(view.resources, createPharmacyMarkerBitmap(view.context))
                pharmacies.forEach { pharmacy ->
                    val marker = Marker(view).apply {
                        position = GeoPoint(pharmacy.lat, pharmacy.lon)
                        title = pharmacy.name
                        snippet = pharmacy.address ?: ""
                        icon = markerIcon
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        setOnMarkerClickListener { _, _ ->
                            viewModel.selectPharmacy(pharmacy)
                            true
                        }
                    }
                    pharmacyMarkers.add(marker)
                    view.overlays.add(marker)
                }
                view.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        error?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }
    }

    selectedPharmacy?.let { pharmacy ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectPharmacy(null) },
            sheetState = sheetState
        ) {
            PharmacyInfoSheet(pharmacy = pharmacy)
        }
    }
}

private fun createPharmacyMarkerBitmap(context: Context): Bitmap {
    val density = context.resources.displayMetrics.density
    val size = (40 * density).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val cx = size / 2f
    val cy = size / 2f
    val radius = size * 0.42f

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF2E5EA6.toInt()
        style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = size * 0.08f
    }
    val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = size * 0.16f
        strokeCap = Paint.Cap.ROUND
    }

    canvas.drawCircle(cx, cy, radius, circlePaint)
    canvas.drawCircle(cx, cy, radius, borderPaint)

    val arm = size * 0.22f
    canvas.drawLine(cx - arm, cy, cx + arm, cy, crossPaint)
    canvas.drawLine(cx, cy - arm, cx, cy + arm, crossPaint)

    return bitmap
}

@Composable
private fun PharmacyInfoSheet(pharmacy: Pharmacy) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = pharmacy.name,
            style = MaterialTheme.typography.titleLarge
        )
        pharmacy.address?.let { address ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
