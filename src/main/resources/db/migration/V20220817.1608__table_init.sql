DROP TABLE IF EXISTS "employee" CASCADE;
CREATE TABLE IF NOT EXISTS "employee" (
	"id" int8 NOT NULL,
	"name" VARCHAR ( 50 ),
	"age" int4,
	"org_ids" jsonb,
	"create_at" TIMESTAMP ( 6 ) DEFAULT ( 'now' :: TEXT ) :: TIMESTAMP ( 6 ) WITHOUT TIME ZONE,
	"update_at" TIMESTAMP ( 6 ),
	"create_by" VARCHAR ( 50 ),
	"update_by" VARCHAR ( 50 ),
	"deleted" int2,
	"version" int4,
	PRIMARY KEY ( ID )
);
COMMENT ON TABLE "employee" IS '院外核酸审核表';
COMMENT ON COLUMN "employee"."id" IS '主键，雪花id';
COMMENT ON COLUMN "employee"."name" IS '名称';
COMMENT ON COLUMN "employee"."age" IS '年龄';
COMMENT ON COLUMN "employee"."org_ids" IS '组织编码，参考organization';
COMMENT ON COLUMN "employee"."create_at" IS '创建时间';
COMMENT ON COLUMN "employee"."update_at" IS '更新时间';
COMMENT ON COLUMN "employee"."create_by" IS '创建人';
COMMENT ON COLUMN "employee"."update_by" IS '更新人';
COMMENT ON COLUMN "employee"."deleted" IS '删除状态，-1：已删除；0：未删除';
COMMENT ON COLUMN "employee"."version" IS '乐观锁';


DROP TABLE IF EXISTS "sys_user" CASCADE;
CREATE TABLE IF NOT EXISTS "sys_user" (
	"id" int8 NOT NULL,
	"name" varchar ( 50 ),
	"code" varchar ( 50 ),
	"password" varchar ( 255 ),
	"token" varchar ( 255 ),
	"status" int2,
	"role_ids" jsonb,
	"create_at" TIMESTAMP ( 6 ) DEFAULT ( 'now' :: TEXT ) :: TIMESTAMP ( 6 ) WITHOUT TIME ZONE,
	"update_at" TIMESTAMP ( 6 ),
	"create_by" VARCHAR ( 50 ),
	"update_by" VARCHAR ( 50 ),
	"deleted" int2,
	"version" int4,
	PRIMARY KEY ( ID )
);
COMMENT ON TABLE "sys_user" IS '院外核酸审核表';
COMMENT ON COLUMN "sys_user"."id" IS '主键，雪花id';
COMMENT ON COLUMN "sys_user"."name" IS '名称';
COMMENT ON COLUMN "sys_user"."code" IS '编码，不允许特殊字符';
COMMENT ON COLUMN "sys_user"."password" IS '密码';
COMMENT ON COLUMN "sys_user"."token" IS '登录token';
COMMENT ON COLUMN "sys_user"."status" IS '账户状态，1:禁用，2:启用';
COMMENT ON COLUMN "sys_user"."role_ids" IS '角色，1:超级管理员，2:管理员';
COMMENT ON COLUMN "sys_user"."create_at" IS '创建时间';
COMMENT ON COLUMN "sys_user"."update_at" IS '更新时间';
COMMENT ON COLUMN "sys_user"."create_by" IS '创建人';
COMMENT ON COLUMN "sys_user"."update_by" IS '更新人';
COMMENT ON COLUMN "sys_user"."deleted" IS '删除状态，-1：已删除；0：未删除';
COMMENT ON COLUMN "sys_user"."version" IS '乐观锁';
