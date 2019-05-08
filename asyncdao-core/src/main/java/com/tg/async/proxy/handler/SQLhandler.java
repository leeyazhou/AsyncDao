package com.tg.async.proxy.handler;

import com.github.mauricio.async.db.QueryResult;
import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;
import com.tg.async.dynamic.mapping.ModelMap;

public interface SQLhandler {
	void handle(MapperMethod mapperMethod, QueryResult queryResult, ModelMap resultMap, DataHandler<?> dataHandler);
}