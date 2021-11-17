
        package com.ase.serwincomm;
        import android.util.Log;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.URL;
        import java.net.URLConnection;
      import java.util.ArrayList;
        import java.util.List;
        import java.util.logging.Level;
        import java.util.logging.Logger;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.location.Location;


public class PlacesService {
    private String API_KEY;

    public PlacesService(String apikey) {
        this.API_KEY = apikey;
    }

    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }

    public List<Places> findPlaces(double latitude, double longitude, String placeSpacification, String lang)
    {
        Location a = new Location("pointA");
        a.setLatitude(latitude);
        a.setLongitude(longitude);

        String urlString = makeUrl(latitude, longitude, placeSpacification.toLowerCase(), lang);

        Log.d("category", placeSpacification);

        try {
            String json = getJSON(urlString);

            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");


            ArrayList<Places> arrayList = new ArrayList<Places>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Places places = Places.jsonToPontoReferencia((JSONObject) array.get(i));
                    Location b = new Location("pointB");
                    b.setLatitude(places.getLatitude());
                    b.setLongitude(places.getLongitude());

                    Float dist = a.distanceTo(b);
                    places.setDistance(dist/1000);
                    Log.d("dist", places.getOpen().toString());
                    Log.v("Places Services ", ""+ places);

                    arrayList.add(places);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String makeUrl(double latitude, double longitude,String place, String lang) {
        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        Log.d("category", place);

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&rankby=prominence");
            urlString.append("&radius=25000");
            urlString.append("&language="+lang);
            //   urlString.append("&types="+place);

            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&rankby=prominence");
            urlString.append("&radius=25000");
            urlString.append("&query="+place);
            urlString.append("&language="+lang);
            urlString.append("&sensor=false&key=" + API_KEY);

        }
        Log.d("link for gps ", urlString.toString());
        return urlString.toString();
    }

    protected String getJSON(String url) {

        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl)
    {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }

            bufferedReader.close();
        }

        catch (Exception e)
        {

            e.printStackTrace();

        }

        return content.toString();
    }

}