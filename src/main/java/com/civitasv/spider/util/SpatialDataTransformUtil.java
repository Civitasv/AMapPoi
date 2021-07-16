package com.civitasv.spider.util;

import org.geotools.data.*;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 空间数据转换工具类
 * <p>
 * csv shp GeoJSON 三种格式相互转换
 */
public class SpatialDataTransformUtil {

    /**
     * 解析 GeoJSON 格式字符串为featureCollection
     *
     * @param geojsonStr GeoJSON 格式字符串
     * @return 若可以解析，则返回 FeatureCollection，否则返回 null
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> geojsonStr2FeatureCollection(String geojsonStr) {
        try (InputStream in = new ByteArrayInputStream(geojsonStr.getBytes())) {
            GeometryJSON geometryJSON = new GeometryJSON();
            FeatureJSON featureJSON = new FeatureJSON(geometryJSON);
            return featureJSON.readFeatureCollection(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析 FeatureCollection 为 GeoJSON 字符串
     *
     * @param featureCollection features
     * @return 若可以解析，则返回 GeoJSON 字符串，否则返回 null
     */
    public static String featureCollection2GeoJson(FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
        FeatureJSON featureJSON = new FeatureJSON();
        try (FeatureIterator<SimpleFeature> featureIterator = featureCollection.features();
             StringWriter writer = new StringWriter()) {
            writer.write("{\"type\":\"FeatureCollection\",\"crs\":");
            featureJSON.writeCRS(featureCollection.getSchema().getCoordinateReferenceSystem(), writer);
            writer.write(",");
            writer.write("\"features\":");
            writer.write("[");
            while (featureIterator.hasNext()) {
                SimpleFeature feature = featureIterator.next();
                featureJSON.writeFeature(feature, writer);
                if (featureIterator.hasNext())
                    writer.write(",");
            }
            writer.write("]");
            writer.write("}");
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * 保存features为shp格式
     *
     * @param features 要素类
     * @param TYPE     要素类型
     * @param shpPath  shp保存路径
     * @return 是否保存成功
     */
    public static boolean saveFeaturesToShp(List<SimpleFeature> features, SimpleFeatureType TYPE, String shpPath) {
        try {
            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            File shpFile = new File(shpPath);
            Map<String, Serializable> params = new HashMap<>();
            params.put("url", shpFile.toURI().toURL());
            params.put("create spatial index", Boolean.TRUE);

            ShapefileDataStore newDataStore =
                    (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
            newDataStore.setCharset(StandardCharsets.UTF_8);

            newDataStore.createSchema(TYPE);

            Transaction transaction = new DefaultTransaction("create");
            String typeName = newDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
                SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
                featureStore.setTransaction(transaction);
                try {
                    featureStore.addFeatures(collection);
                    boolean success = FileUtil.generateCpgFile(shpFile.toString(), StandardCharsets.UTF_8);
                    if (success)
                        transaction.commit();
                    else transaction.rollback();
                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                } finally {
                    transaction.close();
                }
            } else {
                System.out.println(typeName + " 不支持读或写！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存 GeoJSON 格式字符串为 shapefile
     *
     * @param geojsonStr GeoJSON 字符串
     * @param shpPath    shapefile 保存路径
     * @return 格式转换是否成功
     */
    public static boolean transformGeoJsonStrToShp(String geojsonStr, String shpPath) {
        try (InputStream in = new ByteArrayInputStream(geojsonStr.getBytes())) {
            // open geojson
            GeometryJSON geometryJSON = new GeometryJSON();
            FeatureJSON featureJSON = new FeatureJSON(geometryJSON);
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureJSON.readFeatureCollection(in);
            // convert schema for shapefile
            SimpleFeatureType schema = features.getSchema();
            GeometryDescriptor geom = schema.getGeometryDescriptor();
            // GeoJSON 文件属性
            List<AttributeDescriptor> attributes = schema.getAttributeDescriptors();
            // GeoJSON 文件空间类型（必须在第一个）
            GeometryType geomType = null;
            List<AttributeDescriptor> attribs = new ArrayList<>();
            for (AttributeDescriptor attrib : attributes) {
                AttributeType type = attrib.getType();
                if (type instanceof GeometryType) {
                    geomType = (GeometryType) type;
                } else {
                    attribs.add(attrib);
                }
            }
            if (geomType == null)
                return false;

            // 使用geomType创建gt
            GeometryTypeImpl gt = new GeometryTypeImpl(new NameImpl("the_geom"), geomType.getBinding(),
                    geom.getCoordinateReferenceSystem() == null ? DefaultGeographicCRS.WGS84 : geom.getCoordinateReferenceSystem(), // 用户未指定则默认为wgs84
                    geomType.isIdentified(), geomType.isAbstract(), geomType.getRestrictions(),
                    geomType.getSuper(), geomType.getDescription());

            // 创建识别符
            GeometryDescriptor geomDesc = new GeometryDescriptorImpl(gt, new NameImpl("the_geom"), geom.getMinOccurs(),
                    geom.getMaxOccurs(), geom.isNillable(), geom.getDefaultValue());

            // the_geom 属性必须在第一个
            attribs.add(0, geomDesc);

            SimpleFeatureType outSchema = new SimpleFeatureTypeImpl(schema.getName(), attribs, geomDesc, schema.isAbstract(),
                    schema.getRestrictions(), schema.getSuper(), schema.getDescription());
            List<SimpleFeature> outFeatures = new ArrayList<>();
            try (FeatureIterator<SimpleFeature> features2 = features.features()) {
                while (features2.hasNext()) {
                    SimpleFeature f = features2.next();
                    SimpleFeature reType = DataUtilities.reType(outSchema, f, true);

                    reType.setAttribute(outSchema.getGeometryDescriptor().getName(),
                            f.getAttribute(schema.getGeometryDescriptor().getName()));

                    outFeatures.add(reType);
                }
            }
            return saveFeaturesToShp(outFeatures, outSchema, shpPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * GeoJSON to shapefile
     *
     * @param geojsonPath   GeoJSON 文件路径
     * @param shapefilePath shapefile 文件路径
     * @return 转换是否成功
     */
    public static boolean transformGeoJsonToShp(String geojsonPath, String shapefilePath) {
        try (InputStream in = new FileInputStream(geojsonPath)) {
            // open geojson
            GeometryJSON gjson = new GeometryJSON();
            FeatureJSON fjson = new FeatureJSON(gjson);
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = fjson.readFeatureCollection(in);
            // convert schema for shapefile
            SimpleFeatureType schema = features.getSchema();
            GeometryDescriptor geom = schema.getGeometryDescriptor();
            // geojson文件属性
            List<AttributeDescriptor> attributes = schema.getAttributeDescriptors();
            // geojson文件空间类型（必须在第一个）
            GeometryType geomType = null;
            List<AttributeDescriptor> attribs = new ArrayList<>();
            for (AttributeDescriptor attrib : attributes) {
                AttributeType type = attrib.getType();
                if (type instanceof GeometryType) {
                    geomType = (GeometryType) type;
                } else {
                    attribs.add(attrib);
                }
            }
            if (geomType == null)
                return false;

            // 使用geomType创建gt
            GeometryTypeImpl gt = new GeometryTypeImpl(new NameImpl("the_geom"), geomType.getBinding(),
                    geom.getCoordinateReferenceSystem() == null ? DefaultGeographicCRS.WGS84 : geom.getCoordinateReferenceSystem(), // 用户未指定则默认为wgs84
                    geomType.isIdentified(), geomType.isAbstract(), geomType.getRestrictions(),
                    geomType.getSuper(), geomType.getDescription());

            // 创建识别符
            GeometryDescriptor geomDesc = new GeometryDescriptorImpl(gt, new NameImpl("the_geom"), geom.getMinOccurs(),
                    geom.getMaxOccurs(), geom.isNillable(), geom.getDefaultValue());

            // the_geom 属性必须在第一个
            attribs.add(0, geomDesc);

            SimpleFeatureType outSchema = new SimpleFeatureTypeImpl(schema.getName(), attribs, geomDesc, schema.isAbstract(),
                    schema.getRestrictions(), schema.getSuper(), schema.getDescription());
            List<SimpleFeature> outFeatures = new ArrayList<>();
            try (FeatureIterator<SimpleFeature> features2 = features.features()) {
                while (features2.hasNext()) {
                    SimpleFeature f = features2.next();
                    SimpleFeature reType = DataUtilities.reType(outSchema, f, true);

                    reType.setAttribute(outSchema.getGeometryDescriptor().getName(),
                            f.getAttribute(schema.getGeometryDescriptor().getName()));

                    outFeatures.add(reType);
                }
            }
            return saveFeaturesToShp(outFeatures, outSchema, shapefilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * shapefile 转换为 GeoJSON 格式字符串
     *
     * @param shpPath     shapefile 文件路径
     * @param geojsonPath GeoJSON 文件路径
     * @return 转换是否成功
     */
    public static boolean transformShpToGeoJSON(String shpPath, String geojsonPath) {
        try {
            File file = new File(shpPath);
            FileDataStore myData = FileDataStoreFinder.getDataStore(file);
            // 设置解码方式
            ((ShapefileDataStore) myData).setCharset(StandardCharsets.UTF_8);
            SimpleFeatureSource source = myData.getFeatureSource();
            SimpleFeatureType schema = source.getSchema();
            Query query = new Query(schema.getTypeName());

            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);
            FeatureJSON featureJSON = new FeatureJSON();
            File geojsonFile = new File(geojsonPath);
            try (FeatureIterator<SimpleFeature> featureIterator = collection.features();
                 StringWriter writer = new StringWriter();
                 BufferedWriter buffer = new BufferedWriter(Files.newBufferedWriter(geojsonFile.toPath(), StandardCharsets.UTF_8))) {
                writer.write("{\"type\":\"FeatureCollection\",\"crs\":");
                featureJSON.writeCRS(schema.getCoordinateReferenceSystem(), writer);
                writer.write(",");
                writer.write("\"features\":");
                writer.write("[");
                while (featureIterator.hasNext()) {
                    SimpleFeature feature = featureIterator.next();
                    featureJSON.writeFeature(feature, writer);
                    if (featureIterator.hasNext())
                        writer.write(",");
                }
                writer.write("]");
                writer.write("}");
                buffer.write(writer.toString());
                return true;
            } catch (IOException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * shapefile 转换为 CSV
     * @param shpPath shapefile 文件路径
     * @param csvPath CSV 文件路径
     * @return 转换是否成功
     */
    public static boolean transformShpToCsv(String shpPath, String csvPath) {
        try {
            File file = new File(shpPath);
            FileDataStore myData = FileDataStoreFinder.getDataStore(file);
            // 设置解码方式
            ((ShapefileDataStore) myData).setCharset(StandardCharsets.UTF_8);
            SimpleFeatureSource source = myData.getFeatureSource();
            SimpleFeatureType schema = source.getSchema();
            Query query = new Query(schema.getTypeName());

            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);
            File csvFile = new File(csvPath);
            try (FeatureIterator<SimpleFeature> featureIterator = collection.features();
                 StringWriter writer = new StringWriter();
                 BufferedWriter buffer = new BufferedWriter(Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8))) {
                byte index = 0;
                writer.write('\ufeff');
                while (featureIterator.hasNext()) {
                    SimpleFeature feature = featureIterator.next();
                    if (index == 0) {
                        for (Property p : feature.getProperties()) {
                            writer.write(p.getName() + ",");
                        }
                        writer.write("\r\n");
                        index = 1;
                    }
                    for (Property p : feature.getProperties()) {
                        writer.write("\"" + feature.getAttribute(p.getName()).toString() + "\",");
                    }
                    writer.write("\r\n");
                }
                buffer.write(writer.toString());
                return true;
            } catch (IOException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}
