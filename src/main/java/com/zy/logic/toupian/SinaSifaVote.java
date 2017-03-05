package com.zy.logic.toupian;

import com.zy.logic.component.loader.SimpleLoader;
import com.zy.model.entity.Judge;
import com.zy.utils.HttpUtils;
import com.zy.utils.RegexUtil;
import extralib.gson.*;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wuhao.tools.elasticsearch.bean.Http;

import java.util.*;

/**
 * Created by zhouyi on 2017/3/3.
 */
public class SinaSifaVote {

    //获取参与人
    public List<Judge> getJudgeList(){
        List<Judge> judges = new ArrayList<>();
        String url = "http://sifa.sina.com.cn/zhuanti/2017/haofaguan.html";
        String html = SimpleLoader.download(url, "utf-8");
        Document doc = Jsoup.parse(html);
        System.out.println(html);
        List<Element> elements = doc.select("div[class^=voteList]").first().select("div[class^=candidateList]");
        System.out.println(elements.size());

        for(Element element : elements){
            String name = element.getElementsByClass("candidateName").get(0).text();
            String court = element.getElementsByClass("candidateCourt").get(0).select("a").first().text();
            String id = element.getElementsByClass("candidateOptionL").first().select("input[name=checkbox]").first().attr("id");
            System.out.println(id + "--" + name + "--" + court);
            Judge judge = new Judge();
            judge.setId(Integer.parseInt(id));
            judge.setName(name);
            judge.setCourt(court);
            judges.add(judge);
        }
        return judges;
    }
    //获取各人投票数
    public Map<Integer, Integer> getVoteList(){
        Map<Integer, Integer> map = new HashMap<>();
        String url = "http://59.110.12.100:8080/cms/voter/getList.do?callback=jQuery16045185696309839507_"+ System.currentTimeMillis() +"&_=" + System.currentTimeMillis();
        String html = SimpleLoader.download(url, "utf-8");
        System.out.println(html);
        html = RegexUtil.getMatchGroupRegex(html, "jQuery\\d+_\\d+\\((.*?)\\)$");
        System.out.println(html);
        JsonArray jsonArray = new JsonParser().parse(html).getAsJsonObject().get("Data").getAsJsonArray();
        for(JsonElement jsonElement : jsonArray){
            Integer id = jsonElement.getAsJsonObject().get("cid").getAsInt();
            Integer sum = jsonElement.getAsJsonObject().get("sum").getAsInt();
            System.out.println(id + "--" + sum);
            map.put(id, sum);
        }
        return map;
    }

    public boolean vote(String ids){
        boolean flag = false;
        String url = "http://59.110.12.100:8080/cms/voter/voter.do?ids="+ ids +"&callback=jQuery1606313079320825636_"+System.currentTimeMillis()+"&_=" + System.currentTimeMillis();
        Map<String, String> headers=  new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
        headers.put("Host", "59.110.12.100:8080");
        headers.put("Referer", "http://sifa.sina.com.cn/zhuanti/2017/haofaguan.html");
        headers.put("Proxy-Switch-Ip", "yes");
        HttpResponse response = HttpUtils.doGetByProxy(url, headers ,true);
        String html = HttpUtils.getStringFromResponseByCharset(response, "utf8");
        System.out.println(html);

        return flag;

    }

    public static void main(String[] args) {
        SinaSifaVote vote = new SinaSifaVote();
//        vote.getJudgeList();
//        vote.getVoteList();
        vote.vote("29");
    }


}
