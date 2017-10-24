package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProvince is a Querydsl query type for Province
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QProvince extends EntityPathBase<Province> {

    private static final long serialVersionUID = 1949707753L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProvince province = new QProvince("province");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<City, QCity> cityList = this.<City, QCity>createList("cityList", City.class, QCity.class, PathInits.DIRECT2);

    public final QCountry country;

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

    public QProvince(String variable) {
        this(Province.class, forVariable(variable), INITS);
    }

    public QProvince(Path<? extends Province> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProvince(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProvince(PathMetadata metadata, PathInits inits) {
        this(Province.class, metadata, inits);
    }

    public QProvince(Class<? extends Province> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.country = inits.isInitialized("country") ? new QCountry(forProperty("country")) : null;
    }

}

