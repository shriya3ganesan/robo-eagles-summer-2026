package org.json;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import java.util.Iterator;
import java.util.Map;

public class JSONObject {
    @RecentlyNonNull
    public static final Object NULL = null;

    public JSONObject() {
        throw new RuntimeException("Stub!");
    }

    public JSONObject(@RecentlyNonNull Map copyFrom) {
        throw new RuntimeException("Stub!");
    }

//    public JSONObject(@RecentlyNonNull JSONTokener readFrom) throws JSONException {
//        throw new RuntimeException("Stub!");
//    }

    public JSONObject(@RecentlyNonNull String json) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject(@RecentlyNonNull JSONObject copyFrom, @RecentlyNonNull String[] names) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public int length() {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject put(@RecentlyNonNull String name, boolean value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject put(@RecentlyNonNull String name, double value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject put(@RecentlyNonNull String name, int value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject put(@RecentlyNonNull String name, long value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject put(@RecentlyNonNull String name, @RecentlyNullable Object value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject putOpt(@RecentlyNullable String name, @RecentlyNullable Object value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject accumulate(@RecentlyNonNull String name, @RecentlyNullable Object value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNullable
    public Object remove(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean isNull(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean has(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public Object get(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNullable
    public Object opt(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean getBoolean(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public boolean optBoolean(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean optBoolean(@RecentlyNullable String name, boolean fallback) {
        throw new RuntimeException("Stub!");
    }

    public double getDouble(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public double optDouble(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    public double optDouble(@RecentlyNullable String name, double fallback) {
        throw new RuntimeException("Stub!");
    }

    public int getInt(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public int optInt(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    public int optInt(@RecentlyNullable String name, int fallback) {
        throw new RuntimeException("Stub!");
    }

    public long getLong(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public long optLong(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    public long optLong(@RecentlyNullable String name, long fallback) {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public String getString(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public String optString(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public String optString(@RecentlyNullable String name, @RecentlyNonNull String fallback) {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONArray getJSONArray(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNullable
    public JSONArray optJSONArray(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public JSONObject getJSONObject(@RecentlyNonNull String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNullable
    public JSONObject optJSONObject(@RecentlyNullable String name) {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNullable
    public JSONArray toJSONArray(@RecentlyNullable JSONArray names) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public Iterator<String> keys() {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNullable
    public JSONArray names() {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public String toString(int indentSpaces) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public static String numberToString(@RecentlyNonNull Number number) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNonNull
    public static String quote(@RecentlyNullable String data) {
        throw new RuntimeException("Stub!");
    }

    @RecentlyNullable
    public static Object wrap(@RecentlyNullable Object o) {
        throw new RuntimeException("Stub!");
    }
}
