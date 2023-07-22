package gvy

class DynamicGroovyClass {
    def add2(int a, int b) {
        return a + b;
    }

    def methodMissing(String name, args) {
        println "You called $name with ${args.join(', ')}."
        args.size()
    }
}
