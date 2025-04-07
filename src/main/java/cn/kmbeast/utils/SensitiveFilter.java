package cn.kmbeast.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger log = LoggerFactory.getLogger(SensitiveFilter.class);
    private String REPLACEMENT="***";
    private TrieNode  rootNode= new TrieNode();
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while((keyword = reader.readLine()) !=null){
                this.addKeyword(keyword);
            }

        }catch(IOException e){
            log.error("加载敏感字文件失败"+e.getSuppressed());
        }
    }

    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;
            if(i == keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }

        }


    }

    /**
     * 过滤敏感词
     * @param text 过滤前文本
     * @return 过滤后的
     */
    public String filiter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode = new TrieNode();
        int begin= 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();
        while(position < text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //指针3
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode=rootNode;
            }else if(tempNode.isKeyWordEnd()){
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootNode;
            }else{
                position++;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();
    }
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);
    }

    private class TrieNode{
        private  boolean isKeyWordEnd = false;
        private Map<Character,TrieNode> subNodes=new HashMap<>();
        public boolean isKeyWordEnd(){
            return isKeyWordEnd;
        }
        public void setKeyWordEnd(boolean keyWordEnd){
            isKeyWordEnd = keyWordEnd;
        }
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }
}
