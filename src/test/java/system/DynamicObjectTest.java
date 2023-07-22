package system;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class DynamicObjectTest {

    @Test
    void test() throws Exception {
        var vm = new GroovyVM();
        var list = DynamicObject.newList(11, "abc", null, 12L);
        vm.echo(list);
        vm.echo(list.at(0));
        vm.echo(list.at(0).asInt());
        vm.echo(list.at(0).asLong());
        vm.echo(list.at(0).asDouble());
        vm.echoJson(list.at(3).asLong());
        //vm.echo(list.get(1).asInt());
        vm.echo(list.at(2) == null);
        for (int i=0; i<list.size(); i++) {
            vm.echo(list.at(i), "" + i);
        }
        var map = DynamicObject.newMap(list.at(1), "aaa", "xyz", 12.3);
        vm.echo(map);
        var keys = map.keys();
        vm.echo(keys);
        for (int i=0; i<keys.size(); i++) {
            vm.echo(keys.at(i));
            vm.echo(map.get(keys.at(i)));
        }
        vm.echoJson(list);
        vm.echoJson(map);
        var listJson = vm.toJson(list);
        vm.echo(vm.fromJson(listJson));
        var mapJson = vm.toJson(map);
        vm.echo(vm.fromJson(mapJson));
        //var dec = new BigDecimal(3.14);
        var dec = vm.eval("3.14");
        var decJson = vm.toJson(dec);
        vm.echo(vm.fromJson(decJson));
    }

}
