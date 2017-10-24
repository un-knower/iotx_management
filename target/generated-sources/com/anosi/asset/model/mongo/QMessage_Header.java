package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMessage_Header is a Querydsl query type for Header
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QMessage_Header extends BeanPath<Message.Header> {

    private static final long serialVersionUID = -1042364748L;

    public static final QMessage_Header header = new QMessage_Header("header");

    public final StringPath serialNo = createString("serialNo");

    public final StringPath type = createString("type");

    public final StringPath uniqueId = createString("uniqueId");

    public QMessage_Header(String variable) {
        super(Message.Header.class, forVariable(variable));
    }

    public QMessage_Header(Path<? extends Message.Header> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessage_Header(PathMetadata metadata) {
        super(Message.Header.class, metadata);
    }

}

