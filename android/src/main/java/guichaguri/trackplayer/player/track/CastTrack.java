package guichaguri.trackplayer.player.track;

import android.util.Log;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import guichaguri.trackplayer.logic.MediaManager;
import guichaguri.trackplayer.logic.Utils;
import guichaguri.trackplayer.logic.track.Track;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Guilherme Chaguri
 */
public class CastTrack extends Track {

    public final boolean sendUrl;
    public final String contentType;
    public final JSONObject customData;

    public CastTrack(MediaManager manager, ReadableMap data) {
        super(manager, data);

        sendUrl = Utils.getBoolean(data, "sendUrl", true);
        contentType = Utils.getString(data, "contentType", "audio/mpeg");

        ReadableMap custom = Utils.getMap(data, "customData");
        JSONObject obj = null;

        if(custom != null) {
            try {
                obj = transferToObject(custom);
            } catch(JSONException e) {
                Log.w("TrackPlayer", "Couldn't transfer JSON to cast device", e);
            }
        }

        customData = obj;
    }

    private JSONObject transferToObject(ReadableMap map) throws JSONException {
        JSONObject obj = new JSONObject();
        ReadableMapKeySetIterator i = map.keySetIterator();

        while(i.hasNextKey()) {
            String key = i.nextKey();
            ReadableType type = map.getType(key);

            if(type == ReadableType.String) {
                obj.put(key, map.getString(key));
            } else if(type == ReadableType.Number) {
                obj.put(key, map.getDouble(key));
            } else if(type == ReadableType.Boolean) {
                obj.put(key, map.getBoolean(key));
            } else if(type == ReadableType.Null) {
                obj.put(key, null);
            } else if(type == ReadableType.Array) {
                obj.put(key, transferToArray(map.getArray(key)));
            } else if(type == ReadableType.Map) {
                obj.put(key, transferToObject(map.getMap(key)));
            }
        }
        return obj;
    }

    private JSONArray transferToArray(ReadableArray arr) throws JSONException {
        JSONArray array = new JSONArray();

        for(int i = 0; i < arr.size(); i++) {
            ReadableType type = arr.getType(i);

            if(type == ReadableType.String) {
                array.put(arr.getString(i));
            } else if(type == ReadableType.Number) {
                array.put(arr.getDouble(i));
            } else if(type == ReadableType.Boolean) {
                array.put(arr.getBoolean(i));
            } else if(type == ReadableType.Null) {
                array.put(null);
            } else if(type == ReadableType.Array) {
                array.put(transferToArray(arr.getArray(i)));
            } else if(type == ReadableType.Map) {
                array.put(transferToObject(arr.getMap(i)));
            }
        }
        return array;
    }

}
