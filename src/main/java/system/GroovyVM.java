package system;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
////import org.json.JSONArray;
////import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class GroovyVM {

    protected GroovyShell shell = null;
    protected Binding binding = new Binding();

    //public JSONObject imported = new JSONObject();
    public java.util.Map<String, Long> imported = new java.util.HashMap<String, Long>();

    public GroovyVM() {
        this.binding.setProperty("vm", this);
        //CompilerConfiguration config = new CompilerConfiguration();
        //config.setScriptBaseClass("global.GroovyVMPrototype");
        //this.shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), this.binding, config);
        this.shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), this.binding);
        /*
        this.groovy("""
                readAsText = { path -> vm.readAsText(path) }
                readAsJson = { path -> vm.readAsJson(path) }
                load = { path -> vm.load(path) }
                require = { path -> vm.require(path) }
                """);
         */
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
        for (int i = 0; i < args.length; i++) {
            this.setVariable("_" + i, args[i]);
        }
        try {
            return this.shell.evaluate(script);
        } finally {
            for (int i = 0; i < args.length; i++) {
                this.setVariable("_" + i, null);
            }
        }
    }

    public Object eval(String script, Object... args) {
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
        else result = x.toString();
        if (x != null) {
            if (x instanceof DynamicObject)
                result = "<Dynamic:" + ((DynamicObject)x).value().getClass().getName() + "> " + result;
            else
                result = "<" + x.getClass().getName() + "> " + result;
        }
        System.out.println(result);
    }

    public void echo(Object x) {
        echo(x, null);
    }

    public void echoJson(Object x, String title) {
        if (title != null) System.out.printf("%s: ", title);
        String result = toJson(x);
        if (x != null) {
            if (x instanceof DynamicObject)
                result = "<Dynamic:" + ((DynamicObject)x).value().getClass().getName() + "> " + result;
            else
                result = "<" + x.getClass().getName() + "> " + result;
        }
        System.out.println(result);
    }

    public void echoJson(Object x) {
        echoJson(x, null);
    }

    public String toJson(Object x) {
        if (x instanceof DynamicObject)
            return BsonData.ToJson(((DynamicObject) x).toBsonValue(), true);
        else
            return BsonData.ToJson(BsonData.ToValue(x), true);
    }

    public Object fromJson(String json) {
        //return BsonData.FromValue(BsonData.FromJson(json));
        return DynamicObject.fromBsonValue(BsonData.FromJson(json));
    }

    /*
    public java.util.List<Object> newList(Object... args) {
        java.util.List<Object> result = new java.util.ArrayList<Object>();
        for (int i = 0; i < args.length; i++) {
            result.add(args[i]);
        }
        return result;
    }

    public java.util.Map<String, Object> newMap(Object... args) {
        java.util.Map<String, Object> result = new java.util.HashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            result.put((String) args[i], args[i + 1]);
        }
        return result;
    }
    */

    public DynamicObject newList(Object... args) {
        return DynamicObject.newList(args);
    }

    public DynamicObject newMap(Object... args) {
        return DynamicObject.newMap(args);
    }

    public String readAsText(String path) throws Exception {
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

    public Object readAsJson(String path) throws Exception {
        return fromJson(readAsText(path));
    }

    public Object load(String path) throws Exception {
        return eval(readAsText(path));
    }

    public void require(String path) throws Exception {
        if (path.startsWith(":/")) {
        } else if (path.startsWith("http:") || path.startsWith("https:")) {
        } else {
            path = new File(path).getAbsolutePath();
        }
        if (this.imported.containsKey(path)) {
            long count = this.imported.get(path);
            this.imported.put(path, count + 1);
            return;
        }
        eval(readAsText(path));
        this.imported.put(path, 1L);
    }

}
