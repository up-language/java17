package system;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;

public class GroovyVM {

    protected GroovyShell shell = null;
    protected Binding binding = new Binding();

    //public JSONObject imported = new JSONObject();
    public java.util.Map<String, Long> imported = new java.util.LinkedHashMap<String, Long>();

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
        VMCommon.echo(x, title);
    }

    public void echo(Object x) {
        VMCommon.echo(x);
    }

    public void echoJson(Object x, String title) {
        VMCommon.echoJson(x, title);
    }

    public void echoJson(Object x) {
        VMCommon.echoJson(x);
    }

    public String toJson(Object x) {
        return VMCommon.toJson(x);
    }

    public Object fromJson(String json) {
        return VMCommon.fromJson(json);
    }

    public java.util.List<Object> newList(Object... args) {
        return VMCommon.newList(args);
    }

    public java.util.Map<String, Object> newMap(Object... args) {
        return VMCommon.newMap(args);
    }

    public String readAsText(String path) throws Exception {
        return VMCommon.readAsText(path);
    }

    public Object readAsJson(String path) throws Exception {
        return VMCommon.readAsJson(path);
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

    public void writeStringToFile(String path, String data) throws Exception {
        VMCommon.writeStringToFile(path, data);
    }

    public String readStringFromFile(String path, String fallback) throws Exception {
        return VMCommon.readStringFromFile(path, fallback);
    }

}
