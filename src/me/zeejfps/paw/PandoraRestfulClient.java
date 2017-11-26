package me.zeejfps.paw;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.zeejfps.paw.exceptions.AuthenticationException;
import me.zeejfps.paw.models.APIError;
import me.zeejfps.paw.models.Track;
import me.zeejfps.paw.models.Account;
import me.zeejfps.paw.models.Station;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PandoraRestfulClient {

    private static final String BASE_URL = "https://www.pandora.com";
    private static final String API_URL = BASE_URL + "/api";

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private Gson gson;
    private OkHttpClient httpClient;

    private Map<String, Cookie> cookies;
    private Headers headers;

    private Account account;
    private Station[] stations;

    public PandoraRestfulClient() {
        gson = new Gson();
        httpClient = new OkHttpClient.Builder()
            .cookieJar(new MyCookieJar())
            .build();
        cookies = new HashMap<>();
        headers = setupDefaultHeaders();
        account = new Account("");
        stations = new Station[0];
    }

    public void login(String username, String password) throws IOException, AuthenticationException {
        syncCookies();
        setHeader("X-CsrfToken", cookies.get("csrftoken").value());

        account = authenticate(username, password);
        setHeader("X-AuthToken", account.getAuthToken());

        stations = new Station[0];
    }

    public void syncStations() throws IOException {
        stations = fetchStations();
    }

    public Track[] fetchPlaylistFragment(Station station) throws IOException {
        String json = String.format("{ \"isStationStart\" : true, \"stationId\" : \"%s\"}", station.getId());
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(API_URL + "/v1/playlist/getFragment")
                .headers(headers)
                .post(requestBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            APIError error = gson.fromJson(response.body().charStream(), APIError.class);
            throw new IOException(error.getMessage());
        }
        JsonObject obj = gson.fromJson(response.body().charStream(), JsonObject.class);
        return gson.fromJson(obj.getAsJsonArray("tracks").toString(), Track[].class);
    }

    private Station[] fetchStations() throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/v1/station/getStations")
                .headers(headers)
                .post(RequestBody.create(JSON, "{ \"pageSize\" : 250 }"))
                .build();
        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            APIError error = gson.fromJson(response.body().charStream(), APIError.class);
            throw new IOException(error.getMessage());
        }
        JsonObject obj = gson.fromJson(response.body().charStream(), JsonObject.class);
        return gson.fromJson(obj.getAsJsonArray("stations").toString(), Station[].class);
    }

    private Account authenticate(final String username, final String password) throws IOException, AuthenticationException {
        String json = String.format("{ \"username\" : \"%s\", \"password\" : \"%s\" }", username, password);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(API_URL + "/v1/auth/login")
                .headers(headers)
                .post(body)
                .build();
        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            APIError error = gson.fromJson(response.body().charStream(), APIError.class);
            if (error.getErrorString().equals(APIError.AUTH_INVALID_USERNAME_PASSWORD))
                throw new AuthenticationException(error.getMessage());
            else
                throw new IOException(error.getMessage());
        }
        return gson.fromJson(response.body().charStream(), Account.class);
    }

    private void syncCookies() throws IOException {
        Request request = new Request.Builder()
                .head()
                .url(BASE_URL)
                .build();
        httpClient.newCall(request).execute();
    }

    private Headers setupDefaultHeaders() {
        return new Headers.Builder()
                .add("Content-Type", "application/json")
                .add("Accept", "application/json")
                .add("X-CsrfToken", "")
                .add("X-AuthToken", "")
                .build();
    }

    private void setHeader(String header, String value) {
        headers = headers.newBuilder()
                .set(header, value)
                .build();
    }

    public Account getAccount() {
        return account;
    }

    public Station[] getStations() {
        return stations;
    }

    private class MyCookieJar implements CookieJar {

        @Override
        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            for (Cookie cookie : list) {
                cookies.put(cookie.name(), cookie);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
            return new ArrayList<>(cookies.values());
        }

    }

}
