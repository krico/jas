package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * A very simple response for OK/NOK + reason
 * Created by krico on 09/11/14.
 */
public class JsonResponse extends JsonObject {
    private boolean ok;
    private boolean nok;
    private String nokText;

    public JsonResponse() {
    }

    public JsonResponse(boolean ok) {
        this.ok = ok;
    }

    public JsonResponse(String nokText) {
        this.ok = false;
        this.nok = true;
        this.nokText = nokText;
    }

    public static JsonResponse parse(String data) {
        return JSON.fromJson(data, JsonResponse.class);
    }

    public static JsonResponse parse(Reader reader) {
        return JSON.fromJson(reader, JsonResponse.class);
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean isNok() {
        return nok;
    }

    public void setNok(boolean nok) {
        this.nok = nok;
    }

    public String getNokText() {
        return nokText;
    }

    public void setNokText(String nokText) {
        this.nokText = nokText;
    }
}
