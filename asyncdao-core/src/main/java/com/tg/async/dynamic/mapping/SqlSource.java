package com.tg.async.dynamic.mapping;

/**
 * Created by twogoods on 2018/4/13.
 */
public interface SqlSource {
	BoundSql getBoundSql(Object parameterObject);
}
