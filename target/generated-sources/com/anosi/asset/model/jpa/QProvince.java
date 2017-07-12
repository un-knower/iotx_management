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

    public static final QProvince province = new QProvince("province");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<City, QCity> cityList = this.<City, QCity>createList("cityList", City.class, QCity.class, PathInits.DIRECT2);

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

    public final StringPath pid = createString("pid");

    public QProvince(String variable) {
        super(Province.class, forVariable(variable));
    }

    public QProvince(Path<? extends Province> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProvince(PathMetadata metadata) {
        super(Province.class, metadata);
    }

}

