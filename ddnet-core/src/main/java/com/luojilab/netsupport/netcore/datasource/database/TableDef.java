package com.luojilab.netsupport.netcore.datasource.database;

/**
 * Created by liushuo on 16/1/18.
 */
public interface TableDef {

    public interface TableTest {
        public static final String DB_TABLE = "test";

        public interface Column extends BaseColumn {
            String COLUMN_PARENT_ID = "parent_id";
            String COLUMN_CHILD_ID = "child_id";

        }
    }

    public class TableNosql {
        public static final String DB_TABLE = "compatibleNosql";

        public interface Column extends BaseColumn {
            String COLUMN_CONTENT_TYPE = "type";
            String COLUMN_CONTENT = "content";
        }
    }

}

interface BaseColumn {
    String COLUMN_ID = "_id";
}
