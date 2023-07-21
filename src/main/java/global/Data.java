package global;

import com.oracle.truffle.regex.tregex.util.json.JsonNull;
import org.bson.*;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.conversions.Bson;
import org.bson.io.BasicOutputBuffer;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Data {
    public static byte[] EncodeToBytes(BsonDocument doc) {
        BasicOutputBuffer outputBuffer = new BasicOutputBuffer();
        BsonBinaryWriter writer1 = new BsonBinaryWriter(outputBuffer);
        byte[] bsonBytes2 = null;
        try {
            new BsonDocumentCodec().encode(writer1, doc, org.bson.codecs.EncoderContext.builder().build());
            writer1.flush();
            outputBuffer.flush();
            bsonBytes2 = outputBuffer.toByteArray();
            outputBuffer.close();
        } catch (Exception e) {
            throw new BsonSerializationException("Error converting BsonDocument to byte array: " + e.getMessage());
        }
        return bsonBytes2;
    }

    public static BsonDocument DecodeFromBytes(byte[] bytes) {
        RawBsonDocument rawDoc = new RawBsonDocument(bytes);
        // return rawDoc;
        return DecodeFromBytes_Helper(rawDoc).asDocument();
    }

    private static BsonValue DecodeFromBytes_Helper(BsonValue x) {
        if (x.isDocument()) {
            var result = new BsonDocument();
            var keys = x.asDocument().keySet();
            for (var key : keys) {
                result.put(key, DecodeFromBytes_Helper(x.asDocument().get(key)));
            }
            return result;
        } else if (x.isArray()) {
            var result = new BsonArray();
            for (int i = 0; i < x.asArray().size(); i++) {
                result.set(i, DecodeFromBytes_Helper(x.asArray().get(i)));
            }
            return result;
        }
        return x;
    }

    public static String ToJson(BsonValue x, boolean indent) {
        BsonDocument doc = null;
        if (x instanceof BsonDocument)
            doc = (BsonDocument) x;
        else {
            doc = new BsonDocument();
            doc.put("$wrap", x);
        }
        return doc.toJson(JsonWriterSettings.builder().indent(indent).build());
    }

    public static BsonValue FromJson(String json) {
        BsonDocument doc = BsonDocument.parse(json);
        if (doc.containsKey("$wrap")) {
            return doc.get("$wrap");
        }
        return doc;
    }

    public static BsonDocument NewArray() {
        return new BsonDocument();
    }

    public static BsonArray NewObject() {
        return new BsonArray(); //FromJson("{}");
    }

    public static void Print(Object val) {
        Print(val, null);
    }
    public static void Print(Object val, String title) {
        if (title != null) System.out.printf("%s: ", title);
        Object x = null;
        if (val instanceof BsonValue) {
            var doc = new BsonDocument();
            doc.put("?", (BsonValue)val);
            String json = ToJson(doc, false);
            JSONObject obj = new JSONObject(json);
            x = obj.get("?");
        } else {
            x = val;
        }
        String result = "";
        if (x == null) result = "null";
        else if (x instanceof JsonNull) result = "null";
        else if (x instanceof JSONArray) result = ((JSONArray)x).toString(2);
        else if (x instanceof JSONObject) result = ((JSONObject)x).toString(2);
        else result = x.toString();
        if (val != null)
            result = "<" + val.getClass().getSimpleName() + "> " + result;
        System.out.println(result);
    }

    public static BsonValue ToValue(Object x) {
        if (x == null) return new BsonNull();
        if (x instanceof Integer) return new BsonInt32((int)x);
        if (x instanceof Long) return new BsonInt64((long)x);
        if (x instanceof Double) return new BsonDouble((double)x);
        if (x instanceof String) return new BsonString((String)x);
        if (x instanceof Date) return new BsonDateTime(((Date)x).getTime());
        if (x instanceof byte[]) return new BsonBinary((byte[])x);
        throw new RuntimeException(x.getClass().getName());
        //return null;
    }

    public static Object FromValue(BsonValue x) {
        if (x == null) return null;
        if (x instanceof BsonNull) return null;
        if (x instanceof BsonInt32) return x.asInt32().intValue();
        if (x instanceof BsonDouble) return x.asDouble().doubleValue();
        if (x instanceof BsonInt64) return x.asInt64().longValue();
        if (x instanceof BsonString) return x.asString().getValue();
        if (x instanceof BsonDateTime) return new Date(x.asDateTime().getValue());
        if (x instanceof BsonBinary) return x.asBinary().getData();
        if (x instanceof BsonArray) {
            BsonArray array = x.asArray();
            JSONArray result = new JSONArray();
            for (int i=0; i<array.size(); i++) {
                result.put(FromValue(array.get(i)));
            }
            return result;
        }
        if (x instanceof BsonDocument) {
            BsonDocument doc = x.asDocument();
            JSONObject result = new JSONObject();
            Object[] keys = doc.keySet().toArray();
            for (int i=0; i<keys.length; i++) {
                result.put((String)keys[i], FromValue(doc.get((String)keys[i])));
            }
            return result;
        }
        return x;
    }

    public static void PutBsonToFileEnd(String filePath, BsonDocument doc) throws Exception {
        byte[] bytes = EncodeToBytes(doc);
        MiscUtil.PutBytesToFileEnd(filePath, bytes);
    }

    public static BsonDocument GetBsonFromFileEnd(String filePath) throws Exception {
        byte[] bytes = MiscUtil.GetBytesFromFileEnd(filePath);
        return DecodeFromBytes(bytes);
    }

}