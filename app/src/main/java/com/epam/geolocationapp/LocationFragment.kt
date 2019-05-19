package com.epam.geolocationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment which responsible for input of latitude and longitude
 * and starts [GeofenceService] when user setting a point.
 *
 * @see GeofenceService
 *
 * @author Vlad Korotkevich
 */

class LocationFragment : Fragment() {

    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.location_fragment, container, false)

        view.findViewById<EditText>(R.id.latitudeEditText).setText("53.863591") //TODO delete setText
        view.findViewById<EditText>(R.id.longitudeEditText).setText("27.476595")

        val confirmFab = view.findViewById<FloatingActionButton>(R.id.confirmButton)
        confirmFab.setOnClickListener {
            Log.d(TAG, "CONFIRMING")
            initAcceptClickListener(view)
        }

        val cancelFab = view.findViewById<FloatingActionButton>(R.id.cancelButton)
        cancelFab.setOnClickListener {
            initCancelClickListener()
        }

        return view
    }

    private fun initAcceptClickListener(view: View) {
        val latitudeEdit = view.findViewById<EditText>(R.id.latitudeEditText)
        val longitudeEdit = view.findViewById<EditText>(R.id.longitudeEditText)

        val intent = Intent(context, GeofenceService::class.java).apply {
            putExtra(LATITUDE_EXTRA, latitudeEdit.text.toString().toDouble())
            putExtra(LONGITUDE_EXTRA, longitudeEdit.text.toString().toDouble())
            action = ADD_ACTION
        }
        context?.startService(intent)
    }

    private fun initCancelClickListener() {
        val intent = Intent(context, GeofenceService::class.java).apply {
            action = REMOVE_ACTION
        }
        context?.startService(intent)
    }

    companion object {
        private const val TAG = "LOCATION FRAGMENT"
        private const val LATITUDE_EXTRA = "LATITUDE_EXTRA"
        private const val LONGITUDE_EXTRA = "LONGITUDE_EXTRA"
        private const val ADD_ACTION = "ADD_ACTION"
        private const val REMOVE_ACTION = "REMOVE_ACTION"

        fun newInstance() = LocationFragment()
    }
}