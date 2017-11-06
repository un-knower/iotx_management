package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlarmData is a Querydsl query type for AlarmData
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAlarmData extends EntityPathBase<AlarmData> {

    private static final long serialVersionUID = 134259202L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarmData alarmData = new QAlarmData("alarmData");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DateTimePath<java.util.Date> closeTime = createDateTime("closeTime", java.util.Date.class);

    public final DateTimePath<java.util.Date> collectTime = createDateTime("collectTime", java.util.Date.class);

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

    public final EnumPath<AlarmData.Level> level = createEnum("level", AlarmData.Level.class);

    public final QSensor sensor;

    public final NumberPath<Double> val = createNumber("val", Double.class);

    public QAlarmData(String variable) {
        this(AlarmData.class, forVariable(variable), INITS);
    }

    public QAlarmData(Path<? extends AlarmData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlarmData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlarmData(PathMetadata metadata, PathInits inits) {
        this(AlarmData.class, metadata, inits);
    }

    public QAlarmData(Class<? extends AlarmData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sensor = inits.isInitialized("sensor") ? new QSensor(forProperty("sensor"), inits.get("sensor")) : null;
    }

}

