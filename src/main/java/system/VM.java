package system;

import com.oracle.truffle.js.runtime.JSContextOptions;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

//import static org.junit.jupiter.api.Assertions.*;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.Date;

import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
////import org.json.JSONArray;
////import org.json.JSONObject;

public class VM implements Closeable, VMInterface {

	public GraalJSScriptEngine engine;

	public VM() {
		this.engine = GraalJSScriptEngine.create(
				Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build(),
				Context.newBuilder("js").allowHostAccess(HostAccess.ALL).allowHostClassLookup(className -> true)
						.allowAllAccess(true).option(JSContextOptions.ECMASCRIPT_VERSION_NAME, "2022"));
		this.setGlobal("$vm", this);
		try {
			this.js("""
					globalThis.print = function(x, title) {
					  $vm.print(x, title===undefined?null:title);
					}
					""");
			this.js("""
					globalThis.load = function(path) {
					  return $vm.load(path);
					}
					""");
			this.js("""
					globalThis.readAsText = function(path) {
					  return $vm.readAsText(path);
					}
					""");
			this.js("""
					globalThis.readAsJson = function(path) {
					  return $vm.readAsJson(path);
					}
					""");
			this.js("""
					globalThis.verify = function(x) {
					  if (!x) throw Error("Verification failed.");
					}
					""");
			this.js("""
					globalThis.$typeof = function(x) {
					  if (x === null) return "null";
					  if (x instanceof Array) return "array";
					  if (x instanceof Date) return "date";
					  return (typeof x);
					}
					""");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		engine.close();
	}

	@Override
	public Object setGlobal(String name, Object x) {
		Object n = toNative(x);
		engine.put(name, n);
		return n;
	}

	@Override
	public AbstractList<Object> newArray(Object... args) {
		@SuppressWarnings("unchecked")
		AbstractList<Object> result = (AbstractList<Object>) this.__js__("[]");
		for (int i = 0; i < args.length; i++) {
			result.add(toNative(args[i]));
		}
		return result;
	}

	@Override
	public AbstractMap<String, Object> newObject(Object... args) {
		@SuppressWarnings("unchecked")
		AbstractMap<String, Object> result = (AbstractMap<String, Object>) this.__js__("({})");
		for (int i = 0; i < args.length; i += 2) {
			result.put((String) args[i], toNative(args[i + 1]));
		}
		return result;
	}

	@Override
	public String typeof(Object x) {
		return (String) __js__("$typeof(_0)", x);
	}

	@Override
	public boolean typeis(Object x, String type) {
		return typeof(x).equals(type);
	}

	@Override
	public Object newDate() {
		return __js__("new Date()");
	}

	@Override
	public Object newDate(String x) {
		var result = newDate();
		var ts = __js__("Date.parse(_0)", x);
		__js__("_0.setTime(_1)", result, ts);
		return result;
	}

	@Override
	public Object newDate(Date x) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		return newDate(sdf.format(x));
	}

	@SuppressWarnings("unchecked")
	public AbstractList<Object> asArray(Object x) {
		return (AbstractList<Object>) x;
	}

	@SuppressWarnings("unchecked")
	public AbstractMap<String, Object> asObject(Object x) {
		return (AbstractMap<String, Object>) x;
	}

	@Override
	public Date asDate(Object x) throws ParseException {
		verify("_0 instanceof Date", x);
		verify("(typeof _0) === 'object'", x);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		Date date = df.parse(x.toString());
		return date;
	}

	private Object run(String script, Object[] args) throws ScriptException {
		for (int i = 0; i < args.length; i++) {
			this.setGlobal("_" + i, toNative(args[i]));
		}
		try {
			return engine.eval(script);
		} finally {
			engine.eval("""
					for (let x in globalThis) {
					  //console.log("<"+x+">");
					  if (x.startsWith("_")) {
					    //console.log("deleting <"+x+">");
					    delete globalThis[x];
					  }
					}
					""");
		}
	}

	@Override
	public Object js(String script, Object... args) throws ScriptException {
		return run(script, args);
	}

	@Override
	public Object __js__(String script, Object... args) {
		try {
			return run(script, args);
		} catch (ScriptException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object jsToJson(String script, Object... args) throws ScriptException {
		return toJson(run(script, args));
	}

	@Override
	public Object toJson(Object x) {
		if (x == null)
			return null;
		if (typeis(x, "undefined"))
			return null;
		if (typeis(x, "date"))
			try {
				return asDate(x);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		String className = x.getClass().getName();
		switch (className) {
		case "com.oracle.truffle.polyglot.PolyglotList": {
			@SuppressWarnings("unchecked")
			AbstractList<Object> ary = (AbstractList<Object>) x;
			java.util.List<Object> result = new java.util.ArrayList<Object>();
			for (int i = 0; i < ary.size(); i++) {
				result.add(i, toJson(ary.get(i)));
			}
			return result;
		}
		case "com.oracle.truffle.polyglot.PolyglotMap": {
			@SuppressWarnings("unchecked")
			AbstractMap<String, Object> obj = (AbstractMap<String, Object>) x;
			java.util.Map<String, Object> result = new java.util.HashMap<String, Object>();
			Object[] keys = obj.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				result.put((String) keys[i], toJson(obj.get((String) keys[i])));
			}
			return result;
		}
		default: {
			return x;
		}
		}
	}

	@Override
	public Object toNative(Object x) {
		if (x == null)
			return null;
		if (x instanceof java.util.Date)
			return newDate((java.util.Date) x);
		if (x instanceof java.math.BigDecimal) {
			return ((java.math.BigDecimal)x).doubleValue();
		}
		String className = x.getClass().getName();
		switch (className) {

			/*
		case "org.json.JSONArray": {
			org.json.JSONArray ary = (org.json.JSONArray) x;
			@SuppressWarnings("unchecked")
			AbstractList<Object> result = (AbstractList<Object>)newArray();
			for (int i = 0; i < ary.length(); i++) {
				result.add(toNative(ary.get(i)));
			}
			return result;
		}
		case "org.json.JSONObject": {
			org.json.JSONObject obj = (org.json.JSONObject) x;
			@SuppressWarnings("unchecked")
			AbstractMap<String, Object> result = (AbstractMap<String, Object>)newObject();
			Object[] keys = obj.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				result.put((String) keys[i], toNative(obj.get((String) keys[i])));
			}
			return result;
		}
        */
		default: {
			return x;
		}
		}
	}

	@Override
	public void print(Object x, String title) {
		if (title != null) {
			System.out.print(title);
			System.out.print(": ");
		}
		if (x instanceof String) {
			System.out.println(x);
		} else {
			String json = null;
			json = stringify(x, 2);
			System.out.println(json);
		}
	}

	@Override
	public void print(Object x) {
		print(x, null);
	}

	@Override
	public Object load(String path) throws Exception {
		return js(readAsText(path));
	}

	@Override
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

	@Override
	public Object readAsJson(String path) throws ScriptException, Exception {
		return parse(readAsText(path));
	}

	@Override
	public String stringify(Object x, int indent) {
		return (String) __js__("JSON.stringify(_0, null, _1)", x, indent);
	}

	@Override
	public String stringify(Object x) {
		return (String) __js__("JSON.stringify(_0)", x);
	}

	@Override
	public Object parse(String json) throws ScriptException {
		return js("JSON.parse(_0)", json);
	}

	@Override
	public void verify(String script, Object... args) {
		Object result = null;
		try {
			result = run(script, args);
		} catch (ScriptException e) {
			e.printStackTrace();
			org.junit.jupiter.api.Assertions.fail();
		}
		if (result == null)
			org.junit.jupiter.api.Assertions.fail();
		if (!(result instanceof java.lang.Boolean))
			org.junit.jupiter.api.Assertions.fail();
		org.junit.jupiter.api.Assertions.assertTrue((boolean) result);
	}

	/*
	class Printer {
		public void print(Object x) {
			System.out.println(x);
		}
	}
	*/

}
