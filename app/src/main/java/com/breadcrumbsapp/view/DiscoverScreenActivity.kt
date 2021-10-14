package com.breadcrumbsapp.view


/*

Details....

1. On marker click method for marker click
2. searchButton.setOnClick - for search button
3. com.breadcrumbsapp.service.LocationUpdatesService -> fun onNewLocation()
4.  getTrailListFromAPI() - This method helps to load POI
5.  POI Image name - poi_breadcrumbs_marker_undiscovered_1,poi_breadcrumbs_marker_undiscovered_2
6.  ch_type = 0 means, selfie. Else, quiz.


 */

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
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
import com.breadcrumbsapp.R.layout.trail_layout
import com.breadcrumbsapp.R.style.FirebaseUI_Transparent
import com.breadcrumbsapp.adapter.*
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.interfaces.LatLngInterpolator
import com.breadcrumbsapp.interfaces.POIclickedListener
import com.breadcrumbsapp.model.DistanceMatrixApiModel
import com.breadcrumbsapp.model.GetEventsModel
import com.breadcrumbsapp.service.LocationUpdatesService
import com.breadcrumbsapp.util.*
import com.breadcrumbsapp.view.profile.ProfileScreenActivity
import com.breadcrumbsapp.view.rewards.RewardsScreenActivity
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.maps.android.BuildConfig
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.available_trails_layout.*
import kotlinx.android.synthetic.main.discover_screen_activity.*
import kotlinx.android.synthetic.main.discover_screen_bottom_layout.*
import kotlinx.android.synthetic.main.leader_board_activity_layout.*
import kotlinx.android.synthetic.main.more_option_layout.*
import kotlinx.android.synthetic.main.quiz_challenge_question_activity.*
import kotlinx.android.synthetic.main.trail_notification.*
import kotlinx.android.synthetic.main.trails_screen_layout.*
import kotlinx.android.synthetic.main.user_profile_screen_layout.*
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
    private var latLng: LatLng? = null
    private var latLng1: LatLng? = null
    private lateinit var sharedPreference: SessionHandlerClass
    private lateinit var trailTutorialAdapter: TrailTutorialAdapter
    private var isClicked = false
    private var isCheckBoxClicked = false
    private lateinit var currentLocation: Button
    private lateinit var mapListToggleButton: ToggleButton
    private lateinit var backButton: Button
    private lateinit var searchButton: Button
    private lateinit var searchLayout: RelativeLayout
    private lateinit var trailsBtn: Button
    private lateinit var profileLayout: LinearLayoutCompat
    private lateinit var markWindowConstraintLayout: LinearLayoutCompat
    private lateinit var markerWindow_background: ConstraintLayout
    private lateinit var markerWindowDiscoverTextView: TextView
    private lateinit var markWindowPOIIcon: ImageView
    private lateinit var trailsListLayout: LinearLayoutCompat
    private lateinit var listScreenRecycler: RecyclerView
    private lateinit var trailsListAdapter: TrailsListAdapter
    private lateinit var mapMainLayout: LinearLayoutCompat
    private lateinit var takeMeBtn: TextView
    private lateinit var playerName: TextView
    private lateinit var trailsNameText: TextView

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var interceptor = intercept()
    private lateinit var trailName: String
    private lateinit var poiDistance: TextView
    private var selectedPOIName: String = ""
    private var selectedPOIID: String = ""
    private var selectedPOIDiscoveryXP: String = ""
    private var selectedPOIImage: String = ""
    private var selectedPOIDuration: String = ""
    private var selectedPOIQuestion: String = ""
    private var selectedPOIHintContent: String = ""
    private var selectedPOIChallengeType: String = ""
    private var selectedPOITrivia: String = ""
    private var selectedPOIARid: String = ""
    private var selectedPOIDiscoverId: String = ""
    private var selectedPOIQrCode: String = ""
    private var isListScreen: Boolean = false
    private var firstRadioChecked: Boolean = false
    private var secondRadioChecked: Boolean = false
    private var polyline: Polyline? = null
    private var isMarkerClicked: Boolean = false
    private var isDrawPathClicked: Boolean = false
    private var isFromPOIList: Boolean = false
    private var mBound: Boolean = false

    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var currentLocationMarker: Marker? = null
    private var clickedMarker: Marker? = null
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var markerWindowCloseButton: ImageView
    private var searchAdapter: POI_SearchAdapter? = null
    lateinit var recyclerView: RecyclerView
    private lateinit var levelTextView: TextView
    private var matchedPOIMarker: ArrayList<GetEventsModel.Message> = arrayListOf()
    private var locationPermissionGranted = false
    private var distanceMatrixApiModelObj: ArrayList<DistanceMatrixApiModel> = arrayListOf()
    private lateinit var distance: String
    private lateinit var duration: String
    private var markerAnimationHandler = Handler(Looper.getMainLooper())
    private var doubleBackToExitPressedOnce = false
    private var isSelectedMarker = false
    private var isGesturedOnMap = false
    private var moreButtonClicked = 0
    private lateinit var vibrator: Vibrator
    private lateinit var questionObj: JsonArray
    private var noOfQuestions: String = ""
    private lateinit var mapFragment: SupportMapFragment
    private var markerArrayList: ArrayList<Marker> = arrayListOf()
    private var popupPoiCount: String = ""
    private var popupPoiCompletedCount: String = ""
    private var isTakeMeThereBtnClicked: Boolean = false
    private var isFromOnResume: Boolean = false
    private var isDiscovered: Boolean = false
    private var isVibrated: Boolean = false
    private var isTrailCompletedPopUp: Boolean = false
    private var isAchievementUnlockPopUp: Boolean = false
    private var isLevelUpPopUp: Boolean = false
    private var isRewardPopUp: Boolean = false
    private var levelPopUpValue: Int = 0
    private var levelPopUpRankingText: String=""
    private var achievementUnlockImage: String = ""
    private var achievementUnlockTitle: String = ""
    private val REQUEST_STORAGE_PERMISSION = 505
    private val MYPERMISSIONSREQUESTLOCATION = 68
    private val REQUESTCHECKSETTINGS = 129

    fun readJsonFromAssets(context: Context, filePath: String): String? {
        try {
            val source = context.assets.open(filePath).source().buffer()
            return source.readByteString().string(Charset.forName("utf-8"))

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    lateinit var dialog1:Dialog

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission", "ResourceType", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.discover_screen_activity)

        dialog1 = Dialog(this, FirebaseUI_Transparent)

        val policy = ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)

        sharedPreference = SessionHandlerClass(applicationContext)
        trailTutorialAdapter = TrailTutorialAdapter()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        levelTextView = findViewById(R.id.level_textView)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        try {
            // Construct a FusedLocationProviderClient.
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        } catch (e: Exception) {
            error("fusedLocationProviderClient :: ${e.printStackTrace()}")
        }


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


        getUserDetails()
        getUserAchievementsAPI()
        getTrailDetails()


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
                            }

                            latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
                            println("Updated Location : $latLng")

                            if (latLng != null) {
                                if (isFromOnResume) {
                                    isFromOnResume = false
                                    println("clicked_btn:: ${sharedPreference.getSession("clicked_button")}")
                                    if (sharedPreference.getSession("clicked_button") != "take_me_there" &&
                                        sharedPreference.getSession("clicked_button") != "no_reload" &&
                                        sharedPreference.getSession("clicked_button") == ""

                                    ) {

                                        println("On Map Ready :: Receiver=> $latLng")
                                        if (sharedPreference.getSession("selected_trail_id") == "") {

                                            getEventsList("4")
                                        } else {

                                            getEventsList(sharedPreference.getSession("selected_trail_id") as String)
                                        }

                                    } else if (sharedPreference.getSession("clicked_button") == "take_me_there") {
                                        isTakeMeThereBtnClicked = true
                                        drawPath()
                                    } else if (sharedPreference.getSession("clicked_button") == "from_back_button") {
                                        drawPath()
                                    }
                                }

                            }

                            println("updateCurrentLocation Status => $isDrawPathClicked , $isMarkerClicked")
                           // if (isDrawPathClicked || isMarkerClicked) {
                                 updateCurrentLocation(latLng, "1") //1
                            //}

                          /*  runOnUiThread {
                                Handler().postDelayed(Runnable {
                                    updateCurrentLocation(latLng, "1")
                                }, 16)
                            }*/

                            println("isMockLocationEnabled = > ${isMockLocationEnabled()}")


                           // DETECT mock app
                           /* if(mService!!.currentLocation!!.isFromMockProvider)
                            {

                                mockLocationAppDetectDialog()
                            }
                            else
                            {
                                if (dialog1 != null) {
                                    if (dialog1.isShowing) {
                                        dialog1.dismiss()
                                    }
                                }
                            }*/

                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }
                }
            }
        }


        currentLocation = findViewById(R.id.currentLocation)
        mapListToggleButton = findViewById(R.id.chkState)
        backButton = findViewById(R.id.backButton)
        searchButton = findViewById(R.id.searchButton)
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
        markerWindowCloseButton = findViewById(R.id.poi_layout_closeButton)
        poiDistance = findViewById(R.id.tv_distance)
        trailsNameText = findViewById(R.id.trailsName)
        recyclerView = findViewById(R.id.recyclerView)

        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        listScreenRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        listScreenRecycler.overScrollMode = View.OVER_SCROLL_NEVER

        feedIcon.setOnClickListener {
            startActivity(Intent(applicationContext, FeedPostActivity::class.java))
        }
        trailsIcon.setOnClickListener {
            startActivity(Intent(applicationContext, TrailScreenActivity::class.java))
        }
        discover_icon_large.setOnClickListener {
           /* Toast.makeText(applicationContext,"clicked",100)
                .show()   */     }
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
        }

        how_to_play_sub_layout.setOnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE
            startActivity(Intent(applicationContext, HowToPlayActivity::class.java))

        }

        settings_sub_layout.setOnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE
            startActivity(Intent(applicationContext, SettingsScreenAct::class.java))
        }

        rewards_sub_layout.setOnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE
            startActivity(Intent(applicationContext, RewardsScreenActivity::class.java))
        }

        profile_sub_layout.setOnClickListener {
            more_option_layout_header.visibility = View.GONE
            currentLocationLayout.visibility = View.VISIBLE

            startActivity(Intent(applicationContext, ProfileScreenActivity::class.java))
        }

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

            if (takeMeBtn.text.equals(resources.getString(R.string.take_me_there))) {
                val from = resources.getString(R.string.take_me_there)
                println("markWindowConstraintLayout => IF $from")
                startActivity(
                    Intent(
                        this@DiscoverScreenActivity,
                        DiscoverDetailsScreenActivity::class.java
                    ).putExtra("from", from)
                )
            } else if (takeMeBtn.text.equals(resources.getString(R.string.discover))) {

                val from = takeMeBtn.text.toString()
                println("markWindowConstraintLayout => ELSE $from")
                startActivity(
                    Intent(
                        this@DiscoverScreenActivity,
                        DiscoverDetailsScreenActivity::class.java
                    ).putExtra("from", from)

                )
            } else if (takeMeBtn.text.equals("DISCOVERED")) {
                val from = "DISCOVERED"
                println("markWindowConstraintLayout => ELSE $from")
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

            searchButton.background = getDrawable(searchbar_icon)
            trailsListLayout.visibility = View.GONE
            recyclerView.visibility = View.GONE
            //   searchLayout.setBackgroundResource(searchview_bg)
            backButton.visibility = View.VISIBLE
            //  searchView.visibility = View.VISIBLE
            editTextSearchView.visibility = View.VISIBLE

            trailsBtn.visibility = View.GONE
            profileLayout.visibility = View.GONE


            editTextSearchView.requestFocus()
            performSearch()
        }

        backButton.setOnClickListener {

            searchButton.background = getDrawable(search_button)

            searchLayout.setBackgroundResource(0)
            backButton.visibility = View.GONE
            editTextSearchView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            trailsBtn.visibility = View.VISIBLE
            profileLayout.visibility = View.VISIBLE

            // Only for list screen not for map screen.
            if (isListScreen) {
                trailsListLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }

        currentLocation.setOnClickListener {
            println("*** Triggered ****")
            isGesturedOnMap = false
            updateCurrentLocation(latLng, "2") //2
        }

        trailsBtn.setOnClickListener {
            availableTrailsListView()
        }

        mapListToggleButton.setOnCheckedChangeListener { _, isChecked ->
            try {
                if (isChecked) {
                    // Open the poi list..

                    sharedPreference.saveSession("toggle_button", "list")
                    println("Dist Sie = ${distanceMatrixApiModelObj.size}== ${CommonData.eventsModelMessage!!.size}")
                    if (CommonData.eventsModelMessage != null
                        || CommonData.eventsModelMessage!!.isNotEmpty() && (distanceMatrixApiModelObj.size > 0)
                    ) {
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
                        editTextSearchView.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        trailsBtn.visibility = View.VISIBLE
                        searchButton.background = getDrawable(search_button)
                    } else {
                        getEventsList(sharedPreference.getSession("selected_trail_id") as String)
                        Toast.makeText(applicationContext, "Please try again", Toast.LENGTH_SHORT)
                            .show()
                    }


                } else {
                    // open the map screen
                    sharedPreference.saveSession("toggle_button", "map")
                    isListScreen = false
                    trailsListLayout.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    mapMainLayout.visibility = View.VISIBLE
                    currentLocation.visibility = View.VISIBLE

                    if (sharedPreference.getSession("clicked_button").equals("take_me_there")
                        || sharedPreference.getSession("clicked_button").equals("from_back_button")
                        ||isMarkerClicked)
                    {
                        if(trailsNameText.text!="")
                        {
                            markWindowConstraintLayout.visibility = View.VISIBLE
                            takeMeBtn.visibility = View.VISIBLE
                            profileLayout.visibility = View.GONE
                            trailsBtn.visibility = View.GONE
                            searchButton.visibility = View.GONE
                            editTextSearchView.visibility = View.GONE
                            recyclerView.visibility = View.GONE
                            backButton.visibility = View.GONE
                        }

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        takeMeBtn.setOnClickListener {

            var from = ""
            when {

                takeMeBtn.text.equals(resources.getString(R.string.take_me_there)) -> {

                    isTakeMeThereBtnClicked = true
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
            sharedPreference.saveSession("clicked_button", "")
            sharedPreference.saveSession("toggle_button", "")
            isTakeMeThereBtnClicked = false
            if (isSelectedMarker || isFromPOIList || !isFromPOIList) {
                isSelectedMarker = false
                isMarkerClicked = false

                if (selectedPOIDiscoverId == null) {

                    if (sharedPreference.getSession("selected_trail_id") == "6") {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_undiscovered
                            )
                        )

                    } else if (sharedPreference.getSession("selected_trail_id") == "4") {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                poi_breadcrumbs_marker_undiscovered_1
                            )
                        )
                    }
                } else {
                    if (sharedPreference.getSession("selected_trail_id") == "6") {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_discovered
                            )
                        )

                    } else if (sharedPreference.getSession("selected_trail_id") == "4") {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                poi_breadcrumbs_marker_discovered_1
                            )
                        )
                    }
                }

            }

            mMap.animateCamera(CameraUpdateFactory.zoomTo(19.0f))
            markerAnimationHandler.removeCallbacks(markerAnimationRunnable)

            isGesturedOnMap = false
            isSelectedMarker = false
            isDrawPathClicked = false
            isMarkerClicked = false

            searchButton.background = getDrawable(search_button)

            searchLayout.setBackgroundResource(0)
            backButton.visibility = View.GONE
            editTextSearchView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            trailsBtn.visibility = View.VISIBLE
            profileLayout.visibility = View.VISIBLE

            markWindowConstraintLayout.visibility = View.GONE
            takeMeBtn.visibility = View.GONE

            profileLayout.visibility = View.VISIBLE
            searchButton.visibility = View.VISIBLE

            takeMeBtn.background = getDrawable(take_me_discover)
            takeMeBtn.text = resources.getString(R.string.take_me_there)

            markerWindowCloseButton.visibility = View.GONE

        }


    }

    private fun performSearch() {
        println("editTextSearchView :: performSearch ")
        editTextSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("editTextSearchView :: beforeTextChanged ")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("editTextSearchView :: onTextChanged ")
                recyclerView.visibility = View.VISIBLE
                search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                println("editTextSearchView :: afterTextChanged ")
                s?.let {
                    if (s.isNotEmpty()) {
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.GONE
                    }
                    search(s.toString())
                }
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
        println("******** On Pause ******** ")
        sharedPreference.saveSession("clicked_button", "")
        sharedPreference.saveSession("toggle_button", "")
    }

    override fun onResume() {
        super.onResume()

        if (sharedPreference.getSession("selected_trail_id") == "" || sharedPreference.getSession("from_challenge_screen")
                .equals("YES")
        ) {
            container_view.visibility = View.GONE
            loader_layout.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({

                Glide.with(applicationContext).load(R.raw.loader_screen).into(loader_screen_gif)
            }, 1000)
        }


        //from_challenge_screen
        if (sharedPreference.getSession("from_challenge_screen")
                .equals("YES") && sharedPreference.getSession("clicked_button") != "from_back_button"
        ) {
            sharedPreference.saveSession("clicked_button", "")
            sharedPreference.saveSession("toggle_button", "")
            isMarkerClicked = false
            isDrawPathClicked = false
        }

        isFromOnResume = true
        val intentFilter = IntentFilter()
        intentFilter.addAction("NotifyUser")
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it, intentFilter)
        }


        if (sharedPreference.getSession("toggle_button").equals("list")) {
            try {
                mapListToggleButton.isChecked = true
                sharedPreference.saveSession("toggle_button", "list")
                println("Dist Sie = ${distanceMatrixApiModelObj.size}== ${CommonData.eventsModelMessage!!.size}")
                if (CommonData.eventsModelMessage != null
                    || CommonData.eventsModelMessage!!.isNotEmpty() && (distanceMatrixApiModelObj.size > 0)
                ) {
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
                    editTextSearchView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    trailsBtn.visibility = View.VISIBLE

                    searchButton.background = getDrawable(search_button)
                } else {
                    getEventsList(sharedPreference.getSession("selected_trail_id") as String)
                    Toast.makeText(applicationContext, "Please try again", Toast.LENGTH_SHORT)
                        .show()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (sharedPreference.getSession("toggle_button").equals("map")) {
            // open the map screen
            if(trailsNameText.text!="")
            {
                mapListToggleButton.isChecked = false
                sharedPreference.saveSession("toggle_button", "map")
                isListScreen = false
                trailsListLayout.visibility = View.GONE
                recyclerView.visibility = View.GONE
                mapMainLayout.visibility = View.VISIBLE
                currentLocation.visibility = View.VISIBLE
                markWindowConstraintLayout.visibility = View.VISIBLE
                takeMeBtn.visibility = View.VISIBLE
                profileLayout.visibility = View.GONE
                trailsBtn.visibility = View.GONE
                searchButton.visibility = View.GONE
                editTextSearchView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                backButton.visibility = View.GONE
            }

        }


    }

    override fun onPause() {
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        super.onPause()

        println("******** On Pause ******** ")
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
            REQUEST_STORAGE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {

                }
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

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@DiscoverScreenActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        }

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

    }

    var lastlatlng: LatLng? = null
    private fun updateCurrentLocation(newLatLng: LatLng?, place: String) {
        //1.4025944219780742, 103.78994695871972
        println("updateCurrentLocation => $newLatLng :: $place")

        if (newLatLng != null) {
            if (null != currentLocationMarker) {
                currentLocationMarker!!.remove()
            }

            val distance = lastRouteLog?.let {
                SphericalUtil.computeDistanceBetween(newLatLng, it)
            } ?: kotlin.run {
                -1
            }
            println("Just:::::::::::::::::: distance  $distance")
            // discover area
            if ((distance.toDouble() in 0.0..900.0)) {

                if (selectedPOIDiscoverId == null) {
                    Toast.makeText(this, "POI is nearby", Toast.LENGTH_LONG).show()
                    takeMeBtn.visibility = View.VISIBLE
                    takeMeBtn.text = resources.getString(R.string.discover)
                    takeMeBtn.background = getDrawable(take_me_discover)
                }

                println("Vibrator Status = ${sharedPreference.getBoolean("isVibratorOn")}")
                if (sharedPreference.getBoolean("isVibratorOn")) {
                    if (!isVibrated) {
                        vibrateOn()
                    }
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

                SphericalUtil.computeHeading(it, newLatLng).toFloat()


            } ?: kotlin.run {
                -50.12f
            }
            println("Just:::::::::::::::: bearing $bearing")

            if (bearing == 0.0f) {
                bearing = -50.12f
            }

            currentLocationMarker = mMap.addMarker(
                MarkerOptions().position(newLatLng).icon(
                    BitmapDescriptorFactory.fromResource(map_current_location_marker)
                ).anchor(0.5f, 0.5f).rotation(bearing).flat(true)
            )



            println("*** Triggered $isGesturedOnMap")


            mMap.uiSettings.isCompassEnabled = false

           /* MarkerAnimation.animateMarkerToGB(currentLocationMarker!!, newLatLng,
                LatLngInterpolator.Spherical(), mMap)
            lastlatlng?.let {
                currentLocationMarker!!.rotation = getBearing( it, newLatLng)
            }*/

            if (!isGesturedOnMap) {

                if (!isDrawPathClicked || !isMarkerClicked)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng))


                }
                val cameraPosition = CameraPosition.Builder()
                    .target(newLatLng)
                    .zoom(19.0f)
                    .tilt(0f)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            lastlatlng = newLatLng


        } else {
            println("LatLng Is NULL from current Loc.,")
        }

    }

    private fun getBearing( begin : LatLng,  end : LatLng) : Float {
        val lat = Math.abs(begin.latitude - end.latitude);
        val lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return(Math.toDegrees(Math.atan(lng / lat))).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return  ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270).toFloat()
        return -1f;
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
        println("OnMapReady***** ${sharedPreference.getSession("selected_trail_id")}")
        mMap = googleMap
        try {
            if (mMap != null) {
                mMap.clear()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        loader_layout.visibility = View.GONE
        container_view.visibility = View.VISIBLE
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

        mapListToggleButton.isClickable = true
        if (null != currentLocationMarker) {
            currentLocationMarker!!.remove()
        }


        println("On Map Ready :: $latLng")

        currentLocation.performClick()

        for (i in 0 until CommonData.eventsModelMessage!!.count()) {

            latLng1 = LatLng(
                CommonData.eventsModelMessage!![i].latitude.toDouble(),
                CommonData.eventsModelMessage!![i].longitude.toDouble()
            )

            if (sharedPreference.getSession("selected_trail_id") == "4") {

                if (CommonData.eventsModelMessage!![i].disc_id == null) {

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
                clickedMarker!!.tag = i


            } else if (sharedPreference.getSession("selected_trail_id") == "6") {
                if (CommonData.eventsModelMessage!![i].disc_id == null) {

                    println("Executed ===>")
                    clickedMarker = mMap.addMarker(
                        MarkerOptions().position(latLng1).icon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_undiscovered
                            )
                        )
                    )
                } else {
                    clickedMarker = mMap.addMarker(
                        MarkerOptions().position(latLng1).icon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_discovered
                            )
                        )
                    )
                }
                clickedMarker!!.tag = i

            }
            clickedMarker?.let {
                markerArrayList.add(it)
            }

        }

        mMap.setOnMapClickListener {

            println("Map Clicked : $isDrawPathClicked")
            if (!isDrawPathClicked) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19.0f))
                markerAnimationHandler.removeCallbacks(markerAnimationRunnable)

                isDrawPathClicked = false

                isTakeMeThereBtnClicked = false

                if (polyline != null) {
                    polyline!!.remove()
                }


                isGesturedOnMap = false

                sharedPreference.saveSession("clicked_button", "")

                searchButton.background = getDrawable(search_button)

                searchLayout.setBackgroundResource(0)
                backButton.visibility = View.GONE
                editTextSearchView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                trailsBtn.visibility = View.VISIBLE
                profileLayout.visibility = View.VISIBLE
                markWindowConstraintLayout.visibility = View.GONE
                takeMeBtn.visibility = View.GONE
                profileLayout.visibility = View.VISIBLE
                searchButton.visibility = View.VISIBLE
                more_option_layout_header.visibility = View.GONE
                currentLocationLayout.visibility = View.VISIBLE


                if (isSelectedMarker) {
                    isSelectedMarker = false

                    isMarkerClicked = false

                    println("selectedPOIDiscoverId Clicking Map = $selectedPOIDiscoverId")
                    if (sharedPreference.getSession("selected_trail_id") == "4") {
                        if (selectedPOIDiscoverId == null || selectedPOIDiscoverId == "") {

                            clickedMarker?.setIcon(
                                BitmapDescriptorFactory.fromResource(
                                    poi_breadcrumbs_marker_undiscovered_1
                                )
                            )
                        } else {

                            clickedMarker?.setIcon(
                                BitmapDescriptorFactory.fromResource(
                                    poi_breadcrumbs_marker_discovered_1
                                )
                            )
                        }
                    } else if (sharedPreference.getSession("selected_trail_id") == "6") {
                        if (selectedPOIDiscoverId == null || selectedPOIDiscoverId == "") {

                            println("Executed ===>")


                            clickedMarker?.setIcon(
                                BitmapDescriptorFactory.fromResource(
                                    hanse_poi_undiscovered
                                )
                            )
                        } else {

                            clickedMarker?.setIcon(
                                BitmapDescriptorFactory.fromResource(
                                    hanse_poi_discovered
                                )
                            )
                        }
                    }


                }
            } else {
                //do nothing..
            }

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
            val bundle: Bundle? = intent.extras
            val isFromLogin = bundle!!.getString("isFromLogin")
            if (isFromLogin.equals("yes")) {
                disclaimerWindow()
            } else if (isFromLogin.equals("no")) {
                visibleItems()

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

        val checkBox = dialog.findViewById(R.id.checkBox) as CheckBox
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkBox.isChecked

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


        val radioBtn1 = dialog.findViewById(R.id.wild_trail_radio_button) as ImageView
        val radioBtn2 = dialog.findViewById(R.id.hanse_trail_radio_button) as ImageView
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

        println("Trail Window :: ${CommonData.getTrailsData!!.size}")

        /*
        * Trail Id = 4 means, Wild About Twilight
        * Trail Id = 6 means, Anthology Trial
        *
        * Based on radio button choosing, needs to change the trail id value.
        * */

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
            sharedPreference.saveSession("selected_trail_id", "4")
        }


        radioBtn2.setOnClickListener {
            firstRadioChecked = false
            secondRadioChecked = true
            radioBtn2.setImageResource(radio_btn_active)
            radioBtn1.setImageResource(radio_btn_deactive)
            radioBtn3.setImageResource(radio_btn_deactive)
            radioBtn4.setImageResource(radio_btn_deactive)
            sharedPreference.saveSession("selected_trails", trailTwo.text.toString())
            sharedPreference.saveSession("selected_trail_id", "6")
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

            println("Selected Trail : ${sharedPreference.getSession("selected_trail_id")}")
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
                }

                mMap.clear()
                dialog.dismiss()
                println("selected_trail_id:: ${sharedPreference.getSession("selected_trail_id")}")
                getUserAchievementsAPI()
                getEventsList(sharedPreference.getSession("selected_trail_id") as String)
            }

        })


        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

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

        val learnMoreButton = dialog.findViewById(R.id.learn_more_button) as TextView
        learnMoreButton.setOnClickListener {
            startActivity(Intent(applicationContext, TrailScreenActivity::class.java))
        }

        val checkBox = dialog.findViewById(R.id.checkBox) as CheckBox
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkBox.isChecked

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

                if (days.equals("0") || days.equals("1")) {
                    textToGo.text = "$days ${resources.getText(R.string.day_go)}"
                } else {
                    textToGo.text = "$days ${resources.getText(R.string.days_go)}"
                }
            } else {
                println("Date Is :  ELSE: $days")
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    override fun onClickedPOIItem(id: String) {
        CommonData.eventsModelMessage?.let {
            for (i in it.indices) {
                if (it[i].id == id) {
                    isFromPOIList = true
                    println("onClickedPOIItem= ${clickedMarker!!.tag} , $i")
                    if (polyline != null) {
                        polyline!!.remove()
                    }

                    clickedMarker = markerArrayList.get(i)
                    selectedMarker(i)
                    break
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onMarkerClick(p0: Marker): Boolean {


        println("onMarkerClick = > $isMarkerClicked")

        if (!isDrawPathClicked) {
            if (polyline != null) {
                polyline!!.remove()
            }

            if (selectedPOIDiscoverId == null) {
                if (sharedPreference.getSession("selected_trail_id") == "6") {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            hanse_poi_undiscovered
                        )
                    )

                } else if (sharedPreference.getSession("selected_trail_id") == "4") {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_undiscovered_1
                        )
                    )
                }
            } else {
                if (sharedPreference.getSession("selected_trail_id") == "6") {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            hanse_poi_discovered
                        )
                    )

                } else if (sharedPreference.getSession("selected_trail_id") == "4") {
                    clickedMarker?.setIcon(
                        BitmapDescriptorFactory.fromResource(
                            poi_breadcrumbs_marker_discovered_1
                        )
                    )
                }
            }

            isVibrated = false
            takeMeBtn.background = getDrawable(take_me_discover)
            takeMeBtn.text = resources.getString(R.string.take_me_there)
            markerWindowCloseButton.visibility = View.GONE

            if (CommonData.eventsModelMessage != null) {
                for (i in 0 until CommonData.eventsModelMessage!!.count()) {

                    if (i == p0.tag) {
                        println("onMarkerClick => IF $i , ${p0.tag}")
                        isMarkerClicked = true

                        clickedMarker = p0

                        selectedMarker(i)
                    }
                }
            }

        }

        return true
    }

    var lastRouteLog: LatLng? = null
    fun midPoint(lat1: Double, long1: Double, lat2: Double, long2: Double): LatLng {
        return LatLng((lat1 + lat2) / 2, (long1 + long2) / 2)
    }

    private fun drawPath() {

        val pattern = listOf(
            Dot(), Gap(10F)
        )

        println("drawPath - latLng :: $latLng")
        println("drawPath - latLng1 :: $latLng1")
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


        // After click the Take Me There Button.,
        if (isTakeMeThereBtnClicked) {
            isDrawPathClicked = true
            markerWindowCloseButton.visibility = View.VISIBLE

            if (selectedPOIDiscoverId == null) {
                takeMeBtn.setBackgroundResource(travelling_bg)
                takeMeBtn.text = resources.getString(R.string.travelling)
                Glide.with(applicationContext).load(exit_navigation_mode)
                    .into(markerWindowCloseButton)
            } else {
                takeMeBtn.setBackgroundResource(take_me_discover)
                takeMeBtn.text = resources.getString(R.string.travelling)
                //markerWindowCloseButton
                Glide.with(applicationContext).load(discovered_close_btn)
                    .into(markerWindowCloseButton)
            }


            if (polyline != null) {
                polyline!!.remove()
            }
            polyline = mMap.addPolyline(polyLineOptions)


            /*   val lat1 = latLng!!.latitude
               val lng1 = latLng!!.longitude
               val lat2 = latLng1!!.latitude
               val lng2 = latLng1!!.longitude*/
            //val midLatLng = LatLng((lat1 + lat2) / 2, (lng1 + lng2) / 2)
            val cPosition = CameraPosition(latLng, 19.0f, 0.0f, -30.0f)

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cPosition))


        }

    }

    fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {
        val routes: MutableList<List<HashMap<String, String>>> = ArrayList()
        val jRoutes: JSONArray?
        var jLegs: JSONArray?
        var jSteps: JSONArray?
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


    private fun getEventsList(trailID: String) {

        try {

            println("************** getEventsList = $trailID")
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
            jsonObject.put("trails", trailID)
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

                            if (CommonData.eventsList.size > 0) {
                                CommonData.eventsList.clear()
                            }
                            if (distanceMatrixApiModelObj.size > 0) {
                                distanceMatrixApiModelObj.clear()
                            }


                            response.body()?.message?.forEach {
                                if (it.trail_id == trailID) {
                                    println("************** NAME = ${it.title}")
                                    CommonData.eventsList.add(it)

                                    CommonData.eventsModelMessage = CommonData.eventsList
                                }
                            }


                            //     CommonData.eventsModelMessage = response.body()?.message
                            println("************** Event Model = ${CommonData.eventsModelMessage!!.size}")
                            println("************** getEventsList = $trailID")

                            if (CommonData.eventsModelMessage != null) {

                                for (i in 0 until CommonData.eventsModelMessage!!.count()) {

                                    latLng1 = LatLng(
                                        CommonData.eventsModelMessage!![i].latitude.toDouble(),
                                        CommonData.eventsModelMessage!![i].longitude.toDouble()
                                    )
                                    trailName = CommonData.eventsModelMessage!![i].title
                                    println("Trail Name : $trailName")
                                    println("Events Size : ${CommonData.eventsModelMessage!!.size}")


                                    // THE BELOW CODE HELPS TO GET OVERALL POIs DURATION AND DISTANCE
                                    try {
                                        println("Current Latlng :: $latLng,$latLng1")

                                        val distanceURl = getDistanceURL(latLng!!, latLng1!!)
                                        println("distanceURl = $distanceURl")
                                        val result = URL(distanceURl).readText()
                                        val jsonObject = JSONObject(result)

                                        jRoutes = jsonObject.getJSONArray("rows")

                                        println("jRoutes == ${jRoutes.length()}")

                                        for (k in 0 until jRoutes.length()) {
                                            println("Loop Time : k = $k")
                                            jElements =
                                                (jRoutes[k] as JSONObject).getJSONArray("elements")
                                            for (j in 0 until jElements.length()) {
                                                println("Loop Time : j = $j")
                                                distance =
                                                    ((jElements[j] as JSONObject)["distance"] as JSONObject)["text"] as String
                                                println("distance Matrix distance = $j == $distance")

                                                duration =
                                                    ((jElements[j] as JSONObject)["duration"] as JSONObject)["text"] as String
                                                println("distance Matrix duration = $j == $duration")

                                                distanceMatrixApiModelObj.add(
                                                    DistanceMatrixApiModel(
                                                        distance,
                                                        duration
                                                    )
                                                )

                                            }
                                        }
                                        distanceMatrixApiModelObj.size
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }


                                }
                                runOnUiThread {
                                    if (clickedMarker != null) {

                                        clickedMarker!!.remove()
                                    }
                                    println("On Map Ready :: Before => $latLng")


                                    mapFragment.getMapAsync(this@DiscoverScreenActivity)
                                }


                                println("distanceMatrixApiModelObj = Overall= ${distanceMatrixApiModelObj.size}")
                            }
                        }
                    }


                }
            })


        } catch (e: Exception) {
            e.printStackTrace()
            println("************** getEventsList = ${e.printStackTrace()}")
        }

    }


    private fun getTrailDetails() {
        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
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
                            if (sharedPreference.getSession("from_challenge_screen") == "YES") {
                                for (i in CommonData.getTrailsData!!.indices) {

                                    println("Details about POIs ::: ${CommonData.getTrailsData!![i].poi_count} == ${CommonData.getTrailsData!![i].completed_poi_count}")

                                    if (CommonData.getTrailsData!![i].poi_count == CommonData.getTrailsData!![i].completed_poi_count) {
                                        popupPoiCount = CommonData.getTrailsData!![i].poi_count
                                        popupPoiCompletedCount =
                                            CommonData.getTrailsData!![i].completed_poi_count

                                        isTrailCompletedPopUp = true

                                    }
                                }

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


    private fun selectedMarker(pos: Int) {

        try {

            isDrawPathClicked = false
            isSelectedMarker = true
            println("updateCurrentLocation selectedMarker isMarkerClicked= $isMarkerClicked")
            /* When user click / select the particular POI., that time needs to check either the POI discovered or not.*/



            takeMeBtn.text = resources.getString(R.string.take_me_there)
            poiDistance.text = distanceMatrixApiModelObj[pos].distance
            trailsNameText.text = CommonData.eventsModelMessage!![pos].title
            selectedPOIName = CommonData.eventsModelMessage!![pos].title
            selectedPOIID = CommonData.eventsModelMessage!![pos].id
            selectedPOIDiscoveryXP = CommonData.eventsModelMessage!![pos].experience
            println("selectedPOIDiscoveryXP => $selectedPOIDiscoveryXP")
            //  sharedPreference.saveSession("selectedPOIID",selectedPOIID)
            selectedPOIQuestion = CommonData.eventsModelMessage!![pos].ch_question
            selectedPOIChallengeType = CommonData.eventsModelMessage!![pos].ch_type
            selectedPOIARid = CommonData.eventsModelMessage!![pos].ar_id
            selectedPOITrivia = CommonData.eventsModelMessage!![pos].ch_trivia


            if (CommonData.eventsModelMessage!![pos].ar_id == "null" || CommonData.eventsModelMessage!![pos].ar_id == null) {
                println("selectedPOIARid = Before = $selectedPOIARid")
                selectedPOIARid = "0"
                println("selectedPOIARid = After = $selectedPOIARid")
            } else {
                println("selectedPOIARid = ELSE = $selectedPOIARid")
            }
            selectedPOIDiscoverId = CommonData.eventsModelMessage!![pos].disc_id
            selectedPOIQrCode = CommonData.eventsModelMessage!![pos].qr_code
            selectedPOIHintContent = CommonData.eventsModelMessage!![pos].description
            selectedPOIDuration = distanceMatrixApiModelObj[pos].duration
            selectedPOIImage =
                resources.getString(R.string.staging_url) + CommonData.eventsModelMessage!![pos].poi_img
            println("poi_ID selectedPOIID = $selectedPOIID")

            val questionType = CommonData.eventsModelMessage!![pos].ch_type
            val chSelection = CommonData.eventsModelMessage!![pos].ch_question
            when {
                questionType.toInt() == 1 -> {
                    //singleQuestion
                    noOfQuestions = "Answer one question on"
                }
                questionType.toInt() == 2 -> {
                    try {
                        questionObj = JsonParser.parseString(chSelection) as JsonArray
                        println("Question Screen ::: ${questionObj.size()}")
                        noOfQuestions = "Answer ${questionObj.size()} questions on"
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            profileLayout.visibility = View.GONE
            trailsBtn.visibility = View.GONE
            searchButton.visibility = View.GONE
            editTextSearchView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            backButton.visibility = View.GONE
            markWindowConstraintLayout.visibility = View.VISIBLE
            takeMeBtn.visibility = View.VISIBLE

            currentLocation.visibility = View.VISIBLE
            trailsListLayout.visibility = View.GONE
            mapMainLayout.visibility = View.VISIBLE
            try {
                sharedPreference.saveSession("selectedPOIName", selectedPOIName)
                sharedPreference.saveSession("selectedPOIID", selectedPOIID)
                sharedPreference.saveSession("poiDistance", poiDistance.text.toString())
                sharedPreference.saveSession("selectedPOIImage", selectedPOIImage)
                sharedPreference.saveSession("selectedPOIDuration", selectedPOIDuration)
                sharedPreference.saveSession("selectedPOIQuestion", selectedPOIQuestion)
                sharedPreference.saveSession("selectedPOIHintContent", selectedPOIHintContent)
                sharedPreference.saveSession("selectedPOIChallengeType", selectedPOIChallengeType)
                sharedPreference.saveSession("selectedPOITrivia", selectedPOITrivia)
                sharedPreference.saveSession("noOfQuestions", noOfQuestions)
                sharedPreference.saveSession(
                    "selectedPOIDiscovery_XP_Value",
                    selectedPOIDiscoveryXP
                )
                sharedPreference.saveSession(
                    "selectedPOIChallenge_XP_Value",
                    CommonData.eventsModelMessage!![pos].ch_experience
                )

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
            // The below method helps to get Distance btw user and selected POI

            drawPath()

            println("selectedPOIDiscoverId = $selectedPOIID Discover_id = $selectedPOIDiscoverId")


            if (selectedPOIDiscoverId == null) {
                println("selectedPOIDiscoverId IF= $selectedPOIDiscoverId")
                markerWindow_background.background =
                    resources.getDrawable(trail_banner_undiscovered)
                markerWindowDiscoverTextView.text = resources.getString(R.string.undiscovered_txt)
                Glide.with(applicationContext).load(exit_navigation_mode)
                    .into(markerWindowCloseButton)
                isDiscovered = false
                if (sharedPreference.getSession("selected_trail_id") == "4") {

                    Glide.with(applicationContext)
                        .load(list_poi_icon)
                        .into(markWindowPOIIcon)

                } else if (sharedPreference.getSession("selected_trail_id") == "6") {

                    Glide.with(applicationContext)
                        .load(hanse_trail_list_icon_undiscovered)
                        .into(markWindowPOIIcon)

                }

                if (isFromPOIList) {
                    isFromPOIList = false
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
                markerWindowDiscoverTextView.text = resources.getString(R.string.discovered_text)
                Glide.with(applicationContext).load(discovered_close_btn)
                    .into(markerWindowCloseButton)
                takeMeBtn.text = resources.getString(R.string.discovered_text)
                isDiscovered = true
                if (sharedPreference.getSession("selected_trail_id") == "4") {

                    Glide.with(applicationContext)
                        .load(discovered_poi_ico_banner)
                        .into(markWindowPOIIcon)
                } else if (sharedPreference.getSession("selected_trail_id") == "6") {


                    Glide.with(applicationContext)
                        .load(hanse_trail_list_icon__discovered)
                        .into(markWindowPOIIcon)
                }


                takeMeBtn.visibility = View.GONE

                println("isFromPOIList = > $isFromPOIList")
                if (isFromPOIList) {
                    isFromPOIList = false
                    val from = "DISCOVERED"
                    startActivity(
                        Intent(
                            this@DiscoverScreenActivity,
                            DiscoverDetailsScreenActivity::class.java
                        ).putExtra("from", from)

                    )
                }

            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1))
            //  mMap.animateCamera(CameraUpdateFactory.zoomTo(19.0f))

            markerAnimationHandler.removeCallbacks(markerAnimationRunnable)
            markerAnimationHandler.postDelayed(markerAnimationRunnable, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }


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


        println("onClickedPOIItem= markerAnimation=> ${clickedMarker?.tag}")

        if (sharedPreference.getSession("selected_trail_id") == "6") {
            if (isDiscovered) {
                when (iconPosition) {
                    1 -> {


                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_discovered
                            )
                        )
                        iconPosition = 2
                    }
                    2 -> {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_discovered_2
                            )
                        )
                        iconPosition = 3
                    }
                    3 -> {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_discovered_3
                            )
                        )
                        iconPosition = 4
                        reverse = false

                    }
                    4 -> {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_discovered_4
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
                                hanse_poi_discovered_5
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
                                hanse_poi_undiscovered
                            )
                        )
                        iconPosition = 2
                    }
                    2 -> {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_undiscovered_2
                            )
                        )
                        iconPosition = 3
                    }
                    3 -> {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_undiscovered_3
                            )
                        )
                        iconPosition = 4
                        reverse = false

                    }
                    4 -> {
                        clickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromResource(
                                hanse_poi_undiscovered_4
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
                                hanse_poi_undiscovered_5
                            )
                        )
                        iconPosition = 4
                        reverse = true

                    }


                }
            }
        } else if (sharedPreference.getSession("selected_trail_id") == "4") {
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

                            runOnUiThread {
                                println("From Get User :: ${CommonData.getUserDetails!!.username}")
                                playerName.text = CommonData.getUserDetails!!.username
                                calculateUserLevel(Integer.parseInt(CommonData.getUserDetails!!.experience))
                            }

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
                ranking = "Recruit"
                level = 1
                base = 1000
                nextLevel = 1000
            }
            in 1000..1999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 2
                base = 1000
                nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 3
                base = 2000
                nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 4
                base = 3000
                nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 5
                base = 4000
                nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 6
                base = 6000
                nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 7
                base = 8000
                nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 8
                base = 10000
                nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 9
                base = 12000
                nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "Navigator"
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

        println("Level Text => before => ${levelTextView.text} , $level")

        levelPopUpValue = level
        levelPopUpRankingText=ranking
        levelTextView.text = "$ranking LV. $level"

        val expToLevel =
            (nextLevel - base) - (exp - base) // (2000-1000) - (400-1000) = (1000)-(-600)=1600

        println("expToLevel= $expToLevel")


        println("Level Text => after => ${sharedPreference.getSession("lv_value_int")} , $level")
        //from_challenge_screen
        if (sharedPreference.getSession("from_challenge_screen") == "YES") {
            if (level > sharedPreference.getSession("lv_value_int")!!.toInt()) {
                //  sharedPreference.saveSession("from_challenge_screen","")
                //Level Completed..

                isLevelUpPopUp = true

            }
        }

        sharedPreference.saveSession("level_text_value", levelTextView.text.toString())
        sharedPreference.saveSession("expTo_level_value", expToLevel)
        sharedPreference.saveSession("xp_point_base_value", base)
        sharedPreference.saveSession("xp_point_nextLevel_value", nextLevel)
        sharedPreference.saveSession("lv_value", "LV ${level + 1}")
        sharedPreference.saveSession("lv_value_int", "$level")
        sharedPreference.saveSession("current_level", "$ranking $level")


    }

    private fun vibrateOn() {

        isVibrated = true
        println("vibrateOn....")
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(1500)

        }
    }

    private fun vibrateOff() {
        vibrator.cancel()

    }

    private fun getUserAchievementsAPI() {
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


            val jsonObject = JSONObject()
            jsonObject.put("user_id", sharedPreference.getSession("login_id"))

            println("getUserAchievementsAPI Url = ${resources.getString(R.string.staging_url)}")
            println("getUserAchievementsAPI Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)

            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getUserAchievements(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getUserAchievementsModel = response.body()?.message

                        runOnUiThread {
                            if (sharedPreference.getSession("from_challenge_screen") == "YES") {
                                CommonData.getUserAchievementsModel!!.forEach { it ->
                                    if (it.trail_id == sharedPreference.getSession("selected_trail_id")) {

                                        println("User Ach => ${it.trail_id} => ${it.ua_id}")

                                        if (it.ua_id != null) {

                                            achievementUnlockTitle = it.title
                                            achievementUnlockImage =
                                                resources.getString(R.string.staging_url) + it.badge_img
                                            isAchievementUnlockPopUp = true
                                            playerPopUp(1, 0)
                                        } else {
                                            if (!isAchievementUnlockPopUp && isLevelUpPopUp) {
                                                playerPopUp(3, levelPopUpValue)
                                            } else {
                                                sharedPreference.saveSession(
                                                    "from_challenge_screen",
                                                    ""
                                                )
                                            }
                                        }
                                    }


                                }


                            } else {
                                sharedPreference.saveSession("from_challenge_screen", "")
                            }

                        }
                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun playerPopUp(id: Int, levelNumber: Int) {
        val dialog = Dialog(this, FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        println("PopUp = > $id , $levelNumber")

        /*
           ID =
           1 => Achievement Unlock Layout
           2 => Trail Completed Layout
           3 => LeveL Up Layout
           4 => New Reward Layout

         */

        when (id) {
            1 -> {
                dialog.setContentView(R.layout.achievement_unlock_layout)
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                dialog.window?.setDimAmount(0.1f)
                println("PopUp = > $id")


                val achievementName: TextView = dialog.findViewById(R.id.achievement_unlock_name)
                val achievementUnlockIv: ImageView = dialog.findViewById(R.id.achievement_unlock_iv)

                achievementName.text = achievementUnlockTitle
                Glide.with(applicationContext).load(achievementUnlockImage)
                    .into(achievementUnlockIv)

                val popUpCloseButton: ImageView = dialog.findViewById(R.id.pop_up_close_btn)
                popUpCloseButton.setOnClickListener {

                    println("PopUp = > popUpCloseButton $id")
                    dialog.dismiss()
                    isRewardPopUp = true
                    if (isTrailCompletedPopUp) {
                        isTrailCompletedPopUp = false
                        playerPopUp(2, 0)
                    }
                }


            }
            2 -> {
                dialog.setContentView(R.layout.trail_completed_layout)
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                dialog.window?.setDimAmount(0.1f)
                println("PopUp = > $id")
                val infoTV = dialog.findViewById(R.id.trail_completed_info_content) as TextView
                //You've completed the Pioneer Trail!\nVisit the Rewards page to view your new prizes.
                val trailName = sharedPreference.getSession("selected_trails")
                infoTV.text =resources.getString(R.string.trail_completion_text)

                val trailCountTV = dialog.findViewById(R.id.trail_count_tv) as TextView
                trailCountTV.text = "$popupPoiCompletedCount / $popupPoiCount"


                val popUpCloseButton: ImageView = dialog.findViewById(R.id.pop_up_close_btn)
                popUpCloseButton.setOnClickListener {

                    if (isLevelUpPopUp) {
                        isLevelUpPopUp = false
                        playerPopUp(3, levelPopUpValue)
                    } else {
                        if (isRewardPopUp) {
                            isRewardPopUp = false
                            playerPopUp(4, 0)
                        }
                    }

                    dialog.dismiss()
                }

            }
            3 -> {
                dialog.setContentView(R.layout.level_up_layout)
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                dialog.window?.setDimAmount(0.1f)
                println("PopUp = > 3 ENTER ******")

                val levelNumberTV: TextView = dialog.findViewById(R.id.level_pop_up_level_number_tv)
                levelNumberTV.text = "${levelPopUpRankingText.toUpperCase()} $levelNumber"


                val popUpCloseButton: ImageView = dialog.findViewById(R.id.pop_up_close_btn)
                popUpCloseButton.setOnClickListener {

                    if (isRewardPopUp) {
                        isRewardPopUp = false
                        playerPopUp(4, 0)
                    }
                    sharedPreference.saveSession("from_challenge_screen", "")
                    dialog.dismiss()
                }

            }

            4 -> {

                dialog.setContentView(R.layout.new_reward_announcement_layout)
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                dialog.window?.setDimAmount(0.1f)
                println("PopUp = > $id")


                val imageIV: ConstraintLayout = dialog.findViewById(R.id.new_reward_image_layout)

                imageIV.setOnClickListener {
                    dialog.dismiss()
                    startActivity(Intent(applicationContext, RewardsScreenActivity::class.java))
                }
                val popUpCloseButton: ImageView = dialog.findViewById(R.id.pop_up_close_btn)
                popUpCloseButton.setOnClickListener {
                    sharedPreference.saveSession("from_challenge_screen", "")
                    dialog.dismiss()
                }
            }
        }


        // using in final...
        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        if (!dialog.isShowing) {
            dialog.show()
        }
    }



    fun isMockLocationEnabled(): Boolean {
        var isMockLocation = false
        isMockLocation = try {
            //if marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val opsManager =
                    applicationContext.getSystemService(APP_OPS_SERVICE) as AppOpsManager
                opsManager.checkOp(
                    AppOpsManager.OPSTR_MOCK_LOCATION,
                    Process.myUid(),
                    BuildConfig.APPLICATION_ID
                ) == AppOpsManager.MODE_ALLOWED
            } else {
                // in marshmallow this will always return true
                Settings.Secure.getString(
                    applicationContext.contentResolver,
                    "mock_location"
                ) != "0"
            }
        } catch (e: Exception) {
            return isMockLocation
        }
        return isMockLocation
    }


    private fun mockLocationAppDetectDialog() {

        dialog1.setCancelable(false)
        dialog1.setContentView(R.layout.gps_indiaction_layout)
        dialog1.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog1.window?.setDimAmount(0.5f)
        dialog1.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog1.show()
    }
}
