package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMessage_Body is a Querydsl query type for Body
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QMessage_Body extends BeanPath<Message.Body> {

    private static final long serialVersionUID = -729744503L;

    public static final QMessage_Body body = new QMessage_Body("body");

    public final StringPath type = createString("type");

    public final SimplePath<Object> val = createSimple("val", Object.class);

    public QMessage_Body(String variable) {
        super(Message.Body.class, forVariable(variable));
    }

    public QMessage_Body(Path<? extends Message.Body> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessage_Body(PathMetadata metadata) {
        super(Message.Body.class, metadata);
    }

}

