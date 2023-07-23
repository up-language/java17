package spider

import system.Dynamic
import system.GroovyVM
import system.MiscUtil

class Core {
    def vm = null
    def info = null
    Core(GroovyVM vm) {
        this.vm = vm
        this.info = readSettings("spider-explorer")
    }
    def info() {
        if (this.info.info.count == null) this.info.info.count = 0
        this.info.info.count++
        return info
    }
    def stripTest(inf) {
        inf = Dynamic.strip(inf)
        vm.echo(inf)
    }
    def readSettings(product) {
        def info = [:]
        String userHomeDir = System.getProperty("user.home")
        info.userHomeDir = userHomeDir
        println "The User Home Directory is $userHomeDir"
        String settingsPath = userHomeDir + "/$product/settings.json";
        info.path = settingsPath
        String json = vm.readStringFromFile(settingsPath, '{}')
        def obj = vm.fromJson(json)
        info.info = obj
        return info
    }
}
