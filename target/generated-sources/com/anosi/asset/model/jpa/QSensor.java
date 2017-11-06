package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSensor is a Querydsl query type for Sensor
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSensor extends EntityPathBase<Sensor> {

    private static final long serialVersionUID = 1626714291L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSensor sensor = new QSensor("sensor");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<AlarmData, QAlarmData> alarmDataList = this.<AlarmData, QAlarmData>createList("alarmDataList", AlarmData.class, QAlarmData.class, PathInits.DIRECT2);

    public final NumberPath<Long> alarmQuantity = createNumber("alarmQuantity", Long.class);

    //inherited
    public final StringPath createBy = _super.createBy;

    //inherited
    public final DateTimePath<java.util.Date> createdDate = _super.createdDate;

    public final QDust dust;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isWorked = createBoolean("isWorked");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.util.Date> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Double> maxVal = createNumber("maxVal", Double.class);

    public final NumberPath<Double> minVal = createNumber("minVal", Double.class);

    public final StringPath name = createString("name");

    public final StringPath parameterDescribe = createString("parameterDescribe");

    public final QSensorCategory sensorCategory;

    public final StringPath serialNo = createString("serialNo");

    public final StringPath unit = createString("unit");

    public QSensor(String variable) {
        this(Sensor.class, forVariable(variable), INITS);
    }

    public QSensor(Path<? extends Sensor> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSensor(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSensor(PathMetadata metadata, PathInits inits) {
        this(Sensor.class, metadata, inits);
    }

    public QSensor(Class<? extends Sensor> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dust = inits.isInitialized("dust") ? new QDust(forProperty("dust"), inits.get("dust")) : null;
        this.sensorCategory = inits.isInitialized("sensorCategory") ? new QSensorCategory(forProperty("sensorCategory")) : null;
    }

}

