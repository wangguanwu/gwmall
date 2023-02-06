package com.gw.gwmall.cart.config;

//import io.seata.rm.datasource.DataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 * Created by macro on 2019/4/8.
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.gw.gwmall.cart.mapper","com.gw.gwmall.cart.dao"})
public class MyBatisConfig {


    @Autowired
    private MybatisProperties mybatisProperties;



    /* SEATA 分布式事务使用
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        sqlSessionFactoryBean.setMapperLocations(resolveMapperLocations(mybatisProperties));
        sqlSessionFactoryBean.setDataSource(new DataSourceProxy(dataSource));
        return sqlSessionFactoryBean;
    }

    public Resource[] resolveMapperLocations(MybatisProperties mybatisProperties) {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<Resource>();
        if (this.mybatisProperties.getMapperLocations() != null) {
            for (String mapperLocation : mybatisProperties.getMapperLocations()) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }*/

}
