package noppes.npcs.db;

public @interface DatabaseColumn {

    String name();

    String default_value() default "";

    Type type();

    boolean isVirtual() default false;

    enum Type{
        INT, TEXT, VARCHAR, ENUM, UUID, SMALLINT, JSON, BOOLEAN
    }
}
