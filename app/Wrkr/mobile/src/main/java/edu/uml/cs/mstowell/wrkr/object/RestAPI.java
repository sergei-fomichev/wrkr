package edu.uml.cs.mstowell.wrkr.object;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import edu.uml.cs.mstowell.wrkrlib.common.User;

/**
 * API to comm with the wrkr web server
 */
public class RestAPI {

    // URL we connect to
    private static final String baseURL = "http://weblab.cs.uml.edu/~sfomiche/wrkr/api/api.php";

    // standard char set
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    // result of a makeRequest call
    private static class Result {
        public int resultCode; public String response;
        public Result(int r, String s) { resultCode = r; response = s; }
    }

    // private constructor since all methods are static and this class should not be initialized
    private RestAPI() {}

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
    public static User getUser(String email) {

        User u;
        Result r = makeGetRequest("exist&email=" + email);

        if (r.resultCode == -1) {
            System.err.println("RestAPI postUser: response -1 (" + r.response + ")");
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
            u.email = email;
            u.karma = json.getInt("karma");
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
    public static User getExercises(int uid) {

        User u;
        Result r = makeGetRequest("exercises&id=" + uid);

        if (r.resultCode == -1) {
            System.err.println("RestAPI postUser: response -1 (" + r.response + ")");
            return null;
        } else if (r.resultCode == 401) {
            System.err.println("RestAPI getExercises: response 401 (user does not exist)");
            return null;
        }

        try {
            JSONObject json = new JSONObject(r.response);
            u = new User();
            u.id = uid;
            u.exercises = json.getInt("exercises");
            u.timestamps = json.getJSONArray("timestamp");
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
    public static User postUser(String email) {

        User u;
        try {
            String parameter = String.format("email=%s&name=%s",
                    URLEncoder.encode(email, UTF_8.name()),
                    URLEncoder.encode("Test User", UTF_8.name()));

            Result r = makePostRequest(parameter.getBytes(UTF_8));

            if (r.resultCode == -1) {
                System.err.println("RestAPI postUser: response -1 (" + r.response + ")");
                return null;
            } else if (r.resultCode == 401) {
                System.err.println("RestAPI postUser: response 401 (user already exists)");
                return null;
            }

            JSONObject json = new JSONObject(r.response);
            if (!json.getString("status").equalsIgnoreCase("ok")) {
                return null;
            }

            u = new User();
            u.id = json.getInt("id");
            u.email = email;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
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
    public static User postExercise(int uid, long timestamp) {

        User u;

        try {
            String parameter = String.format("id=%s&timestamp=%s",
                    URLEncoder.encode(""+uid, StandardCharsets.UTF_8.name()),
                    URLEncoder.encode(""+timestamp, StandardCharsets.UTF_8.name()));

            Result r = makePostRequest(parameter.getBytes(StandardCharsets.UTF_8));

            if (r.resultCode == -1) {
                System.err.println("RestAPI postUser: response -1 (" + r.response + ")");
                return null;
            } else if (r.resultCode == 401) {
                System.err.println("RestAPI postExercise: response 401 (user does not exist)");
                return null;
            }

            JSONObject json = new JSONObject(r.response);
            if (!json.getString("status").equalsIgnoreCase("ok")) {
                return null;
            }

            u = new User();
            u.id = uid;
            u.exercises = json.getInt("exercises");

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return u;
    }

    private static Result makePostRequest(byte[] mPostData) {

        int responseCode;

        try {
            URL url = new URL(baseURL);
            System.out.println("Connect to: " + url + " with " + new String(mPostData));

            URLConnection urlConnection =  url.openConnection();
            urlConnection.setDoOutput(true); // automatically sets request method to POST

            urlConnection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());

            try (OutputStream out = urlConnection.getOutputStream()) {
                out.write(mPostData);
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response from server: " + responseCode);

            try (BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream())) {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = in.read();
                while (i != -1) {
                    bo.write(i);
                    i = in.read();
                }
                return new Result(responseCode, bo.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return new Result(-1, "Error: IOException reading bytes");
            }

        } catch (ConnectException ce) {
            return new Result(-1, "Connection failed: ENETUNREACH (network not reachable)");
        } catch (FileNotFoundException fnfe) {
            return new Result(-1, "File not found exception (could be user does not exist)");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(-1, "General error");
        }
    }

    private static Result makeGetRequest(String parameters) {

        int responseCode;

        try {
            URL url = new URL(baseURL + "?" + parameters);
            System.out.println("Connect to: " + url);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");

            responseCode = urlConnection.getResponseCode();
            System.out.println("Response from server: " + responseCode);
            if (responseCode == 401)
                return new Result(responseCode, "401 error from server");

            try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = in.read();
                while (i != -1) {
                    bo.write(i);
                    i = in.read();
                }
                return new Result(responseCode, bo.toString());
            } catch (IOException e) {
                return new Result(-1, "Error: IOException reading bytes");
            }

        } catch (ConnectException ce) {
            return new Result(-1, "Connection failed: ENETUNREACH (network not reachable)");
        } catch (FileNotFoundException fnfe) {
            return new Result(-1, "File not found exception (could be user does not exist)");
        } catch (Exception e) {
            return new Result(-1, "General error");
        }
    }
}
