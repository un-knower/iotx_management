package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSensorPort is a Querydsl query type for SensorPort
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSensorPort extends EntityPathBase<SensorPort> {

    private static final long serialVersionUID = -1734464556L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSensorPort sensorPort = new QSensorPort("sensorPort");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final StringPath createBy = _super.createBy;

    //inherited
    public final DateTimePath<java.util.Date> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.util.Date> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final QSensorInterface sensorInterface;

    public final ListPath<Sensor, QSensor> sensorList = this.<Sensor, QSensor>createList("sensorList", Sensor.class, QSensor.class, PathInits.DIRECT2);

    public QSensorPort(String variable) {
        this(SensorPort.class, forVariable(variable), INITS);
    }

    public QSensorPort(Path<? extends SensorPort> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSensorPort(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSensorPort(PathMetadata metadata, PathInits inits) {
        this(SensorPort.class, metadata, inits);
    }

    public QSensorPort(Class<? extends SensorPort> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sensorInterface = inits.isInitialized("sensorInterface") ? new QSensorInterface(forProperty("sensorInterface")) : null;
    }

}

