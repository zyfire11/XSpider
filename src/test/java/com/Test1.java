package com;

import com.zy.utils.EncodeUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by zhouyi on 2017/5/14.
 */
public class Test1 {

    @Test
    public void test(){
        try {
            System.out.println(URLDecoder.decode("clickCount%3D28%26subBtnClick%3D2%26keyPress%3D3%26menuClick%3D0%26mouseMove%3D10483%26checkcode%3D0%26subBtnPosx%3D761%26subBtnPosy%3D757%26subBtnDelay%3D128%26keycode%3D56%2C50%2C54%26winWidth%3D1920%26winHeight%3D911%26userAgent%3DMozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F47.0.2526.106%20Safari%2F537.36", "gbk"));
            System.out.println(URLDecoder.decode("%E6%B3%A8%E5%86%8C%E8%BF%87%E4%BA%8E%E9%A2%91%E7%B9%81%EF%BC%8C%E8%AF%B7%E7%A8%8D%E5%90%8E%E5%86%8D%E8%AF%95", "utf-8"));
            System.out.println(EncodeUtils.unicdoeToGB2312("\\u6fc0\\u6d3b\\u7801\\u53d1\\u9001\\u6210\\u529f\\u3002\\u59821\\u5206\\u949f\\u540e\\u4ecd\\u672a\\u6536\\u5230\\uff0c\\u8bf7\\u91cd\\u65b0\\u83b7\\u53d6\\u3002"));

            System.out.println(EncodeUtils.unicdoeToGB2312("try {window.STK_14947664513082 && STK_14947664513082({\"code\":\"100000\",\"msg\":\"\",\"data\":{\"html\":\"<div id=\\\"pl_remote_registerLayer_index\\\">\\r\\n\\t    <div class=\\\"layer_point\\\">\\r\\n    <div class=\\\"layer_reg_confirm\\\">\\r\\n    <div class=\\\"W_tips tips_warn clearfix\\\" style=\\\"width:190px;\\\">\\r\\n                      <p class=\\\"icon\\\"><span class=\\\"icon_warnS\\\"><\\/span><\\/p>\\r\\n                      <p class=\\\"txt\\\">\\u4e3a\\u907f\\u514d\\u6076\\u610f\\u6ce8\\u518c\\uff0c\\u9700\\u9a8c\\u8bc1\\u624b\\u673a\\u53f7<\\/p>\\r\\n    <\\/div>\\r\\n    <div class=\\\"W_reg_form\\\" style=\\\"display:block\\\">\\r\\n    <div node-type=\\\"mobilesea_wrapper\\\">\\r\\n                    <div class=\\\"info_list\\\">\\r\\n                            <div class=\\\"tit\\\">\\u6240\\u5728\\u5730\\uff1a<\\/div>\\r\\n                            <div class=\\\"inp location\\\">\\r\\n                                <select class=\\\"W_select\\\" node-type=\\\"mobilesea_select\\\" name=\\\"zone\\\">\\r\\n                                    <option value=\\\"0086\\\">\\u4e2d\\u56fd\\u5927\\u9646<\\/option>\\r\\n                                    <option value=\\\"00852\\\">\\u9999\\u6e2f\\u5730\\u533a<\\/option>\\r\\n                                    <option value=\\\"00853\\\">\\u6fb3\\u95e8\\u5730\\u533a<\\/option>\\r\\n                                    <option value=\\\"00886\\\">\\u53f0\\u6e7e\\u5730\\u533a<\\/option>\\r\\n                                    <option value=\\\"001\\\">\\u7f8e\\u56fd<\\/option>\\r\\n                                    <option value=\\\"001\\\">\\u52a0\\u62ff\\u5927<\\/option>\\r\\n                                    <option value=\\\"0055\\\">\\u5df4\\u897f<\\/option>\\r\\n                                    <option value=\\\"0060\\\">\\u9a6c\\u6765\\u897f\\u4e9a<\\/option>\\r\\n                                    <option value=\\\"0061\\\">\\u6fb3\\u6d32<\\/option>\\r\\n                                    <option value=\\\"0081\\\">\\u65e5\\u672c<\\/option>\\r\\n                                    <option value=\\\"0082\\\">\\u97e9\\u56fd<\\/option>\\r\\n                                    <option value=\\\"0065\\\">\\u65b0\\u52a0\\u5761<\\/option>\\r\\n                                    <option value=\\\"0044\\\">\\u82f1\\u56fd<\\/option>\\r\\n                                    <option value=\\\"0033\\\">\\u6cd5\\u56fd<\\/option>\\r\\n                                    <option value=\\\"007\\\">\\u4fc4\\u7f57\\u65af<\\/option>\\r\\n                                    <option value=\\\"0091\\\">\\u5370\\u5ea6<\\/option>\\r\\n                                    <option value=\\\"0066\\\">\\u6cf0\\u56fd<\\/option>\\r\\n                                    <option value=\\\"0049\\\">\\u5fb7\\u56fd<\\/option>\\r\\n                                    <option value=\\\"0062\\\">\\u5370\\u5c3c<\\/option>\\r\\n                                    <option value=\\\"00855\\\">\\u67ec\\u57d4\\u5be8<\\/option>\\r\\n                                    <option value=\\\"0095\\\">\\u7f05\\u7538<\\/option>\\r\\n                                    <option value=\\\"00673\\\">\\u6587\\u83b1<\\/option>\\r\\n                                    <option value=\\\"0063\\\">\\u83f2\\u5f8b\\u5bbe<\\/option>\\r\\n                                    <option value=\\\"0084\\\">\\u8d8a\\u5357<\\/option>\\r\\n                                    <option value=\\\"00856\\\">\\u8001\\u631d<\\/option>\\r\\n                                <\\/select>\\r\\n                            <\\/div>\\r\\n                        <\\/div>\\r\\n                    \\r\\n                    <div class=\\\"info_list\\\">\\r\\n                      <div class=\\\"tit\\\">\\u624b\\u673a\\u53f7\\uff1a<\\/div>\\r\\n                      <div class=\\\"inp cell_phone\\\">\\r\\n                      \\t <div class=\\\"W_input foreign_tel\\\" node-type=\\\"mobilesea_box\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t<span class=\\\"tel_forenum\\\" node-type=\\\"mobilesea_txt\\\">0086<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t\\t<input no_cls=\\\"true\\\" node-type=\\\"mobilesea\\\" action-data=\\\"text=\\u8bf7\\u8f93\\u5165\\u60a8\\u7684\\u624b\\u673a\\u53f7\\u7801\\\" action-type=\\\"text_copy\\\" name=\\\"mobile\\\" type=\\\"text\\\" class=\\\"tel_num\\\" value=\\\"\\\" autocomplete=\\\"off\\\" \\/>\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n                      <\\/div>\\r\\n                    <\\/div>\\r\\n                    <div class=\\\"info_list\\\" node-type=\\\"activation_box\\\">\\r\\n                        <div class=\\\"inp active\\\">\\r\\n                          <a action-data=\\\"type=sendsms\\\" node-type=\\\"btn_sms_activation\\\" action-type=\\\"btn_sms_activation\\\" href=\\\"javascript:void(0)\\\" class=\\\"W_btn_e\\\"><span>\\u514d\\u8d39\\u83b7\\u53d6\\u77ed\\u4fe1\\u6fc0\\u6d3b\\u7801<\\/span><\\/a>\\r\\n                          <a style=\\\"display:none\\\" class=\\\"W_btn_e_disable\\\" href=\\\"javascript:void(0);\\\" node-type=\\\"btn_sms_activation_disable\\\"><span><em node-type=\\\"sms_timer\\\">180<\\/em>\\u79d2\\u540e\\u518d\\u83b7\\u53d6\\u77ed\\u4fe1<\\/span><\\/a>\\r\\n                          <!-- <a href=\\\"\\\" class=\\\"W_btn_e_disable\\\"><span>180\\u79d2\\u540e\\u91cd\\u65b0\\u83b7\\u53d6\\u77ed\\u4fe1<\\/span><\\/a> -->\\r\\n                          <input type=\\\"text\\\" name=\\\"pincode\\\" class=\\\"W_input\\\" value=\\\"\\\" node-type=\\\"activation\\\" maxlength=\\\"6\\\">\\r\\n                        <\\/div>\\r\\n                    <\\/div>\\r\\n                    <div node-type=\\\"btn_submit_wrapper\\\" class=\\\"info_submit clearfix\\\"><a class=\\\"link W_fr\\\" href=\\\"http:\\/\\/help.weibo.com\\/faq\\/q\\/2375\\/20136#20136\\\" target=\\\"_blank\\\">\\u6536\\u4e0d\\u5230\\u9a8c\\u8bc1\\u7801\\uff1f<\\/a><a class=\\\"W_btn_g\\\" action-type=\\\"btn_submit\\\" href=\\\"javascript:void(0);\\\" node-type=\\\"btn_submit\\\"><span>\\u63d0\\u4ea4<\\/span><\\/a><\\/div>\\r\\n                    <div node-type=\\\"btn_check_pincode_wrapper\\\"  class=\\\"info_list buttons clearfix\\\" style=\\\"display:none;\\\">\\r\\n\\t\\t\\t          <a action-type=\\\"btn_close\\\" href=\\\"#\\\" class=\\\"back W_fr\\\">\\u8fd4\\u56de<\\/a>\\r\\n\\t\\t\\t          <a action-type=\\\"btn_check_pincode\\\" node-type=\\\"btn_check_pincode\\\" tabindex=\\\"6\\\" class=\\\"W_btn_g\\\" href=\\\"javascript:void(0)\\\"><span>\\u4e0b\\u4e00\\u6b65<\\/span><\\/a>\\r\\n\\t\\t\\t        <\\/div>\\r\\n                  <\\/div>\\r\\n            <\\/div>\\r\\n    <div class=\\\"other_login\\\" style=\\\"display:none;\\\">\\r\\n        <div class=\\\"chief_log clearfix\\\">\\r\\n          <a style=\\\"display:none;\\\" class=\\\"W_fr\\\" href=\\\"javascript:void(0);\\\" title=\\\"\\u5c55\\u5f00\\\" node-type=\\\"other\\\">\\u66f4\\u591a\\u767b\\u5f55<span class=\\\"W_arrow S_link1\\\"><em title=\\\"\\u5c55\\u5f00\\\" node-type=\\\"arr\\\">\\u25c6<\\/em><\\/span><\\/a>\\r\\n          <a href=\\\"#\\\" class=\\\"S_func1\\\" style=\\\"display:none\\\"><span class=\\\"cp_logo ico_taobao\\\"><\\/span>\\u6dd8\\u5b9d\\u767b\\u5f55<\\/a>\\r\\n          <a class=\\\"S_func1\\\" href=\\\"\\\" suda-uatrack=\\\"key=tblog_weibologin3&value=other_qq\\\"><span class=\\\"cp_logo ico_qq\\\"><\\/span>QQ\\u767b\\u5f55\\u5fae\\u535a<\\/a>\\r\\n          <ul class=\\\"other_login_list clearfix\\\" style=\\\"display:none;\\\">\\r\\n            <li><a class=\\\"S_func1\\\" target=\\\"_blank\\\" href=\\\"http:\\/\\/sersh.passport.189.cn\\/OAuth\\/authorize.aspx?client_id=3535450001600701&response_type=code&redirect_uri=http%3A%2F%2Faccount.weibo.com%2Fset%2Fbindsns%2Fcallback&state=telecom\\\" suda-data=\\\"key=tblog_weibologin3&value=other_tianyi\\\"><span class=\\\"cp_logo ico_tianyi\\\"><\\/span>\\u5929\\u7ffc<\\/a><\\/li>\\r\\n\\t\\t\\t<li><a class=\\\"S_func1\\\" target=\\\"_blank\\\" href=\\\"http:\\/\\/account.weibo.com\\/set\\/bindsns\\/unicomoauth\\\" suda-data=\\\"key=tblog_weibologin3&value=other_liantong\\\"><span class=\\\"cp_logo ico_unicom\\\"><\\/span>\\u8054\\u901a<\\/a><\\/li>\\r\\n\\t\\t\\t<li><a class=\\\"S_func1\\\" target=\\\"_blank\\\" href=\\\"https:\\/\\/openapi.360.cn\\/oauth2\\/authorize?client_id=8819d1babbbc50a42021ee957c4b6e63&response_type=code&redirect_uri=http:\\/\\/account.weibo.com\\/set\\/bindsns\\/callback&scope=basic&display=default&state=360&relogin=sina\\\" suda-data=\\\"key=tblog_weibologin3&value=other_360\\\"><span class=\\\"cp_logo ico_360\\\"><\\/span>360<\\/a><\\/li>\\r\\n\\t\\t\\t<li><a class=\\\"S_func1\\\" target=\\\"_blank\\\" href=\\\"https:\\/\\/passport.weibo.com\\/othersitebind\\/authorize?entry=miniblog&site=baidu\\\" suda-uatrack=\\\"key=tblog_weibologin3&value=other_baidu\\\"><span class=\\\"cp_logo ico_baidu\\\"><\\/span>\\u767e\\u5ea6<\\/a><\\/li>\\r\\n          <\\/ul>\\r\\n        <\\/div>\\r\\n      <\\/div>        \\r\\n    <\\/div>\\r\\n    <\\/div>\\r\\n    <\\/div>\\r\\n\",\"title\":\"\\u77ed\\u4fe1\\u9a8c\\u8bc1\"}});} catch(e) {}"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}