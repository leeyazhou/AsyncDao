package com.tg.async.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mauricio.async.db.QueryResult;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.BoundSql;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.dynamic.mapping.SqlType;
import com.tg.async.mysql.Configuration;
import com.tg.async.mysql.SQLConnection;
import com.tg.async.proxy.handler.DeleteHandle;
import com.tg.async.proxy.handler.SQLhandler;
import com.tg.async.proxy.handler.InsertHandle;
import com.tg.async.proxy.handler.SelectHandle;
import com.tg.async.proxy.handler.UpdateHandle;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Created by twogoods on 2018/4/12.
 */
public class MapperProxy<T> implements InvocationHandler {
	private static final Logger log = LoggerFactory.getLogger(MapperProxy.class);
	private Configuration configuration;
	private Class<T> mapperInterface;

	private Map<SqlType, SQLhandler> sqlHandle = new HashMap<>(4);

	public MapperProxy(Configuration configuration, Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
		this.configuration = configuration;

		sqlHandle.put(SqlType.INSERT, new InsertHandle(configuration));
		sqlHandle.put(SqlType.UPDATE, new UpdateHandle());
		sqlHandle.put(SqlType.DELETE, new DeleteHandle());
		sqlHandle.put(SqlType.SELECT, new SelectHandle());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}
		if (isDefaultMethod(method)) {
			return invokeDefaultMethod(proxy, method, args);
		}
		MapperMethod mapperMethod = getMapperMethod(method);
		MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());

		DataHandler<?> dataHandler = null;
		if (args[args.length - 1] instanceof DataHandler) {
			dataHandler = (DataHandler<?>) args[args.length - 1];
		}
		switch (mappedStatement.getSqlType()) {
		case INSERT: {
			execute(mapperMethod, mappedStatement, args, dataHandler, sqlHandle.get(SqlType.INSERT));
			break;
		}
		case UPDATE: {
			execute(mapperMethod, mappedStatement, args, dataHandler, sqlHandle.get(SqlType.UPDATE));
			break;
		}
		case DELETE: {
			execute(mapperMethod, mappedStatement, args, dataHandler, sqlHandle.get(SqlType.DELETE));
			break;
		}
		case SELECT: {
			execute(mapperMethod, mappedStatement, args, dataHandler, sqlHandle.get(SqlType.SELECT));
			break;
		}
		}
		return null;
	}

	protected void getConnection(Handler<AsyncResult<SQLConnection>> handler) {
		configuration.getConnectionPool().getConnection(res -> {
			if (res.succeeded()) {
				handler.handle(Future.succeededFuture(res.result()));
			} else {
				log.error("get connection error", res.cause());
			}
		});
	}

	private void execute(MapperMethod mapperMethod, MappedStatement mappedStatement, Object[] args,
			DataHandler<?> dataHandler, SQLhandler excuteSQLhandle) {
		BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(convertArgs(mapperMethod, args));
		getConnection(asyncConnection -> {
			SQLConnection connection = asyncConnection.result();
			log.debug("sql : {}", boundSql);
			connection.queryWithParams(boundSql.getSql(), boundSql.getParameters(), qr -> {
				if (qr.succeeded()) {
					QueryResult queryResult = qr.result();
					ModelMap resultMap;
					if (StringUtils.isEmpty(mappedStatement.getResultMap())) {
						resultMap = configuration.getModelMap(mapperMethod.getIface().getName());
					} else {
						resultMap = configuration
								.getModelMap(mapperMethod.getIface().getName() + "." + mappedStatement.getResultMap());
					}
					excuteSQLhandle.handle(mapperMethod, queryResult, resultMap, dataHandler);
				} else {
					log.error("execute sql error !", qr.cause());
				}
			});
		});
	}

	private Object convertArgs(MapperMethod mapperMethod, Object[] args) {
		Map<String, Object> param = new HashMap<>();
		List<String> params = mapperMethod.getParamName();
		for (int i = 0; i < params.size() - 1; i++) {
			param.put(params.get(i), args[i]);
		}
		return param;
	}

	private MapperMethod getMapperMethod(Method method) {
		MapperMethod mapperMethod = null;
		if ((mapperMethod = configuration.getMapperMethod(method)) == null) {
			mapperMethod = new MapperMethod(mapperInterface, method);
			configuration.addMapperMethod(method, mapperMethod);
		}
		return mapperMethod;
	}

	private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
		final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
				.getDeclaredConstructor(Class.class, int.class);
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}
		final Class<?> declaringClass = method.getDeclaringClass();
		return constructor
				.newInstance(declaringClass,
						MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE
								| MethodHandles.Lookup.PUBLIC)
				.unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
	}

	private boolean isDefaultMethod(Method method) {
		return (method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
				&& method.getDeclaringClass().isInterface();
	}
}
