package com.tg.async.proxy.handler;

import com.github.mauricio.async.db.QueryResult;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.ModelMap;

public class DeleteHandle extends BaseSQLhandler {
	@Override
	public void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap,
			DataHandler<?> dataHandler) {
		handleReturnData(mapperMethod, dataHandler, queryResult.rowsAffected(), false);
	}

	@Override
	protected void errorHandle(boolean key, long count) {
		log.error("delete row count is {}, can't convert int ,please change int to long in return type", count);
	}
}