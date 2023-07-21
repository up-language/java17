package global;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;

public class GroovyVM {


    protected GroovyShell shell = null;
    protected Binding binding = new Binding();

    public GroovyVM() {
        this.binding.setProperty("vm", this);
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("global.GroovyVMPrototype");
        this.shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), this.binding, config);
    }

    public void setVariable(String name, Object x) {
        //this.binding.setProperty(name, x);
        this.binding.setVariable(name, x);
    }

    public Object getVariable(String name) {
        return this.binding.getVariable(name);
    }

    public boolean hasVariable(String name) {
        return this.binding.hasVariable(name);
    }

    private Object run(String script, Object[] args) {
        //Binding binding = new Binding();
        //binding.setProperty("vm", this);
        for (int i = 0; i < args.length; i++) {
            //binding.setProperty("_" + i, args[i]);
            this.setVariable("_" + i, args[i]);
        }
        //Script script1 = this._shell.parse(script);
        //script1.setBinding(binding);
        //return script1.run();
        return this.shell.evaluate(script);
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

    public static void echo(Object x, String title) {
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

    public static void echo(Object x) {
        echo(x, null);
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
