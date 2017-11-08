package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIotxStatusPer is a Querydsl query type for IotxStatusPer
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QIotxStatusPer extends EntityPathBase<IotxStatusPer> {

    private static final long serialVersionUID = -1318102616L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIotxStatusPer iotxStatusPer = new QIotxStatusPer("iotxStatusPer");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCompany company;

    public final DateTimePath<java.util.Date> countDate = createDateTime("countDate", java.util.Date.class);

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

    public final NumberPath<Long> offline = createNumber("offline", Long.class);

    public final NumberPath<Long> online = createNumber("online", Long.class);

    public QIotxStatusPer(String variable) {
        this(IotxStatusPer.class, forVariable(variable), INITS);
    }

    public QIotxStatusPer(Path<? extends IotxStatusPer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIotxStatusPer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIotxStatusPer(PathMetadata metadata, PathInits inits) {
        this(IotxStatusPer.class, metadata, inits);
    }

    public QIotxStatusPer(Class<? extends IotxStatusPer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new QCompany(forProperty("company")) : null;
    }

}

