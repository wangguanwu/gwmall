package com.gw.gwmall.promotion.service.impl;

import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.promotion.service.HomePromotionService;
import com.gw.gwmall.promotion.service.ISecKillStaticHtmlService;
import com.gw.gwmall.promotion.service.nginx.SftpUploadService;
import com.jcraft.jsch.ChannelSftp;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Slf4j
@Service
public class SecKillStaticHtmlServiceImpl implements ISecKillStaticHtmlService {

    /**本地存放模板文件目录*/
    @Value("${seckill.templateDir}")
    private String templateDir;

    /**本地存放模板文件名*/
    @Value("${seckill.templateName:seckill.ftl}")
    private String templateName;

    /**本地存放生成的html文件目录*/
    @Value("${seckill.htmlDir}")
    private String htmlDir;

    /**sftp服务器ip地址列表*/
    @Value("#{'${seckill.serverList}'.split(',')}")
    private List<String> nginxServerList;

    /**端口*/
    @Value("${seckill.sftp.port}")
    private int port;

    /**用户名*/
    @Value("${seckill.sftp.userName}")
    private String userName;

    /**密码*/
    @Value("${seckill.sftp.password}")
    private String password;

    /**Nginx存放文件的根目录*/
    @Value("${seckill.sftp.rootPath}")
    private String rootPath;

    @Autowired
    private HomePromotionService homePromotionService;

    @Autowired
    private SftpUploadService sftpUploadService;

    @PostConstruct
    public void init(){
        templateDir = System.getProperty("user.home") + templateDir;
        htmlDir = System.getProperty("user.home") + htmlDir;
    }

    /*具体产品页面的静态化*/
    private String toStatic(FlashPromotionProduct flashPromotionProduct) throws IOException, TemplateException {
        String outPath = "";
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(new File(templateDir));
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");
        // 第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate(templateName);
        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
        Map dataModel = new HashMap();
        // 向数据集中添加数据
        dataModel.put("fpp", flashPromotionProduct);

        String images = flashPromotionProduct.getPic();
        if (StringUtils.isNotEmpty(images)) {
            String[] split = images.split(",");
            List<String> imageList = Arrays.asList(split);
            dataModel.put("imageList", imageList);
        }
        // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
        // 文件名命名规则  seckill_+秒杀活动id + "_" + 秒杀产品ID，如 seckill_1_3.html
        String fileName = "seckill_" + flashPromotionProduct.getFlashPromotionId() + "_" + flashPromotionProduct.getId() + ".html";
        outPath = htmlDir + "/" + fileName;
        Writer out = new FileWriter(new File(outPath));
        // 第七步：调用模板对象的process方法输出文件。
        template.process(dataModel, out);
        // 第八步：关闭流。
        out.close();
        log.info("已在本地生成秒杀产品静态页：{}",outPath);
        return fileName;
    }

    /*根据秒杀活动，静态化该秒杀活动的所有页面*/
    @Override
    public List<String> makeStaticHtml(long secKillId) throws TemplateException, IOException {
        log.info("本地模板目录：{}，本地html目录：{}",templateDir,htmlDir);
        //查询秒杀商品信息
        List<FlashPromotionProduct> flashPromotionProducts =
                homePromotionService.secKillContent(secKillId,ConstantPromotion.SECKILL_OPEN);
        List<String> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(flashPromotionProducts)){
            log.warn("没有秒杀活动{[]}对应的产品信息，请检查DB中的秒杀数据",secKillId);
        }else{
            for(FlashPromotionProduct flashPromotionProduct : flashPromotionProducts){
                result.add(toStatic(flashPromotionProduct));
            }
        }
        return result;
    }

    @Override
    public int deployHtml(long secKillId) throws Exception {
        List<String> result = makeStaticHtml(secKillId);
        if(!CollectionUtils.isEmpty(result)){
            for(String host : nginxServerList){
                ChannelSftp channel = sftpUploadService.getChannel(host, userName, port, password);
                String path = rootPath + "/";
                sftpUploadService.createDir(path,channel);
                for(String fileName : result){
                    sftpUploadService.putFile(channel,new FileInputStream(htmlDir + "/" + fileName),path,fileName);
                }
                channel.quit();
                channel.exit();
                log.info("服务器：{}，静态网页上传完成",host);
            }
            return ConstantPromotion.STATIC_HTML_SUCCESS;
        }else{
            return ConstantPromotion.STATIC_HTML_FAILURE;
        }
    }
}
