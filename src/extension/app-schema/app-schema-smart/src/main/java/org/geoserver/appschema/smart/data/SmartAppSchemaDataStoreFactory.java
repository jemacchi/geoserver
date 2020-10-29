package org.geoserver.appschema.smart.data;

import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.complex.AppSchemaDataAccessFactory;
import org.geotools.util.KVP;
import org.geotools.util.URLs;
import org.geotools.util.logging.Logging;

/**
 * Smart AppSchema DataStore factory - Work in progress
 * 
 * @author Jose Macchi - GeoSolutions
 *
 */
public class SmartAppSchemaDataStoreFactory implements FileDataStoreFactorySpi {

    static final Logger LOGGER = Logging.getLogger(SmartAppSchemaDataStoreFactory.class);

    public static final Param URLP =
            new Param(
                    "url", URL.class, "url to file", true, null, new KVP(Param.EXT, "xml"));

    /** Optional - uri of the FeatureType's namespace */
    public static final Param NAMESPACEP =
            new Param(
                    "namespace",
                    URI.class,
                    "uri to a the namespace",
                    false,
                    null, // not required
                    new KVP(Param.LEVEL, "advanced"));

    public static final Param DSMETADATATYPE =
            new Param(
                    "metadatatype",
                    String.class,
                    "select datastore metadata type.",
                    true,
                    "jdbc",
                    new KVP(
                            Param.LEVEL,
                            "advanced",
                            Param.OPTIONS,
                            Arrays.asList(new String[] {"jdbc"})));
    
    public String getDisplayName() {
        return "Smart AppSchema";
    }

    public String getDescription() {
        return "Smart AppSchema builder tool";
    }

    public Param[] getParametersInfo() {
        return new Param[] {
            NAMESPACEP,
      		URLP,
            DSMETADATATYPE
        };
    }

    public boolean isAvailable() {
        return true;
    }

    public Map<Key, ?> getImplementationHints() {
        return Collections.emptyMap();
    }

    public DataStore createDataStore(Map<String, Serializable> params) throws IOException {
        URL url = lookup(URLP, params, URL.class);
        URI namespace = lookup(NAMESPACEP, params, URI.class);

        // build the store
        DataStore store = (new AppSchemaDataAccessFactory()).createNewDataStore(params);
        if (namespace != null) {
            //store.setNamespaceURI(namespace.toString());
        }
        return store;
    }

    public DataStore createNewDataStore(Map<String, Serializable> params) throws IOException {
        return createDataStore(params);
    }

    /**
     * Looks up a parameter, if not found it returns the default value, assuming there is one, or
     * null otherwise
     *
     * @param <T>
     */
    <T> T lookup(Param param, Map<String, Serializable> params, Class<T> target)
            throws IOException {
        T result = target.cast(param.lookUp(params));
        if (result == null) {
            result = target.cast(param.getDefaultValue());
        }
        return result;
    }

    @Override
    public boolean canProcess(Map<String, Serializable> params) {
        if (!DataUtilities.canProcess(params, getParametersInfo())) {
            return false; // fail basic param check
        }
        try {
            URL url = (URL) URLP.lookUp(params);
            if (canProcess(url)) {
                return true;
            } else {
                File dir = URLs.urlToFile(url);

                // check for null fileType for backwards compatibility

                // Return false if this is a VPF directory
                if (dir != null && dir.isDirectory()) {
                    String dirPath = dir.getPath();

                    String[] vpfTables = {"LAT", "LHT", "DHT", "lat", "lht", "dht"};

                    for (int itab = 0; itab < vpfTables.length; itab++) {

                        String tabFilename = vpfTables[itab];

                        String pathTab = dirPath.concat(File.separator).concat(tabFilename);

                        if (new File(pathTab).exists()) {
                            return false;
                        }
                    }
                }

                return dir != null
                        && dir.isDirectory();
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean canProcess(URL f) {
        return f != null && (f.getFile().toUpperCase().endsWith("XML") || f.getFile().toUpperCase().endsWith("XSD"));
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {".xml", ".xsd"};
    }

    @Override
    public FileDataStore createDataStore(URL url) throws IOException {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(URLP.key, url);

        boolean isLocal = url.getProtocol().equalsIgnoreCase("file");
        File file = URLs.urlToFile(url);
        if (file != null && file.isDirectory()) {
            return null;
        } else {
            if (isLocal && !file.exists()) {
                return (FileDataStore) createNewDataStore(params);
            } else {
                return (FileDataStore) createDataStore(params);
            }
        }
    }

    @Override
    public String getTypeName(URL url) throws IOException {
        DataStore ds = createDataStore(url);
        String[] names = ds.getTypeNames(); // should be exactly one
        ds.dispose();
        return ((names == null || names.length == 0) ? null : names[0]);
    }
}
