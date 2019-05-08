package com.tg.async.proxy.handler;

import java.util.List;

import com.github.mauricio.async.db.QueryResult;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.ModelMap;
import com.tg.async.utils.DataConverter;

public class SelectHandle extends BaseSQLhandler {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap,
			DataHandler dataHandler) {
		if (mapperMethod.isReturnsMany()) {
			List list = DataConverter.queryResultToListObject(queryResult, mapperMethod.getPrimary(), resultMap);
			dataHandler.handle(list);
		} else if (mapperMethod.isReturnsMap()) {
			dataHandler.handle(DataConverter.queryResultToMap(queryResult, resultMap));
		} else if (mapperMethod.isReturnsSingle()) {
			dataHandler.handle(DataConverter.queryResultToObject(queryResult, mapperMethod.getPrimary(), resultMap));
		} else if (mapperMethod.isReturnsVoid()) {
			dataHandler.handle(null);
		}
	}

	@Override
	protected void errorHandle(boolean key, long count) {
	}
}
