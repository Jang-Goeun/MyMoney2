package com.mobile.myMoney

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mobile.myMoney.data.networt.Map.TMapRefService
import com.mobile.myMoney.databinding.ActivityViewMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewMapBinding
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    private lateinit var tMapRefService: TMapRefService

    var lat: String = ""
    var lon: String = ""

    private lateinit var googleMap: GoogleMap
    private val mapReadyCallback = object : OnMapReadyCallback {
        override fun onMapReady(map: GoogleMap) {
            googleMap = map
            Log.d(TAG, "GoogleMap is ready")

            // 지도의 특정 지점 클릭 이벤트 처리
            googleMap.setOnMapClickListener { latLng: LatLng ->
                Toast.makeText(applicationContext, latLng.toString(), Toast.LENGTH_SHORT).show()
            }

            // 지도의 특정 지점 롱클릭 이벤트 처리
            googleMap.setOnMapLongClickListener { latLng: LatLng ->
                Toast.makeText(applicationContext, latLng.toString(), Toast.LENGTH_SHORT).show()
            }

            addMarker(LatLng(37.606537, 127.041758))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TMapRefService 초기화
        tMapRefService = TMapRefService(this)

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(mapReadyCallback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 콜백 설정
        setupLocationCallback()

        binding.btnStart.setOnClickListener {
            checkPermissions()
            startLocationRequest()
        }

        binding.btnStop.setOnClickListener {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        // POI 검색 버튼
        binding.btnSearch.setOnClickListener {
            if (lat.isNotEmpty() && lon.isNotEmpty()) {
                fetchAndDisplayPOIs(lon, lat)
            } else {
                Toast.makeText(this, "현재 위치를 확인 후 다시 시도하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // Toolbar 설정
        setupToolbar()
    }

    // Toolbar 설정 함수
    private fun setupToolbar() {
        val drawable = resources.getDrawable(R.drawable.ic_arrow_back, null)
        val scaledDrawable = Bitmap.createScaledBitmap(
            (drawable as BitmapDrawable).bitmap,
            30, // 너비 (px 단위)
            30, // 높이 (px 단위)
            true
        )
        binding.toolbar.navigationIcon = BitmapDrawable(resources, scaledDrawable)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // 위치 콜백 설정 함수
    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val currentLocation: Location = locationResult.locations[0]
                lat = currentLocation.latitude.toString()
                lon = currentLocation.longitude.toString()

                val targetLoc = LatLng(currentLocation.latitude, currentLocation.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 17F))
                addMarker(targetLoc)
            }
        }
    }

    private lateinit var markerOptions: MarkerOptions
    private var centerMarker: Marker? = null

    fun addMarker(targetLoc: LatLng) {
        if (centerMarker == null) {
            // 마커가 없으면 새로 생성
            markerOptions = MarkerOptions().apply {
                position(targetLoc)
                title("내 위치")
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            centerMarker = googleMap.addMarker(markerOptions)
            centerMarker?.showInfoWindow()
        } else {
            // 기존 마커 위치 업데이트
            centerMarker?.position = targetLoc
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationRequest() {
        locationRequest = LocationRequest.Builder(3000)
            .setMinUpdateIntervalMillis(5000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // Permission 확인
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) ->
                Log.d(TAG, "정확한 위치 사용")
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) ->
                Log.d(TAG, "근사 위치 사용")
            else ->
                Log.d(TAG, "권한 미승인")
        }
    }

    private fun checkPermissions() {
        if ( checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "필요 권한 있음")
        } else {
            locationPermissionRequest.launch(
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun fetchAndDisplayPOIs(lon: String, lat: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 데이터 가져오기
                val pois = tMapRefService.searchPOIs(lon, lat)

                withContext(Dispatchers.Main) {
                    // 기존 내 위치 마커 정보 저장
                    val currentLocation = centerMarker?.position
                    centerMarker = null

                    googleMap.clear()

                    // 기존 내 위치 마커 다시 추가
                    if (currentLocation != null) {
                        addMarker(currentLocation)
                    }

                    // POI 마커 추가
                    pois.forEach { poi ->
                        val markerOptions = MarkerOptions()
                            .position(LatLng(poi.noorLat, poi.noorLon))
                            .title(poi.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        googleMap.addMarker(markerOptions)
                    }
                    Log.d("POI", "마커가 정상적으로 추가되었습니다.")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("POI", "POI 검색 중 오류 발생: ${e.message}")
                    Toast.makeText(this@MapViewActivity, "주변에서 마트를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}