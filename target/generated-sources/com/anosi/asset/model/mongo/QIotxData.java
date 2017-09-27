package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QIotxData is a Querydsl query type for IotxData
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QIotxData extends EntityPathBase<IotxData> {

    private static final long serialVersionUID = -374542028L;

    public static final QIotxData iotxData = new QIotxData("iotxData");

    public final QAbstractDocument _super = new QAbstractDocument(this);

    public final BooleanPath alarm = createBoolean("alarm");

    public final StringPath category = createString("category");

    public final DateTimePath<java.util.Date> closeTime = createDateTime("closeTime", java.util.Date.class);

    public final DateTimePath<java.util.Date> collectTime = createDateTime("collectTime", java.util.Date.class);

    public final StringPath companyName = createString("companyName");

    public final StringPath deviceSN = createString("deviceSN");

    public final StringPath dustSN = createString("dustSN");

    //inherited
    public final NumberPath<java.math.BigInteger> id = _super.id;

    public final StringPath iotxSN = createString("iotxSN");

    public final BooleanPath isAlarm = createBoolean("isAlarm");

    public final EnumPath<IotxData.Level> level = createEnum("level", IotxData.Level.class);

    public final NumberPath<Double> maxVal = createNumber("maxVal", Double.class);

    public final StringPath message = createString("message");

    public final NumberPath<Double> minVal = createNumber("minVal", Double.class);

    public final StringPath sensorSN = createString("sensorSN");

    public final StringPath unit = createString("unit");

    public final NumberPath<Double> val = createNumber("val", Double.class);

    public QIotxData(String variable) {
        super(IotxData.class, forVariable(variable));
    }

    public QIotxData(Path<? extends IotxData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIotxData(PathMetadata metadata) {
        super(IotxData.class, metadata);
    }

}

