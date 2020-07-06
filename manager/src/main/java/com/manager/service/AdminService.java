package com.manager.service;

import com.manager.mapper.AdminMapper;
import com.manager.model.Admin;
import com.manager.model.Exception.EdgeComputingServiceException;
import com.manager.model.Response.ResponseEnum;
import com.manager.model.api.LogFeign;
import com.manager.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Service
public class AdminService {
    private static final String mysqlSdfPatternString = "yyyy-MM-dd HH:mm:ss";

    private final AdminMapper adminMapper;
    private final LogFeign logFeign;

    @Autowired
    public AdminService(AdminMapper adminMapper,LogFeign logFeign) {
        this.adminMapper = adminMapper;
        this.logFeign = logFeign;
    }

    public Admin adminLogin(String account, String password) throws Exception {
        SimpleDateFormat mysqlSdf = new SimpleDateFormat(mysqlSdfPatternString);
        Admin admin = adminMapper.getByAccountAndPassword(account, password);
        if(admin == null) {
            throw new EdgeComputingServiceException(ResponseEnum.LOGIN_FAILED.getCode(), ResponseEnum.LOGIN_FAILED.getMessage());
        } else {
            //采用jwt获得token
            Date createTime = new Date();
            //每七天需要重新登录(可以直接将过期时间写入token，解析token时即可判断是否过期，而无需在代码中判断)
            Date expireTime = new Date(createTime.getTime() + 1000 * 60 * 60 * 24 * 7);
            Map<String, String> content = new HashMap<>();
            content.put("account", admin.getAccount());
            String token = CommonUtil.createJWT(content, "EdgeComputingService", createTime, expireTime);
            //更新数据库token_create_at，之后每次鉴权需要查看数据库查看自己的token是否是最新的
            adminMapper.updateTokenCreateTimeByAccount(mysqlSdf.format(createTime), admin.getAccount());
            adminMapper.updateTokenByAccount(token, admin.getAccount());
            //将token返回作为登录凭证
            admin.setToken(token);
            admin.setPassword(null);
            admin.setTokenCreateAt(null);
            logFeign.addLog(admin.getAccount(),admin.getAdminName(),"管理员'"+admin.getAdminName()+"'登录");
        }
        return admin;
    }

    public void adminLogout(String token) throws Exception {
        String account = adminMapper.getAccountFromToken(token);
        Admin admin = adminMapper.getByAccount(account);
        if(account==null){
            throw new EdgeComputingServiceException(ResponseEnum.LOGIN_FAILED.getCode(), ResponseEnum.LOGIN_FAILED.getMessage());
        }
        else {
            adminMapper.updateTokenCreateTimeByAccount(null, account);
            adminMapper.updateTokenByAccount(null,account);

            logFeign.addLog(admin.getAccount(),admin.getAdminName(),"管理员'"+admin.getAdminName()+"'退出");
        }
    }

    public String adminRegister(String account, String password, String adminName)
            throws Exception{
        if(account == null || adminName == null) {
            throw new EdgeComputingServiceException(ResponseEnum.LOGIN_FAILED.getCode(), ResponseEnum.LOGIN_FAILED.getMessage());
        } else {
            String name = adminMapper.getAdminNameFromAccount(account);
            if (name != null) {//账号已被注册
                return "error";
            } else {
                Date createTime = new Date();
                SimpleDateFormat mysqlSdf = new SimpleDateFormat(mysqlSdfPatternString);
                adminMapper.creatAdmin(null, account, password, adminName, null, null,mysqlSdf.format(createTime));

                /*----日志---*/
                logFeign.addLog("","","注册账号:"+account);

                return  "success";
            }
        }

    }


    public String alterPassword(String account,String oldpassword,String newpassword) throws Exception {
            String password=adminMapper.getPassWordByAccount(account);
            Admin admin = adminMapper.getByAccountAndPassword(account, oldpassword);
            if(admin == null) {
            throw new EdgeComputingServiceException(ResponseEnum.LOGIN_FAILED.getCode(), ResponseEnum.LOGIN_FAILED.getMessage());
            } else {
                if (password.equals(oldpassword)){//还要重新生成token
                    adminMapper.updatePassWordByAccount(account,newpassword);

                    logFeign.addLog(admin.getAccount(),admin.getAdminName(),"管理员'"+admin.getAdminName()+"'修改密码");
                    return "success";
                }
                else{
                return "error";
                }
            }

    }

    public Admin aboutInformationByAccount(String account){
        return adminMapper.getByAccount(account);
    }
    public Admin aboutInformationByToken(String token){
        return adminMapper.getByToken(token);
    }
    public String getAccountByToken(String token){return adminMapper.getAccountFromToken(token);}
    public String getAdminNameByAccount(String account){ return adminMapper.getAdminNameFromAccount(account);}
}