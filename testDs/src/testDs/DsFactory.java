package testDs;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

public class DsFactory implements ObjectFactory {

	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable<?, ?> environment) throws Exception {
		final Object dataSource = new InitialContext()
				.lookup("java:app/jdbc/laf2");
		return Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { DataSource.class }, new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						System.out.println("called " + method);
						return method.invoke(dataSource, args);
					}
				});
	}

}
