package system;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.json.*;

public class JsonUtil {

    static public String stringify(JsonStructure jsonObjectOrArray) {
        StringWriter stWriter = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(stWriter);
        jsonWriter.write(jsonObjectOrArray);
        jsonWriter.close();
        String jsonString = stWriter.toString();
        return jsonString;
    }

    static public JsonObject parse(String jsonObjectString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonObjectString));
        JsonObject jsonObject = jsonReader.readObject();
        return jsonObject;
    }

    static public JsonArray parseArray(String jsonArrayString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonArrayString));
        JsonArray jsonArray = jsonReader.readArray();
        return jsonArray;
    }
}