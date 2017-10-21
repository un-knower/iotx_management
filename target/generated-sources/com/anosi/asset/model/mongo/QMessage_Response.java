package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMessage_Response is a Querydsl query type for Response
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QMessage_Response extends BeanPath<Message.Response> {

    private static final long serialVersionUID = -210203992L;

    public static final QMessage_Response response = new QMessage_Response("response");

    public final StringPath reason = createString("reason");

    public final BooleanPath status = createBoolean("status");

    public QMessage_Response(String variable) {
        super(Message.Response.class, forVariable(variable));
    }

    public QMessage_Response(Path<? extends Message.Response> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessage_Response(PathMetadata metadata) {
        super(Message.Response.class, metadata);
    }

}

