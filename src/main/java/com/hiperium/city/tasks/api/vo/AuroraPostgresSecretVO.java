package com.hiperium.city.tasks.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuroraPostgresSecretVO {

    private String host;
    private String port;
    private String dbname;
    private String username;
    private String password;
    private String engine;
    private String dbClusterIdentifier;
}
