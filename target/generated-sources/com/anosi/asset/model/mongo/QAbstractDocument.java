package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAbstractDocument is a Querydsl query type for AbstractDocument
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QAbstractDocument extends BeanPath<AbstractDocument> {

    private static final long serialVersionUID = 818670493L;

    public static final QAbstractDocument abstractDocument = new QAbstractDocument("abstractDocument");

    public final NumberPath<java.math.BigInteger> id = createNumber("id", java.math.BigInteger.class);

    public final StringPath stringId = createString("stringId");

    public QAbstractDocument(String variable) {
        super(AbstractDocument.class, forVariable(variable));
    }

    public QAbstractDocument(Path<? extends AbstractDocument> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractDocument(PathMetadata metadata) {
        super(AbstractDocument.class, metadata);
    }

}

