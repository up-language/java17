package system;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class VMCommon {

    public static String toJson(Object x) {
        return BsonData.ToJson(BsonData.ToValue(x), true);
    }

    public static Object fromJson(String json) {
        return BsonData.FromValue(BsonData.FromJson(json));
    }

    public static void echo(Object x, String title) {
        if (title != null) System.out.printf("%s: ", title);
        String result = "";
        if (x == null) result = "null";
        else result = x.toString();
        if (x != null) {
            if (x instanceof Dynamic)
                result = "<Dynamic:" + Dynamic.strip(x).getClass().getName() + "> " + result;
            else
                result = "<" + x.getClass().getName() + "> " + result;
        }
        System.out.println(result);
    }

    public static void echo(Object x) {
        echo(x, null);
    }

    public static void echoJson(Object x, String title) {
        if (title != null) System.out.printf("%s: ", title);
        String result = toJson(x);
        if (x != null) {
            if (x instanceof Dynamic)
                result = "<Dynamic:" + Dynamic.strip(x).getClass().getName() + "> " + result;
            else
                result = "<" + x.getClass().getName() + "> " + result;
        }
        System.out.println(result);
    }

    public static void echoJson(Object x) {
        echoJson(x, null);
    }

    public static java.util.List<Object> newList(Object[] args) {
        java.util.List<Object> result = new java.util.ArrayList<Object>();
        for (int i = 0; i < args.length; i++) {
            result.add(args[i]);
        }
        return result;
    }

    public static java.util.Map<String, Object> newMap(Object[] args) {
        java.util.Map<String, Object> result = new java.util.LinkedHashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            result.put((String) args[i], args[i + 1]);
        }
        return result;
    }

    public static String readAsText(String path) throws Exception {
        if (path.startsWith(":/")) {
            return ResourceUtil.GetString(path.substring(2));
        } else if (path.startsWith("http:") || path.startsWith("https:")) {
            try (InputStream in = new URL(path).openStream()) {
                return IOUtils.toString(in);
            }
        } else {
            return FileUtils.readFileToString(new File(path));
        }
    }

    public static Object readAsJson(String path) throws Exception {
        return VMCommon.readAsJson(path);
    }

    public static void writeStringToFile(String path, String data) throws Exception {
        MiscUtil.WriteStringToFile(path, data);
    }

    public static String readStringFromFile(String path, String fallback) throws Exception {
        return MiscUtil.ReadStringFromFile(path, fallback);
    }

}
