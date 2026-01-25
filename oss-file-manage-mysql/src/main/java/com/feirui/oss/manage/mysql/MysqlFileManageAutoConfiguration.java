package com.feirui.oss.manage.mysql;

import com.feirui.oss.manage.mysql.manager.FileBaseManager;
import com.feirui.oss.manage.mysql.manager.impl.FileBaseManagerImpl;
import com.feirui.oss.manage.mysql.mapper.DiskFileMapper;
import com.feirui.oss.sdk.config.CommonFileProperties;
import com.feirui.oss.sdk.factory.FileSdkImplFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ConditionalOnBean({DataSource.class})
public class MysqlFileManageAutoConfiguration {

    @Bean
    @ConditionalOnBean({DiskFileMapper.class, CommonFileProperties.class, FileSdkImplFactory.class})
    public FileBaseManager fileBaseManager(DiskFileMapper diskFileMapper,
                                           CommonFileProperties commonFileProperties,
                                           FileSdkImplFactory fileSdkImplFactory) {
        return new FileBaseManagerImpl(diskFileMapper, commonFileProperties, fileSdkImplFactory);
    }

}
