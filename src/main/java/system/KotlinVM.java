package system;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class KotlinVM {
    public ScriptEngine engine = null;
    public KotlinVM() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByExtension("main.kts");
        System.out.println(engine);
        engine.put("vm", this);
    }
    public void setVariable(String name, Object x) {
        this.engine.put(name, x);
    }

    public Object getVariable(String name) {
        return this.engine.get(name);
    }

    private Object run(String script, Object[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            this.setVariable("_" + i, args[i]);
        }
        return this.engine.eval(script);
    }

    public Object eval(String script, Object... args) throws Exception {
        return run(script, args);
    }

    public Object __eval__(String script, Object... args) {
        try {
            return run(script, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void echo(Object x, String title) {
        if (title != null) System.out.printf("%s: ", title);
        String result = "";
        if (x == null) result = "null";
        else if (x instanceof JSONArray) result = ((JSONArray) x).toString(2);
        else if (x instanceof JSONObject) result = ((JSONObject) x).toString(2);
        else result = x.toString();
        if (x != null)
            result = "<" + x.getClass().getSimpleName() + "> " + result;
        System.out.println(result);
    }

    public void echo(Object x) {
        echo(x, null);
    }

    public String toJson(Object x) {
        return BsonData.ToJson(BsonData.ToValue(x), true);
    }

    public Object fromJson(String json) {
        return BsonData.FromValue(BsonData.FromJson(json));
    }

    public java.util.List<Object> newArray(Object... args) {
        java.util.List<Object> result = new java.util.ArrayList<Object>();
        for (int i = 0; i < args.length; i++) {
            result.add(args[i]);
        }
        return result;
    }

    public java.util.Map<String, Object> newObject(Object... args) {
        java.util.Map<String, Object> result = new java.util.HashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            result.put((String) args[i], args[i + 1]);
        }
        return result;
    }

}
