package org.geoserver.appschema.smart.metadata;

/**
 * Configuration class that determines the type of DataStoreMetadata that the DataStoreMetadataFactory 
 * will build.
 * 
 * @author Jose Macchi - Geosolutions
 *
 */
public abstract class DataStoreMetadataConfig {

    public abstract String getType();
}
