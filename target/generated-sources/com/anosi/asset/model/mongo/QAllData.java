package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAllData is a Querydsl query type for AllData
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAllData extends EntityPathBase<AllData> {

    private static final long serialVersionUID = -1803561813L;

    public static final QAllData allData = new QAllData("allData");

    public final QAbstractDocument _super = new QAbstractDocument(this);

    public final DateTimePath<java.util.Date> collectTime = createDateTime("collectTime", java.util.Date.class);

    //inherited
    public final NumberPath<java.math.BigInteger> id = _super.id;

    //inherited
    public final StringPath stringId = _super.stringId;

    public final StringPath val = createString("val");

    public QAllData(String variable) {
        super(AllData.class, forVariable(variable));
    }

    public QAllData(Path<? extends AllData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAllData(PathMetadata metadata) {
        super(AllData.class, metadata);
    }

}

