package com.example.diagearandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.concurrent.atomic.AtomicBoolean
import com.example.diagearandroid.model.Pharmacy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class MapViewModel : ViewModel() {

    private val _pharmacies = MutableStateFlow<List<Pharmacy>>(emptyList())
    val pharmacies: StateFlow<List<Pharmacy>> = _pharmacies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedPharmacy = MutableStateFlow<Pharmacy?>(null)
    val selectedPharmacy: StateFlow<Pharmacy?> = _selectedPharmacy

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val pharmaciesRequested = AtomicBoolean(false)

    fun loadPharmaciesNear(lat: Double, lon: Double, radiusMeters: Int = 20000) {
        if (!pharmaciesRequested.compareAndSet(false, true)) return
        _userLocation.value = Pair(lat, lon)
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = withContext(Dispatchers.IO) { queryOverpass(lat, lon, radiusMeters) }
                _pharmacies.value = result
            } catch (e: Exception) {
                _error.value = "Failed to load pharmacies"
                pharmaciesRequested.set(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectPharmacy(pharmacy: Pharmacy?) {
        _selectedPharmacy.value = pharmacy
    }

    private fun queryOverpass(lat: Double, lon: Double, radius: Int): List<Pharmacy> {
        val query = "[out:json];node[\"amenity\"=\"pharmacy\"](around:$radius,$lat,$lon);out;"
        val encoded = URLEncoder.encode(query, "UTF-8")
        val response = URL("https://overpass-api.de/api/interpreter?data=$encoded").readText()
        val json = JSONObject(response)
        val elements = json.getJSONArray("elements")
        return List(elements.length()) { i ->
            val el = elements.getJSONObject(i)
            val tags = el.optJSONObject("tags") ?: JSONObject()
            Pharmacy(
                id = el.getLong("id"),
                name = tags.optString("name").ifEmpty { "Pharmacy" },
                lat = el.getDouble("lat"),
                lon = el.getDouble("lon"),
                address = buildAddress(tags)
            )
        }
    }

    private fun buildAddress(tags: JSONObject): String? {
        val street = tags.optString("addr:street", "")
        val number = tags.optString("addr:housenumber", "")
        val city = tags.optString("addr:city", "")
        val streetLine = "$street $number".trim()
        return listOf(streetLine, city).filter { it.isNotEmpty() }.joinToString(", ").ifEmpty { null }
    }
}
