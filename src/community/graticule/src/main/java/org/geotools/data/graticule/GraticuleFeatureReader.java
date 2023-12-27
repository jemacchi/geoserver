/* (c) 2023 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geotools.data.graticule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.geotools.api.data.Query;
import org.geotools.api.data.SimpleFeatureReader;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.graticule.gridsupport.LineFeatureBuilder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.ortholine.LineOrientation;
import org.geotools.grid.ortholine.OrthoLineBuilder;
import org.geotools.grid.ortholine.OrthoLineDef;

public class GraticuleFeatureReader implements SimpleFeatureReader {
    private final List<Double> steps;
    private SimpleFeatureSource currentGrid;
    private SimpleFeatureIterator delegate;
    SimpleFeatureType schema;
    Query query;
    int level = 0;

    public GraticuleFeatureReader(GraticuleFeatureSource fs, Query query) throws IOException {
        this.query = query;
        this.steps = fs.getSteps();
        schema = fs.getSchema();
        currentGrid = buildGrid(fs.getBounds());
        delegate = currentGrid.getFeatures(query).features();
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return schema;
    }

    public SimpleFeatureIterator getDelegate() {
        return delegate;
    }

    @Override
    public SimpleFeature next() throws IllegalArgumentException, NoSuchElementException {
        SimpleFeature next = delegate.next();
        return next;
    }

    @Override
    public boolean hasNext() throws IOException {
        return delegate.hasNext();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    private SimpleFeatureSource buildGrid(ReferencedEnvelope box) {
        List<SimpleFeature> lines = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);

            List<OrthoLineDef> lineDefs = new ArrayList<>();
            // vertical (longitude) lines
            lineDefs.add(new OrthoLineDef(LineOrientation.VERTICAL, i, step));
            // horizontal (latitude) lines
            lineDefs.add(new OrthoLineDef(LineOrientation.HORIZONTAL, i, step));

            final ListFeatureCollection fc = new ListFeatureCollection(schema);
            OrthoLineBuilder lineBuilder = new OrthoLineBuilder(box);
            LineFeatureBuilder gridBuilder = new LineFeatureBuilder(schema);
            lineBuilder.buildGrid(lineDefs, gridBuilder, -1, fc);
            lines.addAll(DataUtilities.list(fc));
        }

        return DataUtilities.source(new ListFeatureCollection(schema, lines));
    }
}
