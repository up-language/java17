package global;

import groovy.lang.GroovyShell;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptException;

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

}
