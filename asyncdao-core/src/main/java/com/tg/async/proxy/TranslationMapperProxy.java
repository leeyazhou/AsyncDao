package com.tg.async.proxy;

import com.tg.async.mysql.Configuration;
import com.tg.async.mysql.SQLConnection;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Created by twogoods on 2018/8/23.
 */
public class TranslationMapperProxy<T> extends MapperProxy<T> {

	private SQLConnection connection;

	public TranslationMapperProxy(Configuration configuration, Class<T> mapperInterface, SQLConnection connection) {
		super(configuration, mapperInterface);
		this.connection = connection;
	}

	@Override
	protected void getConnection(Handler<AsyncResult<SQLConnection>> handler) {
		handler.handle(Future.succeededFuture(connection));
	}
}
