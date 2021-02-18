package wang.wangby.gupiao;

import java.util.Random;

public class Kaili {
    public static void main(String[] args) {
        Kaili k=new Kaili();
        int remain=0;
        for(int i=0;i<10;i++){
            GamblingResult result=k.doBet(10000,100,600,2);
            System.out.println(result);
            remain+=result.getEndMoney();
        }
        System.out.println(remain);
    }

    /**
     * @param  sum 总金额
     * @param per 单次投注
     * @param  times 执行次数
     * @param invest 连续几次之后投注
     * */
    GamblingResult doBet(int sum,int per,int times,int invest){
        GamblingResult result=new GamblingResult();
        result.setBeginMoney(sum);

        int success=0;
        int failure=0;
        int empty=0;
        int big=0;
        int small=0;
        int currnt=per;
        int maxcurrent=per;
        for(int i=0;i<times;i++){
            if(sum<=1000){
                System.out.println("赔光了:"+i);
                break;
            }
            boolean isbig=isBig();
            System.out.println(i+":"+isbig);
            if(big>=invest){

                if(isbig){
                    failure++;
                    sum-=currnt;
                    //currnt*=2;
                }else{
                    success++;
                    sum+=currnt;
                    currnt=per;
                }
                System.out.println("------------"+big+":"+small+":"+currnt);
            }else if(small>=invest){
                if(isbig){
                    success++;
                    sum+=currnt;
                    currnt=per;
                }else{
                    failure++;
                    sum-=currnt;
                   // currnt*=2;
                }
                System.out.println("------------"+big+":"+small+":"+currnt);
            }else{
                empty++;
                currnt=per;
            }
            if(isbig){
                big++;
                small=0;
            }else{
                small++;
                big=0;
            }
            if(currnt>maxcurrent){
                maxcurrent=currnt;
            }
            if(currnt>500){
                currnt=per;
            }
        }

        result.setSuccess(success);
        result.setFailue(failure);
        result.setEmpty(empty);
        result.setEndMoney(sum);
        result.setMaxPer(maxcurrent);
        return result;
    }

    Random rd=new Random();

     boolean isBig(){
        int i=rd.nextInt(2);
        switch (i){
            case 0:
                return true;
        }
        return false;
    }
}
