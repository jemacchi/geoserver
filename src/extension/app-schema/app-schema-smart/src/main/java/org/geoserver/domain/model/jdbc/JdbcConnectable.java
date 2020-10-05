package org.geoserver.domain.model.jdbc;

import java.sql.Connection;

public interface JdbcConnectable {

    public Connection getConnection();
}
