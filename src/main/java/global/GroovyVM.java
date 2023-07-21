package global;

import groovy.lang.GroovyShell;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptException;
import java.util.AbstractList;
import java.util.AbstractMap;

public class GroovyVM {
    public GroovyShell _shell = null;

    public GroovyVM() {
        this._shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
    }

    public void setGlobal(String name, Object x) {
        this._shell.setProperty(name, x);
    }

    private Object run(String script, Object[] args) {
        for (int i = 0; i < args.length; i++) {
            this.setGlobal("_" + i, args[i]);
        }
        try {
            return this._shell.evaluate(script);
        } finally {
        }
    }

    public Object groovy(String script, Object... args) {
        return run(script, args);
    }

    public Object __groovy__(String script, Object... args) {
        try {
            return run(script, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void print(Object x, String title) {
        if (title != null) System.out.printf("%s: ", title);
        String result = "";
        if (x == null) result = "null";
        else if (x instanceof JSONArray) result = ((JSONArray)x).toString(2);
        else if (x instanceof JSONObject) result = ((JSONObject)x).toString(2);
        else result = x.toString();
        if (x != null)
            result = "<" + x.getClass().getSimpleName() + "> " + result;
        System.out.println(result);
    }

    public void print(Object x) {
        this.print(x, null);
    }

    public String toJson(Object x) {
        return Data.ToJson(Data.ToValue(x), true);
    }

    public Object fromJson(String json) {
        return Data.FromValue(Data.FromJson(json));
    }

    public JSONArray newArray(Object... args) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < args.length; i++) {
            result.put(args[i]);
        }
        return result;
    }

    public JSONObject newObject(Object... args) {
        JSONObject result = new JSONObject();
        for (int i = 0; i < args.length; i += 2) {
            result.put((String) args[i], args[i + 1]);
        }
        return result;
    }

}
