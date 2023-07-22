package system;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonValue;

public class DynamicObject {
    protected Object value = null;

    protected DynamicObject(Object x) {
        this.value = x;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    public String type() {
        return this.value.getClass().getName();
    }

    public Object value() {
        return this.value;
    }

    protected static Object strip(Object x) {
        if (x == null) {
            return null;
        } else if (!(x instanceof DynamicObject)) {
            return x;
        } else {
            var vo = (DynamicObject) x;
            return vo.value;
        }
    }

    public static DynamicObject newList(Object[] args) {
        java.util.List<Object> result = new java.util.ArrayList<Object>();
        for (int i = 0; i < args.length; i++) {
            result.add(strip(args[i]));
        }
        return new DynamicObject(result);
    }

    public int size() {
        java.util.List<Object> list = (java.util.List<Object>) this.value;
        return list.size();
    }

    public DynamicObject getAt(int index) {
        java.util.List<Object> list = (java.util.List<Object>) this.value;
        Object result = list.get(index);
        if (result == null) return null;
        return new DynamicObject(result);
    }

    public DynamicObject getAt(DynamicObject index) {
        return getAt(index.asInt());
    }

    public void add(Object x) {
        java.util.List<Object> list = (java.util.List<Object>) this.value;
        list.add(strip(x));
    }

    public void putAt(int index, Object x) {
        java.util.List<Object> list = (java.util.List<Object>) this.value;
        list.set(index, strip(x));
    }

    public void putAt(DynamicObject index, Object x) {
        putAt(index.asInt(), x);
    }

    public static DynamicObject newMap(Object[] args) {
        java.util.Map<String, Object> result = new java.util.HashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            result.put((String) strip(args[i]), strip(args[i + 1]));
        }
        return new DynamicObject(result);
    }

    public DynamicObject keys() {
        java.util.Map<String, Object> map = (java.util.Map<String, Object>) this.value;
        var keys = map.keySet().toArray();
        var result = newList(new Object[] {});
        for (int i = 0; i < keys.length; i++) {
            result.add(keys[i]);
        }
        return result;
    }

    public DynamicObject get(String key) {
        java.util.Map<String, Object> map = (java.util.Map<String, Object>) this.value;
        if (!map.containsKey(key)) return null;
        var result = map.get(key);
        if (result == null) return null;
        return new DynamicObject(result);
    }

    public DynamicObject get(DynamicObject key) {
        return get(key.asString());
    }

    public void put(String key, Object x) {
        java.util.Map<String, Object> map = (java.util.Map<String, Object>) this.value;
        map.put(key, strip(x));
    }

    public void put(DynamicObject key, Object x) {
        put(key.asString(), x);
    }

    public int asInt() {
        if (this.value instanceof Integer) return (int) this.value;
        return Integer.valueOf(this.value.toString());
    }

    public long asLong() {
        if (this.value instanceof Long) return (long) this.value;
        return Long.valueOf(this.value.toString());
    }

    public double asDouble() {
        if (this.value instanceof Double) return (double) this.value;
        return Double.valueOf(this.value.toString());
    }

    public String asString() {
        return this.value.toString();
    }

    /*
    public BsonValue toBsonValue() {
        return toBsonValue(this);
    }
    */

    public static BsonValue toBsonValue(DynamicObject x) {
        if (x instanceof java.util.List<?>) {
            var list = (java.util.List<Object>) x;
            var result = new BsonArray();
            for (int i=0; i<list.size(); i++) {
                if (list.get(i) == null)
                    result.add(new BsonNull());
                else
                    result.add(toBsonValue(new DynamicObject(list.get(i))));
            }
            return result;
        }
        if (x instanceof java.util.Map<?, ?>) {
            var map = (java.util.Map<String, Object>) x;
            var result = new BsonDocument();
            var keys = map.keySet().toArray();
            for (int i=0; i<keys.length; i++) {
                if (map.get(keys[i]) == null)
                    result.put((String)keys[i], new BsonNull());
                else
                    result.put((String)keys[i], toBsonValue(new DynamicObject(map.get(keys[i]))));
            }
            return result;
        }
        return BsonData.ToValue(x.value);
    }

    public static DynamicObject fromBsonValue(BsonValue x) {
        if (x instanceof BsonArray) {
            var array = (BsonArray)x;
            var result = newList(new Object[] {});
            for (int i=0; i<array.size(); i++) {
                result.add(fromBsonValue(array.get(i)));
            }
            return result;
        }
        if (x instanceof BsonDocument) {
            var doc = (BsonDocument)x;
            var result = newMap(new Object[] {});
            var keys = doc.keySet().toArray();
            for (int i=0; i<keys.length; i++) {
                result.put((String)keys[i], fromBsonValue(doc.get((String)keys[i])));
            }
            return result;
        }
        var val = BsonData.FromValue(x);
        if (val == null) return null;
        return new DynamicObject(val);
    }

    public static DynamicObject fromObject(Object x) {
        if (x == null) return null;
        if (x instanceof DynamicObject) return (DynamicObject) x;
        return new DynamicObject(x);
    }

    public static Object toObjexxt(DynamicObject x) {
        return x.value;
    }

}
