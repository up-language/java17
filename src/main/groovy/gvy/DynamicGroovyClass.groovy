package gvy

import system.DynamicObject

class DynamicGroovyClass {
    def add2(int a, int b) {
        return a + b;
    }

    DynamicObject returnList() {
        def result = ['a', 11, null]
        return DynamicObject.fromObject(result)
    }

    def methodMissing(String name, args) {
        println "You called $name with ${args.join(', ')}."
        args.size()
    }
}
