package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoleFunctionBtn is a Querydsl query type for RoleFunctionBtn
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRoleFunctionBtn extends EntityPathBase<RoleFunctionBtn> {

    private static final long serialVersionUID = -144543691L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoleFunctionBtn roleFunctionBtn = new QRoleFunctionBtn("roleFunctionBtn");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath btnId = createString("btnId");

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

    public final ListPath<Privilege, QPrivilege> privilegeList = this.<Privilege, QPrivilege>createList("privilegeList", Privilege.class, QPrivilege.class, PathInits.DIRECT2);

    public final QRoleFunction roleFunction;

    public QRoleFunctionBtn(String variable) {
        this(RoleFunctionBtn.class, forVariable(variable), INITS);
    }

    public QRoleFunctionBtn(Path<? extends RoleFunctionBtn> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoleFunctionBtn(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoleFunctionBtn(PathMetadata metadata, PathInits inits) {
        this(RoleFunctionBtn.class, metadata, inits);
    }

    public QRoleFunctionBtn(Class<? extends RoleFunctionBtn> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.roleFunction = inits.isInitialized("roleFunction") ? new QRoleFunction(forProperty("roleFunction"), inits.get("roleFunction")) : null;
    }

}

