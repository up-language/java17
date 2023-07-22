package system;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

////import org.json.JSONArray;
import org.junit.jupiter.api.Test;

class VMTest {

	@Test
	void test() throws Exception {

		VM vm = new VM();
		//VM vm = new VM();

		Object tmp = null;
		vm.js("console.log('hello-world-0');");
		Object o1 = vm.jsToJson("[11,22]");
		System.out.printf("o1=%s\n", o1);
		assertEquals("[11, 22]", o1.toString());
		Object o2 = vm.jsToJson("({x:11,y:22})");
		System.out.printf("o2=%s\n", o2);
		assertEquals("{x=11, y=22}", o2.toString());
		Object o3 = vm.jsToJson("123.45");
		System.out.printf("o3=%s\n", o3);
		assertEquals(123.45, o3);
		vm.setGlobal("count", 3);
		vm.js("print(count)");
		assertEquals(3, vm.js("count"));
		// vm.load(":/json.js");
		vm.load(":/json.js");
		// vm.loadFile(":/json.js");
		// vm.loadFile("https://raw.githubusercontent.com/up-language/up-language/main/om-java/json.js");
		vm.js("print(JSON.stringify(json, null, 2))");
		assertEquals("{a=abc, b=123, c=[11, 22, 33]}", vm.jsToJson("json").toString());
		Object json = vm.js("json");
		assertEquals(33, vm.jsToJson("_0.c[2]", json));
		assertEquals(33, vm.jsToJson("_0.c[_1]", json, 2));
		vm.jsToJson("_0.c[_1]=_2", json, 2, 777);
		vm.js("print(JSON.stringify(json, null, 2))");
		assertEquals(777, vm.js("json.c[2]"));
		vm.print(json, "json");
		////vm.print(vm.js("ary"));
		vm.js("console.log(_0)", "this is _0");
		// vm.load(":/run.js");
		// vm.load(":/error.js");
		Object dt = vm.load(":/date.js");
		vm.print(dt.getClass().getName(), "dt.getClass().getName()");
		System.out.println(dt.toString());

		vm.js("console.log(JSON.stringify(new Date()))");
		// com.oracle.truffle.polyglot.PolyglotMap
		vm.print(vm.newDate(), "vm.newDate()");
		vm.print(vm.newDate("2023-07-17T17:13:12.577Z"), "vm.newDate(\"2023-07-17T17:13:12.577Z\")");
		vm.print(vm.newDate(new Date()), "vm.newDate(new Date()");

		for (int i = 0; i < (int) vm.js("json.c.length"); i++) {
			vm.print(vm.js("json.c[_0]", i), "enum");
		}

		if ((boolean) vm.js("json.hasOwnProperty('a')")) {
			vm.print("has a");
		}

		vm.js("""
				$xyz = 123;
				console.log($xyz);
				""");


		vm.print(vm.toJson(vm.js("undefined")));

		vm.print(vm.parse("3.14").getClass().getName());

		var build_cmd = vm.readAsText("https://raw.githubusercontent.com/atom/atom/master/script/build.cmd");
		vm.print(build_cmd, "build_cmd");
		build_cmd = (String) vm.js("readAsText('https://raw.githubusercontent.com/atom/atom/master/script/build.cmd')");
		vm.print(build_cmd, "build_cmd");

		/*
		var package_json = vm.readAsJson("https://raw.githubusercontent.com/atom/atom/master/package.json");
		vm.print(package_json, "package_json");
		package_json = vm.js("readAsJson('https://raw.githubusercontent.com/atom/atom/master/package.json')");
		vm.print(package_json, "package_json");
        */

		vm.js("""
				const errorMsg = 'the # is not even';
				for (let number = 2; number <= 5; number++) {
				  console.log(`the # is ${number}`);
				  console.assert(number % 2 === 0, number);
				}
							 """);

		try {
			vm.js("verify(1==2)");
			vm.print("assertion ng");
		} catch (Exception e) {
			vm.print("assertion ok");
		}

		vm.js("""
				var $file = new (Java.type('java.io.File'))("test.md");
				var $fileName = $file.getName();
				console.log($fileName);
				verify($fileName === "test.md");
				//verify($fileName === "test.mdx");
				         """);

		tmp = vm.newObject("x", 1, "y", 2);
		vm.print(tmp);
		
		vm.js("dt = new Date()");
		vm.asDate(vm.js("dt"));

		var dtAry = vm.newArray(vm.newDate());
		var dtAryJson = vm.toJson(dtAry);
		System.out.println(dtAryJson);

		vm.load(":/class.js");

		vm.close();
	}

}
