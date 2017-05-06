package mav.prayertimes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    //region declare UI objects
    TextView
            prayerTimesDate,
            fajrAdhan,
            fajrIqaamah,
            sunriseAdhan,
            dhuhrAdhan,
            dhuhrIqaamah,
            asrAdhan,
            asrIqaamah,
            maghribAdhan,
            maghribIqaamah,
            ishaAdhan,
            ishaIqaamah;
    //endregion

    private static final String PRAYER_TIMES_REST_ENDPOINT = "https://api.myjson.com/bins/d0ywh";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region initialize UI objects
        prayerTimesDate = (TextView)findViewById(R.id.prayerTimesDate);
        fajrAdhan = (TextView)findViewById(R.id.fajrAdhan);
        fajrIqaamah = (TextView)findViewById(R.id.fajrIqaamah);
        sunriseAdhan = (TextView)findViewById(R.id.sunriseAdhan);
        dhuhrAdhan = (TextView)findViewById(R.id.dhuhrAdhan);
        dhuhrIqaamah = (TextView)findViewById(R.id.dhuhrIqaamah);
        asrAdhan = (TextView)findViewById(R.id.asrAdhan);
        asrIqaamah = (TextView)findViewById(R.id.asrIqaamah);
        maghribAdhan = (TextView)findViewById(R.id.maghribAdhan);
        maghribIqaamah = (TextView)findViewById(R.id.maghribIqaamah);
        ishaAdhan = (TextView)findViewById(R.id.ishaAdhan);
        ishaIqaamah = (TextView)findViewById(R.id.ishaIqaamah);
        //endregion

        try {
            (new Parser()).execute(new String[]{PRAYER_TIMES_REST_ENDPOINT});
        } catch(Exception ex) {
            prayerTimesDate.setText(ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }

    private class Parser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //JSONObject jsonResponse = new JSONObject();
            StringBuffer response = new StringBuffer();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("FAILED: HTTP error code: " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output;
                while((output = br.readLine()) != null) {
                    response.append(output);
                }
                conn.disconnect();
            }
            catch (MalformedURLException e) {
                // TODO: handle exception
                e.printStackTrace();
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                JSONObject jsnObject = new JSONObject(response);
                JSONArray jsonArray = jsnObject.getJSONArray("prayerTimes");

                DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                String curDate = sdf.format(new Date());

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject prayerTimes = jsonArray.getJSONObject(i);
                    String date = prayerTimes.getString("date");

                    if (date.equals(curDate)) {
                        // format date and set to UI element
                        DateFormat fullDf = DateFormat.getDateInstance(DateFormat.FULL);
                        String[] dateParts = date.split("-");
                        Integer month = Integer.parseInt(dateParts[0]);
                        Integer day = Integer.parseInt(dateParts[1]);
                        Integer year = Integer.parseInt(dateParts[2]);
                        prayerTimesDate.setText("Prayer times for:" + fullDf.format(new Date(year - 1900, month - 1, day)));

                        // set prayer times
                        fajrAdhan.setText(prayerTimes.getJSONObject("fajr").getString("adhan") + "-");
                        fajrIqaamah.setText(prayerTimes.getJSONObject("fajr").getString("iqaamah"));

                        // set sunrise times
                        sunriseAdhan.setText(prayerTimes.getJSONObject("sunrise").getString("adhan"));

                        // set dhuhr times
                        dhuhrAdhan.setText(prayerTimes.getJSONObject("dhuhr").getString("adhan") + "-");
                        dhuhrIqaamah.setText(prayerTimes.getJSONObject("dhuhr").getString("iqaamah"));

                        // set asr times
                        asrAdhan.setText(prayerTimes.getJSONObject("asr").getString("adhan") + "-");
                        asrIqaamah.setText(prayerTimes.getJSONObject("asr").getString("iqaamah"));


                        // set maghrib times
                        maghribAdhan.setText(prayerTimes.getJSONObject("maghrib").getString("adhan") + "-");
                        maghribIqaamah.setText(prayerTimes.getJSONObject("maghrib").getString("iqaamah"));

                        // set isha times
                        ishaAdhan.setText(prayerTimes.getJSONObject("isha").getString("adhan") + "-");
                        ishaIqaamah.setText(prayerTimes.getJSONObject("isha").getString("iqaamah"));
                        break;
                    }
                }

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        private String getMonth(int monthNum) {
            String[] months = {
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December" };

            return months[monthNum - 1];
        }
    }
}
