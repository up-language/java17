package system;

import java.util.Date;

public interface VMInterface {
	public Object setGlobal(String name, Object x);
	public Object newArray(Object... args);
	public Object newObject(Object... args);
	public String typeof(Object x);
	public boolean typeis(Object x, String type);
	public Object newDate();
	public Object newDate(String x);
	public Object newDate(Date x);
	public Date asDate(Object x) throws Exception;
	public Object js(String script, Object... args) throws Exception;
	public Object __js__(String script, Object... args);
	public Object jsToJson(String script, Object... args) throws Exception;
	public Object toJson(Object x);
	public Object toNative(Object x);
	public void print(Object x, String title);
	public void print(Object x);
	public Object load(String path) throws Exception;
	public String readAsText(String path) throws Exception;
	public Object readAsJson(String path) throws Exception;
	public String stringify(Object x, int indent);
	public String stringify(Object x);
	public Object parse(String json) throws Exception;
	public void verify(String script, Object... args);
}
