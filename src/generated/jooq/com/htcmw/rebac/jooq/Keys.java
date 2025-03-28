/*
 * This file is generated by jOOQ.
 */
package com.htcmw.rebac.jooq;


import com.htcmw.rebac.jooq.tables.RelationTuple;
import com.htcmw.rebac.jooq.tables.records.RelationTupleRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<RelationTupleRecord> RELATION_TUPLE_PKEY = Internal.createUniqueKey(RelationTuple.RELATION_TUPLE, DSL.name("relation_tuple_pkey"), new TableField[] { RelationTuple.RELATION_TUPLE.ID }, true);
    public static final UniqueKey<RelationTupleRecord> UC_RELATION_TUPLE_RESOURCE_RELATION_SUBJECT = Internal.createUniqueKey(RelationTuple.RELATION_TUPLE, DSL.name("uc_relation_tuple_resource_relation_subject"), new TableField[] { RelationTuple.RELATION_TUPLE.RESOURCE, RelationTuple.RELATION_TUPLE.RELATION, RelationTuple.RELATION_TUPLE.SUBJECT }, true);
}
