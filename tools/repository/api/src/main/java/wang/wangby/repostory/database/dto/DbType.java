package wang.wangby.repostory.database.dto;

public enum DbType {
    mysql("com.mysql.jdbc.Driver"), derby("org.apache.derby.jdbc.EmbeddedDriver");
    String driverClass;
    DbType(String driverClass){
        this.driverClass=driverClass;
    }

    public String getDriverClassName() {
       return driverClass;
    }
}
