package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileMetaData is a Querydsl query type for FileMetaData
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFileMetaData extends EntityPathBase<FileMetaData> {

    private static final long serialVersionUID = -1115933397L;

    public static final QFileMetaData fileMetaData = new QFileMetaData("fileMetaData");

    public final QAbstractDocument _super = new QAbstractDocument(this);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    //inherited
    public final NumberPath<java.math.BigInteger> id = _super.id;

    public final StringPath identification = createString("identification");

    public final NumberPath<java.math.BigInteger> objectId = createNumber("objectId", java.math.BigInteger.class);

    //inherited
    public final StringPath stringId = _super.stringId;

    public final StringPath uploader = createString("uploader");

    public final DateTimePath<java.util.Date> uploadTime = createDateTime("uploadTime", java.util.Date.class);

    public QFileMetaData(String variable) {
        super(FileMetaData.class, forVariable(variable));
    }

    public QFileMetaData(Path<? extends FileMetaData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileMetaData(PathMetadata metadata) {
        super(FileMetaData.class, metadata);
    }

}

