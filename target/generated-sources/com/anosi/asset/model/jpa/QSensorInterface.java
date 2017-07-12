package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSensorInterface is a Querydsl query type for SensorInterface
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSensorInterface extends EntityPathBase<SensorInterface> {

    private static final long serialVersionUID = -986581562L;

    public static final QSensorInterface sensorInterface = new QSensorInterface("sensorInterface");

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

    public final ListPath<SensorPort, QSensorPort> sensorPortList = this.<SensorPort, QSensorPort>createList("sensorPortList", SensorPort.class, QSensorPort.class, PathInits.DIRECT2);

    public QSensorInterface(String variable) {
        super(SensorInterface.class, forVariable(variable));
    }

    public QSensorInterface(Path<? extends SensorInterface> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSensorInterface(PathMetadata metadata) {
        super(SensorInterface.class, metadata);
    }

}

