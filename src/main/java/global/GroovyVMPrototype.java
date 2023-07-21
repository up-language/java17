package global;

import groovy.lang.Script;

public abstract class GroovyVMPrototype extends Script {
    public void echo(Object x, String title) {
        GroovyVM.echo(x, title);
    }

    public void echo(Object x) {
        GroovyVM.echo(x);
    }

}
