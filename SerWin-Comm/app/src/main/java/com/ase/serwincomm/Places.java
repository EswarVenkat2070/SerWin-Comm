package com.ase.serwincomm;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;



public class Places implements Parcelable {

    private String id;
    private String icon;
    private String name;
    private float distance;
    private String address;
    private double rating;
    //private String vicinity;
    private Double latitude;
    private Double longitude;
    private Boolean open;

    public Places() {
    }

    protected Places(Parcel in) {
        id = in.readString();
        icon = in.readString();
        name = in.readString();
        distance = in.readFloat();
        address = in.readString();
        rating = in.readDouble();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        byte tmpOpen = in.readByte();
        open = tmpOpen == 0 ? null : tmpOpen == 1;
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    static Places jsonToPontoReferencia(JSONObject pontoReferencia) {
        try {
            Places result = new Places();
            JSONObject geometry = (JSONObject) pontoReferencia.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(pontoReferencia.getString("icon"));
            result.setName(pontoReferencia.getString("name"));

            JSONObject openingHoursJSON =
                    pontoReferencia.getJSONObject("opening_hours");
            // Determine whether this location
            // is currently open.
            boolean openNow =
                    openingHoursJSON.getBoolean("open_now");
            // Record this information somewhere, like this.
            result.setOpen(openNow);

//            result.setVicinity(pontoReferencia.getString("vicinity"));
            result.setAddress(pontoReferencia.getString("formatted_address"));
            result.setRating(pontoReferencia.getDouble("rating"));
//            result.setId(pontoReferencia.getString("id"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Places.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Places{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude +
                ", longitude=" + longitude + " , rating " + rating + '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeFloat(distance);
        dest.writeString(address);
        dest.writeDouble(rating);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        dest.writeByte((byte) (open == null ? 0 : open ? 1 : 2));
    }
}
