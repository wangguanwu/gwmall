package com.gw.gwmall.ordercurrent.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan({"com.gw.gwmall.ordercurrent.mapper","com.gw.gwmall.ordercurrent.dao"})
public class MyBatisConfig {

    /*@Autowired
    private MybatisProperties mybatisProperties;

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
