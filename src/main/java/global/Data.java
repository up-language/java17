package global;

import org.bson.*;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.io.BasicOutputBuffer;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public static String ToJson(BsonDocument doc, boolean indent) {
        return doc.toJson(JsonWriterSettings.builder().indent(indent).build());
    }

    public static BsonDocument FromJson(String json) {
        return BsonDocument.parse(json);
    }

    public static BsonDocument NewArray() {
        return new BsonDocument();
    }

    public static BsonArray NewObject() {
        return new BsonArray(); //FromJson("{}");
    }

    /*
    public static void Dump(BsonValue val) {
        var doc = new BsonDocument();
        Dump(val, "");
    }

    public static void Dump(BsonValue val, String title) {
        var doc = new BsonDocument();
        doc.put("!", new BsonString(title + "[" + val.getClass().getName() + "]"));
        doc.put("?", val);
        System.out.println(ToJson(doc, true));
    }
    */

    public static void Print(BsonValue val) {
        Print(val, null);
    }
    public static void Print(BsonValue val, String title) {
        if (title != null) System.out.printf("%s: ", title);
        var doc = new BsonDocument();
        doc.put("?", val);
        String json = ToJson(doc, false);
        JSONObject obj = new JSONObject(json);
        Object x = obj.get("?");
        String result = "";
        if (x == null) result = "null";
        else if (x instanceof JSONArray) result = ((JSONArray)x).toString(2);
        else if (x instanceof JSONObject) result = ((JSONObject)x).toString(2);
        else result = x.toString();
        System.out.println(result);
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
