package gvy

import system.Dynamic

class DynamicGroovyClass {
    def add2(int a, int b) {
        return a + b;
    }

    Dynamic returnList() {
        def result = ['a', 11, null]
        return Dynamic.wrap(result)
    }

    def receiveDynamicList(Dynamic list) {
        println list
        def l = Dynamic.toStatic(list)
        println l
    }

    def methodMissing(String name, args) {
        println "You called $name with ${args.join(', ')}."
        args.size()
    }
}
