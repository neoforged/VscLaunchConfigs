package cz.nightenom.vsclaunch.writer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils
{
    public static String getStringOrKeep(final JsonObject object, final String key)
    {
        return object == null ? null : getStringOrSetDefault(object, key, null);
    }

    public static String getStringOrSetDefault(final JsonObject object, final String key, final String defaultValue)
    {
        final JsonElement element = object.get(key);

        if (element == null)
        {
            if (defaultValue != null)
            {
                object.addProperty(key, defaultValue);
            }
            return defaultValue;
        }

        if (!element.isJsonPrimitive())
        {
            if (defaultValue == null)
            {
                return null;
            }
            throw new JsonParseException(key + " is not a string");
        }

        final JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isString())
        {
            if (defaultValue == null)
            {
                return null;
            }
            throw new JsonParseException(key + " is not a string");
        }

        return primitive.getAsString();
    }

    public static JsonArray getArrayOrKeep(final JsonObject object, final String key)
    {
        return object == null ? null : getArrayOrSetDefault(object, key, null);
    }

    public static JsonArray getArrayOrSetDefault(final JsonObject object, final String key, final JsonArray defaultValue)
    {
        final JsonElement element = object.get(key);

        if (element == null)
        {
            if (defaultValue != null)
            {
                object.add(key, defaultValue);
            }
            return defaultValue;
        }

        if (!element.isJsonArray())
        {
            if (defaultValue == null)
            {
                return null;
            }
            throw new JsonParseException(key + " is not an json array");
        }

        return element.getAsJsonArray();
    }

    public static JsonObject getObjectOrKeep(final JsonObject object, final String key)
    {
        return object == null ? null : getObjectOrSetDefault(object, key, null);
    }

    public static JsonObject getObjectOrSetDefault(final JsonObject object, final String key, final JsonObject defaultValue)
    {
        final JsonElement element = object.get(key);

        if (element == null)
        {
            if (defaultValue != null)
            {
                object.add(key, defaultValue);
            }
            return defaultValue;
        }

        if (!element.isJsonObject())
        {
            if (defaultValue == null)
            {
                return null;
            }
            throw new JsonParseException(key + " is not an json array");
        }

        return element.getAsJsonObject();
    }

    public static List<JsonObject> getObjectsFromArray(final JsonArray array)
    {
        final List<JsonObject> result = new ArrayList<>();
        for (final JsonElement element : array)
        {
            if (element.isJsonObject())
            {
                result.add(element.getAsJsonObject());
            }
        }
        return result;
    }
}
