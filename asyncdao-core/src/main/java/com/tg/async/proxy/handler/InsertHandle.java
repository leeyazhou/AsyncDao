package com.tg.async.proxy.handler;

import com.github.mauricio.async.db.QueryResult;
import com.github.mauricio.async.db.mysql.MySQLQueryResult;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.MappedStatement;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.mysql.Configuration;

public class InsertHandle extends BaseSQLhandler {
	private Configuration configuration;

	public InsertHandle(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap,
			DataHandler<?> dataHandler) {
		MappedStatement mappedStatement = configuration.getMappedStatement(mapperMethod.getName());
		if ("true".equals(mappedStatement.getUseGeneratedKeys())) {
			long generatedKey = ((MySQLQueryResult) queryResult).lastInsertId();
			handleReturnData(mapperMethod, dataHandler, generatedKey, true);
		} else {
			long rowsAffected = queryResult.rowsAffected();
			handleReturnData(mapperMethod, dataHandler, rowsAffected, false);
		}
	}

	@Override
	protected void errorHandle(boolean key, long count) {
		if (key) {
			log.error("generatedKey is {}, can't convert int ,please change int to long in return type", count);
		} else {
			log.error("rowsAffected is {}, can't convert int ,please change int to long in return type", count);
		}
	}
}
