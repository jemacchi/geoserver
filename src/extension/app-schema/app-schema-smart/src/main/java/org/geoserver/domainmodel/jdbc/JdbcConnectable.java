package org.geoserver.domainmodel.jdbc;

import java.sql.Connection;

public interface JdbcConnectable {
	
	public Connection getConnection();
	
}
