package com.nidone.fitness.vchat.utils.wxutils;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by yuny on 2017/8/7.
 */
public class WXCommonUtil {
    // 得到access_token
    public static String Getaccess_token(String appid, String appsecret) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + appsecret;
        JSONObject object = HttpRequestUtil.httpsRequest(url, "GET", "");
        String access_token = object.getString("access_token");
        return access_token;
    }

    // 根据openid获取用户信息
    public static JSONObject GetInfoMessage(String access_token, String openid) {
        String url1 = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
        JSONObject object = HttpRequestUtil.httpsRequest(url1, "GET", "");
        return object;
    }

    // 获取带参数的二维码地址
    public static String GetQrcodeUrl(String access_token, String param, int expire_seconds) {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token;
        String json = "{\"" + expire_seconds + "\": 604800, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + param + "\"}}}";
        JSONObject resp = HttpRequestUtil.httpsRequest(url, "POST", json);
        String QrcodeUrl = resp.getString("url");
        //System.out.println(resp);
        return QrcodeUrl;
    }

    //
    // 验证服务器接口
    public static void Validate(HttpServletRequest request, HttpServletResponse response) {
        String TOKEN = "   ";// 自行填写  与微信端一致就行
        try {
            // 开发者提交信息后，微信服务器将发送GET请求到填写的服务器地址URL上，GET请求携带参数
            String signature = request.getParameter("signature");// 微信加密签名（token、timestamp、nonce。）
            String timestamp = request.getParameter("timestamp");// 时间戳
            String nonce = request.getParameter("nonce");// 随机数
            String echostr = request.getParameter("echostr");// 随机字符串
            PrintWriter out = response.getWriter();
            // 将token、timestamp、nonce三个参数进行字典序排序
            String[] params = new String[]{TOKEN, timestamp, nonce};
            Arrays.sort(params);
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            String clearText = params[0] + params[1] + params[2];
            String algorithm = "SHA-1";
            String sign = new String(
                    Hex.encodeHex(MessageDigest.getInstance(algorithm).digest((clearText).getBytes()), true));
            // 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
            if (signature.equals(sign)) {
                response.getWriter().print(echostr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //

    // 封装模板消息
    public static void putTemplateMessage(JSONObject jsonObject, String openid, String template_id, String topcolor, String data, String url) {
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        jsonObject.put("topcolor", topcolor);
        jsonObject.put("data", data);
    }

    //发送模板消息
    public static JSONObject sendTemplateMessage(String access_token, JSONObject jsonObject) {
        String URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
        JSONObject result = HttpRequestUtil.httpsRequest(URL, "POST", jsonObject);
        return result;
    }

    // 获取公众号已创建的标签
    public static JSONObject getTags(String access_token) {
        String url = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=" + access_token;
        return HttpRequestUtil.httpsRequest(url, "GET", "");
    }

    // 批量为用户打标签
    public static JSONObject batchTags(String access_token) {
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token" + access_token;
        String json = "{\n" +
                "  \"openid_list\" : [//粉丝列表\n" +
                "    \"ocYxcuAEy30bX0NXmGn4ypqx3tI0\",\n" +
                "    \"ocYxcuBt0mRugKZ7tGAHPnUaOW7Y\"\n" +
                "  ],\n" +
                "  \"tagid\" : 134\n" +
                "}";
        // JSONObject jsonObject=JSONObject.fromObject(json);
        return HttpRequestUtil.httpsRequest(url, "POST", json);
    }

    // 自定义菜单
    public static JSONObject CreateMenu(String access_token) {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token;
        String data = "{\n" +
                "    \"button\": [\n" +
                "        {\n" +
                "            \"type\": \"view\",\n" +
                "            \"name\": \"我的主页\",\n" +
                "            \"url\": \"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx672373b33d5a1143&redirect_uri=http://6f5319ac.ngrok.io/v1/index&response_type=code&scope=snsapi_base&state=123#wechat_redirect\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"type\": \"view\",\n" +
                "            \"name\": \"课程训练\",\n" +
                "            \"url\": \"http://6246f23f.ngrok.io/v1/trainingplan\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        // JSONObject jsonObject=JSONObject.fromObject(data);
        JSONObject jsonObject = HttpRequestUtil.httpsRequest(url, "POST", data);
        return jsonObject;
    }
}
