package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIotx is a Querydsl query type for Iotx
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QIotx extends EntityPathBase<Iotx> {

    private static final long serialVersionUID = 144421219L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIotx iotx = new QIotx("iotx");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> alarmQuantity = createNumber("alarmQuantity", Long.class);

    public final QCompany company;

    public final StringPath cpu = createString("cpu");

    //inherited
    public final StringPath createBy = _super.createBy;

    //inherited
    public final DateTimePath<java.util.Date> createdDate = _super.createdDate;

    public final QDistrict district;

    public final ListPath<Dust, QDust> dustList = this.<Dust, QDust>createList("dustList", Dust.class, QDust.class, PathInits.DIRECT2);

    public final NumberPath<Long> dustQuantity = createNumber("dustQuantity", Long.class);

    public final StringPath hardDisk = createString("hardDisk");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath installLocation = createString("installLocation");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.util.Date> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath mac = createString("mac");

    public final StringPath memory = createString("memory");

    public final EnumPath<Iotx.NetworkCategory> networkCategory = createEnum("networkCategory", Iotx.NetworkCategory.class);

    public final DateTimePath<java.util.Date> openTime = createDateTime("openTime", java.util.Date.class);

    public final NumberPath<Long> sensorQuantity = createNumber("sensorQuantity", Long.class);

    public final StringPath serialNo = createString("serialNo");

    public final EnumPath<Iotx.Status> status = createEnum("status", Iotx.Status.class);

    public final StringPath version = createString("version");

    public QIotx(String variable) {
        this(Iotx.class, forVariable(variable), INITS);
    }

    public QIotx(Path<? extends Iotx> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIotx(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIotx(PathMetadata metadata, PathInits inits) {
        this(Iotx.class, metadata, inits);
    }

    public QIotx(Class<? extends Iotx> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new QCompany(forProperty("company")) : null;
        this.district = inits.isInitialized("district") ? new QDistrict(forProperty("district"), inits.get("district")) : null;
    }

}

