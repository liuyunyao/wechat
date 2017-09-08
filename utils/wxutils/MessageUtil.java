package com.nidone.fitness.vchat.utils.wxutils;

import com.nidone.fitness.vchat.utils.aes.AesException;
import com.nidone.fitness.vchat.utils.aes.WXBizMsgCrypt;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    // 交换发送人和接收人

    /**
     * @param map     接收到的map消息
     * @param respmap 回复的map消息
     */
    public static void ChangeUser(Map<String, String> map, Map respmap) {
        respmap.put("ToUserName", map.get("FromUserName"));
        respmap.put("FromUserName", map.get("ToUserName"));
    }

    // 回复文字消息
    public static void RespTextXmlMessage(String content, Map map, Map respmap, HttpServletResponse response) throws Exception {
        ChangeUser(map, respmap);
        respmap.put("Content", content);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(MessageUtil.getTextXmlMessage(respmap));
    }

    /**
     * 获取文本消息
     */
    public static String getTextXmlMessage(Map<String, Object> params) {
        String str = "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[%s]]></Content>" +
                "</xml>";
        String message = String.format(
                str,
                params.get("ToUserName"),
                params.get("FromUserName"),
                new Date().getTime(),
                params.get("Content")
        );
        return message;
    }

    public static String getVoiceXmlMessage(Map<String, Object> params) {
        String str = "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[voice]]></MsgType>" +
                "<Voice>" +
                "<MediaId><![CDATA[%s]]></MediaId>" +
                "</Voice>" +
                "</xml>";
        String message = String.format(
                str,
                params.get("toUserName"),
                params.get("fromUserName"),
                new Date().getTime(),
                params.get("mediaId")
        );
        return message;
    }

    //获取图片信息
    public static String getImageXmlMessage(Map<String, Object> params) {
        String str = "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[image]]></MsgType>" +
                "<Image>" +
                "<MediaId><![%s]]></MediaId>" +
                "</Image>" +
                "</xml>";
        String message = String.format(
                str,
                params.get("ToUserName"),
                params.get("FromUserName"),
                new Date().getTime(),
                params.get("MediaId")
        );
        return message;
    }

    public static String getVideoXmlMessage(Map<String, Object> params) {
        String str = "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[video]]></MsgType>" +
                "<Video>" +
                "<MediaId><![CDATA[%s]]></MediaId>" +
                "<Title><![CDATA[%s]]></Title>" +
                "<Description><![CDATA[%s]]></Description>" +
                "</Video>" +
                "</xml>";
        String message = String.format(
                str,
                params.get("toUserName"),
                params.get("fromUserName"),
                new Date().getTime(),
                params.get("mediaId"),
                params.get("title"),
                params.get("description")
        );
        return message;
    }

    public static String getMusicXmlMessage(Map<String, Object> params) {
        String str = "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[music]]></MsgType>" +
                "<Music>" +
                "<Title><![CDATA[%s]]></Title>" +
                "<Description><![%s]]></Description>" +
                "<MusicUrl><![CDATA[%s]]></MusicUrl>" +
                "<HQMusicUrl><![CDATA[%s]]></HQMusicUrl>" +
                "<ThumbMediaId><![CDATA[%s]]></ThumbMediaId>" +
                "</Music>" +
                "</xml>";
        String message = String.format(
                str,
                params.get("toUserName"),
                params.get("fromUserName"),
                new Date().getTime(),
                params.get("mediaId"),
                params.get("title"),
                params.get("description"),
                params.get("musicUrl"),
                params.get("hqMusicUrl"),
                params.get("thumbMediaId")
        );
        return message;
    }

    public static String getNewsXmlMessage(Map<String, Object> params) {
        StringBuffer str = new StringBuffer();
        List<Map<String, Object>> list = (List<Map<String, Object>>) params.get("items");
        str.append("<xml>");
        str.append("<ToUserName><![CDATA[").append(params.get("toUserName")).append("]]></ToUserName>");
        str.append("<FromUserName><![CDATA[").append(params.get("fromUserName")).append("]]></FromUserName>");
        str.append("<CreateTime>").append(new Date().getTime()).append("</CreateTime>");
        str.append("<MsgType><![CDATA[news]]></MsgType>");
        str.append("<ArticleCount>").append(list.size()).append("</ArticleCount>");
        str.append("<Articles>");
        //有多少条目循环多次
        for (Map<String, Object> args : list) {
            str.append("<item>");
            str.append("<Title><![CDATA[").append(args.get("title")).append("]]></Title>");
            str.append("<Description><![CDATA[").append(args.get("description")).append("]]></Description>");
            str.append("<PicUrl><![CDATA[").append(args.get("picurl")).append("]]></PicUrl>");
            str.append("<Url><![CDATA[").append(args.get("url")).append("]]></Url>");
            str.append("</item>");
        }
        str.append("</Articles>");
        str.append("</xml>");
        return str.toString();
    }

    public static WXBizMsgCrypt getWXBizMsgCrypt() throws AesException {
        String encodingAesKey = "LAibIr23SfP0ojgBMsc27QP0K2mVNm7XvS7i9nwrNJF";
        String token = "nidfit";
        String appId = "wx822a456f8b87aff3";
        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
        return pc;
    }

    /**
     * 安全模式解析消息体
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXMLCrypt(HttpServletRequest request)
            throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        /**
         * 第一步：从inputStream中读取XML文件
         */
        InputStream is = request.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        // 没次读取的内容
        String line = null;
        // 最终读取的内容
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        is.close();

        /**
         //		 * 第二部：解密
         //		 */
        String msgSignature = request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        WXBizMsgCrypt wxCrypt = MessageUtil.getWXBizMsgCrypt();
        String fromXML = wxCrypt.decryptMsg(msgSignature, timestamp, nonce, sb.toString());
        //String fromXML=sb.toString();
        /**
         * 第三步，解析XML，获取请求参数
         */
        // 通过IO得到Document
        Document doc = DocumentHelper.parseText(fromXML);
        // 得到跟节点
        Element root = doc.getRootElement();
        recursiveParseXml(root, map);
        return map;
    }

    /**
     * 明文模式解析消息体
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXML(HttpServletRequest request)
            throws Exception {
        // 将解析结果存储在 HashMap 中
        Map<String, String> map = new HashMap<String, String>();
        // 从 request 中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到 xml 根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        // 释放资源
        inputStream.close();
        return map;
    }

    private static void recursiveParseXml(Element root, Map<String, String> map) {
        List<Element> elementList = root.elements();
        if (elementList.size() == 0) {
            map.put(root.getName(), root.getTextTrim());
        } else {
            for (Element e : elementList) {
                recursiveParseXml(e, map);
            }
        }
    }

}
