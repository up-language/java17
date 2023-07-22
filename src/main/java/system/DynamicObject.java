package system;

public class DynamicObject {
    protected Object value = null;

    protected DynamicObject(Object x) {
        this.value = x;
    }

    @Override
    public String toString() {
        if (this.value == null) return "null";
        return this.value.toString();
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

    public static DynamicObject newList(Object... args) {
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

    public DynamicObject at(int index) {
        java.util.List<Object> list = (java.util.List<Object>) this.value;
        Object result = list.get(index);
        if (result == null) return null;
        return new DynamicObject(result);
    }

    public DynamicObject at(DynamicObject index) {
        return at(index.asInt());
    }

    public void add(Object x) {
        java.util.List<Object> list = (java.util.List<Object>) this.value;
        list.add(strip(x));
    }

    public static DynamicObject newMap(Object... args) {
        java.util.Map<String, Object> result = new java.util.HashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            result.put((String) strip(args[i]), strip(args[i + 1]));
        }
        return new DynamicObject(result);
    }

    public DynamicObject keys() {
        java.util.Map<String, Object> map = (java.util.Map<String, Object>) this.value;
        var keys = map.keySet().toArray();
        //String[] result = new String[keys.length];
        var result = newList();
        for (int i = 0; i < keys.length; i++) {
            //result[i] = (String) keys[i];
            result.add(keys[i]);
        }
        return result;
    }

    public DynamicObject get(String key) {
        java.util.Map<String, Object> map = (java.util.Map<String, Object>) this.value;
        if (!map.containsKey(key)) return null;
        return new DynamicObject(map.get(key));
    }

    public DynamicObject get(DynamicObject key) {
        return get(key.asString());
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

}
