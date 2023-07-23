package spider

import system.Dynamic
import system.GroovyVM
import system.MiscUtil

class Core {
    def vm = null
    def info = [:]
    Core(GroovyVM vm) {
        this.vm = vm
    }
    def info() {
        info = [:]
        String userHomeDir = System.getProperty("user.home")
        info.userHomeDir = userHomeDir
        System.out.printf("The User Home Directory is %s\n", userHomeDir)
        String settingsPath = userHomeDir + "/.apps/spider-explorer/settings.json";
        info.settingsPath = settingsPath
        String json = vm.readStringFromFile(settingsPath, '{ "count": 0 }')
        def obj = vm.fromJson(json)
        vm.echo(obj);
        vm.echo(obj.xyz, "obj.xyz")
        obj.xyz = "xyz"
        obj.count = obj.count + 1
        info.obj = obj
        json = vm.toJson(obj)
        vm.echo(json)
        vm.writeStringToFile(settingsPath, json)
        return info
    }
    def stripTest(inf) {
        inf = Dynamic.strip(inf)
        vm.echo(inf)
    }
}
