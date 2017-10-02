package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDust is a Querydsl query type for Dust
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDust extends EntityPathBase<Dust> {

    private static final long serialVersionUID = 144277995L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDust dust = new QDust("dust");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath configId = createString("configId");

    //inherited
    public final StringPath createBy = _super.createBy;

    //inherited
    public final DateTimePath<java.util.Date> createdDate = _super.createdDate;

    public final QDevice device;

    public final NumberPath<Double> frequency = createNumber("frequency", Double.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final QIotx iotx;

    public final BooleanPath isWorked = createBoolean("isWorked");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.util.Date> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final StringPath powerType = createString("powerType");

    public final ListPath<Sensor, QSensor> sensorList = this.<Sensor, QSensor>createList("sensorList", Sensor.class, QSensor.class, PathInits.DIRECT2);

    public final NumberPath<Long> sensorQuantity = createNumber("sensorQuantity", Long.class);

    public final StringPath serialNo = createString("serialNo");

    public final StringPath type = createString("type");

    public QDust(String variable) {
        this(Dust.class, forVariable(variable), INITS);
    }

    public QDust(Path<? extends Dust> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDust(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDust(PathMetadata metadata, PathInits inits) {
        this(Dust.class, metadata, inits);
    }

    public QDust(Class<? extends Dust> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.device = inits.isInitialized("device") ? new QDevice(forProperty("device"), inits.get("device")) : null;
        this.iotx = inits.isInitialized("iotx") ? new QIotx(forProperty("iotx"), inits.get("iotx")) : null;
    }

}

