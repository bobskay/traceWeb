git clone https://github.com/bobskay/traceWeb.git  

依次到以下目录执行mvn install  
~/traceWeb/parent/  
~/traceWeb/compile/a-api/  
~/traceWeb/tools/web/api/  
~/traceWeb/tools/web  
~/traceWeb/compile/a-tools/  
~/traceWeb/tools/repository/file/  
~/traceWeb/config  
~/traceWeb/app/trace/  

killall -9 java  
nohup java -jar ./target/trace-1.jar &  
tail -f ./nohup  