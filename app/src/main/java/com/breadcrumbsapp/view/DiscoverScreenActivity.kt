package com.breadcrumbsapp.view


/*

Details....

1. On marker click method for marker click
2. searchbutton.setonclick - for search button
3. com.breadcrumbsapp.service.LocationUpdatesService -> fun onNewLocation()
4.  getTrailListFromAPI() - This method helps to load POI
5.  POI Image name - poi_breadcrumbs_marker_undiscovered_1,poi_breadcrumbs_marker_undiscovered_2
6.  ch_type = 0 means, selfie. Else, quiz.

 */

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.R.drawable.*
import com.breadcrumbsapp.R.id.checkBox
import com.breadcrumbsapp.R.layout.trail_layout
import com.breadcrumbsapp.R.style.FirebaseUI_Transparent
import com.breadcrumbsapp.adapter.*
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.interfaces.POIclickedListener
import com.breadcrumbsapp.model.DistanceMatrixApiModel
import com.breadcrumbsapp.model.GetEventsModel
import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.service.LocationUpdatesService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.RecyclerItemClickListenr
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.profile.ProfileScreenActivity
import com.breadcrumbsapp.view.rewards.RewardsScreenActivity
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.android.synthetic.main.available_trails_layout.*
import kotlinx.android.synthetic.main.discover_screen_activity.*
import kotlinx.android.synthetic.main.discover_screen_bottom_layout.*
import kotlinx.android.synthetic.main.leader_board_activity_layout.*
import kotlinx.android.synthetic.main.more_option_layout.*
import kotlinx.android.synthetic.main.quiz_challenge_question_activity.*
import kotlinx.android.synthetic.main.trails_screen_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.buffer
import okio.source
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.URL
import java.nio.charset.Charset
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set


class DiscoverScreenActivity : FragmentActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, POIclickedListener, GoogleMap.OnMarkerDragListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraIdleListener,
    GoogleMap.OnCameraMoveStartedListener {

    private lateinit var mMap: GoogleMap
    private var latLng: LatLng? = null //LatLng(1.40259300, 103.78974600)
    private var latLng1: LatLng? = null

    private lateinit var sharedPreference: SessionHandlerClass
    private lateinit var trailTutorialAdapter: TrailTutorialAdapter
    private var isClicked = false
    private var isCheckBoxClicked = false
    private lateinit var currentLocation: Button
    private lateinit var mapListToggleButton: ToggleButton
    private var isListScreen: Boolean = false
    private lateinit var backButton: Button
    private lateinit var searchButton: Button
    private lateinit var searchView: SearchView
    private lateinit var searchViewTextView: TextView
    private lateinit var searchLayout: RelativeLayout
    private lateinit var trailsBtn: Button
    private lateinit var profileLayout: LinearLayoutCompat
    private lateinit var markWindowConstraintLayout: LinearLayoutCompat
    private lateinit var markerWindow_background: LinearLayoutCompat
    private lateinit var markerWindowDiscoverTextView: TextView
    private lateinit var markWindowPOIIcon: ImageView
    private lateinit var trailsListLayout: LinearLayoutCompat
    private lateinit var listScreenRecycler: RecyclerView
    private lateinit var trailsListAdapter: TrailsListAdapter
    private lateinit var mapMainLayout: LinearLayoutCompat
    private lateinit var takeMeBtn: TextView

    private lateinit var playerName: TextView

    //lateinit var profileImage: ImageView
    private lateinit var trailsNameText: TextView


    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val accessToken: String =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjQ3MDEiLCJlbWFpbCI6InRlc3RkZXZpY2VicmVhZEBnbWFpbC5jb20iLCJwcm9maWxlX3BpY3R1cmUiOiJ1cGxvYWRzXC80NzAxXC9wcm9maWxlX3BpY18xOTMyMDIxMTUyMDQzLmpwZyIsInVzZXJuYW1lIjoiQXJ1biBrdW1hciIsInBhc3N3b3JkIjoiIiwicmFuayI6IjEiLCJmaW5pc2hlZF9wb2lzIjoiMCIsImZpbmlzaGVkX3RyYWlscyI6IjAiLCJleHBlcmllbmNlIjoiNDIwMCIsImlzX3Nwb25zb3IiOiIwIiwicmVnaXN0ZXJfcGxhdGZvcm0iOiIxIiwiY3JlYXRlZCI6IjIwMjEtMDQtMTkgMDQ6NDI6MzgiLCJ1cGRhdGVkIjoiMjAyMS0wNC0xOSAwNDo0MjozOCIsIkFQSV9USU1FIjoxNjE5NjA0MjgyfQ.HIvUDe4sTB5kyVstz33ShSylWUObN3C3cTf8zS_ayOs"

    private var interceptor = intercept()
    private lateinit var trailName: String
    private lateinit var poiDistance: TextView

    private var selectedPOIName: String = ""
    private var selectedPOIID: String = ""
    private var selectedPOIImage: String = ""
    private var selectedPOIDuration: String = ""
    private var selectedPOIQuestion: String = ""
    private var selectedPOIHintContent: String = ""
    private var selectedPOIChallengeType: String = ""
    private var selectedPOIARid: String = ""
    private var selectedPOIDiscoverId: String = ""
    private var selectedPOIQrCode: String = ""
    private var firstRadioChecked: Boolean = false
    private var secondRadioChecked: Boolean = false
    private var polyline: Polyline? = null
    private var isMarkerClicked: Boolean = false
    private var isDrawPathClicked: Boolean = false


    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    private var mBound: Boolean = false

    private val MYPERMISSIONSREQUESTLOCATION = 68
    private val REQUESTCHECKSETTINGS = 129

    private var broadcastReceiver: BroadcastReceiver? = null


    private var currentLocationMarker: Marker? = null
    private var clickedMarker: Marker? = null
    private var markerPOI: Marker? = null


    private lateinit var inputMethodManager: InputMethodManager

    private lateinit var closeButtonLayout: LinearLayoutCompat

    private lateinit var markerWindowCloseButton: ImageView

    private var searchAdapter: POI_SearchAdapter? = null
    lateinit var recyclerView: RecyclerView
    private lateinit var levelTextView: TextView

    private var matchedPOIMarker: ArrayList<GetEventsModel.Message> = arrayListOf()

    private var locationPermissionGranted = false


    private var distanceMatrixApiModelObj: ArrayList<DistanceMatrixApiModel> = arrayListOf()

    lateinit var distance: String
    lateinit var duration: String

    private var markerAnimationHandler = Handler(Looper.getMainLooper())


    private var isDiscovered: Boolean = false
    private var doubleBackToExitPressedOnce = false
    private var isSelectedMarker = false
    private var isGesturedOnMap = false

    private var moreButtonClicked = 0
    private lateinit var getTrailsData: GetTrailsModel

    //ranking,level,base,nextLevel

    private lateinit var vibrator: Vibrator
    fun readJsonFromAssets(context: Context, filePath: String): String? {
        try {
            val source = context.assets.open(filePath).source().buffer()
            return source.readByteString().string(Charset.forName("utf-8"))

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission", "ResourceType", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.discover_screen_activity)


        val policy = ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)

        sharedPreference = SessionHandlerClass(applicationContext)
        trailTutorialAdapter = TrailTutorialAdapter()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        Glide.with(applicationContext).load(R.raw.loading).into(loadingImage)
        getRankingDetails()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@DiscoverScreenActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MYPERMISSIONSREQUESTLOCATION
            )
        } else {
            Log.e("MainActivity:", "Location Permission Already Granted")
            if (getLocationMode() == 3) {
                Log.e("MainActivity:", "Already set High Accuracy Mode")
                initializeService()
            } else {
                Log.e("MainActivity:", "Alert Dialog Shown")
                showAlertDialog(this@DiscoverScreenActivity)
            }
        }
        // Get the trail list
        // getTrailDetails()

        val jsonFileString = readJsonFromAssets(applicationContext, "trails.json")

        getTrailsData = Gson().fromJson(jsonFileString, GetTrailsModel::class.java)
        print("CommonData.getTrailsData = ${jsonFileString.toString()}")
        CommonData.getTrailsData = getTrailsData.message

        getUserDetails()

        // Getting Crash .. when login after install from slack ro gdrive

        try {
            val expPoints: Int =
                Integer.parseInt(sharedPreference.getSession("player_experience_points"))
            println("expPoints= $expPoints")
            calculateUserLevel(expPoints)
        } catch (e: Exception) {
            e.printStackTrace()
        }



        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    "NotifyUser" -> {
                        try {

                            val lat = intent.getStringExtra("pinned_location_lat")
                            val long = intent.getStringExtra("pinned_location_long")

                            println("Lat = $lat , $long")
                            if (latLng == null) {
                                latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
                                mapFragment.getMapAsync(this@DiscoverScreenActivity)
                            }

                            latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
                            println("Updated Location : $latLng")
                            // if (takeMeBtn.text.equals(resources.getString(R.string.travelling))) {
                            //  updateCurrentLocation(latLng)
                            //}

                            if (isDrawPathClicked) {
                                updateCurrentLocation(latLng)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }
                }
            }
        }

        mapFragment.getMapAsync(this@DiscoverScreenActivity)
        currentLocation = findViewById(R.id.currentLocation)
        mapListToggleButton = findViewById(R.id.chkState)
        backButton = findViewById(R.id.backButton)
        searchButton = findViewById(R.id.searchButton)
        searchView = findViewById(R.id.searchView)
        searchLayout = findViewById(R.id.searchLayout)
        trailsBtn = findViewById(R.id.trailsBtn)
        profileLayout = findViewById(R.id.profileLayout)
        markWindowConstraintLayout = findViewById(R.id.markInfoLayout)
        markerWindow_background = findViewById(R.id.markerWindow_background)
        markerWindowDiscoverTextView = findViewById(R.id.markerWindowDiscoverTextView)
        markWindowPOIIcon = findViewById(R.id.markWindowPOIIcon)
        trailsListLayout = findViewById(R.id.trailsListLayout)
        listScreenRecycler = findViewById(R.id.trails_recyclerview)
        mapMainLayout = findViewById(R.id.mapMainLayout)
        takeMeBtn = findViewById(R.id.takeMeThereBtn)
        playerName = findViewById(R.id.playerName)
        closeButtonLayout = findViewById(R.id.closeButtonLayout)
        markerWindowCloseButton = findViewById(R.id.closeButton)
        // profileImage = findViewById(R.id.profileImage)
        poiDistance = findViewById(R.id.tv_distance)
        trailsNameText = findViewById(R.id.trailsName)
        recyclerView = findViewById(R.id.recyclerView)
        levelTextView = findViewById(R.id.level_textView)

        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (!sharedPreference.getSession("player_name").equals("")) {
            playerName.text = sharedPreference.getSession("player_name")
        } else {
            playerName.text = "Player 1"
        }
        listScreenRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        listScreenRecycler.overScrollMode = View.OVER_SCROLL_NEVER

        feedIcon.setOnClickListener {
            startActivity(Intent(applicationContext, FeedPostActivity::class.java))
        }
        trailsIcon.setOnClickListener {
            startActivity(Intent(applicationContext, TrailScreenActivity::class.java))
        }
        moreIcon.setOnClickListener {


            if (moreButtonClicked == 0) {
                moreButtonClicked = 1
                currentLocationLayout.visibility = View.GONE
                more_option_layout_header.visibility = View.VISIBLE
            } else {
                more_option_layout_header.visibility = View.GONE
                currentLocationLayout.visibility = View.VISIBLE
                moreButtonClicked = 0
            }
        }
        leaderboardIcon.setOnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE
            startActivity(Intent(applicationContext, LeaderBoardActivity::class.java))
            //Toast.makeText(applicationContext,"Under Construction!",1000).show()

        }

        how_to_play_sub_layout.setOnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE
            startActivity(Intent(applicationContext, HowToPlayActivity::class.java))

        }

        settings_sub_layout.setOnClickListener(View.OnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE
            startActivity(Intent(applicationContext, SettingsScreenAct::class.java))
        })

        rewards_sub_layout.setOnClickListener(View.OnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE

            startActivity(Intent(applicationContext, RewardsScreenActivity::class.java))
        })

        profile_sub_layout.setOnClickListener(View.OnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE

            startActivity(Intent(applicationContext, ProfileScreenActivity::class.java))
        })

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                recyclerView,
                object : RecyclerItemClickListenr.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        //do your work here..

                        latLng1 = LatLng(
                            matchedPOIMarker[position].latitude.toDouble(),
                            matchedPOIMarker[position].longitude.toDouble()
                        )


                        recyclerView.visibility = View.GONE
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        TODO("do nothing")
                    }
                })
        )

        markWindowConstraintLayout.setOnClickListener {
            //do nothing
            if (markerWindowDiscoverTextView.text == "Undiscovered") {
                val from = resources.getString(R.string.take_me_there)
                startActivity(
                    Intent(
                        this@DiscoverScreenActivity,
                        DiscoverDetailsScreenActivity::class.java
                    ).putExtra("from", from)
                )
            } else {
                val from = "discovered"
                startActivity(
                    Intent(
                        this@DiscoverScreenActivity,
                        DiscoverDetailsScreenActivity::class.java
                    ).putExtra("from", from)

                )
            }
        }


        searchButton.setOnClickListener {

            searchAdapter = POI_SearchAdapter(
                CommonData.eventsModelMessage!!,
                this,
                distanceMatrixApiModelObj
            ).also {
                recyclerView.adapter = it
                recyclerView.adapter!!.notifyDataSetChanged()

            }

            val id =
                searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
            searchViewTextView = searchView.findViewById<View>(id) as TextView

            searchViewTextView.hint = "Search POI name"

            searchViewTextView.setTextColor(Color.BLACK)

            searchViewTextView.setHintTextColor(Color.parseColor("#A7A7A7"))
            searchViewTextView.text = ""
            searchViewTextView.requestFocus()



            performSearch()

            println("recycle = ${recyclerView.size}")

            searchButton.background = getDrawable(searchbar_icon)

            trailsListLayout.visibility = View.GONE
            recyclerView.visibility = View.GONE

            searchLayout.setBackgroundResource(searchview_bg)
            backButton.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            trailsBtn.visibility = View.GONE
            profileLayout.visibility = View.GONE

            // Show the Mobile keypad

            inputMethodManager.toggleSoftInputFromWindow(
                searchView.applicationWindowToken,
                InputMethodManager.SHOW_FORCED, 0
            )

            // Set the cursor with in the search box
            searchView.requestFocus()


        }

        backButton.setOnClickListener {

            searchButton.background = getDrawable(search_button)

            searchLayout.setBackgroundResource(0)
            backButton.visibility = View.GONE
            searchView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            trailsBtn.visibility = View.VISIBLE
            profileLayout.visibility = View.VISIBLE

            // Hide the Mobile keypad

            inputMethodManager.toggleSoftInputFromWindow(
                searchView.applicationWindowToken,
                InputMethodManager.HIDE_IMPLICIT_ONLY, 0
            )


            // Only for list screen not for map screen.
            if (isListScreen) {
                trailsListLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }

        currentLocation.setOnClickListener {
            isGesturedOnMap = false
            updateCurrentLocation(latLng)
        }

        trailsBtn.setOnClickListener {
            availableTrailsListView()

        }

        mapListToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Open the poi list..

                isListScreen = true
                trailsListAdapter = TrailsListAdapter(
                    CommonData.eventsModelMessage!!,
                    this,
                    distanceMatrixApiModelObj
                ).also {
                    listScreenRecycler.adapter = it
                    listScreenRecycler.adapter!!.notifyDataSetChanged()
                }
                println("trailsListAdapter = ${trailsListAdapter.itemCount} , ${listScreenRecycler.size}")
                profileLayout.visibility = View.VISIBLE
                trailsBtn.visibility = View.VISIBLE
                searchButton.visibility = View.VISIBLE
                markWindowConstraintLayout.visibility = View.GONE
                takeMeBtn.visibility = View.GONE

                trailsListLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                mapMainLayout.visibility = View.GONE
                currentLocation.visibility = View.GONE

                searchLayout.setBackgroundResource(0)
                backButton.visibility = View.GONE
                searchView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                trailsBtn.visibility = View.VISIBLE

                searchButton.background = getDrawable(search_button)


            } else {
                // open the map screen
                isListScreen = false
                trailsListLayout.visibility = View.GONE
                recyclerView.visibility = View.GONE
                mapMainLayout.visibility = View.VISIBLE
                currentLocation.visibility = View.VISIBLE
            }

        }

        takeMeBtn.setOnClickListener {

            var from = ""


            when {
                /*  takeMeBtn.text.equals(resources.getString(R.string.travelling)) -> {
                      takeMeBtn.text = resources.getString(R.string.discover)
                      takeMeBtn.background = getDrawable(take_me_discover)
                  }*/
                takeMeBtn.text.equals(resources.getString(R.string.take_me_there)) -> {

                    drawPath()

                }
                takeMeBtn.text.equals(resources.getString(R.string.discover)) -> {
                    from = takeMeBtn.text.toString()
                    startActivity(
                        Intent(
                            this@DiscoverScreenActivity,
                            DiscoverDetailsScreenActivity::class.java
                        ).putExtra("from", from)

                    )
                }
            }


        }

        markerWindowCloseButton.setOnClickListener {

            polyline?.remove()

            isGesturedOnMap = false
            isSelectedMarker = false
            isDrawPathClicked = false
            //markerAnimationHandler.removeCallbacks(markerAnimationRunnable)

            searchButton.background = getDrawable(search_button)

            searchLayout.setBackgroundResource(0)
            backButton.visibility = View.GONE
            searchView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            trailsBtn.visibility = View.VISIBLE
            profileLayout.visibility = View.VISIBLE

            markWindowConstraintLayout.visibility = View.GONE
            takeMeBtn.visibility = View.GONE

            profileLayout.visibility = View.VISIBLE
            searchButton.visibility = View.VISIBLE



            sharedPreference.saveSession("clicked_button", "")

            takeMeBtn.background = getDrawable(take_me_discover)
            takeMeBtn.text = resources.getString(R.string.take_me_there)

            closeButtonLayout.visibility = View.GONE


        }


    }

    private fun performSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                recyclerView.visibility = View.VISIBLE
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (!newText.equals("")) {
                    recyclerView.visibility = View.VISIBLE

                } else {
                    recyclerView.visibility = View.GONE
                }
                search(newText)
                return true
            }
        })
    }

    private fun search(text: String?) {
        matchedPOIMarker = arrayListOf()

        text?.let {
            CommonData.eventsModelMessage!!.forEach { poiMarker ->
                if (poiMarker.title.contains(text, true)) {
                    //  matchedMarker.add(poiMarker)
                    matchedPOIMarker.add(poiMarker)
                    updateRecyclerView()
                }
            }
            if (matchedPOIMarker.isEmpty()) {
                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView()
        }
    }


    private fun updateRecyclerView() {


        recyclerView.apply {
            searchAdapter!!.list = matchedPOIMarker
            searchAdapter!!.notifyDataSetChanged()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        sharedPreference.saveSession("clicked_button", "")
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("NotifyUser")
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it, intentFilter)
        }


    }

    override fun onPause() {
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        super.onPause()

        sharedPreference.saveSession("clicked_button", "")
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MYPERMISSIONSREQUESTLOCATION -> {
                locationPermissionGranted = true
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.e("MainActivity:", "Location Permission Granted")
                    if (getLocationMode() == 3) {
                        Log.e("MainActivity:", "Already set High Accuracy Mode")
                        initializeService()
                    } else {
                        Log.e("MainActivity:", "Alert Dialog Shown")
                        showAlertDialog(this@DiscoverScreenActivity)
                    }
                }
                return
            }
        }
    }

    private fun showAlertDialog(context: Context?) {
        try {
            context?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle(it.resources.getString(R.string.app_name))
                    .setMessage("Please select High accuracy Location Mode from Mode Settings")
                    .setPositiveButton(it.resources.getString(android.R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                        startActivityForResult(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            REQUESTCHECKSETTINGS
                        )
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLocationMode(): Int {
        return Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCHECKSETTINGS) {
            initializeService()
        }
    }

    // Monitors the state of the connection to the service.
    private var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: LocationUpdatesService.LocalBinder =
                service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
            // Check that the user hasn't revoked permissions by going to Settings.

            mService?.requestLocationUpdates()

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            mBound = false
        }
    }

    private fun initializeService() {
        bindService(
            Intent(this, LocationUpdatesService::class.java),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
        super.onStop()
    }


    private fun visibleItems() {

        currentLocation.visibility = View.VISIBLE
        mapListToggleButton.visibility = View.VISIBLE
        searchButton.visibility = View.VISIBLE
        trailsBtn.visibility = View.VISIBLE
        searchButton.visibility = View.VISIBLE
        profileLayout.visibility = View.VISIBLE


        // Get Trails ....
        //getTrailListFromAPI()

        if (latLng != null) {


            getEventsList()
        }


    }

    var lastlatlng: LatLng? = null
    private fun updateCurrentLocation(newLatLng: LatLng?) {
        //1.4025944219780742, 103.78994695871972
        //  latLng = LatLng(1.401945229052399, 103.78865687018656)
        if (newLatLng != null) {
            if (null != currentLocationMarker) {
                currentLocationMarker!!.remove()
            }

            var distance = lastRouteLog?.let {
                SphericalUtil.computeDistanceBetween(newLatLng, it)
            } ?: kotlin.run {
                -1
            }
            println("Just:::::::::::::::::: distance  $distance")
            // discover area
            if (distance.toDouble() in 0.0..300.0) {

                Toast.makeText(this, "POI is nearby", Toast.LENGTH_LONG).show()
                takeMeBtn.visibility = View.VISIBLE
                takeMeBtn.text = resources.getString(R.string.discover)
                takeMeBtn.background = getDrawable(take_me_discover)

                println("Vibrator Status = ${sharedPreference.getBoolean("isVibratorOn")}")
                if (sharedPreference.getBoolean("isVibratorOn")) {
                    vibrateOn()
                } else {
                    vibrateOff()
                }

            }


            var bearing = lastlatlng?.let {
                val lastLocation = Location("LastLatLng")
                lastLocation.latitude = it.latitude
                lastLocation.longitude = it.longitude

                val curLocation = Location("CurrentLocation")
                curLocation.latitude = newLatLng.latitude
                curLocation.latitude = newLatLng.longitude


                // lastLocation.bearingTo(curLocation)
                SphericalUtil.computeHeading(it, newLatLng).toFloat()


            } ?: kotlin.run {
                0f
            }
            //    println("Just:::::::::::::::: bearing $bearing")

            if (!bearing.equals(0.0)) {
                currentLocationMarker = mMap.addMarker(
                    MarkerOptions().position(newLatLng).icon(
                        BitmapDescriptorFactory.fromResource(map_current_location_marker)
                    ).anchor(0.5f, 0.5f).rotation(bearing).flat(true)
                )

            }

            mMap.uiSettings.isCompassEnabled = false


            if (!isGesturedOnMap) {
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng))
                val cameraPosition = CameraPosition.Builder()
                    .target(latLng)
                    // Sets the center of the map to Mountain View
                    .zoom(20f)            // Sets the zoom
                    // .bearing(bearing)         // Sets the orientation of the camera to east
                    .tilt(30f)             // Sets the tilt of the camera to 30 degrees
                    .build()              // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }


            // mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))


            lastlatlng = newLatLng
        } else {
            println("LatLng Is NULL from current Loc.,")
        }
        changePositionSmoothly(currentLocationMarker,lastlatlng)

    }


    private fun getURL(from: LatLng, to: LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val mode = "mode=walking"
        val key = "key=" + resources.getString(R.string.directionsApiKey)
        val params = "$origin&$dest&$sensor&$mode&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    private fun getDistanceURL(from: LatLng, to: LatLng): String {
        val units = "units=metric"
        val origin = "origins=" + from.latitude + "," + from.longitude
        val dest = "destinations=" + to.latitude + "," + to.longitude
        val key = "key=" + resources.getString(R.string.directionsApiKey)
        val mode = "mode=walking"
        val params = "$units&$origin&$dest&$mode&$key"
        return "https://maps.googleapis.com/maps/api/distancematrix/json?$params"
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this@DiscoverScreenActivity,
                R.raw.google_map_style
            )
        )

        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveStartedListener(this)
        mMap.setOnCameraMoveListener(this)
        mMap.setOnCameraMoveCanceledListener(this)
        mMap.setOnMarkerDragListener(this)




        mMap.setOnMapClickListener {


            isGesturedOnMap = false

            sharedPreference.saveSession("clicked_button", "")

            searchButton.background = getDrawable(search_button)

            searchLayout.setBackgroundResource(0)
            backButton.visibility = View.GONE
            searchView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            trailsBtn.visibility = View.VISIBLE
            profileLayout.visibility = View.VISIBLE

            markWindowConstraintLayout.visibility = View.GONE
            takeMeBtn.visibility = View.GONE


            profileLayout.visibility = View.VISIBLE

            searchButton.visibility = View.VISIBLE

            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE

            if (polyline != null) {
                polyline!!.remove()
            }




            if (isSelectedMarker) {
                isSelectedMarker = false
                clickedMarker!!.remove()

                println("selectedPOIDiscoverId Clicking Map = $selectedPOIDiscoverId")
                if (selectedPOIDiscoverId == null || selectedPOIDiscoverId == "") {
                    clickedMarker = mMap.addMarker(
                        MarkerOptions().position(latLng1).icon(
                            BitmapDescriptorFactory.fromResource(
                                poi_breadcrumbs_marker_undiscovered_1
                            )
                        )
                    )
                } else {
                    clickedMarker = mMap.addMarker(
                        MarkerOptions().position(latLng1).icon(
                            BitmapDescriptorFactory.fromResource(
                                poi_breadcrumbs_marker_discovered_1
                            )
                        )
                    )
                }

            }

            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
            markerAnimationHandler.removeCallbacks(markerAnimationRunnable)

        }

        //val bearing  = calculateBearing(1.401945229052399, 103.78865687018656, 1.40080100, 103.79036900)
        // val marker:Marker

        // latLng = LatLng(1.401945229052399, 103.78865687018656)
        // println("Current Loc : $latLng")
        if (null != currentLocationMarker) {
            currentLocationMarker!!.remove()
        }


        latLng?.let {
            currentLocationMarker = mMap.addMarker(
                MarkerOptions().position(it).icon(
                    BitmapDescriptorFactory.fromResource(map_current_location_marker)
                )
            )
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = false

        } else {//condition for Marshmello and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MYPERMISSIONSREQUESTLOCATION
                )
            }
        }

        mMap.setOnMarkerClickListener(this)



        if (!isClicked) {

            //  disclaimerWindow()


            val bundle: Bundle? = intent.extras
            val isFromLogin = bundle!!.getString("isFromLogin")
            if (isFromLogin.equals("yes")) {
                disclaimerWindow()
            } else if (isFromLogin.equals("no")) {
                visibleItems()
                updateCurrentLocation(latLng)
            }
        }


    }


    private fun decodePoly(encoded: String): List<LatLng>? {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }


    private fun disclaimerWindow() {
        isClicked = true
        val dialog = Dialog(this, FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.disclaimer_window)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setDimAmount(0.5f)
        val yesBtn = dialog.findViewById(R.id.okButton) as TextView

        yesBtn.setOnClickListener {

            sharedPreference.saveBoolean("okButton", true)

            dialog.dismiss()
            if (!isCheckBoxClicked) {

                recycleViewWindow()

            }

        }

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        if (!dialog.isShowing) {

            dialog.show()
        }
    }

    private fun recycleViewWindow() {
        val dialog = Dialog(this, FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(trail_layout)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setDimAmount(0.3f)
        val tutRecyclerView = dialog.findViewById(R.id.recyclerView2) as RecyclerView

        tutRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        tutRecyclerView.adapter = trailTutorialAdapter

        val checkBox = dialog.findViewById(checkBox) as CheckBox
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkBox.isChecked
            /*if (isChecked) {

                isCheckBoxClicked = true
                dialog.dismiss()
                trailNotificationWindow()
            }*/
        }

        val closeButton: TextView = dialog.findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            isCheckBoxClicked = true
            dialog.dismiss()
            trailNotificationWindow()
        }

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    private fun availableTrailsListView() {

        val dialog = Dialog(this, FirebaseUI_Transparent)
        // val dialog = DialogFragment()
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!

        layoutParams.gravity = Gravity.TOP
        layoutParams.x = 0
        layoutParams.y = 160


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.available_trails_layout)
        dialog.window?.setDimAmount(0.3f)


        val radioBtn1 = dialog.findViewById(R.id.radioBtn1) as ImageView
        val radioBtn2 = dialog.findViewById(R.id.radioBtn2) as ImageView
        val radioBtn3 = dialog.findViewById(R.id.radioBtn3) as ImageView
        val radioBtn4 = dialog.findViewById(R.id.radioBtn4) as ImageView


        val trailOne = dialog.findViewById(R.id.trailOne) as TextView
        val trailTwo = dialog.findViewById(R.id.trailTwo) as TextView
        val trailThree = dialog.findViewById(R.id.trailThree) as TextView
        val trailFour = dialog.findViewById(R.id.trailFour) as TextView


        val row1 = dialog.findViewById(R.id.row1) as RelativeLayout
        val row2 = dialog.findViewById(R.id.row2) as ConstraintLayout
        val row3 = dialog.findViewById(R.id.row3) as ConstraintLayout
        val row4 = dialog.findViewById(R.id.row4) as ConstraintLayout


        when (CommonData.getTrailsData!!.size) {
            1 -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE

            }
            2 -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.VISIBLE
                row3.visibility = View.GONE
                row4.visibility = View.GONE

            }
            3 -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.VISIBLE
                row3.visibility = View.VISIBLE
                row4.visibility = View.GONE

            }
            4 -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.VISIBLE
                row3.visibility = View.VISIBLE
                row4.visibility = View.VISIBLE

            }
        }
        when {
            sharedPreference.getSession("selected_trails").equals(trailOne.text.toString()) -> {
                radioBtn1.setImageResource(radio_btn_active)
            }
            sharedPreference.getSession("selected_trails")
                .equals(trailTwo.text.toString()) -> {
                radioBtn2.setImageResource(radio_btn_active)
            }
            sharedPreference.getSession("selected_trails")
                .equals(trailThree.text.toString()) -> {
                radioBtn3.setImageResource(radio_btn_active)
            }
            sharedPreference.getSession("selected_trails")
                .equals(trailFour.text.toString()) -> {
                radioBtn4.setImageResource(radio_btn_active)
            }
        }


        radioBtn1.setOnClickListener {
            firstRadioChecked = true
            secondRadioChecked = false
            radioBtn1.setImageResource(radio_btn_active)
            radioBtn2.setImageResource(radio_btn_deactive)
            radioBtn3.setImageResource(radio_btn_deactive)
            radioBtn4.setImageResource(radio_btn_deactive)
            sharedPreference.saveSession("selected_trails", trailOne.text.toString())
        }


        radioBtn2.setOnClickListener {
            firstRadioChecked = false
            secondRadioChecked = true
            radioBtn2.setImageResource(radio_btn_active)
            radioBtn1.setImageResource(radio_btn_deactive)
            radioBtn3.setImageResource(radio_btn_deactive)
            radioBtn4.setImageResource(radio_btn_deactive)
            sharedPreference.saveSession("selected_trails", trailTwo.text.toString())
        }

        radioBtn3.setOnClickListener {
            firstRadioChecked = false
            secondRadioChecked = true
            radioBtn3.setImageResource(radio_btn_active)
            radioBtn1.setImageResource(radio_btn_deactive)
            radioBtn2.setImageResource(radio_btn_deactive)
            radioBtn4.setImageResource(radio_btn_deactive)
            sharedPreference.saveSession("selected_trails", trailThree.text.toString())
        }

        radioBtn4.setOnClickListener {
            firstRadioChecked = false
            secondRadioChecked = true
            radioBtn4.setImageResource(radio_btn_active)
            radioBtn3.setImageResource(radio_btn_deactive)
            radioBtn2.setImageResource(radio_btn_deactive)
            radioBtn1.setImageResource(radio_btn_deactive)
            sharedPreference.saveSession("selected_trails", trailFour.text.toString())
        }


        val okButton = dialog.findViewById(R.id.doneButton) as TextView

        okButton.setOnClickListener(View.OnClickListener {

            println("Selected Trail : ${sharedPreference.getSession("selected_trails")}")
            if (sharedPreference.getSession("selected_trails").equals("")) {
                Toast.makeText(
                    this,
                    "Please Choose a trail from the list",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                if (isCheckBoxClicked) {
                    isCheckBoxClicked = false

                    visibleItems()
                    updateCurrentLocation(latLng)
                }
                dialog.dismiss()
            }

        })



        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun trailNotificationWindow() {
        val dialog = Dialog(this, FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.trail_notification)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val checkBox = dialog.findViewById(checkBox) as CheckBox
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkBox.isChecked
            /*if (isChecked) {
                dialog.dismiss()
                isCheckBoxClicked = true
                visibleItems()
            }*/
        }
        val closeButton: TextView = dialog.findViewById(R.id.closeButton)

        val textToGo: TextView = dialog.findViewById(R.id.textToGo)

        closeButton.setOnClickListener {
            isCheckBoxClicked = true
            dialog.dismiss()
            availableTrailsListView()

        }

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()


        val postCreatedDateStatic = "2021-07-10 12:56:50"
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss"
        )

        try {
            val postCreatedDate: Date = dateFormat.parse(postCreatedDateStatic)
            val currentDate = Date()
            println("Date Is :  Old Date = $postCreatedDate , Today Date = $currentDate")


            //  val diff = postCreatedDate.time - currentDate.time
            val diff = currentDate.time - postCreatedDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            println("Date Is :  Remaining Date: $days")
            if (postCreatedDate.before(currentDate)) {

                println("Date Is :  IF: $days")

                textToGo.text = "$days Days Ago"
            } else {
                println("Date Is :  ELSE: $days")
            }

            // Log.e("toyBornTime", "" + toyBornTime);
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onMarkerClick(p0: Marker): Boolean {

        if (polyline != null) {
            polyline!!.remove()
        }

        takeMeBtn.background = getDrawable(take_me_discover)
        takeMeBtn.text = resources.getString(R.string.take_me_there)
        closeButtonLayout.visibility = View.GONE

        //1.401945229052399, 103.78865687018656
        // latLng = LatLng(1.401945229052399, 103.78865687018656)
        if (CommonData.eventsModelMessage != null) {
            for (i in 0 until CommonData.eventsModelMessage!!.count()) {
                if (i == p0.tag) {
                    isMarkerClicked = true
                    selectedMarker(i)
                }
            }
        }

        return true
    }

    var lastRouteLog: LatLng? = null

    private fun drawPath() {


        isDrawPathClicked = true
        takeMeBtn.background = resources.getDrawable(travelling_bg)
        takeMeBtn.text = resources.getString(R.string.travelling)
        markerWindowCloseButton.visibility = View.VISIBLE
        val pattern = listOf(
            Dot(), Gap(10F)
        )

        closeButtonLayout.visibility = View.VISIBLE


        var routes: MutableList<List<HashMap<String, String>>> = ArrayList()
        val url = getURL(latLng!!, latLng1!!)

        println("URL = $url")


        val result = URL(url).readText()

        val jsonObject = JSONObject(result)
        println("jsonObject = $jsonObject")

        routes = parse(jsonObject) as MutableList<List<HashMap<String, String>>>

        println("Routes::::: ${routes.size}")

        var points: ArrayList<LatLng>?
        var polyLineOptions = PolylineOptions()

        for (i in 0 until routes.size) {
            points = ArrayList<LatLng>()
            polyLineOptions = PolylineOptions()
            val path = routes[i]
            for (j in path.indices) {
                val point = path[j]
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()

                println("Just::::::::::::: lat $lat  lng   $lng")
                val position = LatLng(lat, lng)
                points.add(position)
            }

            lastRouteLog = points.last()
            polyLineOptions.addAll(points)
            polyLineOptions.width(15f)
            polyLineOptions.color(Color.parseColor("#79856C"))
            polyLineOptions.pattern(pattern)

        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f))
        polyline = mMap.addPolyline(polyLineOptions)
    }

    fun parse(jObject: JSONObject): List<List<HashMap<String, String>>>? {
        val routes: MutableList<List<HashMap<String, String>>> = ArrayList()
        var jRoutes: JSONArray? = null
        var jLegs: JSONArray? = null
        var jSteps: JSONArray? = null
        try {
            jRoutes = jObject.getJSONArray("routes")
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                val path: MutableList<HashMap<String, String>> = ArrayList()
                for (j in 0 until jLegs.length()) {
                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    for (k in 0 until jSteps.length()) {

                        var distance =
                            ((jSteps[k] as JSONObject)["distance"] as JSONObject)["text"] as String
                        println("distance = $distance")

                        var polyline = ""
                        polyline =
                            ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list = decodePoly(polyline)
                        for (l in list!!.indices) {
                            val hm = HashMap<String, String>()
                            hm["lat"] = list[l].latitude.toString()
                            hm["lng"] = list[l].longitude.toString()
                            path.add(hm)
                        }
                    }
                    routes.add(path)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return routes
    }

    private fun getGeoContext(): GeoApiContext? {
        val geoApiContext = GeoApiContext()
        return geoApiContext
            .setApiKey(getString(R.string.directionsApiKey))
            .setConnectTimeout(1, TimeUnit.SECONDS)
            .setReadTimeout(1, TimeUnit.SECONDS)
            .setWriteTimeout(1, TimeUnit.SECONDS)
    }

    private fun getDirectionsDetails(
        origin: String,
        destination: String,
        mode: TravelMode
    ): DirectionsResult? {

        return try {
            DirectionsApi.newRequest(getGeoContext())
                .mode(mode)
                .origin(origin)
                .destination(destination)

                .await()
        } catch (e: ApiException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getEventsList() {

        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", sharedPreference.getSession("login_id"))
            jsonObject.put("trails", "4")
            println("getEvents Url = ${resources.getString(R.string.staging_url)}")
            println("getEvents Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)


            this@DiscoverScreenActivity.runOnUiThread(Runnable {
                CoroutineScope(Dispatchers.IO).launch {

                    // Create Service
                    val service = retrofit.create(APIService::class.java)

                    val response = service.getEventsList(
                        resources.getString(R.string.api_access_token),
                        requestBody
                    )

                    var jRoutes: JSONArray?
                    var jElements: JSONArray?


                    if (response.isSuccessful) {
                        if (response.body()!!.status) {

                            CommonData.eventsModelMessage = response.body()?.message
                            println("Event Model = ${CommonData.eventsModelMessage!!.size}")

                            if (CommonData.eventsModelMessage != null) {

                                for (i in 0 until CommonData.eventsModelMessage!!.count()) {

                                    val latLng1 = LatLng(
                                        CommonData.eventsModelMessage!![i].latitude.toDouble(),
                                        CommonData.eventsModelMessage!![i].longitude.toDouble()
                                    )
                                    trailName = CommonData.eventsModelMessage!![i].title
                                    println("Trail Name : $trailName")

                                    runOnUiThread {

                                        if (CommonData.eventsModelMessage!![i].disc_id == null) {
                                            markerPOI = mMap.addMarker(
                                                MarkerOptions().position(latLng1)
                                                    .icon(
                                                        BitmapDescriptorFactory.fromResource(
                                                            poi_breadcrumbs_marker_undiscovered_1
                                                        )
                                                    ).title(trailName)
                                            )

                                        } else {
                                            markerPOI = mMap.addMarker(
                                                MarkerOptions().position(latLng1)
                                                    .icon(
                                                        BitmapDescriptorFactory.fromResource(
                                                            poi_breadcrumbs_marker_discovered_1
                                                        )
                                                    ).title(trailName)
                                            )
                                        }

                                        markerPOI!!.tag = i

                                    }
                                    try {
                                        println("Current Latlng $latLng")

                                        val distanceURl = getDistanceURL(latLng!!, latLng1)
                                        println("distanceURl = $distanceURl")
                                        val result = URL(distanceURl).readText()
                                        val jsonObject = JSONObject(result)


                                        jRoutes = jsonObject.getJSONArray("rows")

                                        for (k in 0 until jRoutes.length()) {
                                            println("Loop Time : k = $k")
                                            jElements =
                                                (jRoutes[k] as JSONObject).getJSONArray("elements")
                                            for (j in 0 until jElements.length()) {
                                                println("Loop Time : j = $j")
                                                distance =
                                                    ((jElements[j] as JSONObject)["distance"] as JSONObject)["text"] as String
                                                println("distance Matrix distance = $distance")

                                                duration =
                                                    ((jElements[j] as JSONObject)["duration"] as JSONObject)["text"] as String
                                                println("distance Matrix duration = $duration")

                                                distanceMatrixApiModelObj.add(
                                                    DistanceMatrixApiModel(
                                                        distance,
                                                        duration
                                                    )
                                                )

                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }


                                }
                                println("distanceMatrixApiModelObj = Overall= ${distanceMatrixApiModelObj.size}")
                            }
                        }
                    }


                }
            })


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getTrailDetails() {
        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.live_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", sharedPreference.getSession("login_id"))


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)

            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getTrailsList(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getTrailsData = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getTrailsData != null) {


                            }

                        }


                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }

    override fun onClickedPOIItem(id: String) {
        CommonData.eventsModelMessage?.let {
            for (i in it.indices) {
                if (it[i].id == id) {
                    isMarkerClicked=false
                    selectedMarker(i)
                    break
                }
            }
        }


    }

    private fun selectedMarker(pos: Int) {


        isSelectedMarker = true
        /* When user click / select the particular POI., that time needs to check either the POI discovered or not.*/

        profileLayout.visibility = View.GONE
        trailsBtn.visibility = View.GONE
        searchButton.visibility = View.GONE
        searchView.visibility = View.GONE
        recyclerView.visibility = View.GONE
        backButton.visibility = View.GONE
        markWindowConstraintLayout.visibility = View.VISIBLE
        takeMeBtn.visibility = View.VISIBLE

        currentLocation.visibility = View.VISIBLE
        trailsListLayout.visibility = View.GONE
        mapMainLayout.visibility = View.VISIBLE

        takeMeBtn.text = resources.getString(R.string.take_me_there)
        poiDistance.text = distanceMatrixApiModelObj[pos].distance
        trailsNameText.text = CommonData.eventsModelMessage!![pos].title
        selectedPOIName = CommonData.eventsModelMessage!![pos].title
        selectedPOIID = CommonData.eventsModelMessage!![pos].id
        //  sharedPreference.saveSession("selectedPOIID",selectedPOIID)
        selectedPOIQuestion = CommonData.eventsModelMessage!![pos].ch_question
        selectedPOIChallengeType = CommonData.eventsModelMessage!![pos].ch_type
        selectedPOIARid = CommonData.eventsModelMessage!![pos].ar_id

        if (CommonData.eventsModelMessage!![pos].ar_id == "null" || CommonData.eventsModelMessage!![pos].ar_id == null) {
            println("selectedPOIARid = Before = $selectedPOIARid")
            selectedPOIARid = "0"
            println("selectedPOIARid = After = $selectedPOIARid")
        } else {
            println("selectedPOIARid = ELSE = $selectedPOIARid")
        }
        selectedPOIDiscoverId = CommonData.eventsModelMessage!![pos].disc_id
        selectedPOIQrCode = CommonData.eventsModelMessage!![pos].qr_code
        // selectedPOIHintContent = CommonData.eventsModelMessage!![pos].hint
        selectedPOIHintContent = CommonData.eventsModelMessage!![pos].description
        selectedPOIDuration = distanceMatrixApiModelObj[pos].duration
        selectedPOIImage =
            resources.getString(R.string.live_url) + CommonData.eventsModelMessage!![pos].poi_img
        println("poi_ID selectedPOIID = $selectedPOIID")
        mapListToggleButton.isChecked = false

        try {
            sharedPreference.saveSession("selectedPOIName", selectedPOIName)
            sharedPreference.saveSession("selectedPOIID", selectedPOIID)
            sharedPreference.saveSession("poiDistance", poiDistance.text.toString())
            sharedPreference.saveSession("selectedPOIImage", selectedPOIImage)
            sharedPreference.saveSession("selectedPOIDuration", selectedPOIDuration)
            sharedPreference.saveSession("selectedPOIQuestion", selectedPOIQuestion)
            sharedPreference.saveSession("selectedPOIHintContent", selectedPOIHintContent)
            sharedPreference.saveSession("selectedPOIChallengeType", selectedPOIChallengeType)
            if (selectedPOIARid != null) {

                sharedPreference.saveSession("selectedPOIARid", selectedPOIARid)
            }
            sharedPreference.saveSession("selectedPOIQrCode", selectedPOIQrCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        latLng1 = LatLng(
            CommonData.eventsModelMessage!![pos].latitude.toDouble(),
            CommonData.eventsModelMessage!![pos].longitude.toDouble()
        )


        clickedMarker?.remove()

        println("selectedPOIDiscoverId = $selectedPOIID Discover_id = $selectedPOIDiscoverId")

        if (selectedPOIDiscoverId == null) {
            println("selectedPOIDiscoverId IF= $selectedPOIDiscoverId")
            markerWindow_background.background = resources.getDrawable(trail_banner_undiscovered)
            markerWindowDiscoverTextView.text = "Undiscovered"
            Glide.with(applicationContext)
                .load(list_poi_icon)
                .into(markWindowPOIIcon)
            isDiscovered = false
            clickedMarker = mMap.addMarker(
                MarkerOptions().position(latLng1).icon(
                    BitmapDescriptorFactory.fromResource(
                        poi_breadcrumbs_marker_undiscovered_2
                    )
                )
            )

            if (!isMarkerClicked) {
                val from = resources.getString(R.string.take_me_there)
                startActivity(
                    Intent(
                        this@DiscoverScreenActivity,
                        DiscoverDetailsScreenActivity::class.java
                    ).putExtra("from", from)
                )
            }


        } else {
            println("selectedPOIDiscoverId ELSE= $selectedPOIDiscoverId")
            markerWindow_background.background = resources.getDrawable(trail_banner_discovered)
            markerWindowDiscoverTextView.text = "Discovered"
            Glide.with(applicationContext)
                .load(discovered_poi_ico_banner)
                .into(markWindowPOIIcon)
            isDiscovered = true
            clickedMarker = mMap.addMarker(
                MarkerOptions().position(latLng1).icon(
                    BitmapDescriptorFactory.fromResource(
                        poi_breadcrumbs_marker_discovered_2
                    )
                )
            )


            takeMeBtn.visibility = View.GONE

            if (!isMarkerClicked) {
                val from = "discovered"
                startActivity(
                    Intent(
                        this@DiscoverScreenActivity,
                        DiscoverDetailsScreenActivity::class.java
                    ).putExtra("from", from)

                )
            }

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1))
      //  mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))


        markerAnimationHandler.removeCallbacks(markerAnimationRunnable)
        markerAnimationHandler.postDelayed(markerAnimationRunnable, 500)
    }


    val tempMarkerPosition: Int = 0
    private var iconPosition = 1

    var reverse = false
    private var markerAnimationRunnable = object : Runnable {


        override fun run() {
            try {

                markerAnimation()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    private fun markerAnimation() {


        if (isDiscovered) {
            when (iconPosition) {
                1 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_discovered_1
                        )
                    )
                    iconPosition = 2
                }
                2 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_discovered_2
                        )
                    )
                    iconPosition = 3
                }
                3 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_discovered_3
                        )
                    )
                    iconPosition = 4
                    reverse = false

                }
                4 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_discovered_4
                        )
                    )

                    iconPosition = if (reverse) {
                        3
                    } else {
                        5
                    }
                }
                5 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_discovered_5
                        )
                    )
                    iconPosition = 4
                    reverse = true

                }


            }
        } else {
            when (iconPosition) {
                1 -> {

                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_undiscovered_1
                        )
                    )
                    iconPosition = 2
                }
                2 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_undiscovered_2
                        )
                    )
                    iconPosition = 3
                }
                3 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_undiscovered_3
                        )
                    )
                    iconPosition = 4
                    reverse = false

                }
                4 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_undiscovered_4
                        )
                    )



                    iconPosition = if (reverse) {
                        3
                    } else {
                        5
                    }
                }
                5 -> {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_undiscovered_5
                        )
                    )
                    iconPosition = 4
                    reverse = true

                }


            }
        }


        markerAnimationHandler.postDelayed(markerAnimationRunnable, 500)
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onMarkerDragStart(p0: Marker) {
        println("******** onMarkerDragStart")
    }

    override fun onMarkerDrag(p0: Marker) {
        println("******** onMarkerDrag")
    }

    override fun onMarkerDragEnd(p0: Marker) {
        println("******** onMarkerDragEnd")
    }

    override fun onCameraMove() {
        println("******** onCameraMove")
    }

    override fun onCameraMoveCanceled() {
        println("******** onCameraMoveCanceled")
    }

    override fun onCameraIdle() {
        println("******** onCameraIdle")
    }

    override fun onCameraMoveStarted(reason: Int) {
        println("******** onCameraMoveStarted")

        when (reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {

                isGesturedOnMap = true
            }
            GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION -> {

            }
            GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {

            }
        }
    }


    private fun getUserDetails() {

        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", sharedPreference.getSession("login_id"))

            println("getUserDetails Url = ${resources.getString(R.string.staging_url)}")
            println("getUserDetails Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getUserDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )


                if (response.isSuccessful) {
                    if (response.body()!!.status) {
                        if (response.body()!!.message != null) {

                            CommonData.getUserDetails = response.body()?.message

                            println("GetUseDetails = ${CommonData.getUserDetails!!.experience}")

                            sharedPreference.saveSession(
                                "player_experience_points",
                                CommonData.getUserDetails!!.experience
                            )
                            sharedPreference.saveSession(
                                "player_register_date",
                                CommonData.getUserDetails!!.created
                            )
                            sharedPreference.saveSession(
                                "player_user_name",
                                CommonData.getUserDetails!!.username
                            )
                            sharedPreference.saveSession(
                                "player_email_id",
                                CommonData.getUserDetails!!.email
                            )
                            sharedPreference.saveSession(
                                "player_id",
                                CommonData.getUserDetails!!.id
                            )

                            calculateUserLevel(Integer.parseInt(CommonData.getUserDetails!!.experience))

                        } else {

                        }
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun calculateUserLevel(exp: Int) {
          var ranking: String = ""
          var level: Int = 0
          var base: Int = 0
          var nextLevel: Int = 0
        when (exp) {
            in 0..999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 1
                base = 1000
                nextLevel = 2000
            }
            in 1000..1999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 2
                base = 1000
                nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 3
                base = 2000
                nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 4
                base = 3000
                nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 5
                base = 4000
                nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 6
                base = 6000
                nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 7
                base = 8000
                nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 8
                base = 10000
                nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 9
                base = 12000
                nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "NAVIGATOR"
                level = 10
                base = 14000
                nextLevel = 17000

            }
            in 17000..20499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 11
                base = 17000
                nextLevel = 20500

            }
            in 20500..24499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 12
                base = 20500
                nextLevel = 24500

            }
            in 24500..28499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 13
                base = 24500
                nextLevel = 28500

            }
            in 28500..33499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 14
                base = 28500
                nextLevel = 33500

            }
            in 33500..38999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 15
                base = 33500
                nextLevel = 39000

            }
            in 39000..44999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 16
                base = 39000
                nextLevel = 45000

            }
            in 45000..51499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 17
                base = 45000
                nextLevel = 51500

            }
            in 51500..58499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 18
                base = 51500
                nextLevel = 58500

            }
            in 58500..65999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 19
                base = 58500
                nextLevel = 66000

            }
            in 66000..73999 -> { // 2000 thresh
                ranking = "Captain"
                level = 20
                base = 66000
                nextLevel = 74000

            }
        }
        levelTextView.text = "$ranking Lv. $level"

        var expToLevel = (nextLevel - base) - (exp - base)

        println("expToLevel= $expToLevel")
        sharedPreference.saveSession("level_text_value", levelTextView.text.toString())
        sharedPreference.saveSession("expTo_level_value", expToLevel)
        sharedPreference.saveSession("xp_point_base_value", base)
        sharedPreference.saveSession("xp_point_nextLevel_value", nextLevel)
        sharedPreference.saveSession("lv_value", "LV ${level + 1}")

    }

    private fun vibrateOn() {

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(1500)

        }


    }

    private fun vibrateOff() {
        vibrator.cancel()

    }

    private fun getRankingDetails() {
        try {
           /* loadingImage.visibility = View.VISIBLE
            Glide.with(applicationContext).load(R.raw.loading).into(loaderImage)*/
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


            // Create JSON using JSONObject
            val jsonObject = JSONObject()
            jsonObject.put("trail_id", "4")

            println("getRankingDetails Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getRankingDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getRankData = response.body()?.message

                        println("Discover Screen Ranking API:: ${CommonData.getRankData!!.size}")





                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changePositionSmoothly(marker:Marker?, newLatLng: LatLng?){
        if(marker == null){
            return
        }
        val animation = ValueAnimator.ofFloat(0f, 10f)
        var previousStep = 0f
        val deltaLatitude = newLatLng!!.latitude - marker.position.latitude
        val deltaLongitude = newLatLng!!.longitude - marker.position.longitude
        animation.duration = 1500
        animation.addUpdateListener { updatedAnimation ->
            val deltaStep = updatedAnimation.animatedValue as Float - previousStep
            previousStep = updatedAnimation.animatedValue as Float
            marker.position = LatLng(marker.position.latitude + deltaLatitude * deltaStep * 1/10, marker.position.longitude + deltaStep * deltaLongitude * 1/10)
        }
        animation.start()
    }
}