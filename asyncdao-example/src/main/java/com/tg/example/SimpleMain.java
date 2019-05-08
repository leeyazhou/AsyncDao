package com.tg.example;

import java.util.Arrays;
import java.util.List;

import com.tg.async.mysql.AsyncConfig;
import com.tg.async.mysql.AsyncDaoFactory;
import com.tg.async.mysql.pool.PoolConfiguration;
import com.tg.example.dao.CommonDao;
import com.tg.example.dao.UserSearch;

public class SimpleMain {

	public static void main(String[] args) {
		CommonDao commonDao = asyncDaoFactory().getMapper(CommonDao.class);
		UserSearch userSearch = new UserSearch();
		userSearch.setUsername("ha");
		userSearch.setMaxAge(28);
		userSearch.setMinAge(8);
		userSearch.setLimit(5);
		System.out.println("查询数据库： " + userSearch);
		commonDao.query(userSearch, users -> {
			System.out.println("查询结果result: " + users);
		});
		
	}

	private static AsyncDaoFactory daoFactory = null;

	private static AsyncDaoFactory asyncDaoFactory() {
		if (daoFactory != null) {
			return daoFactory;
		}
		final String username = "root";
		final String host = "10.100.216.147";
		final int port = 3306;
		final String password = "UJ9FeAm3Yc@#E%IH8dLj6guyr5K&u#J3";
		final String database = "test";
		final List<String> basePackages = Arrays.asList("com.tg.example.dao");
		final List<String> mapperLocations = Arrays.asList("/mapper");
		AsyncConfig asyncConfig = new AsyncConfig();
		PoolConfiguration configuration = new PoolConfiguration(username, host, port, password, database);
		asyncConfig.setPoolConfiguration(configuration);
		asyncConfig.setMapperPackages(basePackages);
		asyncConfig.setXmlLocations(mapperLocations);
		try {
			daoFactory = AsyncDaoFactory.build(asyncConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daoFactory;
	}
}
