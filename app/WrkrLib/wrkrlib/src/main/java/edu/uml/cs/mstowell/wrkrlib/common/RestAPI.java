package edu.uml.cs.mstowell.wrkrlib.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * API to comm with the wrkr web server
 */
public class RestAPI {

    // single instance of the RestAPI class
    private static RestAPI instance = null;

    // URL we connect to
    private final String baseURL = "http://weblab.cs.uml.edu/~sfomiche/wrkr/api/api.php";

    // request type enum
    private enum RequestType { GET, POST }

    // result of a makeRequest call
    private class Result {
        public int resultCode; public String response;
        public Result(int r, String s) { resultCode = r; response = s; }
    }

    // private constructor
    private RestAPI() {}

    // initialize the singleton API instance
    public static RestAPI getInstance() {

        if (instance == null) {
            instance = new RestAPI();
        }

        return instance;
    }

    /* == Check if user exist ==
    Request
    {
        "exist":
        "email": Email as string
    }
    Respond [header - 200]
    {
        "id": userID
        "name": name
    }
    OR Respond [header - 401] if not exists
    */
    public User getUser(String email) {

        User u;
        Result r = makeRequest("exist&email=" + email, RequestType.GET);

        if (r.resultCode == -1) {
            System.err.println("RestAPI getUser: response -1 (makeRequst malformed)");
            return null;
        } else if (r.resultCode == 401) {
            System.err.println("RestAPI getUser: response 401 (user does not exist)");
            return null;
        }

        try {
            JSONObject json = new JSONObject(r.response);
            u = new User();
            u.id = json.getInt("id");
            u.name = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return u;
    }

    /* == Check a user's outstanding exercise count ==
    Request
    {
        "exercises":
        "id": userID
    }
    Respond [header - 200]
    {
        "exercises": number of exercises
        "timestamp": last issued UNIX timestamp (to determine time remaining of an exercise)
    }
    OR Respond [header - 401] if user not exists in database
    */
    public User getExercises(int uid) {

        User u;
        Result r = makeRequest("exercises&id=" + uid, RequestType.GET);

        if (r.resultCode == -1) {
            System.err.println("RestAPI getExercises: response -1 (makeRequst malformed)");
            return null;
        } else if (r.resultCode == 401) {
            System.err.println("RestAPI getExercises: response 401 (user does not exist)");
            return null;
        }

        try {
            JSONObject json = new JSONObject(r.response);
            u = new User();
            u.exercises = json.getInt("exercises");
            u.timestamp = json.getLong("timestamp");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return u;
    }

    /* == Insert a new user as a participant ==
    Request
    {
        "email": email //string
    }
    Respond [header - 200]
    {
        "status": "ok"
        "id": userID
    }
    OR Respond [header - 401] if already exists
    */
    public User postUser(String email) {

        User u;
        Result r = makeRequest("email=" + email, RequestType.POST);

        if (r.resultCode == -1) {
            System.err.println("RestAPI getExercises: response -1 (makeRequst malformed)");
            return null;
        } else if (r.resultCode == 401) {
            System.err.println("RestAPI getExercises: response 401 (user already exists)");
            return null;
        }

        try {
            JSONObject json = new JSONObject(r.response);
            if (!json.getString("status").equalsIgnoreCase("ok")) {
                return null;
            }
            u = new User();
            u.id = json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return u;
    }

    /* == User has a new exercise that is due ==
    Request
    {
        "id": userID
        "timestamp": UNIX timestamp of when the user got notified they have an exercise to do
    }
    Respond [header - 200]
    {
        "status": "ok"
        "exercises": new exercise count
    }
    OR Respond [header - 401] if user does not exist
    */
    public User postExercise(int uid, long timestamp) {

        User u;
        Result r = makeRequest("id=" + uid + "&timestamp=" + timestamp, RequestType.POST);

        if (r.resultCode == -1) {
            System.err.println("RestAPI getExercises: response -1 (makeRequst malformed)");
            return null;
        } else if (r.resultCode == 401) {
            System.err.println("RestAPI getExercises: response 401 (user does not exist)");
            return null;
        }

        try {
            JSONObject json = new JSONObject(r.response);
            if (!json.getString("status").equalsIgnoreCase("ok")) {
                return null;
            }
            u = new User();
            u.exercises = json.getInt("exercises");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return u;
    }

    // make the actual API connection request
    private Result makeRequest(String parameters, RequestType reqType) {

        int responseCode = 0;

        try {
            URL url = new URL(baseURL + "?" + parameters);
            System.out.println("Connect to: " + url);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if (reqType != RequestType.GET) {
                urlConnection.setDoOutput(true);
            }

            urlConnection.setRequestMethod(rtts(reqType));
            urlConnection.setRequestProperty("Accept", "application/json");

            responseCode = urlConnection.getResponseCode();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            System.out.println("Response from server: " + responseCode);

            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = in.read();
                while (i != -1) {
                    bo.write(i);
                    i = in.read();
                }

                return new Result(responseCode, bo.toString());
            } catch (IOException e) {
                return new Result(-1, "Error: IOException reading bytes");
            } finally {
                in.close();
            }

        } catch (ConnectException ce) {
            System.err.println("Connection failed: ENETUNREACH (network not reachable)");
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(-1, "Error: status " + responseCode);
    }

    // _R_equest _T_ype _T_o _S_tring
    private String rtts(RequestType rt) {
        if (rt == RequestType.GET)
            return "GET";
        else return "POST";
    }
}
