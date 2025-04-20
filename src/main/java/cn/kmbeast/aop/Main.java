package cn.kmbeast.aop;

import java.util.Scanner;
import java.util.*;

// 注意类名必须为 Main, 不要有任何 package xxx 信息
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int t = in.nextInt();
        System.out.println("t:"+t);
        for(int i=0;i < t ;i++){
            int n = in.nextInt();
            System.out.println("n:"+n);
            int num[] = new int[n];
            for(int j=0;j<n;j++){
                num[j] = in.nextInt();
            }
            int num2[] = new int[n];
            num2=num;
            Arrays.sort(num2);
            int mid = num2[n/2];
            int index=0;
            for(int j =0;i<n;i++){
                if(num[j]==mid){
                    index = j;
                }
            }
            int zhengxiang = 0;
            int nixiang = 0;
            for(int j=0;j<index;j++){
                if(num[j]<num[index]){
                    zhengxiang++;
                }else{
                    nixiang++;
                }
            }
            for(int j =index+1; j<n;j++){
                if(num[j]>num[index]){
                    zhengxiang++;
                }else{
                    nixiang++;
                }
            }
            System.out.print(zhengxiang+" "+nixiang);
        }




        // // 注意 hasNext 和 hasNextLine 的区别
        // while (in.hasNextInt()) { // 注意 while 处理多个 case
        //     int a = in.nextInt();
        //     int b = in.nextInt();
        //     System.out.println(a + b);
        // }
    }
}