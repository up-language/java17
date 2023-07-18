package global;

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
}
