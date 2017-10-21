package com.anosi.asset.model.mongo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMessage is a Querydsl query type for Message
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMessage extends EntityPathBase<Message> {

    private static final long serialVersionUID = 64008135L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMessage message = new QMessage("message");

    public final QAbstractDocument _super = new QAbstractDocument(this);

    public final QMessage_Body body;

    public final QMessage_Header header;

    //inherited
    public final NumberPath<java.math.BigInteger> id = _super.id;

    public final QMessage_Response response;

    //inherited
    public final StringPath stringId = _super.stringId;

    public final EnumPath<Message.Type> type = createEnum("type", Message.Type.class);

    public QMessage(String variable) {
        this(Message.class, forVariable(variable), INITS);
    }

    public QMessage(Path<? extends Message> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMessage(PathMetadata metadata, PathInits inits) {
        this(Message.class, metadata, inits);
    }

    public QMessage(Class<? extends Message> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.body = inits.isInitialized("body") ? new QMessage_Body(forProperty("body")) : null;
        this.header = inits.isInitialized("header") ? new QMessage_Header(forProperty("header")) : null;
        this.response = inits.isInitialized("response") ? new QMessage_Response(forProperty("response")) : null;
    }

}

