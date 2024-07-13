
package whut.pilipili.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 
 */
@MapperScan("whut.pilipili.mall.dao")
@SpringBootApplication
public class PilipiliMallAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(PilipiliMallAPIApplication.class, args);
    }

}
