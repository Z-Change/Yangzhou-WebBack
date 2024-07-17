
package whut.pilipili.mall;

import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 
 */
@MapperScan("whut.pilipili.mall.dao")
@SpringBootApplication
@EnableNacosDiscovery
public class PilipiliMallAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(PilipiliMallAPIApplication.class, args);
    }

}
