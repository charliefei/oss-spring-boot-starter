package com.feirui.oss.manage.mysql.mapper;

import com.feirui.oss.sdk.domain.entity.DiskFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiskFileMapper {

    int insert(DiskFile diskFile);

    int insertBatch(@Param("list") List<DiskFile> diskFileList);

    int deleteById(@Param("id") String id);

    int deleteBatchByIds(@Param("list") List<String> idList);

    DiskFile selectById(@Param("id") String id);

    List<DiskFile> listByIds(@Param("list") List<String> idList);

    long countTotalSize(@Param("companyCode") String companyCode);

}
