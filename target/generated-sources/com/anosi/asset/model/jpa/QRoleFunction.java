package com.anosi.asset.model.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoleFunction is a Querydsl query type for RoleFunction
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRoleFunction extends EntityPathBase<RoleFunction> {

    private static final long serialVersionUID = -589659993L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoleFunction roleFunction = new QRoleFunction("roleFunction");

    public final QBaseEntity _super = new QBaseEntity(this);

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

    public final QRoleFunction parentRoleFunction;

    public final ListPath<Privilege, QPrivilege> privilegeList = this.<Privilege, QPrivilege>createList("privilegeList", Privilege.class, QPrivilege.class, PathInits.DIRECT2);

    public final ListPath<RoleFunctionBtn, QRoleFunctionBtn> roleFunctionBtnList = this.<RoleFunctionBtn, QRoleFunctionBtn>createList("roleFunctionBtnList", RoleFunctionBtn.class, QRoleFunctionBtn.class, PathInits.DIRECT2);

    public final ListPath<RoleFunctionGroup, QRoleFunctionGroup> roleFunctionGroupList = this.<RoleFunctionGroup, QRoleFunctionGroup>createList("roleFunctionGroupList", RoleFunctionGroup.class, QRoleFunctionGroup.class, PathInits.DIRECT2);

    public final StringPath roleFunctionPageId = createString("roleFunctionPageId");

    public final ListPath<RoleFunction, QRoleFunction> subRoleFunction = this.<RoleFunction, QRoleFunction>createList("subRoleFunction", RoleFunction.class, QRoleFunction.class, PathInits.DIRECT2);

    public QRoleFunction(String variable) {
        this(RoleFunction.class, forVariable(variable), INITS);
    }

    public QRoleFunction(Path<? extends RoleFunction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoleFunction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoleFunction(PathMetadata metadata, PathInits inits) {
        this(RoleFunction.class, metadata, inits);
    }

    public QRoleFunction(Class<? extends RoleFunction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentRoleFunction = inits.isInitialized("parentRoleFunction") ? new QRoleFunction(forProperty("parentRoleFunction"), inits.get("parentRoleFunction")) : null;
    }

}

