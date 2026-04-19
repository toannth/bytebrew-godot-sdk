package com.bytebrew.bytebrewlibrary;

import android.os.Bundle;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ByteBundleToJson {
	public static String getJson(Bundle bundle, int indents) {
		if (bundle == null)
			return null;
		JSONObject jsonObject = new JSONObject();
		for (String key : bundle.keySet()) {
			Object obj = bundle.get(key);
			try {
				jsonObject.put(key, wrap(bundle.get(key)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (indents > 0)
			try {
				return jsonObject.toString(indents);
			} catch (JSONException e) {
				e.printStackTrace();
				return jsonObject.toString();
			}
		return jsonObject.toString();
	}

	private static Object wrap(Object o) {
		if (o == null)
			return JSONObject.NULL;
		if (o instanceof JSONArray || o instanceof JSONObject)
			return o;
		if (o.equals(JSONObject.NULL))
			return o;
		try {
			if (o instanceof Collection)
				return new JSONArray((Collection) o);
			if (o.getClass().isArray())
				return toJSONArray(o);
			if (o instanceof Map)
				return new JSONObject((Map) o);
			if (o instanceof Boolean || o instanceof Byte || o instanceof Character || o instanceof Double
					|| o instanceof Float || o instanceof Integer || o instanceof Long || o instanceof Short
					|| o instanceof String)
				return o;
			if (o.getClass().getPackage().getName().startsWith("java."))
				return o.toString();
		} catch (Exception exception) {
		}
		return null;
	}

	private static JSONArray toJSONArray(Object array) throws JSONException {
		JSONArray result = new JSONArray();
		if (!array.getClass().isArray())
			throw new JSONException("Not a primitive array: " + array.getClass());
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++)
			result.put(wrap(Array.get(array, i)));
		return result;
	}
}
