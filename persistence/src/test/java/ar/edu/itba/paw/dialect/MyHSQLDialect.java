package ar.edu.itba.paw.dialect;

import org.hibernate.dialect.HSQLDialect;
import java.sql.Types;

/** Soluciona el problema que el picture byte[] data, era mas grande que 255 bytes que es el default de HSQL si no se
 ** le especifica el length en @Column, solucion tomada de:
 ** https://stackoverflow.com/questions/12876551/hibernate-4-1-with-hsqldb-gives-data-exception-string-data-right-truncation
 **/
public class MyHSQLDialect extends HSQLDialect {
    public MyHSQLDialect() {
        super();
        registerColumnType(Types.CLOB, "clob");
        registerColumnType(Types.BLOB, "blob");
    }
}

