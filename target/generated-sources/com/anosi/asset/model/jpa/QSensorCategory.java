package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSensorCategory is a Querydsl query type for SensorCategory
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSensorCategory extends EntityPathBase<SensorCategory> {

    private static final long serialVersionUID = 1526492881L;

    public static final QSensorCategory sensorCategory = new QSensorCategory("sensorCategory");

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

    public final ListPath<Sensor, QSensor> sensorList = this.<Sensor, QSensor>createList("sensorList", Sensor.class, QSensor.class, PathInits.DIRECT2);

    public QSensorCategory(String variable) {
        super(SensorCategory.class, forVariable(variable));
    }

    public QSensorCategory(Path<? extends SensorCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSensorCategory(PathMetadata metadata) {
        super(SensorCategory.class, metadata);
    }

}

