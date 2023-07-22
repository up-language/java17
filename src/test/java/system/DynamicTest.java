package system;

import org.junit.jupiter.api.Test;

class DynamicTest {

    @Test
    void test() throws Exception {
        var vm = new GroovyVM();
        var list = Dynamic.newList(new Object[] {11, "abc", null, 12L});
        vm.echo(list);
        vm.echo(list.getAt(0));
        vm.echo(list.getAt(0).asInt());
        vm.echo(list.getAt(0).asLong());
        vm.echo(list.getAt(0).asDouble());
        vm.echoJson(list.getAt(3).asLong());
        //vm.echo(list.get(1).asInt());
        vm.echo(list.getAt(2) == null);
        for (int i=0; i<list.size(); i++) {
            vm.echo(list.getAt(i), "" + i);
        }
        var map = Dynamic.newMap(new Object[] {list.getAt(1), "aaa", "xyz", 12.3});
        vm.echo(map);
        var keys = map.keys();
        vm.echo(keys);
        for (int i=0; i<keys.size(); i++) {
            vm.echo(keys.getAt(i));
            vm.echo(map.get(keys.getAt(i)));
        }
    }

}
