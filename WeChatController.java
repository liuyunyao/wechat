
/**
 * Created by yuny on 7/13/17.
 */
@Controller
@RequestMapping(value = "/v1")
public class WeChatController{
     
    @RequestMapping(value = "weixinCore",method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public void Coreservice(HttpServletRequest request,HttpServletResponse response) {
           System.out.println("进入chat");
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        if(isGet) {
            /**验证服务器接口（第一次为get请求）*/
            System.out.println("enter get");
            WXCommonUtil.Validate(request,response);
        }else {
            System.out.println("enter post");
            /**接收消息并返回消息*/
            acceptMessage(request, response);
        }
    }


    /**
     *   接收微信服务器发的信息
     * @param request
     * @param response
     */
    private void acceptMessage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("接收消息成功");
        try {
            //Map<String,String> map=MessageUtil.parseXMLCrypt(request);  //解析加密信息
            Map<String,String> map=MessageUtil.parseXML(request);
            System.out.println(map);
            Map<String,Object> respmap=new HashMap<>();
            /** 第一次关注后，自动回复并保存信息*/
            if (map.containsKey("Event") && map.get("Event").equals("subscribe")) {
                MessageUtil.RespTextXmlMessage("欢迎关注", map, respmap, response);
                String openid = map.get("FromUserName");
                /** 获取access_token*/
                String access_token = null;               
                access_token = WXCommonUtil.Getaccess_token(appid,appsecret);                    
                /** 获取用户个人信息 */
                JSONObject wxInfo= WXCommonUtil.GetInfoMessage(access_token, openid);
                ThirdAccountInfo thirdAccount=new ThirdAccountInfo();
                thirdAccount.setRefId(wxInfo.getString("openid"));
                System.out.println(wxInfo);             
            } else {
                /** 接收其他事件时，返回的信息 */
                MessageUtil.RespTextXmlMessage("你好", map, respmap, response);
            }
        } catch (Exception e) {
            LOGGER.error("post request failed",e);
        }
    }
}
