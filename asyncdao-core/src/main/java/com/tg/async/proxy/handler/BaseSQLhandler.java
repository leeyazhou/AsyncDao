package com.tg.async.proxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tg.async.base.DataHandler;
import com.tg.async.base.MapperMethod;

abstract class BaseSQLhandler implements SQLhandler {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handleReturnData(MapperMethod mapperMethod, DataHandler dataHandler, long count, boolean key) {
		if (Integer.TYPE.equals(mapperMethod.getPrimary()) || Integer.class.equals(mapperMethod.getPrimary())) {
			try {
				dataHandler.handle(Integer.parseInt(Long.toString(count)));
			} catch (NumberFormatException e) {
				errorHandle(key, count);
			}
		} else if (Long.TYPE.equals(mapperMethod.getPrimary()) || Long.class.equals(mapperMethod.getPrimary())) {
			dataHandler.handle(count);
		} else if (Boolean.TYPE.equals(mapperMethod.getPrimary()) || Boolean.class.equals(mapperMethod.getPrimary())) {
			if (count > 0) {
				dataHandler.handle(true);
			} else {
				dataHandler.handle(false);
			}
		} else {
			dataHandler.handle(null);
		}
	}

	protected abstract void errorHandle(boolean key, long count);
}