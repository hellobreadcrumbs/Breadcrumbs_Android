package com.breadcrumbsapp.interfaces

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

interface LatLngInterpolator {
    fun interpolate( fraction: Float,  a: LatLng,  b: LatLng): LatLng

    public class Linear : LatLngInterpolator
    {
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            val lat =(b.latitude - a.latitude) * fraction + a.latitude;
            val lng =(b.longitude - a.longitude) * fraction + a.longitude;
            return  LatLng (lat, lng)
        }
    }

    public class LinearFixed : LatLngInterpolator
    {
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            val lat =(b.latitude - a.latitude) * fraction + a.latitude;
            var lngDelta = b . longitude -a.longitude;
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360;
            }
            val lng = lngDelta * fraction +a.longitude;
            return  LatLng (lat, lng);
        }

    }

    public class Spherical : LatLngInterpolator
    {

        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            // http://en.wikipedia.org/wiki/Slerp
            val fromLat = Math.toRadians (a.latitude);
            val fromLng = Math.toRadians (a.longitude);
            val toLat = Math.toRadians (b.latitude);
            val toLng = Math.toRadians (b.longitude);
            val cosFromLat = cos (fromLat);
            val cosToLat = cos (toLat);

            // Computes Spherical interpolation coefficients.
            val angle = computeAngleBetween (fromLat, fromLng, toLat, toLng);
            val sinAngle = sin (angle);
            if (sinAngle < 1E-6) {
                return a;
            }
            val a = sin ((1 - fraction) * angle) / sinAngle;
            val b = sin (fraction * angle) / sinAngle;

            // Converts from polar to vector and interpolate.
            val x = a * cosFromLat * cos (fromLng) + b * cosToLat * cos(toLng);
            val y = a * cosFromLat * sin (fromLng) + b * cosToLat * sin(toLng);
            val z = a * sin (fromLat) + b * sin(toLat);

            // Converts interpolated vector back to polar.
            val lat = atan2 (z, sqrt(x * x+y * y));
            val lng = atan2 (y, x);
            return  LatLng (Math.toDegrees(lat), Math.toDegrees(lng));
        }
        /* From github.com/googlemaps/android-maps-utils */

        fun computeAngleBetween(
             fromLat : Double,
             fromLng : Double,
             toLat : Double,
             toLng : Double
        ) : Double{
            // Haversine's formula
            val dLat = fromLat -toLat;
            val dLng = fromLng -toLng;
            return 2 * asin(
                sqrt(
                    Math.pow(sin(dLat / 2), 2.0) +
                            cos(fromLat) * cos(toLat) * Math.pow(sin(dLng / 2), 2.0)
                )
            );
        }
    }
}