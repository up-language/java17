package global;

import groovy.lang.Script;

public abstract class GroovyVMPrototype extends Script {
    public void print(Object x, String title) {
        GroovyVM.print(x, title);
    }

    public void print(Object x) {
        GroovyVM.print(x);
    }

}
