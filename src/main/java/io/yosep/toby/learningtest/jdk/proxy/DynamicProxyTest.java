package io.yosep.toby.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;


public class DynamicProxyTest {
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget(); // 타깃은 인터페이스를 통해 접근하는 습관을 기르자.
		assertThat(hello.sayHello("Yosep"), is("Hello Yosep"));
		assertThat(hello.sayHi("Yosep"), is("Hi Yosep"));
		assertThat(hello.sayThankYou("Yosep"), is("Thank You Yosep"));

		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader(), // 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
				new Class[] { Hello.class}, // 구현할 인터페이스
				new UppercaseHandler(new HelloTarget())); // 부가기능과 위임 코드를 담은 InvocationHandler
	
//		Hello proxiedHello = new HelloUppercase(new HelloTarget());
		assertThat(proxiedHello.sayHello("Yosep"), is("HELLO YOSEP"));
		assertThat(proxiedHello.sayHi("Yosep"), is("HI YOSEP"));
		assertThat(proxiedHello.sayThankYou("Yosep"), is("THANK YOU YOSEP"));
	}

	static interface Hello {
		String sayHello(String name);

		String sayHi(String name);

		String sayThankYou(String name);
	}

	static class HelloTarget implements Hello {

		@Override
		public String sayHello(String name) {
			// TODO Auto-generated method stub
			return "Hello " + name;
		}

		@Override
		public String sayHi(String name) {
			// TODO Auto-generated method stub
			return "Hi " + name;
		}

		@Override
		public String sayThankYou(String name) {
			// TODO Auto-generated method stub
			return "Thank You " + name;
		}

	}

	static class HelloUppercase implements Hello {
		Hello hello; // 위임할 타깃 오브젝트, 여기서는 타깃 클래스의 오브젝트인 것은 알지만, 다른 프록시를 추가할 수도 있으므로 인터페이스로 접근한다.

		public HelloUppercase(Hello hello) {
			// TODO Auto-generated constructor stub
			this.hello = hello;
		}
		
		@Override
		public String sayHello(String name) {
			// TODO Auto-generated method stub
			return hello.sayHello(name).toUpperCase();
		}

		@Override
		public String sayHi(String name) {
			// TODO Auto-generated method stub
			return hello.sayHi(name).toUpperCase();
		}

		@Override
		public String sayThankYou(String name) {
			// TODO Auto-generated method stub
			return hello.sayThankYou(name).toUpperCase();
		}

	}
	
	static class UppercaseHandler implements InvocationHandler {
		Object target;
		
		public UppercaseHandler(Object target) {
			// TODO Auto-generated constructor stub
			this.target = target;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// TODO Auto-generated method stub
			Object ret = method.invoke(target, args); // 타깃으로 위임. 인터페이스의 메소드 호출에 모두 적용된다.
			if(ret instanceof String && method.getName().startsWith("say")) { // 리턴 타입과 메소드 이름이 일치하는 경우에만 부가기능을 적용한다.
				return ((String)ret).toUpperCase();
			}else {
				return ret;
			}
		}
		
	}
}
