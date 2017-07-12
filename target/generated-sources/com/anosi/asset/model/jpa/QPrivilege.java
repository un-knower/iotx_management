package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPrivilege is a Querydsl query type for Privilege
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPrivilege extends EntityPathBase<Privilege> {

    private static final long serialVersionUID = -718714088L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPrivilege privilege = new QPrivilege("privilege");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QAccount account;

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

    public final QRoleFunction roleFunction;

    public final ListPath<RoleFunctionBtn, QRoleFunctionBtn> roleFunctionBtnList = this.<RoleFunctionBtn, QRoleFunctionBtn>createList("roleFunctionBtnList", RoleFunctionBtn.class, QRoleFunctionBtn.class, PathInits.DIRECT2);

    public QPrivilege(String variable) {
        this(Privilege.class, forVariable(variable), INITS);
    }

    public QPrivilege(Path<? extends Privilege> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPrivilege(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPrivilege(PathMetadata metadata, PathInits inits) {
        this(Privilege.class, metadata, inits);
    }

    public QPrivilege(Class<? extends Privilege> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
        this.roleFunction = inits.isInitialized("roleFunction") ? new QRoleFunction(forProperty("roleFunction"), inits.get("roleFunction")) : null;
    }

}

